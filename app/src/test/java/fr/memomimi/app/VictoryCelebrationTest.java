package fr.memomimi.app;

import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

public class VictoryCelebrationTest {
    @Test public void beginsOnceAndReplayResetsReaction() {
        VictoryCelebration state = new VictoryCelebration();
        assertFalse(state.touch(1));
        state.begin(100, new Random(1));
        String message = state.message;
        state.begin(300, new Random(2));
        assertEquals(100, state.startedAt);
        assertSame(message, state.message);
        assertTrue(state.touch(500));
        assertFalse(state.touch(600));
        assertTrue(state.touch(2000));
        state.reset();
        assertFalse(state.touch(2000));
        state.begin(2000, new Random(3));
        assertEquals(2000, state.startedAt);
        assertEquals(-1, state.reactionAt);
    }
}
