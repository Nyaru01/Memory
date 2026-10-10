"""Generate exact signed APK deltas from unchanged ZIP payloads; standard library only."""
import argparse
import gzip
import hashlib
import json
import struct
import zipfile
from pathlib import Path


def digest(data):
    return hashlib.sha256(data).hexdigest()


def payloads(path, data):
    with zipfile.ZipFile(path) as archive:
        for entry in archive.infolist():
            header = entry.header_offset
            name_len, extra_len = struct.unpack_from("<HH", data, header + 26)
            start = header + 30 + name_len + extra_len
            size = entry.compress_size
            if size >= 64:
                yield start, size, digest(memoryview(data)[start:start + size])


def generate(base_path, target_path, patch_path):
    base, target = base_path.read_bytes(), target_path.read_bytes()
    sources = {(size, sha): start for start, size, sha in payloads(base_path, base)}
    copied = 0
    with patch_path.open("wb") as raw, gzip.GzipFile(fileobj=raw, mode="wb", mtime=0, filename="", compresslevel=9) as patch:
        patch.write(struct.pack(">IQ", 0x4D4D4431, len(target)))

        def literal(start, end):
            for offset in range(start, end, 1024 * 1024):
                chunk = target[offset:min(end, offset + 1024 * 1024)]
                patch.write(struct.pack(">BI", 1, len(chunk)))
                patch.write(chunk)

        cursor = 0
        for start, size, sha in payloads(target_path, target):
            old = sources.get((size, sha))
            if old is None:
                continue
            literal(cursor, start)
            patch.write(struct.pack(">BQI", 0, old, size))
            copied += size
            cursor = start + size
        literal(cursor, len(target))
        patch.write(b"\x02")
    # Independent replay catches ZIP offsets and framing errors before publication.
    rebuilt = bytearray()
    with gzip.open(patch_path, "rb") as patch:
        assert patch.read(12) == struct.pack(">IQ", 0x4D4D4431, len(target))
        while True:
            command = patch.read(1)
            if command == b"\x02":
                assert patch.read() == b""
                break
            if command == b"\x00":
                offset, length = struct.unpack(">QI", patch.read(12))
                rebuilt.extend(base[offset:offset + length])
            elif command == b"\x01":
                length, = struct.unpack(">I", patch.read(4))
                rebuilt.extend(patch.read(length))
            else:
                raise ValueError("Invalid patch")
    assert rebuilt == target, "Reconstructed APK differs from signed target"
    return {"baseSha256": digest(base), "sha256": digest(patch_path.read_bytes()),
            "size": patch_path.stat().st_size, "reusedBytes": copied}


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--base", type=Path, action="append", default=[])
    parser.add_argument("--target", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--version", required=True)
    parser.add_argument("--code", type=int, required=True)
    args = parser.parse_args()
    args.output.mkdir(parents=True, exist_ok=True)
    target = args.target.read_bytes()
    manifest = {"schema": 1, "packageName": "fr.memomimi.app", "versionName": args.version,
                "versionCode": args.code, "minSdk": 23, "apkSize": len(target), "apkSha256": digest(target), "patches": []}
    for base in args.base:
        name = "from-" + digest(base.read_bytes()) + ".mmd.gz"
        patch = generate(base, args.target, args.output / name)
        patch["url"] = f"https://github.com/Nyaru01/Memory/releases/download/v{args.version}/{name}"
        if patch["size"] >= min(len(target), 64 * 1024 * 1024):
            raise ValueError("Patch is too large; revise assets or omit this unsupported base")
        manifest["patches"].append(patch)
        print(f"{base.name}: {patch['size']:,} bytes downloaded / {len(target):,} bytes APK")
    (args.output / "update.json").write_text(json.dumps(manifest, indent=2) + "\n", encoding="utf-8")


if __name__ == "__main__":
    main()
