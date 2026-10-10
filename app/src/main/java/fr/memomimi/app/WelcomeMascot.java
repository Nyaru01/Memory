package fr.memomimi.app;

import android.content.Context;
import android.graphics.*;
import java.io.*;

/** Dedicated, registered paw animation. Loading never invokes the victory timeline. */
final class WelcomeMascot {
    static final long DURATION_MS=5000;
    final Bitmap atlas;
    final Rect[] cells=new Rect[8];
    final int[] feet=new int[8];
    final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    final RectF target=new RectF();
    int frame;
    private WelcomeMascot(Bitmap bitmap){
        atlas=bitmap;int width=bitmap.getWidth()/4,height=bitmap.getHeight()/2;
        int[] row=new int[width];
        for(int i=0;i<8;i++){
            cells[i]=new Rect(i%4*width,i/4*height,(i%4+1)*width,(i/4+1)*height);
            // Feet registration ignores tiny antialiasing/isolated alpha remnants.
            feet[i]=height*91/100;
            for(int y=height-1;y>height/2;y--){
                bitmap.getPixels(row,0,width,cells[i].left,cells[i].top+y,width,1);int solid=0;
                for(int x=width/4;x<width*3/4;x++)if(Color.alpha(row[x])>220)solid++;
                if(solid>width/12){feet[i]=y;break;}
            }
        }
    }
    static WelcomeMascot load(Context context){
        try(InputStream in=context.getAssets().open("mascot/lumi-welcome-v910.png")){
            BitmapFactory.Options options=new BitmapFactory.Options();options.inScaled=false;
            Bitmap b=BitmapFactory.decodeStream(in,null,options);
            if(b==null)return null;
            if(b.getAllocationByteCount()>8*1024*1024){b.recycle();return null;}
            return new WelcomeMascot(b);
        }catch(IOException|OutOfMemoryError ignored){return null;}
    }
    static int pose(long elapsed,boolean reduced){
        if(reduced)return 0;
        long t=Math.max(0,elapsed);
        if(t<850)return 0;if(t<1120)return 1;if(t<1420)return 2;
        if(t<1690)return 3;if(t<1820)return 4;if(t<2120)return 5;
        if(t<2380)return 3;if(t<2640)return 5;if(t<2940)return 6;
        return 7;
    }
    void draw(Canvas canvas,RectF area,long elapsed,boolean reduced){
        frame=pose(elapsed,reduced);
        float size=Math.min(area.height(),area.width()*.76f),floor=area.bottom-size*.012f;
        float breath=reduced?0:.014f*(float)Math.sin(Math.max(0,elapsed)*Math.PI*2/3800d);
        paint.setColor(0x26556960);
        canvas.drawOval(area.centerX()-size*.23f,floor-size*.022f,area.centerX()+size*.23f,floor+size*.018f,paint);
        canvas.save();canvas.translate(area.centerX(),floor);
        canvas.scale(1-breath*.3f,1+breath);
        float cellHeight=cells[frame].height(),scale=size/cellHeight;
        target.set(-size/2,-feet[frame]*scale,size/2,(cellHeight-feet[frame])*scale);
        paint.setColor(Color.WHITE);canvas.drawBitmap(atlas,cells[frame],target,paint);canvas.restore();
    }
}
