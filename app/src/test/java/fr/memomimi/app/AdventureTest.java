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
        view.startAdventure(1);finish();assertFalse(view.nextLevelButton.isEmpty());click(view.play);assertEquals(2,view.adventureLevel);assertEquals(1,view.screen);
        finish();click(view.homeBtn);assertEquals(7,view.screen);assertEquals(2,view.adventure.completed());
        render(null,360,640);
        click(view.adventureMap.back);assertEquals(0,view.screen);assertEquals(0,view.adventureLevel);
    }
}
