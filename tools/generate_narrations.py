"""Render the four new world stories with ElevenLabs; key via environment only."""

import json
import os
import re
import urllib.error
import urllib.request
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "app/src/main/java/fr/memomimi/app/StoryLibrary.java"
DESTINATION = ROOT / "app/src/main/assets/stories"
VOICE = "cgSgspJ2msm6clMCkdW9"


def main():
    key = os.environ.get("ELEVENLABS_API_KEY")
    if not key:
        raise SystemExit("ELEVENLABS_API_KEY is not configured")
    source = SOURCE.read_text(encoding="utf-8")
    block = source.split("static final String[] EXTRA_TEXTS = {", 1)[1].split("};", 1)[0]
    stories = re.findall(r'"([^"\\]*(?:\\.[^"\\]*)*)"', block)
    if len(stories) != 4:
        raise SystemExit(f"Expected 4 stories, found {len(stories)}")
    DESTINATION.mkdir(parents=True, exist_ok=True)
    for offset, story in enumerate(stories):
        destination = DESTINATION / f"story_{14 + offset}.mp3"
        body = json.dumps({
            "text": story,
            "model_id": "eleven_flash_v2_5",
            "voice_settings": {"stability": 0.72, "similarity_boost": 0.78,
                               "style": 0.2, "use_speaker_boost": True},
        }, ensure_ascii=False).encode("utf-8")
        request = urllib.request.Request(
            f"https://api.elevenlabs.io/v1/text-to-speech/{VOICE}?output_format=mp3_44100_128",
            data=body,
            headers={"xi-api-key": key, "Content-Type": "application/json",
                     "Accept": "audio/mpeg"},
            method="POST")
        try:
            with urllib.request.urlopen(request, timeout=120) as response:
                audio = response.read()
        except urllib.error.HTTPError as error:
            print(f"Story {14 + offset}: ElevenLabs HTTP {error.code}")
            print(error.read().decode("utf-8", errors="replace")[:300])
            continue
        if not audio.startswith(b"ID3") and not audio[:2] == b"\xff\xfb":
            print(f"Story {14 + offset}: unexpected audio response")
            continue
        destination.write_bytes(audio)
        print(f"Story {14 + offset}: {len(audio)} bytes")


if __name__ == "__main__":
    main()
