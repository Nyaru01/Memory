package fr.memomimi.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityNodeInfo;
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
@Config(sdk={28,35})
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class V2AcceptanceTest {
    Context context; GameView view;
    @Before public void setup() {
        context=RuntimeEnvironment.getApplication(); context.getSharedPreferences("m",0).edit().clear().commit();
        create();
    }
    void create() { view=new GameView(context); view.sound=false; view.haptics=false; view.activityActive=false; view.d=1; }
    @After public void close() { view.releaseAudio(); }
    void render(String name,int width,int height) {
        view.layout(0,0,width,height); Bitmap bitmap=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        view.onDraw(new Canvas(bitmap));
        if(name!=null) {
            File file=new File(System.getProperty("memomondes.captureDir"),name+".png"); file.getParentFile().mkdirs();
            try(FileOutputStream out=new FileOutputStream(file)) { bitmap.compress(Bitmap.CompressFormat.PNG,100,out); }
            catch(Exception e) { throw new AssertionError(e); }
        }
        bitmap.recycle();
    }
    void click(RectF area) { event(MotionEvent.ACTION_UP,area.centerX(),area.centerY()); }
    void event(int action,float x,float y) {
        MotionEvent event=MotionEvent.obtain(GameView.now(),GameView.now(),action,x,y,0); view.onTouchEvent(event); event.recycle();
    }
    @Test public void allDifficultiesFitSmallTallAndDenseScreens() {
        int[][] sizes={{320,480},{360,640},{412,850},{600,900}};
        for(int[] size:sizes) for(float font:new float[]{1,1.3f}) for(int pairs:new int[]{6,8,10,12}) {
            context.getResources().getConfiguration().fontScale=font;
            view.pairs=pairs; view.start(); view.tutorialActive=false;
            render("v2-board-"+pairs+"-"+size[0]+"-font"+font,size[0],size[1]);
            assertEquals(pairs*2,view.cards.length); int[] counts=new int[pairs];
            for(int i=0;i<view.cards.length;i++) {
                counts[view.cards[i]]++; RectF rect=view.cardBounds[i];
                assertTrue(rect.left>=0 && rect.right<=size[0]);
                assertTrue(rect.top>=view.systemTop()+view.gameHeaderHeight() && rect.bottom<=size[1]-view.systemBottom()+.1f);
                assertTrue("48dp target "+pairs+" on "+size[0],Math.min(rect.width(),rect.height())>=48);
                assertEquals(i,view.hitCard(rect.centerX(),rect.centerY()));
                for(int j=0;j<i;j++) assertFalse(RectF.intersects(rect,view.cardBounds[j]));
            }
            for(int count:counts) assertEquals(2,count);
        }
        context.getResources().getConfiguration().fontScale=1;
    }
    @Test public void homeWorldSelectionDifficultyAndSwipeDoNotStartAccidentally() {
        view.screen=0; render("v2-home-small",320,480);
        for(int pairs:new int[]{6,8,10,12}) {
            click(pairs==6 ? view.easy : pairs==8 ? view.medium : pairs==10 ? view.hard : view.expert);
            assertEquals(pairs,view.pairs); assertEquals(0,view.screen);
        }
        click(view.chooseTheme); render("v2-worlds-small",320,480);
        assertEquals(4,view.screen); assertEquals(18,GameView.WORLD_COUNT); assertEquals(2,view.worldColumns());
        event(MotionEvent.ACTION_DOWN,150,420); event(MotionEvent.ACTION_MOVE,150,200); event(MotionEvent.ACTION_UP,150,200);
        assertEquals(4,view.screen); assertTrue(view.worldsScroll>0);
        while(view.scrollPage(true)) render(null,320,480);
        render("v2-worlds-last",320,480); click(view.themeTiles[17]);
        assertEquals(17,view.theme); assertEquals(0,view.screen);
        render(null,320,480); click(view.play); assertEquals(1,view.screen); assertEquals(24,view.cards.length);
    }
    @Test public void oldProgressRecordsAndPreferencesSurviveRecreation() {
        view.releaseAudio(); SharedPreferences prefs=context.getSharedPreferences("m",0);
        prefs.edit().putInt("data_version",5).putInt("theme",17).putInt("pairs",12).putInt("album_17",0x3f)
                .putInt("best_12",18).putInt("wins",9).putInt("completed_games",9)
                .putBoolean("audio_default_v7",true).putBoolean("sound",false).putBoolean("calm",true).commit();
        create(); assertEquals(17,view.theme); assertEquals(12,view.pairs); assertFalse(view.sound); assertTrue(view.calm);
        assertEquals(18,view.best()); assertEquals(6,view.albumCount()); assertEquals(9,view.progress.completedGames());
        view.screen=5; view.albumPage=17; view.albumTab=0; render("v2-album-existing",320,480);
        assertEquals(0,view.albumFreshMask);
        view.unlockProgress(); assertEquals(6,view.newUnlocks);
        view.albumFreshWorld=-1; render("v2-album-new",320,480); assertEquals(0xfc0,view.albumFreshMask);
        view.albumFreshWorld=-1; render(null,320,480); assertEquals(0,view.albumFreshMask);
        assertEquals(18,view.best()); assertEquals(12,view.albumCount());
    }
    @Test public void albumSelectsTheActualWorldAndAccessibleScrollWorks() {
        view.screen=5; view.albumTab=2; render("v2-album-worlds",360,640);
        click(view.themeTiles[1]); assertEquals(1,view.albumPage); assertEquals(0,view.albumTab);
        view.screen=4; render(null,320,480);
        assertTrue(view.accessible.performAction(-1,AccessibilityNodeInfo.ACTION_SCROLL_FORWARD,null));
        render(null,320,480);
        for(GameAccessibility.Target target:view.accessible.targets()) assertTrue(target.bounds.bottom>view.visibleTop(target.bounds));
        view.screen=0; view.homeScroll=0; render("v2-home",360,640);
        assertTrue(view.accessible.targets().stream().anyMatch(t->t.label.contains("sélectionné")));
    }
    @Test public void completeAndReplayEveryDifficultyKeepsComparableRecords() {
        view.calm=true;
        for(int pairs:new int[]{6,8,10,12}) {
            view.pairs=pairs; view.theme=pairs/2-3;
            for(int attempt=0;attempt<2;attempt++) {
                view.start(); view.tutorialActive=false;
                for(int i=0;i<pairs*2;i++) view.cards[i]=i/2;
                render(null,360,640);
                for(int i=0;i<pairs*2;i+=2) {
                    click(view.cardBounds[i]); click(view.cardBounds[i+1]);
                    assertEquals(i/2+1,view.found); assertEquals(i/2+1,view.moves);
                    if(i<pairs*2-2) assertEquals(0,view.pendingWinAt);
                }
                assertEquals(pairs,view.best()); assertEquals(attempt==0,view.newRecord);
                assertEquals(1,view.screen);
                view.pendingWinAt=GameView.now()-1;
                render("v2-win-"+pairs,360,640); assertEquals(2,view.screen);
                click(view.play); assertEquals(1,view.screen); assertEquals(0,view.found);
            }
        }
    }
    @Test public void worldBitmapCacheIsBoundedDuringAlbumBrowsing() {
        for(int world=0;world<GameView.WORLD_COUNT;world++) {
            assertNotNull(view.worldSheet(world)); int loaded=0;
            for(Bitmap sheet:view.sheets) if(sheet!=null) { loaded++; assertFalse(sheet.isRecycled()); }
            assertTrue(loaded<=2);
        }
    }
    @Test public void boardCoordinatesScaleWithAndroidDensity() {
        BoardLayout layout=new BoardLayout();
        for(float density:new float[]{.75f,1,1.5f,2,3}) for(int pairs:new int[]{6,8,10,12}) {
            layout.fit(pairs*2,320*density,294*density,density,170*density);
            assertTrue(layout.width/density>=48);
            assertTrue(layout.top+layout.rows*layout.height+(layout.rows-1)*layout.gap<=464*density+.1f);
        }
    }
    @Test public void abandonedParentHoldCannotShortenTheNextHold() {
        org.robolectric.android.controller.ActivityController<android.app.Activity> controller =
                org.robolectric.Robolectric.buildActivity(android.app.Activity.class).setup();
        controller.get().setContentView(view);
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
        view.screen=0; render(null,360,640);
        event(MotionEvent.ACTION_DOWN,view.parentBtn.centerX(),view.parentBtn.centerY());
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(java.time.Duration.ofMillis(2000));
        event(MotionEvent.ACTION_UP,view.parentBtn.centerX(),view.parentBtn.centerY());
        event(MotionEvent.ACTION_DOWN,view.parentBtn.centerX(),view.parentBtn.centerY());
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(java.time.Duration.ofMillis(600));
        assertEquals(0,view.screen);
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(java.time.Duration.ofMillis(1900));
        assertEquals(3,view.screen);
        controller.pause().stop().destroy();
    }
}
