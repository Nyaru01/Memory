package fr.memomimi.app;

import android.graphics.*;
import org.junit.*;
import static org.junit.Assert.*;

/** Meaningful renderer regressions in addition to the full progression scenarios. */
public class AdventureIllustrationTest extends AdventureTest {
    @Test public void cachedWalkingPathsCoverAllLevelsWithoutOvershootAndShareTheirEndpoints(){
        AdventureJourney route=new AdventureJourney();
        for(int width:new int[]{320,360,412}){
            route.layout(width,1);
            for(int level=1;level<40;level++){
                route.sample(level,0);assertEquals(AdventureRoute.x(level,width,1),route.position[0],.05);
                assertEquals(AdventureRoute.y(level,width,1),route.position[1],.05);
                float previous=route.position[1];
                for(int i=1;i<=60;i++){route.sample(level,i/60f);assertTrue(route.position[0]>=31&&route.position[0]<=width-31);assertTrue(route.position[1]>=previous-.01);previous=route.position[1];}
                assertEquals(AdventureRoute.x(level+1,width,1),route.position[0],.05);assertEquals(AdventureRoute.y(level+1,width,1),route.position[1],.05);
            }
            Path identity=route.paths[0];route.layout(width,1);assertSame(identity,route.paths[0]);
        }
    }
    @Test public void globalProgressSceneAndPlayablePositionStayIndependentAndSignsCoverAllChapters(){
        context.getSharedPreferences("m",0).edit().putInt("adventure_completed",17).commit();view.openAdventure();
        for(int section=0;section<20;section++){
            view.adventureMap.focus(section*2+1,488,1);render("world-917-"+section,360,640);
            Bitmap guide=Bitmap.createBitmap(360,640,Bitmap.Config.ARGB_8888);Canvas guideCanvas=new Canvas(guide);view.onDraw(guideCanvas);
            guideCanvas.save();guideCanvas.translate(0,view.adventureMap.top-view.adventureMap.scroll);
            Paint routeInk=new Paint(Paint.ANTI_ALIAS_FLAG);routeInk.setStyle(Paint.Style.STROKE);routeInk.setStrokeWidth(3);routeInk.setColor(0xccf70067);
            guideCanvas.drawPath(view.adventureMap.journey.paths[section*2],routeInk);guideCanvas.restore();
            try(java.io.FileOutputStream out=new java.io.FileOutputStream(new java.io.File(System.getProperty("memomondes.captureDir"),"route-917-"+section+".png"))){guide.compress(Bitmap.CompressFormat.PNG,100,out);}catch(Exception e){throw new AssertionError(e);}guide.recycle();
            assertEquals(17,view.adventure.completed());assertEquals(18,view.adventure.current());
            assertTrue(view.adventureMap.visibleSection>=Math.max(0,section-1)&&view.adventureMap.visibleSection<=section+1);
            assertTrue(view.adventureMap.scenery.retainedTiles()<=AdventureScenery.MAX_TILES);
            assertTrue(view.adventureMap.scenery.retainedBytes()<=AdventureScenery.MAX_BYTES);
        }
        assertEquals(0,AdventureChapters.visible(0,50,450,1));
        assertEquals(1,AdventureChapters.visible(350,50,550,1));
        assertEquals(19,AdventureChapters.visible(7600,50,550,1));
        click(view.adventureMap.focusButton);render(null,360,640);
        assertTrue(view.adventureMap.lumi.bottom>view.adventureMap.top&&view.adventureMap.lumi.top<view.adventureMap.bottom);
    }
    @Test public void leavingMapFreezesTimelineAndProgressSurvivesViewRecreation(){
        AdventureMap map=view.adventureMap;map.motion.completed(1);map.open(2,488,1,false,1000);
        float before=map.motion.progress(2000);map.motion.reactionAt=1800;map.hide(2000);
        map.pause(3000);map.resume(6000);map.open(2,488,1,false,9000);
        assertEquals(before,map.motion.progress(9000),.0001);assertEquals(8800,map.motion.reactionAt);assertEquals(1,map.motion.from);
        assertTrue(view.adventure.complete(1));view.releaseAudio();view=new GameView(context);view.d=1;view.activityActive=false;view.sound=false;view.calm=true;
        view.openAdventure();render(null,360,640);assertEquals(2,view.adventure.current());assertEquals(2,view.adventureMap.motion.to);
        assertEquals(view.adventureMap.nodes[1].centerX(),view.adventureMap.lumi.centerX(),.01);
    }
    @Test public void allNineteenComposedPassagesKeepEdgesIntactAndBounded(){
        AdventurePassage passage=new AdventurePassage();passage.load(context);assertTrue(passage.bytes()>0&&passage.bytes()<=2*1024*1024);
        Bitmap before=Bitmap.createBitmap(64,64,Bitmap.Config.RGB_565),after=Bitmap.createBitmap(64,64,Bitmap.Config.RGB_565);
        before.eraseColor(Color.RED);after.eraseColor(Color.BLUE);
        for(int boundary=1;boundary<20;boundary++){
            Bitmap strip=AdventureSeam.bake(before,after,360,400,boundary,passage);
            assertEquals(Color.RED,strip.getPixel(180,0));assertEquals(Color.BLUE,strip.getPixel(180,strip.getHeight()-1));
            assertTrue(strip.getAllocationByteCount()*3<=AdventureScenery.MAX_SEAM_BYTES);strip.recycle();
        }
        assertFalse(AdventurePassage.portal(1));assertTrue(AdventurePassage.portal(5));assertTrue(AdventurePassage.portal(10));
        passage.release();before.recycle();after.recycle();
    }
    @Test public void pauseFreezesTravelAndTouchWithoutChangingProgress(){
        AdventureMap map=view.adventureMap;map.motion.completed(1);map.open(2,488,1,false,1000);
        map.motion.reactionAt=1500;float progress=map.motion.progress(2000);
        map.pause(2000);map.pause(2500);map.resume(9000);
        assertEquals(progress,map.motion.progress(9000),.0001);assertEquals(8500,map.motion.reactionAt);
        assertTrue(map.motion.travelling(9000));assertEquals(0,view.adventure.completed());
        assertTrue(view.victoryMascot.hasMapAtlas());
    }
    @Test public void landscapeFillsBottomAtStartAndEndWithSoftHeader() {
        view.openAdventure();render(null,360,640);
        for(int level:new int[]{1,40}){
            view.adventureMap.focus(level,view.adventureMap.bottom-view.adventureMap.top,1);render(null,360,640);
            Bitmap image=Bitmap.createBitmap(360,640,Bitmap.Config.ARGB_8888);view.onDraw(new Canvas(image));
            assertNotEquals(0xffe3f0e7,image.getPixel(2,638));
            int edge=image.getPixel(2,(int)view.adventureMap.top+1);
            assertNotEquals(0xffe3f0e7,edge);assertNotEquals(0xffe3f0e7,image.getPixel(180,1));
            if(level==40)assertTrue(view.adventureMap.nodes[39].centerY()<view.adventureMap.continueButton.top);
            image.recycle();
        }
    }
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
        assertTrue(view.adventureMap.top-view.systemTop()<=60);assertTrue(view.adventureMap.continueButton.height()>=48);
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
        assertTrue(view.adventureMap.scenery.retainedBytes()<=6*1024*1024);
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
            assertTrue(view.adventureMap.scenery.retainedTiles()<=AdventureScenery.MAX_TILES);
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
        assertTrue(view.accessible.targets().stream().anyMatch(t->t.label.startsWith("Recentrer sur Lumi, prochain niveau jouable 1")));
    }
}
