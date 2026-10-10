package fr.memomimi.app;

import java.util.Random;

/** Victory lifecycle, independent from graphics and audio. Times use uptimeMillis. */
final class VictoryCelebration {
    private static final String[] MESSAGES = {
        "Bravo, tu as réussi !", "Youpi, toutes les paires !",
        "Bien joué, petit explorateur !", "Quelle belle découverte !",
        "Bravo pour tes efforts !", "Tu peux être fier de toi !"
    };
    long startedAt;
    long reactionAt = -1;
    boolean active;
    int reaction;
    String message = MESSAGES[0];

    void begin(long now, Random random) {
        if (active) return;
        active = true;
        startedAt = now;
        reactionAt = -1;
        reaction = 0;
        message = MESSAGES[random.nextInt(MESSAGES.length)];
    }

    boolean touch(long now) {
        if (!active || (reactionAt >= 0 && now - reactionAt < MascotAnimation.TOUCH_MS)) return false;
        reactionAt = now;
        reaction = (reaction + 1) % 2;
        return true;
    }

    void reset() { active = false; reactionAt = -1; }
}
