package fr.memomimi.app;

import org.junit.Test;
import static org.junit.Assert.*;

public class MascotAnimationTest {
    @Test public void loadingChangesPoseWithinItsShortDurationAndRespectsCalm() {
        MascotAnimation animation = new MascotAnimation();
        int[] seen = new int[6];
        for (int i = 0; i < 6; i++) {
            animation.sampleLoading(i*290+140, false);
            seen[i] = animation.frame;
        }
        assertEquals(0, seen[0]); assertEquals(19, seen[2]);
        assertEquals(22, seen[3]); assertEquals(0, seen[5]);
        animation.sampleLoading(1000, false);
        assertTrue(animation.lift > 0);
        animation.sampleLoading(1000, true);
        assertEquals(0, animation.frame);
        assertEquals(0f, animation.lift, 0f);
        assertEquals(0f, animation.tilt, 0f);
        assertEquals(1f, animation.scaleY, 0f);
    }
    @Test public void timelineSelectsEveryPoseAndReturnsToIdle() {
        MascotAnimation animation = new MascotAnimation();
        for (int i = 0; i < 6; i++) {
            animation.sample(i*180, -1, 0, false);
            assertEquals(MascotAnimation.State.APPEAR, animation.state);
            assertEquals(6+i, animation.frame);
            animation.sample(1080+i*225, -1, 0, false);
            assertEquals(MascotAnimation.State.VICTORY, animation.state);
            assertEquals(12+i, animation.frame);
            animation.sample(4500, i*240, 0, false);
            assertEquals(MascotAnimation.State.TOUCH, animation.state);
            assertEquals(18+i, animation.frame);
        }
        animation.sample(3780, -1, 0, false);
        assertEquals(MascotAnimation.State.IDLE, animation.state);
        animation.sample(3780+5850, -1, 0, false);
        assertEquals(MascotAnimation.State.BLINK, animation.state);
        assertEquals(5, animation.frame);
        animation.sample(12750, 1440, 0, false);
        assertEquals(MascotAnimation.State.IDLE, animation.state);
    }

    @Test public void reducedMotionHasNoTransformsOrTimeDependentPose() {
        MascotAnimation animation = new MascotAnimation();
        animation.sample(1, -1, 0, true);
        int frame = animation.frame;
        animation.sample(6500, -1, 0, true);
        assertEquals(frame, animation.frame);
        assertEquals(1f, animation.scaleX, 0f);
        assertEquals(1f, animation.scaleY, 0f);
        assertEquals(0f, animation.lift, 0f);
        assertEquals(0f, animation.tilt, 0f);
    }

    @Test public void longRuntimeStaysWithinAtlasAndTransformBudget() {
        MascotAnimation animation = new MascotAnimation();
        for (long time = 0; time < 60000; time += 33) {
            animation.sample(time, -1, 0, false);
            assertTrue(animation.frame >= 0 && animation.frame < 24);
            assertTrue(animation.scaleX > .8f && animation.scaleX < 1.2f);
            assertTrue(animation.scaleY > .8f && animation.scaleY < 1.2f);
        }
    }
}
