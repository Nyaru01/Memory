package fr.memomimi.app;

import android.content.Context;
import android.graphics.*;
import java.io.*;

/** Illustrated physical/scenographic passages, composed off-thread into cached joins. */
final class AdventurePassage {
    private static final int[] PROPS={0,0,1,2,2,3,4,4,5,6,7,7,8,9,10,1,6,11,6,11};
    private static final boolean[] PORTALS={false,false,true,true,false,true,true,false,true,false,true,true,false,true,true,true,false,true,false,true};
    private volatile Bitmap atlas;
    private final Rect[] frames=new Rect[12];
    private final RectF target=new RectF();
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    private boolean attempted;
    private int generation;
    static boolean portal(int boundary){return PORTALS[boundary];}
    void load(Context context){
        int token;synchronized(this){if(attempted)return;attempted=true;token=generation;}
        Bitmap decoded=null;
        try(InputStream input=context.getAssets().open("adventure/passages-v917.png")){
            BitmapFactory.Options options=new BitmapFactory.Options();options.inScaled=false;options.inSampleSize=2;
            decoded=BitmapFactory.decodeStream(input,null,options);
        }catch(IOException|OutOfMemoryError ignored){ }
        synchronized(this){
            if(token!=generation){if(decoded!=null)decoded.recycle();return;}
            if(decoded!=null)for(int i=0;i<12;i++)frames[i]=new Rect(i%4*decoded.getWidth()/4,i/4*decoded.getHeight()/3,(i%4+1)*decoded.getWidth()/4,(i/4+1)*decoded.getHeight()/3);
            atlas=decoded;
        }
    }
    synchronized void compose(Canvas canvas,int boundary,int width,int height,int sectionHeight){
        Bitmap sheet=atlas;if(sheet==null)return;
        float d=sectionHeight/400f,x=AdventureRoute.passageX(boundary,width,d),size=Math.min(116*d,height*.94f);
        target.set(x-size/2,height*.5f-size/2,x+size/2,height*.5f+size/2);
        canvas.drawBitmap(sheet,frames[PROPS[boundary]],target,paint);
    }
    int bytes(){Bitmap sheet=atlas;return sheet==null?0:sheet.getAllocationByteCount();}
    synchronized void release(){generation++;atlas=null;attempted=false;}
}
