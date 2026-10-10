package fr.memomimi.app;

/** Allocation-free atlas timeline. Row-major 6 x 4 sheet; motion uses monotonic time. */
final class MascotAnimation {
    enum State { IDLE, BLINK, APPEAR, VICTORY, TOUCH }
    State state = State.IDLE;
    int frame;
    int previousFrame;
    float blend = 1;
    float scaleX = 1, scaleY = 1, lift, tilt;
    static final long APPEAR_MS = 1080, VICTORY_MS = 2700, TOUCH_MS = 1440;
    static final long WELCOME_HOLD_MS = 650, WELCOME_BLEND_MS = 450;
    private static float smooth(float t) {
        t=Math.max(0,Math.min(1,t)); return t*t*(3-2*t);
    }

    void sampleLoading(long elapsed, boolean reduced) {
        state = State.IDLE;
        scaleX = scaleY = 1; lift = tilt = 0; blend = 1;
        if (reduced) { frame = previousFrame = 0; return; }
        long local = Math.max(0, elapsed);
        // One unhurried greeting, then hold. Never squeeze a full joy cycle into startup.
        frame = local < WELCOME_HOLD_MS ? 0 : 19;
        previousFrame = 0;
        float progress = Math.max(0, Math.min(1, (local-WELCOME_HOLD_MS)/(float)WELCOME_BLEND_MS));
        blend = frame == 0 ? 1 : progress*progress*(3-2*progress);
        tilt = (float)Math.sin(local/1400d)*1.2f;
        scaleY = 1+.012f*(float)Math.sin(local/1200d);
        scaleX = 1-(scaleY-1)*.6f;
    }

    void sample(long elapsed, long reactionElapsed, int reaction, boolean reduced) {
        elapsed = Math.max(0, elapsed) * 2 / 3;
        reactionElapsed = reactionElapsed < 0 ? -1 : reactionElapsed * 2 / 3;
        scaleX = scaleY = 1; lift = tilt = 0; blend = 1;
        if (reduced) {
            state = State.IDLE;
            frame = reactionElapsed >= 0 ? (reaction == 0 ? 3 : 2) : 0;
            previousFrame = frame;
            return;
        }
        long local;
        if (reactionElapsed >= 0 && reactionElapsed < 960) {
            state = State.TOUCH; local = reactionElapsed;
            frame = 18 + Math.min(5, (int)(local / 160));
            float t = local / 960f;
            tilt = (float)Math.sin(t * Math.PI * 4) * (reaction == 0 ? 9 : -9);
            lift = .09f * (float)Math.sin(t * Math.PI);
            scaleX = 1 + .06f * (float)Math.sin(t*Math.PI*4);
            scaleY = 1 / scaleX;
            previousFrame = frame == 18 ? 0 : frame-1;
            blend = smooth((local%160)/160f);
        } else if (elapsed < 720) {
            state = State.APPEAR; local = elapsed;
            frame = 6 + Math.min(5, (int)(local / 120));
            float t = elapsed / 720f;
            lift = -1.6f*(float)Math.pow(1-t, 3) + .13f*(float)Math.sin(Math.PI*t);
            scaleX = 1 + .12f*(float)Math.cos(t*Math.PI*4)*(1-t);
            scaleY = 1 / scaleX;
            previousFrame = frame == 6 ? 6 : frame-1;
            blend = smooth((local%120)/120f);
        } else if (elapsed < 2520) {
            state = State.VICTORY; local = (elapsed - 720) % 900;
            frame = 12 + Math.min(5, (int)(local / 150));
            float t = local / 900f;
            float arc=(float)Math.sin(t*Math.PI);
            lift = .14f * arc*arc;
            tilt = (float)Math.sin(t*Math.PI*2)*4*arc;
            scaleX = 1 + .07f*(float)Math.sin(t*Math.PI*2)*arc;
            scaleY = 1 / scaleX;
            previousFrame = frame == 12 ? (elapsed < 870 ? 11 : 17) : frame-1;
            blend = smooth((local%150)/150f);
        } else {
            local = elapsed - 2520;
            long cycle = local % 5400;
            if (cycle >= 3800 && cycle < 4100) {
                state = State.BLINK;
                frame = cycle < 3880 || cycle >= 4020 ? 4 : 5;
                previousFrame = cycle < 3880 ? 0 : cycle < 4020 ? 4 : 5;
                blend = smooth((cycle-(cycle < 3880 ? 3800 : cycle < 4020 ? 3880 : 4020))/(cycle < 3880 ? 80f : cycle < 4020 ? 140f : 80f));
            } else {
                state = State.IDLE;
                // A stable silhouette breathes continuously; no constant unrelated pose swaps.
                frame = 0; previousFrame = 0;
                if (local<360) { previousFrame=17; blend=smooth(local/360f); }
                else if(cycle>=4100 && cycle<4280) { previousFrame=4; blend=smooth((cycle-4100)/180f); }
            }
            scaleY = 1 + .025f*(float)Math.sin(local/850d);
            scaleX = 1 - (scaleY-1)*.55f;
            lift = .015f*(float)Math.sin(local/1100d);
            tilt = (float)Math.sin(local/1400d)*2.8f;
        }
    }
}
