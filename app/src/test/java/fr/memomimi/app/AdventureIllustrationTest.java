package fr.memomimi.app;

import android.graphics.*;
import org.junit.*;
import static org.junit.Assert.*;

/** Meaningful renderer regressions in addition to the full progression scenarios. */
public class AdventureIllustrationTest extends AdventureTest {
    @Test public void posesStayOpaqueAndNeverDoubleTheFace() {
        Bitmap sheet=Bitmap.createBitmap(16,8,Bitmap.Config.ARGB_8888);sheet.eraseColor(Color.RED);new Canvas(sheet).drawColor(Color.RED);Paint blue=new Paint();blue.setColor(Color.BLUE);new Canvas(sheet).drawRect(8,0,16,8,blue);
        Bitmap output=Bitmap.createBitmap(8,8,Bitmap.Config.ARGB_8888);
        Paint paint=new Paint();SpritePose.draw(new Canvas(output),sheet,new Rect(0,0,8,8),new Rect(8,0,16,8),new RectF(0,0,8,8),.5f,paint);
        assertEquals(255,Color.alpha(output.getPixel(4,4)));assertEquals(Color.BLUE,output.getPixel(4,4));
        assertEquals(255,paint.getAlpha());assertNull(paint.getXfermode());sheet.recycle();output.recycle();
    }
    @Test public void organicJoinHasExactEdgesAndNoHardHorizontalCut() {
        Bitmap before=Bitmap.createBitmap(64,64,Bitmap.Config.RGB_565),after=Bitmap.createBitmap(64,64,Bitmap.Config.RGB_565);
        before.eraseColor(Color.RED);after.eraseColor(Color.BLUE);
        Bitmap seam=AdventureSeam.bake(before,after,360,400,1);
        assertNotNull(seam);assertEquals(Color.RED,seam.getPixel(180,0));assertEquals(Color.BLUE,seam.getPixel(180,seam.getHeight()-1));
        int middle=seam.getPixel(180,seam.getHeight()/2);assertTrue(Color.red(middle)>20 && Color.blue(middle)>20);
        assertNotEquals(seam.getPixel(30,seam.getHeight()/2),seam.getPixel(150,seam.getHeight()/2));
        assertEquals(Color.RED,before.getPixel(32,32));assertEquals(Color.BLUE,after.getPixel(32,32));
        before.recycle();after.recycle();seam.recycle();
    }
    @Test public void routeLandmarksStayReachableAndOpeningFocusesNextPlayableLevel() {
        for(float density:new float[]{1,2,3})for(int dp:new int[]{320,360,412}){
            float previous=-1;
            for(int level=1;level<=40;level++){
                float x=AdventureRoute.x(level,dp*density,density),y=AdventureRoute.y(level,dp*density,density);
                assertTrue(x>=32*density && x<=dp*density-32*density);assertTrue(y>previous);previous=y;
                assertFalse(AdventureRoute.landmark(level).isEmpty());
            }
        }
        context.getSharedPreferences("m",0).edit().putInt("adventure_completed",17).commit();view.openAdventure();render("adventure-polish-level18",360,640);
        assertEquals(18,view.adventureMap.motion.from);assertFalse(view.adventureMap.motion.travelling(GameView.now()));
        assertTrue(view.adventureMap.nodes[17].centerY()>view.adventureMap.top && view.adventureMap.nodes[17].centerY()<view.adventureMap.bottom);
        assertEquals(60,view.adventureMap.top-view.systemTop(),0);assertEquals(48,view.adventureMap.continueButton.height(),0);
        assertTrue(view.adventureMap.scenery.seamBytes()>0);assertTrue(view.adventureMap.scenery.seamBytes()<=AdventureScenery.MAX_SEAM_BYTES);
        view.adventureMap.scrollBy(900);render(null,360,640);assertEquals(17,view.adventure.completed());
    }
    @Test public void nativeResolutionCacheLoadsOnceAndSurvivesReleaseDuringLoading() {
        view.d=3;view.openAdventure();render("adventure-hd-1080",1080,2400);
        assertEquals(android.view.View.LAYER_TYPE_NONE,view.getLayerType());
        assertTrue(view.adventureMap.scenery.tile(0).getWidth()>=900);
        assertTrue(view.adventureMap.scenery.retainedBytes()<=AdventureScenery.MAX_BYTES);
        int count=view.adventureMap.scenery.decodeCount();
        for(int i=0;i<30;i++)render(null,1080,2400);
        assertEquals(count,view.adventureMap.scenery.decodeCount());
        view.adventureMap.focus(33,1944,3);Bitmap pending=Bitmap.createBitmap(1080,2400,Bitmap.Config.ARGB_8888);view.onDraw(new Canvas(pending));pending.recycle();
        view.adventureMap.release();assertEquals(0,view.adventureMap.scenery.retainedTiles());
        view.openAdventure();render(null,1080,2400);
        assertNotNull(view.adventureMap.scenery.tile(0));
        assertTrue(view.adventureMap.decor.bytes()>0);
        view.d=2;view.adventureMap.focus(17,1296,2);render(null,720,1600);
        assertEquals(720,view.adventureMap.scenery.tile(8).getWidth());
        assertTrue(view.adventureMap.scenery.retainedBytes()<=4*1024*1024);
        view.startAdventure(1);render(null,1080,2400);
        assertEquals(android.view.View.LAYER_TYPE_SOFTWARE,view.getLayerType());
    }
    @Test public void allTwentyLandscapeAssetsHaveResolvedPhoneScaleDetail() throws Exception {
        for(int section=0;section<20;section++){
            BitmapFactory.Options bounds=new BitmapFactory.Options();bounds.inJustDecodeBounds=true;
            try(java.io.InputStream input=context.getAssets().open(String.format(java.util.Locale.ROOT,"adventure/landscapes/world-%02d.jpg",section))){BitmapFactory.decodeStream(input,null,bounds);}
            assertTrue("HD scene "+section,bounds.outWidth>=900 && bounds.outHeight>=900);
        }
    }
    @Test public void firstLevelOpensWithAnimationsEnabledAndNoPreviousVictory() {
        view.calm=false;
        view.systemReducedMotion=false;
        view.openAdventure();
        render(null,360,640);
        assertEquals(1,view.adventureMap.motion.from);
        assertFalse(view.adventureMap.motion.travelling(GameView.now()));
        assertEquals(0,view.adventure.completed());
        assertEquals(7,view.screen);
    }
    @Test public void illustratedTilesRemainBoundedDuringWholeRouteAndReopen() {
        view.openAdventure();
        for(int level=1;level<=40;level++) {
            view.adventureMap.focus(level,488,1);render(level==13?"adventure-illustrated-ocean":level==21?"adventure-illustrated-space":null,360,640);
            assertTrue(view.adventureMap.scenery.retainedTiles()>0);
            assertTrue(view.adventureMap.scenery.retainedTiles()<=3);
            assertTrue(view.adventureMap.scenery.retainedBytes()<=6*1024*1024);
        }
        assertEquals(0,view.adventure.completed());
        view.adventureMap.release();assertEquals(0,view.adventureMap.scenery.retainedBytes());
        view.openAdventure();render("adventure-illustrated-start",360,640);assertTrue(view.adventureMap.scenery.retainedTiles()>0);assertTrue(view.adventureMap.decor.bytes()>0);
    }
    @Test public void travelOccursOnceAndCalmModeSnapsToCurrent() {
        AdventureMotion m=new AdventureMotion();m.completed(1);m.open(2,1000,false);
        assertEquals(1,m.from);assertTrue(m.travelling(1000));assertEquals(0,m.progress(1000),0);
        assertEquals(0,m.progress(1000+AdventureMotion.DEPART_MS),0);
        assertEquals(.5,m.progress(1000+AdventureMotion.DEPART_MS+AdventureMotion.MOVE_MS/2),.001);
        assertEquals(1,m.progress(1000+AdventureMotion.TRAVEL_MS),0);assertFalse(m.travelling(1000+AdventureMotion.TRAVEL_MS));
        assertEquals(0,m.arrival(1000+AdventureMotion.TRAVEL_MS));
        assertEquals(-1,m.arrival(1000+AdventureMotion.TRAVEL_MS+AdventureMotion.CELEBRATE_MS));
        m.open(2,4000,false);assertFalse(m.travelling(4000));
        m.completed(2);m.open(3,5000,true);assertEquals(3,m.from);assertFalse(m.travelling(5000));
        m.completed(4);m.open(6,6000,false);assertEquals(6,m.from);
    }
    @Test public void focusButtonAndMascotReactionDoNotLaunchOrUnlock() {
        view.openAdventure();render(null,360,640);
        click(view.adventureMap.lumi);assertEquals(7,view.screen);assertTrue(view.adventureMap.motion.reactionAt>=0);
        view.adventureMap.scrollBy(2000);render(null,360,640);click(view.adventureMap.focusButton);render(null,360,640);
        assertTrue(view.adventureMap.nodes[0].centerY()>=view.adventureMap.top);
        assertTrue(view.adventureMap.nodes[0].centerY()<=view.adventureMap.bottom);assertEquals(0,view.adventure.completed());
        assertTrue(view.accessible.targets().stream().anyMatch(t->t.label.equals("Revenir au niveau actuel")));
    }
}
