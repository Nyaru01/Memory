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
    private static final int[] WELCOME_FRAMES = {0, 1, 19, 22, 23, 0};

    void sampleLoading(long elapsed, boolean reduced) {
        state = State.IDLE;
        scaleX = scaleY = 1; lift = tilt = 0; blend = 1;
        if (reduced) { frame = previousFrame = 0; return; }
        long local = Math.max(0, elapsed);
        int index = Math.min(5, (int)(local/290));
        frame = WELCOME_FRAMES[index];
        previousFrame = WELCOME_FRAMES[Math.max(0, index-1)];
        blend = Math.min(1, (local%290)/90f);
        float wave = (float)Math.sin(Math.PI*Math.min(1f, local/1750f));
        tilt = (float)Math.sin(local/330d)*4f*wave;
        scaleY = 1+.035f*(float)Math.sin(local/250d)*wave;
        scaleX = 1-(scaleY-1)*.6f;
        if (index == 3) {
            lift = .075f*(float)Math.sin(Math.PI*(local%290)/290f);
        }
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
            previousFrame = frame == 18 ? 18 : frame-1;
            blend = Math.min(1, (local%160)/50f);
        } else if (elapsed < 720) {
            state = State.APPEAR; local = elapsed;
            frame = 6 + Math.min(5, (int)(local / 120));
            float t = elapsed / 720f;
            lift = -1.6f*(float)Math.pow(1-t, 3) + .13f*(float)Math.sin(Math.PI*t);
            scaleX = 1 + .12f*(float)Math.cos(t*Math.PI*4)*(1-t);
            scaleY = 1 / scaleX;
            previousFrame = frame == 6 ? 6 : frame-1;
            blend = Math.min(1, (local%120)/45f);
        } else if (elapsed < 2520) {
            state = State.VICTORY; local = (elapsed - 720) % 900;
            frame = 12 + Math.min(5, (int)(local / 150));
            float t = local / 900f;
            lift = .16f * (float)Math.sin(t*Math.PI);
            tilt = (float)Math.sin(t*Math.PI*2)*5;
            scaleX = 1 + .09f*(float)Math.cos(t*Math.PI*2);
            scaleY = 1 / scaleX;
            previousFrame = frame == 12 ? (elapsed < 870 ? 11 : 17) : frame-1;
            blend = Math.min(1, (local%150)/50f);
        } else {
            local = elapsed - 2520;
            long cycle = local % 5400;
            if (cycle >= 3800 && cycle < 4100) {
                state = State.BLINK;
                frame = cycle < 3880 || cycle >= 4020 ? 4 : 5;
                previousFrame = cycle < 3880 ? 2 : cycle < 4020 ? 4 : 5;
                blend = Math.min(1, (cycle-(cycle < 3880 ? 3800 : cycle < 4020 ? 3880 : 4020))/40f);
            } else {
                state = State.IDLE;
                // Long holds avoid constant swapping; the body breathes between pose changes.
                frame = cycle < 1700 ? 0 : cycle < 3000 ? 1 : cycle < 4300 ? 2 : 3;
                long poseStart = frame == 0 ? 0 : frame == 1 ? 1700 : frame == 2 ? 3000 : 4300;
                previousFrame = frame == 0 ? 3 : frame-1;
                if (cycle >= 4100 && cycle < 4300) { previousFrame = 4; poseStart = 4100; }
                if (elapsed < 2640) previousFrame = 17;
                blend = Math.min(1, (cycle-poseStart)/120f);
            }
            scaleY = 1 + .025f*(float)Math.sin(local/850d);
            scaleX = 1 - (scaleY-1)*.55f;
            lift = .015f*(float)Math.sin(local/1100d);
            tilt = (float)Math.sin(local/1400d)*2.8f;
        }
    }
}
