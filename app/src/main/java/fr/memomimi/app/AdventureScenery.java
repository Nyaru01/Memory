package fr.memomimi.app;

import android.content.Context;
import android.graphics.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/** Native-resolution landscape tiles decoded off the UI thread, with a bounded three-tile cache. */
final class AdventureScenery {
    static final int MAX_BYTES=16*1024*1024;
    private final LinkedHashMap<Integer,Bitmap> tiles=new LinkedHashMap<>(4,.75f,true);
    private final boolean[] failed=new boolean[20];
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    private final Rect source=new Rect();
    private final RectF target=new RectF();
    private Context context;
    private ExecutorService worker;
    private int generation,first,last,width=360;
    private boolean running;
    private Runnable repaint;
    private int decodes;

    synchronized void load(Context value){context=value.getApplicationContext();}
    synchronized ExecutorService executor(){
        if(worker==null)worker=Executors.newSingleThreadExecutor(r->{Thread t=new Thread(r,"MemoMondes-map-images");t.setDaemon(true);return t;});
        return worker;
    }
    synchronized void request(int start,int end,int pixels,Runnable changed){
        int newWidth=Math.max(256,Math.min(1280,pixels));
        if(width!=newWidth){generation++;tiles.clear();Arrays.fill(failed,false);width=newWidth;}
        first=Math.max(0,Math.min(19,start));last=Math.max(first,Math.min(first+2,Math.min(19,end)));repaint=changed;
        if(context==null||running||nextMissing()<0)return;
        running=true;ExecutorService owner=executor();owner.execute(()->fill(owner));
    }
    private int nextMissing(){
        for(int section=first;section<=last;section++)if(!tiles.containsKey(section)&&!failed[section])return section;
        if(last-first<2 && last<19 && !tiles.containsKey(last+1)&&!failed[last+1])return last+1;
        return -1;
    }
    private void fill(ExecutorService owner){
        while(true){
            int section,token,pixels;Context ctx;
            synchronized(this){if(owner!=worker)return;if(context==null){running=false;return;}section=nextMissing();if(section<0){running=false;return;}token=generation;pixels=width;ctx=context;}
            Bitmap bitmap=decode(ctx,section,pixels);
            Runnable notify;
            synchronized(this){
                if(owner!=worker){if(bitmap!=null)bitmap.recycle();return;}
                if(token!=generation){if(bitmap!=null)bitmap.recycle();if(context==null)return;continue;}
                decodes++;
                if(bitmap==null)failed[section]=true;
                else {
                    while(tiles.size()>=3 || retainedBytes()+bitmap.getAllocationByteCount()>MAX_BYTES){
                        Integer victim=null;
                        for(Integer key:tiles.keySet())if(key<first||key>last){victim=key;break;}
                        if(victim==null && !tiles.isEmpty())victim=tiles.keySet().iterator().next();
                        if(victim==null)break;
                        // Recorded GPU frames can still reference evicted images: let Android release them.
                        tiles.remove(victim);
                    }
                    tiles.put(section,bitmap);
                }
                notify=repaint;
            }
            if(notify!=null)notify.run();
        }
    }
    private static Bitmap decode(Context ctx,int section,int pixels){
        String file=String.format(Locale.ROOT,"adventure/landscapes/world-%02d.jpg",section);
        BitmapFactory.Options bounds=new BitmapFactory.Options();bounds.inJustDecodeBounds=true;
        try(InputStream in=ctx.getAssets().open(file)){BitmapFactory.decodeStream(in,null,bounds);}catch(IOException e){return null;}
        if(bounds.outWidth<=0||bounds.outHeight<=0)return null;
        BitmapFactory.Options options=new BitmapFactory.Options();options.inScaled=false;options.inPreferredConfig=Bitmap.Config.RGB_565;options.inSampleSize=1;
        while(bounds.outWidth/(options.inSampleSize*2)>=pixels)options.inSampleSize*=2;
        while((long)bounds.outWidth*bounds.outHeight*2/(options.inSampleSize*options.inSampleSize)>(MAX_BYTES-128*1024)/3)options.inSampleSize*=2;
        for(int attempt=0;attempt<2;attempt++){
            try(InputStream in=ctx.getAssets().open(file)){
                Bitmap decoded=BitmapFactory.decodeStream(in,null,options);
                if(decoded!=null && decoded.getWidth()>pixels*1.15f){
                    Bitmap scaled;
                    try{scaled=Bitmap.createScaledBitmap(decoded,pixels,Math.round(decoded.getHeight()*pixels/(float)decoded.getWidth()),true);}
                    catch(OutOfMemoryError exhausted){decoded.recycle();throw exhausted;}
                    decoded.recycle();decoded=scaled;
                }
                return decoded;
            }
            catch(IOException|IllegalArgumentException e){return null;}
            catch(OutOfMemoryError e){options.inSampleSize*=2;}
        }
        return null;
    }
    synchronized Bitmap tile(int section){return tiles.get(section);}
    void draw(Canvas c,int section,float y,float w,float h){
        target.set(0,y,w,y+h);Bitmap b=tile(section);
        if(b==null){paint.setColor(0xffe4eef0);c.drawRect(target,paint);return;}
        // Center crop preserves landscape proportions; never stretch the illustration.
        float ratio=w/h;
        if(b.getWidth()/(float)b.getHeight()>ratio){int crop=Math.round(b.getHeight()*ratio);source.set((b.getWidth()-crop)/2,0,(b.getWidth()+crop)/2,b.getHeight());}
        else {int crop=Math.round(b.getWidth()/ratio);source.set(0,(b.getHeight()-crop)/2,b.getWidth(),(b.getHeight()+crop)/2);}
        paint.setColor(Color.WHITE);c.drawBitmap(b,source,target,paint);
    }
    synchronized int retainedBytes(){int bytes=0;for(Bitmap b:tiles.values())bytes+=b.getAllocationByteCount();return bytes;}
    synchronized int retainedTiles(){return tiles.size();}
    synchronized int decodeCount(){return decodes;}
    /** Test synchronization only; the production renderer never waits for decoding. */
    void awaitReady(){ExecutorService current; synchronized(this){current=worker;}if(current!=null)try{current.submit(()->{}).get(15,TimeUnit.SECONDS);}catch(Exception e){throw new IllegalStateException("Map image loading failed",e);}}
    synchronized void release(){generation++;context=null;repaint=null;tiles.clear();Arrays.fill(failed,false);if(worker!=null)worker.shutdownNow();worker=null;running=false;}
}
