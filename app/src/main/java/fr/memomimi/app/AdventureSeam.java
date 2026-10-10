package fr.memomimi.app;

import android.graphics.*;

/** Bake only the small visible join, off the UI thread. Original paintings are never altered. */
final class AdventureSeam {
    static Bitmap bake(Bitmap before,Bitmap after,int width,int sectionHeight,int seed){
        int height=Math.min(Math.round(sectionHeight*.24f),AdventureScenery.MAX_SEAM_BYTES/(width*6));
        height=Math.max(8,height);
        Bitmap result=null,mask=null;
        try {
            result=Bitmap.createBitmap(width,height,Bitmap.Config.RGB_565);
            Canvas canvas=new Canvas(result);Paint p=new Paint(Paint.FILTER_BITMAP_FLAG|Paint.ANTI_ALIAS_FLAG);
            p.setShader(landscape(before,width,sectionHeight,-sectionHeight+height/2f));canvas.drawRect(0,0,width,height,p);
            int layer=canvas.saveLayer(0,0,width,height,null);
            p.setShader(landscape(after,width,sectionHeight,height/2f));canvas.drawRect(0,0,width,height,p);
            mask=Bitmap.createBitmap(64,64,Bitmap.Config.ARGB_8888);int[] pixels=new int[4096];
            for(int y=0;y<64;y++)for(int x=0;x<64;x++){
                double bend=.10*Math.sin(x*Math.PI/32+seed*.73)+.035*Math.sin(x*Math.PI/11+seed);
                float t=y==0?0:y==63?1:(float)Math.max(0,Math.min(1,(y/63d-.12-bend)/.76));t=t*t*(3-2*t);
                pixels[y*64+x]=Math.round(t*255)<<24|0xffffff;
            }
            mask.setPixels(pixels,0,64,0,0,64,64);p.setShader(null);p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            canvas.drawBitmap(mask,null,new RectF(0,0,width,height),p);p.setXfermode(null);canvas.restoreToCount(layer);
            return result;
        } catch(OutOfMemoryError error){if(result!=null)result.recycle();return null;}
        finally{if(mask!=null)mask.recycle();}
    }
    private static Shader landscape(Bitmap b,int width,int height,float top){
        Rect src=new Rect();AdventureScenery.crop(b,width,height,src);
        BitmapShader shader=new BitmapShader(b,Shader.TileMode.MIRROR,Shader.TileMode.MIRROR);
        Matrix matrix=new Matrix();matrix.setRectToRect(new RectF(src),new RectF(0,top,width,top+height),Matrix.ScaleToFit.FILL);shader.setLocalMatrix(matrix);return shader;
    }
}
