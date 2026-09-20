"""Create two original, gently looping PCM soundtracks for MémoMondes."""

import math
import random
import struct
import wave
from pathlib import Path

RATE = 22050
BEAT = 0.6
OUTPUT = Path(__file__).resolve().parents[1] / "app/src/main/assets/music"


def tone(buffer, start, duration, frequency, volume, voice="bell"):
    first = int(start * RATE)
    count = min(int(duration * RATE), len(buffer) - first)
    if count <= 0:
        return
    for n in range(count):
        t = n / RATE
        envelope = (1 - math.exp(-t * 35)) * min(1, (duration - t) * 8)
        if voice == "bell":
            envelope *= math.exp(-t * 2.0)
            value = math.sin(2 * math.pi * frequency * t) + 0.25 * math.sin(2 * math.pi * frequency * 2 * t)
        elif voice == "pad":
            value = 0.7 * math.sin(2 * math.pi * frequency * t) + 0.2 * math.sin(2 * math.pi * frequency * 2 * t)
        else:
            envelope *= math.exp(-t * 3.1)
            value = math.sin(2 * math.pi * frequency * t) + 0.16 * math.sin(2 * math.pi * frequency * 3 * t)
        buffer[first + n] += volume * envelope * value


def render(name, chords, melody, seed):
    random.seed(seed)
    duration = len(chords) * 4 * BEAT
    buffer = [0.0] * int(duration * RATE)
    for measure, (root, third, fifth) in enumerate(chords):
        start = measure * 4 * BEAT
        for pitch in (root, third, fifth):
            tone(buffer, start, 4 * BEAT, pitch, 0.025, "pad")
        tone(buffer, start, 1.8 * BEAT, root / 2, 0.038, "pad")
        tone(buffer, start + 2 * BEAT, 1.8 * BEAT, fifth / 2, 0.03, "pad")
        for step in range(4):
            tone(buffer, start + step * BEAT, 0.75 * BEAT,
                 (root, third, fifth, third)[step] * 2, 0.026, "pluck")
    for step, pitch in enumerate(melody):
        if pitch:
            tone(buffer, step * BEAT, 1.25 * BEAT, pitch, 0.085, "bell")
    peak = max(abs(sample) for sample in buffer)
    pcm = bytearray()
    for sample in buffer:
        pcm += struct.pack("<h", int(max(-1, min(1, sample / peak * 0.72)) * 32767))
    OUTPUT.mkdir(parents=True, exist_ok=True)
    with wave.open(str(OUTPUT / name), "wb") as output:
        output.setnchannels(1)
        output.setsampwidth(2)
        output.setframerate(RATE)
        output.writeframes(pcm)


if __name__ == "__main__":
    # 8 measures / 32 beats each. The phrase ends on the tonic for a gentle loop.
    render("star_garden.wav",
           [(293.66, 369.99, 440), (220, 277.18, 329.63),
            (246.94, 293.66, 369.99), (196, 246.94, 293.66)] * 2,
           [587.33, 0, 739.99, 659.25, 587.33, 0, 440, 0,
            554.37, 0, 659.25, 554.37, 493.88, 0, 440, 0,
            493.88, 0, 587.33, 739.99, 659.25, 0, 587.33, 0,
            440, 0, 493.88, 554.37, 587.33, 0, 0, 0], 12)
    render("little_steps.wav",
           [(261.63, 329.63, 392), (174.61, 220, 261.63),
            (220, 261.63, 329.63), (196, 246.94, 293.66)] * 2,
           [523.25, 587.33, 659.25, 0, 783.99, 659.25, 587.33, 0,
            523.25, 0, 440, 523.25, 587.33, 0, 392, 0,
            440, 523.25, 587.33, 0, 659.25, 587.33, 523.25, 0,
            493.88, 0, 587.33, 493.88, 523.25, 0, 0, 0], 29)
