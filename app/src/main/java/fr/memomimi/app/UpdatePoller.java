package fr.memomimi.app;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

/** Foreground-only polling; resume is throttled to avoid rapid repeated requests. */
final class UpdatePoller {
    static final long INTERVAL_MS = 60000, RESUME_GAP_MS = 15000;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable check;
    private boolean active;
    private long lastCheck = -1;
    private final Runnable tick = new Runnable() {
        @Override public void run() {
            if (!active) return;
            long now = SystemClock.uptimeMillis();
            if (lastCheck < 0 || now-lastCheck >= RESUME_GAP_MS) {
                lastCheck = now;
                check.run();
            }
            if (active) handler.postDelayed(this, INTERVAL_MS);
        }
    };

    UpdatePoller(Runnable check) { this.check = check; }
    void resume() {
        if (active) return;
        active = true;
        long delay = lastCheck < 0 ? 0 : Math.max(0, RESUME_GAP_MS-(SystemClock.uptimeMillis()-lastCheck));
        handler.postDelayed(tick, delay);
    }
    void pause() { active = false; handler.removeCallbacks(tick); }
}
