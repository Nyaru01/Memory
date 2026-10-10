package fr.memomimi.app;

import org.junit.Test;
import static org.junit.Assert.*;

public class MascotAnimationTest {
    @Test public void loadingGivesOneSlowGreetingWithoutHopOrPoseLoop() {
        MascotAnimation animation = new MascotAnimation();
        for (long time=0;time<650;time+=50) {
            animation.sampleLoading(time, false);
            assertEquals(0, animation.frame);
        }
        float previousBlend = 0;
        for (long time=650;time<=1100;time+=50) {
            animation.sampleLoading(time, false);
            assertEquals(19, animation.frame);
            assertEquals(0, animation.previousFrame);
            assertTrue(animation.blend>=previousBlend);
            assertEquals(0f, animation.lift, 0f);
            assertTrue(Math.abs(animation.tilt)<=1.2f);
            assertTrue(Math.abs(animation.scaleY-1)<=.0121f);
            previousBlend=animation.blend;
        }
        for (long time : new long[]{1100, 1749, 5000, 20000}) {
            animation.sampleLoading(time, false);
            assertEquals(19, animation.frame);
            assertEquals(1f, animation.blend, 0f);
        }
        animation.sampleLoading(1000, false);
        assertEquals(0f, animation.lift, 0f);
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
