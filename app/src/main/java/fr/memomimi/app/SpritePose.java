package fr.memomimi.app;

import android.graphics.*;

/** Keep faces sharp: choose one illustrated pose; body transforms interpolate separately. */
final class SpritePose {
    static void draw(Canvas canvas,Bitmap sheet,Rect previous,Rect current,RectF target,float phase,Paint paint){
        paint.setAlpha(255);
        canvas.drawBitmap(sheet,phase<.5f?previous:current,target,paint);
    }
}
