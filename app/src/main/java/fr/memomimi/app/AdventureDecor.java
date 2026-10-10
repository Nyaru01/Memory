package fr.memomimi.app;

import android.content.Context;
import android.graphics.*;
import java.io.*;

/** Four illustrated creatures, each with four real atlas poses; shared across visible sections. */
final class AdventureDecor {
    private volatile Bitmap atlas;
    private int generation;
    private boolean attempted;
    private final Rect[] frames=new Rect[16];
    private final RectF target=new RectF();
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    synchronized void request(Context context,java.util.concurrent.Executor executor,Runnable changed){
        if(attempted)return;attempted=true;int token=generation;
        executor.execute(()->{
        Bitmap decoded=null;
        try(InputStream input=context.getApplicationContext().getAssets().open("adventure/creatures-atlas.png")){
            BitmapFactory.Options options=new BitmapFactory.Options();options.inSampleSize=2;options.inScaled=false;
            decoded=BitmapFactory.decodeStream(input,null,options);
        }catch(IOException|OutOfMemoryError ignored){ }
        synchronized(this){
            if(token!=generation){if(decoded!=null)decoded.recycle();return;}
            if(decoded!=null)for(int i=0;i<16;i++)frames[i]=new Rect(i%4*decoded.getWidth()/4,i/4*decoded.getHeight()/4,(i%4+1)*decoded.getWidth()/4,(i/4+1)*decoded.getHeight()/4);
            atlas=decoded;
        }
        changed.run();
        });
    }
    void draw(Canvas c,int section,float y,float w,float d,long elapsed,boolean reduced){
        if(atlas==null)return;
        int family=AdventureStore.island(section*2+1);
        for(int i=0;i<2;i++){
            int row=family==1?2+i:family==2||family==4?1:0;
            long time=reduced?0:Math.max(0,elapsed+section*311+i*530);
            int frame=(int)(time/320%4),previous=(frame+3)%4;
            float fraction=reduced?1:(time%320)/320f;fraction=fraction*fraction*(3-2*fraction);
            float x=i==0?33*d:w-37*d;
            float cy=y+(i==0?198:340)*d+(reduced?0:(float)Math.sin(time/1800d)*7*d);
            float size=(i==0?38:32)*d;target.set(x-size/2,cy-size/2,x+size/2,cy+size/2);
            paint.setAlpha((int)(255*(1-fraction)));c.drawBitmap(atlas,frames[row*4+previous],target,paint);
            paint.setAlpha((int)(255*fraction));c.drawBitmap(atlas,frames[row*4+frame],target,paint);
        }
        paint.setAlpha(255);
    }
    int bytes(){return atlas==null?0:atlas.getAllocationByteCount();}
    synchronized void release(){generation++;atlas=null;attempted=false;}
}
