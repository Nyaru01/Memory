"""Publish validated release assets with the existing Git credential, never print credentials."""
import argparse
import base64
import json
import subprocess
import urllib.request
import urllib.error
from pathlib import Path

REPO = "Nyaru01/Memory"
API = f"https://api.github.com/repos/{REPO}"


def token():
    result = subprocess.run(["git", "credential", "fill"], input="protocol=https\nhost=github.com\n\n", text=True,
                            capture_output=True, check=True)
    fields = dict(line.split("=", 1) for line in result.stdout.splitlines() if "=" in line)
    if not fields.get("password"):
        raise RuntimeError("Connect GitHub through Git Credential Manager first")
    return fields["password"]


def request(secret, url, method="GET", body=None, content_type="application/json"):
    if not url.startswith(("https://api.github.com/", "https://uploads.github.com/")):
        raise ValueError("Credential destination rejected")
    data = json.dumps(body).encode() if isinstance(body, dict) else body
    req = urllib.request.Request(url, data=data, method=method, headers={
        "Authorization": f"Bearer {secret}", "Accept": "application/vnd.github+json", "User-Agent": "MemoMondes-Release",
        "X-GitHub-Api-Version": "2022-11-28", "Content-Type": content_type})
    with urllib.request.urlopen(req, timeout=120) as response:
        result = response.read()
        return json.loads(result) if result else None


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--directory", type=Path, required=True)
    parser.add_argument("--apk", type=Path, required=True)
    parser.add_argument("--notes", type=Path, required=True)
    parser.add_argument("--replace-manifest", action="store_true")
    args = parser.parse_args()
    manifest = json.loads((args.directory / "update.json").read_text(encoding="utf-8"))
    import hashlib
    if hashlib.sha256(args.apk.read_bytes()).hexdigest() != manifest["apkSha256"]:
        raise ValueError("Final APK differs from manifest")
    secret = token()
    tag = "v" + manifest["versionName"]
    if args.replace_manifest:
        release = request(secret, API + "/releases/tags/" + tag)
        for asset in release["assets"]:
            if asset["name"] == "update.json": request(secret, asset["url"], "DELETE")
        path = args.directory / "update.json"
        request(secret, release["upload_url"].split("{")[0] + "?name=update.json", "POST", path.read_bytes(), "application/json")
        print(release["html_url"])
        return
    # GitHub needs one commit to create a tag in an empty repository.
    if not request(secret, API + "/branches"):
        request(secret, API + "/contents/README.md", "PUT", {
            "message": "Document MemoMondes update distribution",
            "content": base64.b64encode(("# MémoMondes\n\nAPK et correctifs de mise à jour dans Releases.\n"
                "Installer une première fois l'APK 8.6 ou suivant pour activer les mises à jour.\n"
                "Le jeu télécharge ensuite uniquement un correctif compatible ; Android confirme l'installation.\n").encode()).decode()})
    release = request(secret, API + "/releases", "POST", {"tag_name": tag, "name": "MémoMondes " + manifest["versionName"],
                      "body": args.notes.read_text(encoding="utf-8"), "draft": True, "prerelease": False})
    try:
        files = [(args.apk, "MemoMondes.apk", "application/vnd.android.package-archive")]
        files += [(path, path.name, "application/gzip") for path in args.directory.glob("*.mmd.gz")]
        files.append((args.directory / "update.json", "update.json", "application/json"))
        for path, name, mime in files:
            request(secret, release["upload_url"].split("{")[0] + "?name=" + name, "POST", path.read_bytes(), mime)
        release = request(secret, release["url"], "PATCH", {"draft": False, "make_latest": "true"})
        print(release["html_url"])
    except Exception:
        print("Publication incomplete; release remains draft. Existing latest release unchanged.")
        raise


if __name__ == "__main__":
    main()
