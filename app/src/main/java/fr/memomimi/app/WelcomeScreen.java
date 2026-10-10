package fr.memomimi.app;

import android.graphics.*;
import android.content.Context;
import java.io.*;

/** Quiet brand composition: cached light, a grounded character, understated loading feedback. */
final class WelcomeScreen {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private LinearGradient background;
    private LinearGradient skyWash,groundWash;
    private volatile Bitmap artwork;
    volatile WelcomeMascot greeting;
    long reactionAt=-1;
    private volatile long artworkAt;
    private boolean attempted;
    private int generation;
    private final Rect source=new Rect();
    private final RectF target=new RectF();
    private float width=-1,height=-1,origin=-1;
    void draw(Canvas canvas,GameView view,long elapsed,float progress){
        float d=view.d,w=view.W,top=view.systemTop(),bottom=view.H-view.systemBottom(),h=bottom-top;
        request(view.getContext(),view.adventureMap.scenery.executor(),view::postInvalidate,w);
        if(width!=w || height!=h || origin!=top){
            width=w;height=h;origin=top;
            background=new LinearGradient(0,0,w*.35f,view.H,0xfffffbf5,0xffdef0e8,Shader.TileMode.CLAMP);
            skyWash=new LinearGradient(0,0,0,view.H*.38f,0xbafffff2,0x00fffff2,Shader.TileMode.CLAMP);
            groundWash=new LinearGradient(0,view.H*.72f,0,view.H,0x00ecf8ee,0xe4ecf8ee,Shader.TileMode.CLAMP);
        }
        paint.setColor(Color.WHITE);paint.setShader(background);canvas.drawRect(0,0,w,view.H,paint);
        paint.setShader(null);Bitmap scene=artwork;
        if(scene!=null){
            AdventureScenery.crop(scene,w,view.H,source);target.set(0,0,w,view.H);
            float fade=view.animationsReduced()?1:Math.min(1,Math.max(0,(GameView.now()-artworkAt)/260f));
            paint.setAlpha(Math.round(255*fade));canvas.drawBitmap(scene,source,target,paint);paint.setAlpha(255);
        }
        paint.setShader(skyWash);canvas.drawRect(0,0,w,view.H*.38f,paint);
        paint.setShader(groundWash);canvas.drawRect(0,view.H*.72f,w,view.H,paint);paint.setShader(null);
        view.splashPanel.set(18*d,top,w-18*d,bottom);
        view.fitLabel(canvas,"MémoMondes",w/2,top+h*.17f,35,25,w-36*d,0xff493f50);
        view.fitLabel(canvas,"De petites paires, de grandes aventures",w/2,top+h*.235f,13,10,w-36*d,0xff736c7b);
        float hero=Math.min(152*d,h*.255f),floor=top+h*.65f;
        view.splashMascotArea.set(w/2-hero*.68f,floor-hero,w/2+hero*.68f,floor);
        boolean reduced=view.animationsReduced();
        float t=Math.max(0,Math.min(1,elapsed/240f));t=t*t*(3-2*t);
        int layer=!reduced && t<1 ? canvas.saveLayerAlpha(view.splashMascotArea,Math.round(t*255)) : -1;
        WelcomeMascot hello=greeting;
        if(hello!=null)hello.draw(canvas,view.splashMascotArea,reactionAt>=0?Math.max(0,GameView.now()-reactionAt)+850:elapsed,reduced);
        else view.victoryMascot.drawLoading(canvas,view.splashMascotArea,elapsed,reduced,0xff68b8a7);
        if(layer>=0)canvas.restoreToCount(layer);
        view.splashBadge.set(18*d,top+h*.75f,w-18*d,top+h*.80f);
        view.fitLabel(canvas,GameView.WORLD_COUNT+" mondes à découvrir",w/2,view.splashBadge.centerY(),12,10,w-36*d,0xff493f50);
        float barWidth=Math.min(132*d,w*.42f),y=top+h*.85f;
        view.splashBar.set((w-barWidth)/2,y,(w+barWidth)/2,y+4*d);
        paint.setColor(0xffcddfd8);canvas.drawRoundRect(view.splashBar,2*d,2*d,paint);
        view.splashFill.set(view.splashBar);view.splashFill.right=view.splashBar.left+view.splashBar.width()*progress;
        paint.setColor(0xff62bda7);canvas.drawRoundRect(view.splashFill,2*d,2*d,paint);
        view.fitLabel(canvas,"Lumi te souhaite la bienvenue !",w/2,top+h*.92f,12,10,w-36*d,0xff57736b);
    }
    private synchronized void request(Context context,java.util.concurrent.Executor worker,Runnable changed,float pixels){
        if(attempted)return;attempted=true;int token=generation;
        worker.execute(()->{
            Bitmap decoded=null;
            try(InputStream input=context.getApplicationContext().getAssets().open("welcome/clearing-v99.png")){
                BitmapFactory.Options options=new BitmapFactory.Options();options.inScaled=false;options.inPreferredConfig=Bitmap.Config.RGB_565;
                options.inSampleSize=pixels<=550?2:1;decoded=BitmapFactory.decodeStream(input,null,options);
                if(decoded!=null && decoded.getConfig()!=Bitmap.Config.RGB_565){Bitmap opaque=decoded.copy(Bitmap.Config.RGB_565,false);decoded.recycle();decoded=opaque;}
            }catch(IOException|OutOfMemoryError ignored){if(decoded!=null){decoded.recycle();decoded=null;}}
            WelcomeMascot hello=WelcomeMascot.load(context.getApplicationContext());
            synchronized(this){
                if(token!=generation){if(decoded!=null)decoded.recycle();if(hello!=null)hello.atlas.recycle();return;}
                greeting=hello;artworkAt=GameView.now();artwork=decoded;
            }
            changed.run();
        });
    }
    boolean hasArtwork(){return artwork!=null;}
    int bytes(){Bitmap b=artwork;return b==null?0:b.getAllocationByteCount();}
    synchronized void release(){generation++;artwork=null;greeting=null;reactionAt=-1;attempted=false;}
}
