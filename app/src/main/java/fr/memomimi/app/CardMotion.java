package fr.memomimi.app;

/** Smooth virtual rotation, shared by rendering, touch and accessibility. */
final class CardMotion {
    static final long OPEN_MS = 280, CLOSE_MS = 320;
    static float progress(long elapsed, long duration) {
        return Math.max(0f, Math.min(1f, elapsed/(float)duration));
    }
    static float eased(float t) { return t*t*(3-2*t); }
    static float width(float t) { return Math.max(.025f, Math.abs((float)Math.cos(Math.PI*eased(t)))); }
}
