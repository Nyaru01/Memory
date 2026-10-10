package fr.memomimi.app;

/** Shared Canvas design tokens; no runtime dependency. Values are dp before scaling. */
final class UiStyle {
    static final int BACKGROUND = 0xfffff8ec, SURFACE = 0xfffffcf7;
    static final int TEXT = 0xff3d3540, MUTED = 0xff625b69;
    static final int PRIMARY = 0xffb94735, ACCENT = 0xff7156aa;
    static final int SUCCESS = 0xff287b69, MINT = 0xff5ccbae;
    static final float RADIUS = 18, GAP = 8, MARGIN = 16, TARGET = 48;
    static final long SELECT_MS = 160, NAVIGATION_MS = 220, CONFETTI_MS = 1800;
    static final android.graphics.Typeface FONT = android.graphics.Typeface.create("sans", android.graphics.Typeface.BOLD);
    private UiStyle() { }
}
