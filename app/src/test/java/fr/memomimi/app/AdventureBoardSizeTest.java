package fr.memomimi.app;

import android.graphics.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import java.io.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(sdk={28,35}) @GraphicsMode(GraphicsMode.Mode.NATIVE)
public class AdventureBoardSizeTest {
    @Test public void everyAdventureDeckKeepsItsPairsAndUsesSharedHitBounds() throws Exception {
        android.content.Context context=RuntimeEnvironment.getApplication();context.getSharedPreferences("m",0).edit().clear().putInt("adventure_completed",6).putInt("album_18",63).putInt("best_12",19).commit();
        GameView v=new GameView(context);v.sound=false;v.haptics=false;v.activityActive=false;v.calm=true;v.d=1;
        try{
            for(int[] stage:new int[][]{{1,6},{7,8},{17,10},{29,12},{40,12}}){
            context.getSharedPreferences("m",0).edit().putInt("adventure_completed",stage[0]-1).commit();v.clearSavedGame();
            assertTrue(v.startAdventure(stage[0]));v.tutorialActive=false;int[] deck=v.cards.clone();
            for(int[] size:new int[][]{{360,780},{320,480},{412,850}}){
                v.layout(0,0,size[0],size[1]);Bitmap bitmap=Bitmap.createBitmap(size[0],size[1],Bitmap.Config.ARGB_8888);v.onDraw(new Canvas(bitmap));
                try(FileOutputStream out=new FileOutputStream(new File(System.getProperty("memomondes.captureDir"),"level"+stage[0]+"-920-"+size[0]+".png"))){bitmap.compress(Bitmap.CompressFormat.PNG,100,out);}bitmap.recycle();
                assertEquals(stage[1],v.pairs);assertEquals(stage[1]*2,v.cards.length);assertArrayEquals(deck,v.cards);assertEquals(stage[0]-1,v.adventure.completed());assertEquals(63,v.albumMask(18));assertEquals(19,context.getSharedPreferences("m",0).getInt("best_12",0));
                for(int i=0;i<v.cards.length;i++){RectF bounds=v.cardBounds[i];assertTrue(bounds.width()>=48);assertTrue(bounds.top>=v.systemTop()+v.gameHeaderHeight());assertTrue(bounds.bottom<=v.H-v.systemBottom()+.1f);assertEquals(i,v.hitCard(bounds.centerX(),bounds.centerY()));}
                if(stage[1]==8){assertEquals(4,v.boardLayout.columns);if(size[0]==360){assertTrue(v.boardLayout.width>=79.5f);assertTrue(v.boardLayout.height>=103);}}
            }
            }
        }finally{v.releaseAudio();}
    }
}
