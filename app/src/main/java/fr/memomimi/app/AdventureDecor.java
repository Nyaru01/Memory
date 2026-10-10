package fr.memomimi.app;

import android.content.Context;
import android.graphics.*;
import java.io.*;

/** Four illustrated creatures, each with four real atlas poses; shared across visible sections. */
final class AdventureDecor {
    private volatile Bitmap atlas;
    private volatile Bitmap wildlife;
    private int generation;
    private boolean attempted;
    private final Rect[] frames=new Rect[16];
    private final Rect[] animalFrames=new Rect[16];
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
        Bitmap animals=null;
        try(InputStream input=context.getApplicationContext().getAssets().open("adventure/wildlife-atlas.png")){
            BitmapFactory.Options options=new BitmapFactory.Options();options.inSampleSize=2;options.inScaled=false;animals=BitmapFactory.decodeStream(input,null,options);
        }catch(IOException|OutOfMemoryError ignored){ }
        synchronized(this){
            if(token!=generation){if(animals!=null)animals.recycle();return;}
            if(animals!=null)for(int i=0;i<16;i++)animalFrames[i]=new Rect(i%4*animals.getWidth()/4,i/4*animals.getHeight()/4,(i%4+1)*animals.getWidth()/4,(i/4+1)*animals.getHeight()/4);
            wildlife=animals;
        }
        changed.run();
        });
    }
    void draw(Canvas c,int section,float y,float w,float d,long elapsed,boolean reduced){
        if(atlas==null)return;
        int family=AdventureStore.island(section*2+1);
        for(int i=0;i<2;i++){
            if(section==5)break;
            int row=family==1?2+i:family==2||family==4?1:0;
            long time=reduced?0:Math.max(0,elapsed+section*311+i*530);
            int frame=(int)(time/320%4),previous=(frame+3)%4;
            float fraction=reduced?1:(time%320)/320f;fraction=fraction*fraction*(3-2*fraction);
            float x=i==0?33*d:w-37*d;
            float cy=y+(i==0?198:340)*d+(reduced?0:(float)Math.sin(time/1800d)*7*d);
            float size=(i==0?38:32)*d;target.set(x-size/2,cy-size/2,x+size/2,cy+size/2);
            SpritePose.draw(c,atlas,frames[row*4+previous],frames[row*4+frame],target,fraction,paint);
        }
        paint.setAlpha(255);
        drawAnimal(c,section,y,w,d,elapsed,reduced);
    }
    private void drawAnimal(Canvas c,int section,float y,float w,float d,long elapsed,boolean reduced){
        Bitmap sheet=wildlife;if(sheet==null)return;
        int row=section==0?0:section==1||section==8?1:section==2||section==3?3:section==12?2:section==15||section==16||section==18?1:-1;
        if(row<0)return;
        float rx=section%2==0?.78f:.22f,ry=section==2?.75f:.66f;
        float x=AdventureRoute.project(section,rx,ry,w,d,true),cy=y+AdventureRoute.project(section,rx,ry,w,d,false);
        long time=reduced?0:Math.max(0,elapsed+section*439);long period=row==1?480:950;
        int frame=(int)(time/period%4),previous=(frame+3)%4;float blend=reduced?1:Math.min(1,time%period/(period*.45f));blend=blend*blend*(3-2*blend);
        float size=(row==2?38:34)*d;target.set(x-size/2,cy-size/2,x+size/2,cy+size/2);
        paint.setColor(0x24615336);c.drawOval(x-size*.28f,cy+size*.35f,x+size*.28f,cy+size*.42f,paint);paint.setColor(Color.WHITE);
        SpritePose.draw(c,sheet,animalFrames[row*4+previous],animalFrames[row*4+frame],target,blend,paint);
    }
    int bytes(){Bitmap a=atlas,b=wildlife;return (a==null?0:a.getAllocationByteCount())+(b==null?0:b.getAllocationByteCount());}
    synchronized void release(){generation++;atlas=null;wildlife=null;attempted=false;}
}
