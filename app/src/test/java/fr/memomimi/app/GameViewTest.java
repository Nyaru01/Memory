package fr.memomimi.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;
import java.io.File;
import java.io.FileOutputStream;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28, 35})
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class GameViewTest {
    GameView view;
    Context context;

    @Before public void setup() {
        context = RuntimeEnvironment.getApplication();
        context.getSharedPreferences("m", 0).edit().clear().commit();
        view = new GameView(context);
        view.sound = false;
        view.haptics = false;
        view.activityActive = false;
        view.pairs = 3;
    }

    @After public void cleanup() { view.releaseAudio(); }

    @Test public void soloCompletion() { scenario(1, false); }
    @Test public void duelCompletion() { scenario(2, false); }
    @Test public void cooperativeCompletion() { scenario(3, false); }
    @Test public void calmCompletion() { scenario(1, true); }

    @Test public void loadingUsesMascotWithoutStartingVictory() {
        view.screen = -1; view.splashStart = GameView.now()-700;
        render("loading-lumi-small", 320, 616);
        assertEquals(-1, view.screen);
        assertFalse(view.celebration.active);
        assertTrue(view.splashMascotArea.bottom < view.splashBadge.top);
        assertEquals(0, view.victoryMascot.animation.frame);
        assertTrue(view.welcomeScreen.hasArtwork());assertTrue(view.welcomeScreen.bytes()<=4*1024*1024);
        view.splashStart = GameView.now()-5100;
        render(null, 320, 616);
        assertEquals(0, view.screen);
        assertEquals(0,view.welcomeScreen.bytes());
    }

    @Test public void rapidTapWaitsForFullTurnAndReducedMotionDoesNot() {
        start(1, false);
        tap(0);
        org.robolectric.shadows.ShadowSystemClock.advanceBy(java.time.Duration.ofMillis(200));
        tap(1); assertEquals(-1, view.second); assertEquals(0, view.found);
        org.robolectric.shadows.ShadowSystemClock.advanceBy(java.time.Duration.ofMillis(90));
        tap(1); assertEquals(1, view.found);
        start(1, true);
        tap(0); tap(1); assertEquals(1, view.found);
    }

    @Test public void cardTurnRestoresPaintAndCompletes() {
        start(1, false); tap(0);
        org.robolectric.shadows.ShadowSystemClock.advanceBy(java.time.Duration.ofMillis(90));
        render("card-turn-90ms", 480, 900);
        assertEquals(0, view.flipIndex);
        org.robolectric.shadows.ShadowSystemClock.advanceBy(java.time.Duration.ofMillis(80));
        render("card-turn-170ms", 480, 900);
        assertEquals(255, view.p.getAlpha());
        assertEquals(Paint.Style.FILL, view.p.getStyle());
        org.robolectric.shadows.ShadowSystemClock.advanceBy(java.time.Duration.ofMillis(120));
        render("card-turn-open", 480, 900);
        assertEquals(-1, view.flipIndex);
        assertEquals(0, view.first);
    }

    @Test public void incompleteGameCannotTriggerCelebration() {
        start(1, false);
        view.pendingWinAt = GameView.now() - 1;
        render(null, 480, 900);
        assertEquals(1, view.screen);
        assertFalse(view.celebration.active);
    }

    @Test public void atlasIsLoadedOnceWithBoundedMemory() {
        assertTrue(view.victoryMascot.hasAtlas());
        int bytes = view.victoryMascot.atlasBytes();
        assertTrue(bytes > 0 && bytes <= 8 * 1024 * 1024);
        view.victoryMascot.load(context);
        assertEquals(bytes, view.victoryMascot.atlasBytes());
    }

    @Test public void mascotTouchAndNavigationRemainAvailable() {
        scenario(1, false);
        view.found = view.pairs;
        view.pendingWinAt = GameView.now()-1;
        render(null, 320, 616);
        String message = view.celebration.message;
        org.robolectric.shadows.ShadowSystemClock.advanceBy(java.time.Duration.ofMillis(4200));
        render("mascot-idle-small", 320, 616);
        assertTrue(view.victoryMascot.bounds.bottom < view.storyBtn.top);
        assertTrue(view.victoryMascot.bounds.top > view.H * .345f);
        click(view.victoryMascot.bounds);
        assertTrue(view.celebration.reactionAt >= 0);
        render("mascot-touch-small", 320, 616);
        assertSame(message, view.celebration.message);
        click(view.homeBtn);
        assertEquals(8, view.screen);
        view.screen = 2;
        click(view.play);
        assertEquals(1, view.screen);
        assertFalse(view.celebration.active);
    }

    private void click(android.graphics.RectF bounds) {
        MotionEvent event = MotionEvent.obtain(GameView.now(), GameView.now(), MotionEvent.ACTION_UP,
                bounds.centerX(), bounds.centerY(), 0);
        view.onTouchEvent(event); event.recycle();
    }

    @Test public void accessibilityCanSelectCardsWithoutExposingHiddenImages() {
        start(1, false);
        android.view.accessibility.AccessibilityNodeProvider provider = view.getAccessibilityNodeProvider();
        android.view.accessibility.AccessibilityNodeInfo hidden = provider.createAccessibilityNodeInfo(GameAccessibility.CARD_BASE);
        assertTrue(hidden.getContentDescription().toString().contains("cachée"));
        assertFalse(hidden.getContentDescription().toString().contains("image"));
        assertTrue(provider.performAction(GameAccessibility.CARD_BASE, android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK, null));
        assertEquals(0, view.first);
        ready();
        assertTrue(provider.createAccessibilityNodeInfo(GameAccessibility.CARD_BASE).getContentDescription().toString().contains("image 1"));
        assertTrue(provider.performAction(GameAccessibility.CARD_BASE + 1, android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK, null));
        assertEquals(1, view.found);
        assertFalse(provider.createAccessibilityNodeInfo(GameAccessibility.CARD_BASE).isClickable());
    }

    @Test public void androidMotionPreferenceIsRespected() {
        android.provider.Settings.Global.putFloat(context.getContentResolver(), android.provider.Settings.Global.ANIMATOR_DURATION_SCALE, 0f);
        view.resumeMusic();
        assertTrue(view.animationsReduced());
        view.calm = false;
        start(1, false); tap(0);
        render(null, 480, 900);
        assertEquals(-1, view.flipIndex);
    }

    @Test public void cancelledParentPressDoesNotOpenSettings() {
        view.screen = 0; render(null, 480, 900);
        MotionEvent down = MotionEvent.obtain(GameView.now(), GameView.now(), MotionEvent.ACTION_DOWN, view.parentBtn.centerX(), view.parentBtn.centerY(), 0);
        view.onTouchEvent(down); down.recycle();
        assertTrue(view.parentHolding);
        assertSame(view.parentBtn, view.pressedTarget);
        MotionEvent cancel = MotionEvent.obtain(GameView.now(), GameView.now(), MotionEvent.ACTION_CANCEL, 0, 0, 0);
        view.onTouchEvent(cancel); cancel.recycle();
        view.m1lambda$onTouchEvent$3$frmemomimiappGameView();
        assertFalse(view.parentHolding); assertNull(view.pressedTarget); assertEquals(0, view.screen);
    }

    @Test public void pauseInvalidatesQueuedMatchFeedbackAndParentHold() {
        view.parentHolding = true;
        int generation = view.matchFeedbackGeneration;
        view.pauseMusic();
        assertFalse(view.activityActive); assertFalse(view.parentHolding);
        assertTrue(view.matchFeedbackGeneration > generation);
        assertTrue(view.soundExecutor.getQueue().isEmpty());
    }

    @Test public void ordinaryPairCanBeResumed() {
        start(1, false);
        tap(0); ready(); tap(1); ready();
        view.saveForPause();
        assertTrue(context.getSharedPreferences("m", 0).getBoolean("game_active", false));
        view.loadGame();
        assertEquals(1, view.found);
        assertTrue(view.matched[0]);
        assertTrue(view.matched[1]);
        assertEquals(1, view.screen);
    }

    @Test public void denseBoardsHaveDistinctTouchTargets() {
        for (int pairs : new int[]{3, 6, 10, 12, 15}) {
            view.pairs = pairs;
            view.start(); view.tutorialActive = false;
            render("grid-" + pairs, 360, 740);
            for (int index = 0; index < pairs * 2; index++) assertNotNull(position(index));
            view.saveForPause();
            int columns = view.cols;
            view.loadGame();
            assertEquals("Restored grid columns for " + pairs, columns, view.cols);
        }
    }

    @Test public void matchedPairVibratesOnceAndRespectsToggle() {
        view.releaseAudio();
        final int[] successes={0};
        view=new GameView(context) {
            @Override public boolean performHapticFeedback(int effect) {
                if(effect==android.view.HapticFeedbackConstants.CONFIRM
                        || effect==android.view.HapticFeedbackConstants.LONG_PRESS) successes[0]++;
                return false;
            }
        };
        view.sound=false; view.activityActive=false;
        for(boolean enabled:new boolean[]{true,false}) {
            view.pairs=3; view.haptics=enabled; start(1,true);
            int before=successes[0]; tap(0);tap(2); // mismatch
            assertEquals(before,successes[0]); ready();view.first=-1;view.second=-1;
            tap(0);tap(1); assertEquals(before+(enabled?1:0),successes[0]);
            tap(0); assertEquals(before+(enabled?1:0),successes[0]);
        }
    }

    @Test public void smallMenusHaveReadableTargetsAndCompletedAdventureLabel() {
        for(int[] size:new int[][]{{320,480},{360,640}}) {
            view.screen=0;view.homeScroll=0;render(null,size[0],size[1]);
            assertTrue(view.adventureButton.height()/view.d>=48);
            assertTrue(view.freePlayButton.height()/view.d>=48);
            view.openFreePlay();render(null,size[0],size[1]);
            for(android.graphics.RectF target:new android.graphics.RectF[]{view.easy,view.medium,view.hard,view.expert,view.play,view.homeBtn}) {
                assertTrue(target.width()/view.d>=48);assertTrue(target.height()/view.d>=48);
            }
        }
        context.getSharedPreferences("m",0).edit().putInt("adventure_completed",40).commit();
        view.screen=0;view.homeScroll=0;render(null,360,640);
        assertTrue(view.accessible.targets().get(0).label.contains("Aventure terminée"));
    }

    @Test public void accessibilityActivatesVisiblePartOfClippedControl() {
        view.openFreePlay(); render(null,320,320);
        view.pairs=8;
        // Leave only the bottom strip of the six-pair control below the fixed header.
        view.homeScroll=view.easy.top-(view.systemTop()+64*view.d)+view.easy.height()-8*view.d;
        render(null,320,320);
        assertTrue(view.easy.centerY()<view.visibleTop(view.easy));
        for(GameAccessibility.Target target:view.accessible.targets()) if(target.bounds==view.easy) {
            assertTrue(view.accessible.performAction(target.id,android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK,null));
            assertEquals(6,view.pairs); return;
        }
        fail("Clipped control must remain accessible");
    }

    @Test public void parentHoldSurvivesFingerJitterAndOpensSettings() {
        android.app.Activity activity=org.robolectric.Robolectric.buildActivity(android.app.Activity.class).setup().get();
        activity.setContentView(view);
        try {
            view.screen=0;view.showResumePrompt=false;view.homeScroll=0;render(null,320,640);
            parentEvent(MotionEvent.ACTION_DOWN,view.parentBtn.centerX(),view.parentBtn.centerY());
            parentEvent(MotionEvent.ACTION_MOVE,view.parentBtn.centerX(),view.parentBtn.centerY()+12*view.d);
            assertTrue(view.parentHolding);assertFalse(view.dragging);
            org.robolectric.shadows.ShadowLooper.idleMainLooper(GameView.PARENT_HOLD_MS,java.util.concurrent.TimeUnit.MILLISECONDS);
            assertEquals(3,view.screen);assertFalse(view.parentHolding);
            render(null,320,640);boolean haptics=view.haptics;
            parentEvent(MotionEvent.ACTION_UP,view.hapticBtn.centerX(),view.hapticBtn.centerY());
            assertEquals(haptics,view.haptics);assertFalse(view.parentReleasePending);
        } finally { activity.finish(); }
    }

    @Test public void shortParentTapDoesNotOpenSettings() {
        view.screen=0;render(null,320,640);
        parentEvent(MotionEvent.ACTION_DOWN,view.parentBtn.centerX(),view.parentBtn.centerY());
        parentEvent(MotionEvent.ACTION_UP,view.parentBtn.centerX(),view.parentBtn.centerY());
        view.m1lambda$onTouchEvent$3$frmemomimiappGameView();assertEquals(0,view.screen);
    }

    void parentEvent(int action,float x,float y) {
        MotionEvent event=MotionEvent.obtain(GameView.now(),GameView.now(),action,x,y,0);
        view.onTouchEvent(event);event.recycle();
    }

    @Test public void adventureResetRequiresConfirmationAndPreservesCollection() {
        android.content.SharedPreferences prefs=context.getSharedPreferences("m",0);
        prefs.edit().putInt("adventure_completed",4).putInt("album_0",63).putInt("best_6",12)
                .putInt("saved_adventure_level",1).putBoolean("game_active",true).putString("saved_cards","old")
                .putBoolean("calm",true).commit();
        view.calm=true;
        view.screen=3;render(null,320,480);view.parentsScroll=view.parentsScrollMax;render(null,320,480);
        click(view.adventureResetBtn);assertTrue(view.showResetConfirm);render(null,320,480);
        assertTrue(view.resetConfirmBtn.height()/view.d>=48);
        view.handleBack();assertFalse(view.showResetConfirm);assertEquals(4,view.adventure.completed());
        click(view.adventureResetBtn);render(null,320,480);click(view.resetCancelBtn);
        assertEquals(4,view.adventure.completed());
        click(view.adventureResetBtn);render(null,320,480);click(view.resetConfirmBtn);
        assertEquals(0,view.adventure.completed());assertEquals(1,view.adventure.current());
        assertFalse(prefs.getBoolean("game_active",true));assertFalse(prefs.contains("saved_cards"));
        assertEquals(63,prefs.getInt("album_0",0));assertEquals(12,prefs.getInt("best_6",0));assertTrue(prefs.getBoolean("calm",false));
        assertEquals(0,view.adventureMap.motion.pendingFrom);assertFalse(view.showResetConfirm);
    }

    @Test public void adventureResetKeepsFreePlayResume() {
        android.content.SharedPreferences prefs=context.getSharedPreferences("m",0);
        prefs.edit().putInt("adventure_completed",40).putInt("saved_adventure_level",0)
                .putBoolean("game_active",true).putString("saved_cards","free").commit();
        assertTrue(view.adventure.reset());assertTrue(prefs.getBoolean("game_active",false));
        assertEquals("free",prefs.getString("saved_cards",""));assertEquals(0,view.adventure.completed());
    }

    private void start(int players, boolean calm) {
        view.players = players; view.calm = calm; view.start();
        view.tutorialActive = false; view.hintUntil = 0; view.previewUntil = 0;
        view.cards = new int[]{0, 0, 1, 1, 2, 2};
        render(null, 480, 900);
    }

    private void scenario(int players, boolean calm) {
        start(players, calm);
        tap(0); ready(); tap(1);
        assertEquals(1, view.found);
        assertTrue(view.matched[0] && view.matched[1]);
        assertEquals(1, view.screen);
        assertEquals(0, view.pendingWinAt);
        ready(); view.successUntil = GameView.now() + 700;
        render("match-" + players + "-" + calm, 480, 900);
        assertEquals(255, view.p.getAlpha());
        assertEquals(Paint.Style.FILL, view.p.getStyle());
        if (players == 2) {
            assertEquals(1, view.owners[0]); assertEquals(1, view.owners[1]); assertEquals(1, view.score1);
        }
        tap(2); ready(); tap(4);
        assertTrue(view.hideAt > 0);
        assertFalse(view.matched[2] || view.matched[4]);
        render("mismatch-" + players, 480, 900);
        org.robolectric.shadows.ShadowSystemClock.advanceBy(java.time.Duration.ofMillis(2000));
        render(null, 480, 900);
        org.robolectric.shadows.ShadowSystemClock.advanceBy(java.time.Duration.ofMillis(400));
        render(null, 480, 900);
        assertEquals(-1, view.first); assertEquals(-1, view.second); assertEquals(-1, view.closeA);
        if (players == 2) assertEquals(2, view.turn);
        ready(); tap(2); ready(); tap(3); ready(); tap(4); ready(); tap(5);
        assertEquals(3, view.found);
        assertEquals(1, view.screen);
        assertTrue(view.pendingWinAt > GameView.now());
        assertTrue(view.pendingWinAt - GameView.now() <= (calm ? 450 : GameView.MATCH_DURATION_MS));
        assertTrue(view.handleBack()); assertFalse(view.showQuit);
        int moves = view.moves;
        tap(0); assertEquals(moves, view.moves);
        view.saveForPause();
        assertFalse(context.getSharedPreferences("m", 0).getBoolean("game_active", true));
        view.pendingWinAt = GameView.now() - 1;
        render("win-" + players + "-" + calm, 480, 900);
        assertEquals(2, view.screen); assertEquals(0, view.pendingWinAt);
        assertTrue(view.celebration.active);
        long started = view.celebration.startedAt;
        render(null, 480, 900);
        assertEquals(started, view.celebration.startedAt);
        org.robolectric.shadows.ShadowSystemClock.advanceBy(java.time.Duration.ofMillis(1000));
        render("mascot-joy-" + players + "-" + calm, 480, 900);
        view.start();
        assertFalse(view.celebration.active);
        assertEquals(0, view.pendingWinAt); assertEquals(0, view.found); assertEquals(1, view.screen);
    }

    private void ready() { view.flipIndex = -1; view.closeA = -1; view.closeB = -1; view.hideAt = 0; }

    private float[] position(int index) {
        for (int y = 115; y < view.H - 15; y += 3) {
            for (int x = 14; x < view.W - 14; x += 3) {
                if (view.hitCard(x, y) == index) return new float[]{x, y};
            }
        }
        return null;
    }

    private void tap(int index) {
        float[] xy = position(index);
        assertNotNull("Touch target for card " + index, xy);
        MotionEvent event = MotionEvent.obtain(GameView.now(), GameView.now(), MotionEvent.ACTION_UP, xy[0], xy[1], 0);
        view.onTouchEvent(event); event.recycle();
    }

    private void render(String name, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        view.layout(0, 0, width, height);
        view.onDraw(new Canvas(bitmap));
        if(view.screen==-1){view.adventureMap.scenery.awaitReady();org.robolectric.shadows.ShadowSystemClock.advanceBy(java.time.Duration.ofMillis(280));view.onDraw(new Canvas(bitmap));}
        if (name != null) {
            File directory = new File(System.getProperty("memomondes.captureDir"));
            directory.mkdirs();
            try (FileOutputStream output = new FileOutputStream(new File(directory, name + ".png"))) {
                assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, output));
            } catch (Exception failure) { throw new AssertionError(failure); }
        }
        bitmap.recycle();
    }
}
