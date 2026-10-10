package fr.memomimi.app;

import org.junit.Test;
import static org.junit.Assert.*;

public class MascotAnimationTest {
    @Test public void dedicatedWelcomeGreetsSlowlyThenSettlesAndCanReplay() {
        assertEquals(5000,WelcomeMascot.DURATION_MS);
        assertEquals(0,WelcomeMascot.pose(500,false));assertEquals(1,WelcomeMascot.pose(1000,false));
        assertEquals(3,WelcomeMascot.pose(1500,false));assertEquals(4,WelcomeMascot.pose(1750,false));
        assertEquals(6,WelcomeMascot.pose(2800,false));assertEquals(7,WelcomeMascot.pose(4200,false));
        assertEquals(1,WelcomeMascot.pose(1000,false));
        for(int t=0;t<=5000;t+=20)assertEquals(0,WelcomeMascot.pose(t,true));
    }
    @Test public void loadingStaysGroundedWithOneBriefBlinkAndContinuousBreathing() {
        MascotAnimation animation=new MascotAnimation();float previous=1;
        for(long time=0;time<1750;time+=10){
            animation.sampleLoading(time,false);
            assertEquals(time>=1050 && time<1190?4:0,animation.frame);
            assertEquals(animation.frame,animation.previousFrame);
            assertEquals(0,animation.lift,0);assertEquals(0,animation.tilt,0);
            assertTrue(Math.abs(animation.scaleY-1)<=.0061f);
            assertTrue(Math.abs(animation.scaleY-previous)<.0001f);previous=animation.scaleY;
        }
        animation.sampleLoading(1100,true);assertEquals(0,animation.frame);
        assertEquals(1,animation.scaleY,0);assertEquals(0,animation.tilt,0);
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

    @Test public void transitionsUseWholePoseDurationAndLandWithoutTransformJumps() {
        MascotAnimation a=new MascotAnimation();
        a.sample(1080+112,-1,0,false);
        assertTrue(a.blend>.45f && a.blend<.55f);
        for(int end:new int[]{2430,3780}) {
            a.sample(end-1,-1,0,false); float lift=a.lift, tilt=a.tilt, scale=a.scaleX;
            a.sample(end,-1,0,false);
            assertEquals(lift,a.lift,.002f); assertEquals(tilt,a.tilt,.02f); assertEquals(scale,a.scaleX,.002f);
        }
        for(int t=4400;t<9400;t+=33) { a.sample(t,-1,0,false); assertEquals(0,a.frame); }
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
