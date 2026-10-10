package fr.memomimi.app;

import android.content.Context;
import android.graphics.*;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

/** Illustrated world tiles, decoded on demand; at most three RGB565 tiles retained. */
final class AdventureScenery {
    private BitmapRegionDecoder decoder;
    private final LinkedHashMap<Integer,Bitmap> tiles=new LinkedHashMap<>(4,.75f,true);
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    private final Rect source=new Rect();
    private final RectF target=new RectF();
    private boolean attempted;
    void load(Context context) {
        if(attempted)return;attempted=true;
        try(InputStream in=context.getAssets().open("adventure/worlds-atlas.jpg")) {
            decoder=BitmapRegionDecoder.newInstance(in,false);
        }catch(IOException ignored){/* Neutral background if asset is unavailable; no fake illustration. */}
    }
    Bitmap tile(int section) {
        Bitmap cached=tiles.get(section);if(cached!=null)return cached;
        if(decoder==null)return null;
        int col=section%4,row=section/4;
        source.set(col*decoder.getWidth()/4,row*decoder.getHeight()/5,(col+1)*decoder.getWidth()/4,(row+1)*decoder.getHeight()/5);
        BitmapFactory.Options options=new BitmapFactory.Options();options.inPreferredConfig=Bitmap.Config.RGB_565;
        options.inSampleSize=1;
        while(source.width()/options.inSampleSize>768)options.inSampleSize*=2;
        Bitmap bitmap=decoder.decodeRegion(source,options);
        if(bitmap!=null) {
            if(tiles.size()==3){Map.Entry<Integer,Bitmap> oldest=tiles.entrySet().iterator().next();oldest.getValue().recycle();tiles.remove(oldest.getKey());}
            tiles.put(section,bitmap);
        }
        return bitmap;
    }
    void draw(Canvas c,int section,float y,float width,float height) {
        target.set(0,y,width,y+height);Bitmap b=tile(section);
        if(b!=null){paint.setColor(Color.WHITE);c.drawBitmap(b,null,target,paint);}
        else {paint.setColor(0xffe4eef0);c.drawRect(target,paint);}
    }
    int retainedBytes(){int bytes=0;for(Bitmap b:tiles.values())bytes+=b.getAllocationByteCount();return bytes;}
    int retainedTiles(){return tiles.size();}
    void release(){for(Bitmap b:tiles.values())b.recycle();tiles.clear();if(decoder!=null)decoder.recycle();decoder=null;attempted=false;}
}
