package fr.memomimi.app;

import android.content.Context;
import android.graphics.*;
import android.view.MotionEvent;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import org.robolectric.shadows.ShadowSystemClock;
import java.time.Duration;
import java.io.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(sdk={28,35}) @GraphicsMode(GraphicsMode.Mode.NATIVE)
public class WorldSelectorTest {
    Context context;GameView view;float originalFont;
    @Before public void setup(){context=RuntimeEnvironment.getApplication();context.getSharedPreferences("m",0).edit().clear().putInt("adventure_completed",17).putInt("album_18",63).putInt("best_12",19).commit();originalFont=context.getResources().getConfiguration().fontScale;view=new GameView(context);view.d=1;view.sound=false;view.haptics=false;view.activityActive=false;view.screen=4;view.calm=true;}
    @After public void close(){view.worldSelector.release();view.releaseAudio();font(originalFont);}
    void font(float value){android.content.res.Configuration config=new android.content.res.Configuration(context.getResources().getConfiguration());config.fontScale=value;context.getResources().updateConfiguration(config,context.getResources().getDisplayMetrics());}
    void render(int w,int h,String name){view.layout(0,0,w,h);Bitmap b=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);view.onDraw(new Canvas(b));if(name!=null)try(FileOutputStream out=new FileOutputStream(new File(System.getProperty("memomondes.captureDir"),name+".png"))){b.compress(Bitmap.CompressFormat.PNG,100,out);}catch(Exception error){throw new AssertionError(error);}b.recycle();}
    void event(int action,float x,float y){MotionEvent e=MotionEvent.obtain(GameView.now(),GameView.now(),action,x,y,0);view.onTouchEvent(e);e.recycle();}
    void click(RectF rect){event(MotionEvent.ACTION_UP,rect.centerX(),rect.centerY());}
    @Test public void realCatalogueFitsPhoneTabletAndLargeText() throws Exception {
        for(float scale:new float[]{1,1.35f}){font(scale);for(int[] size:new int[][]{{320,480},{412,850},{800,1000}}){
            for(int family=0;family<5;family++){java.util.Arrays.fill(view.familyOpen,false);view.familyOpen[family]=true;view.worldsScroll=0;render(size[0],size[1],null);view.worldSelector.awaitReady();render(size[0],size[1],"catalogue-918-"+size[0]+"-"+scale+"-"+family);
                assertEquals(android.view.View.LAYER_TYPE_NONE,view.getLayerType());
                assertEquals(size[0]<600?2:scale>1.15f?3:3,view.worldSelector.columns);
                int[] worlds=WorldCatalog.WORLDS[family];for(int world:worlds){RectF tile=view.themeTiles[world];assertFalse(tile.isEmpty());assertTrue(tile.width()>=130);assertTrue(tile.height()>=160);assertTrue(tile.left>=16&&tile.right<=size[0]-16+.1f);assertNotNull(view.worldSelector.image(world));}
                for(int i=0;i<worlds.length;i++)for(int j=i+1;j<worlds.length;j++)assertFalse(RectF.intersects(view.themeTiles[worlds[i]],view.themeTiles[worlds[j]]));
                assertTrue(view.worldSelector.bytes()<=WorldSelector.MAX_BYTES);assertEquals(17,view.adventure.completed());assertEquals(63,view.albumMask(18));
            }
        }}
    }
    @Test public void animationAndResizeCannotSelectAWorldUnderMovingFinger(){
        view.calm=false;render(360,640,null);RectF header=new RectF(view.familyTiles[0]);event(MotionEvent.ACTION_DOWN,header.centerX(),header.centerY());event(MotionEvent.ACTION_UP,header.centerX(),header.centerY());render(360,640,null);
        assertTrue(view.worldSelector.moving(view));assertTrue(view.themeTiles[0].isEmpty());click(header);assertFalse(view.familyOpen[0]);
        ShadowSystemClock.advanceBy(Duration.ofMillis(250));render(360,640,null);assertFalse(view.worldSelector.moving(view));click(view.familyTiles[0]);ShadowSystemClock.advanceBy(Duration.ofMillis(250));render(360,640,null);
        RectF tile=new RectF(view.themeTiles[3]);event(MotionEvent.ACTION_DOWN,tile.centerX(),tile.centerY());render(412,850,null);event(MotionEvent.ACTION_UP,tile.centerX(),tile.centerY());assertEquals(4,view.screen);assertEquals(0,view.theme);
        event(MotionEvent.ACTION_DOWN,200,430);event(MotionEvent.ACTION_MOVE,200,170);render(412,850,null);event(MotionEvent.ACTION_UP,200,170);assertEquals(4,view.screen);assertTrue(view.worldsScroll>0);assertEquals(0,view.theme);
    }
    @Test public void selectionReturnsDirectlyToPairsAndPreservesAdventureAndCollection(){
        java.util.Arrays.fill(view.familyOpen,false);view.familyOpen[4]=true;view.theme=18;render(360,640,null);
        click(view.familyTiles[4]);render(360,640,null);assertTrue(view.themeTiles[18].isEmpty());click(view.familyTiles[4]);render(360,640,null);assertFalse(view.themeTiles[18].isEmpty());
        while(view.themeTiles[18].centerY()>view.H-view.systemBottom()){view.scrollPage(true);render(360,640,null);}click(view.themeTiles[18]);assertEquals(8,view.screen);assertEquals(18,view.theme);assertEquals(18,context.getSharedPreferences("m",0).getInt("free_theme",-1));
        render(360,640,null);click(view.medium);assertEquals(8,view.pairs);click(view.play);assertEquals(1,view.screen);assertEquals(0,view.adventureLevel);assertEquals(17,view.adventure.completed());assertEquals(63,view.albumMask(18));assertEquals(19,context.getSharedPreferences("m",0).getInt("best_12",0));
    }
}
