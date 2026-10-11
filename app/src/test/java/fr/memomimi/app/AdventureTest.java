package fr.memomimi.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.MotionEvent;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;
import java.io.File;
import java.io.FileOutputStream;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(sdk={28,35}) @GraphicsMode(GraphicsMode.Mode.NATIVE)
public class AdventureTest {
    Context context; GameView view;
    @Before public void setup() {
        context=RuntimeEnvironment.getApplication();context.getSharedPreferences("m",0).edit().clear().commit();
        view=new GameView(context);view.d=1;view.sound=false;view.haptics=false;view.calm=true;view.activityActive=false;
    }
    @After public void close() {view.releaseAudio();}
    void render(String name,int w,int h) {
        view.layout(0,0,w,h);Bitmap b=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);view.onDraw(new Canvas(b));
        if(view.screen==7){view.adventureMap.scenery.awaitReady();view.onDraw(new Canvas(b));}
        if(name!=null) try(FileOutputStream out=new FileOutputStream(new File(System.getProperty("memomondes.captureDir"),name+".png"))) {b.compress(Bitmap.CompressFormat.PNG,100,out);} catch(Exception e){throw new AssertionError(e);}
        b.recycle();
    }
    void click(RectF r) { MotionEvent e=MotionEvent.obtain(GameView.now(),GameView.now(),MotionEvent.ACTION_UP,r.centerX(),r.centerY(),0);view.onTouchEvent(e);e.recycle(); }
    void finish() {
        view.tutorialActive=false;for(int i=0;i<view.cards.length;i++)view.cards[i]=i/2;
        render(null,360,640);
        for(int i=0;i<view.cards.length;i+=2){click(view.cardBounds[i]);click(view.cardBounds[i+1]);}
        assertEquals(view.pairs,view.found);view.pendingWinAt=GameView.now()-1;render(null,360,640);assertEquals(2,view.screen);
    }
    @Test public void onlyNextLevelUnlocksAndOldWinsDoNotSkipLevels() {
        assertEquals(0,view.adventure.completed());assertFalse(view.startAdventure(2));assertFalse(view.startAdventure(0));
        for(int level=1;level<=40;level++) {
            assertTrue(view.startAdventure(level));assertEquals(AdventureStore.world(level),view.theme);assertEquals(AdventureStore.pairs(level),view.pairs);
            finish();assertEquals(level,view.adventure.completed());assertFalse(view.adventure.complete(level));
            if(level<39)assertFalse(view.startAdventure(level+2));
        }
        assertFalse(view.startAdventure(41));assertTrue(view.startAdventure(1));finish();assertEquals(40,view.adventure.completed());
    }
    @Test public void freePlayAndUnfinishedBoardsCannotUnlockAdventure() {
        view.pairs=6;view.start();finish();assertEquals(0,view.adventure.completed());
        assertTrue(view.startAdventure(1));view.screen=2;render(null,360,640);assertEquals(0,view.adventure.completed());
        view.unlockProgress();assertEquals(0,view.adventure.completed());
        view.start();assertEquals(0,view.adventureLevel);finish();assertEquals(0,view.adventure.completed());
    }
    @Test public void resumePreservesLevelAndOldAlbumRecords() {
        context.getSharedPreferences("m",0).edit().putInt("album_17",63).putInt("best_12",19).commit();
        view.startAdventure(1);view.tutorialActive=false;render(null,360,640);click(view.cardBounds[0]);view.saveForPause();
        view.releaseAudio();view=new GameView(context);view.sound=false;view.calm=true;view.d=1;view.loadGame();
        assertEquals(1,view.adventureLevel);assertEquals(1,view.screen);assertEquals(6,view.pairs);
        assertEquals(63,view.albumMask(17));assertEquals(19,context.getSharedPreferences("m",0).getInt("best_12",0));
        assertEquals(0,view.adventure.completed());
        context.getSharedPreferences("m",0).edit().putInt("saved_adventure_level",2).commit();view.loadGame();assertEquals(0,view.screen);
    }
    @Test public void mapHasReachableTargetsAndNoInvisibleLockedLaunch() {
        for(int[] size:new int[][]{{320,480},{360,640},{412,850}}) {
            view.layout(0,0,size[0],size[1]);view.openAdventure();render("adventure-map-"+size[0],size[0],size[1]);
            assertTrue(view.adventureMap.nodes[0].height()>=48);assertEquals(1,view.adventureMap.hit(view.adventureMap.nodes[0].centerX(),view.adventureMap.nodes[0].centerY()));
            click(view.adventureMap.nodes[1]);assertEquals(7,view.screen);assertEquals(0,view.adventure.completed());
            assertTrue(view.accessible.targets().stream().anyMatch(t->t.label.contains("Niveau 2") && !t.enabled));
            while(view.scrollPage(true))render(null,size[0],size[1]);render("adventure-map-end-"+size[0],size[0],size[1]);
            assertTrue(view.adventureMap.nodes[39].top<view.adventureMap.bottom);click(view.adventureMap.nodes[39]);assertEquals(7,view.screen);
            click(view.adventureMap.continueButton);assertEquals(1,view.screen);assertEquals(1,view.adventureLevel);
        }
    }
    @Test public void victoryNextButtonStartsExactlyNextLevelAndMapReturns() {
        view.startAdventure(1);finish();assertFalse(view.nextLevelButton.isEmpty());click(view.play);assertEquals(7,view.screen);render(null,360,640);click(view.adventureMap.continueButton);assertEquals(2,view.adventureLevel);assertEquals(1,view.screen);
        finish();click(view.homeBtn);assertEquals(7,view.screen);assertEquals(2,view.adventure.completed());
        render(null,360,640);
        click(view.adventureMap.back);assertEquals(0,view.screen);assertEquals(0,view.adventureLevel);
    }
    @Test public void illustratedHomeHasTwoEntrancesAndFreeConfigurationReturnsThroughWorlds() {
        for(int[] size:new int[][]{{320,480},{360,640},{412,850}}){
            view.screen=0;view.homeScroll=0;render(null,size[0],size[1]);
            view.adventureMap.scenery.awaitReady();render("home-910-"+size[0],size[0],size[1]);
            assertTrue(view.homeScreen.bytes()>0);assertTrue(view.homeScreen.bytes()<=12*1024*1024);
            assertTrue(view.easy.isEmpty());assertTrue(view.chooseTheme.isEmpty());
            assertTrue(view.adventureButton.height()>=48);assertTrue(view.freePlayButton.height()>=48);
            click(view.adventureButton);assertEquals(7,view.screen);view.handleBack();assertEquals(0,view.screen);
            render(null,size[0],size[1]);click(view.freePlayButton);assertEquals(8,view.screen);
            render("free-910-"+size[0],size[0],size[1]);click(view.chooseTheme);assertEquals(4,view.screen);
            view.handleBack();assertEquals(8,view.screen);view.handleBack();assertEquals(0,view.screen);
        }
    }
    @Test public void homeContinuesFirstSavedLevelWithoutReplacingItsDeckOrProgress() {
        assertFalse(view.adventureStarted());
        assertTrue(view.startAdventure(1));
        for(int i=0;i<view.cards.length;i++)view.cards[i]=i/2;
        view.matched[0]=view.matched[1]=true;view.found=1;view.moves=2;view.errors=1;
        view.saveGame();int[] deck=view.cards.clone();
        view.screen=0;view.showResumePrompt=false;
        render("home-914-started",360,740);
        assertTrue(view.adventureStarted());assertEquals(0,view.adventure.completed());
        click(view.adventureButton);assertEquals(7,view.screen);
        assertTrue(view.startAdventure(view.adventure.current()));
        assertArrayEquals(deck,view.cards);assertEquals(1,view.found);
        assertEquals(2,view.moves);assertEquals(1,view.errors);assertTrue(view.matched[0]);
    }
    @Test public void homeReflectsLaterChapterAndCompletedJourney() {
        for(int completed:new int[]{17,40}) {
            context.getSharedPreferences("m",0).edit().putInt("adventure_completed",completed).putBoolean("game_active",false).commit();
            view.screen=0;view.showResumePrompt=false;view.homeScroll=0;
            render(null,360,740);view.adventureMap.scenery.awaitReady();
            render("home-914-progress-"+completed,360,740);
            assertTrue(view.adventureStarted());click(view.adventureButton);
            assertEquals(7,view.screen);assertEquals(Math.min(40,completed+1),view.adventure.current());
        }
    }
    @Test public void freePreferencesSurviveAdventureAndBothModesRewardAlbumIndependently() {
        view.openFreePlay();view.theme=18;view.choosePairs(12);view.remember();
        view.startAdventure(1);finish();assertEquals(1,view.adventure.completed());assertEquals(63,view.albumMask(0));
        view.openFreePlay();assertEquals(18,view.theme);assertEquals(12,view.pairs);assertEquals(0,view.adventureLevel);
        render(null,360,640);click(view.play);finish();assertEquals(1,view.adventure.completed());assertEquals(4095,view.albumMask(18));
        render(null,360,640);click(view.homeBtn);assertEquals(8,view.screen);
        view.releaseAudio();view=new GameView(context);view.d=1;view.sound=false;view.calm=true;view.activityActive=false;
        view.openFreePlay();assertEquals(18,view.theme);assertEquals(12,view.pairs);assertEquals(1,view.adventure.completed());
        assertEquals(63,view.albumMask(0));assertEquals(4095,view.albumMask(18));
    }
}
