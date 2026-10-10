package fr.memomimi.app;

import org.junit.Test;
import static org.junit.Assert.*;

public class CardMotionTest {
    @Test public void smoothTurnIsSymmetricAndHasAVisibleEdge() {
        assertEquals(1f, CardMotion.width(0), .001f);
        assertEquals(.025f, CardMotion.width(.5f), .001f);
        assertEquals(1f, CardMotion.width(1), .001f);
        for (int i = 0; i <= 50; i++) {
            float t = i/100f;
            assertEquals(CardMotion.width(t), CardMotion.width(1-t), .001f);
            assertTrue(CardMotion.width(t) >= .025f);
        }
        assertTrue(CardMotion.eased(.1f) < .1f);
        assertTrue(CardMotion.eased(.9f) > .9f);
    }
}
