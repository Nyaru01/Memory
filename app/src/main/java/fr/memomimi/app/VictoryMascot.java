package fr.memomimi.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import java.io.InputStream;
import java.io.IOException;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;

/** Cached sprite atlas with a monotonic animation timeline and lightweight Canvas effects. */
final class VictoryMascot {
    final RectF bounds = new RectF();
    final MascotAnimation animation = new MascotAnimation();
    private Bitmap atlas;
    private final Rect[] frames = new Rect[24];
    private final RectF destination = new RectF();

    void load(Context context) {
        if (atlas != null) return;
        try (InputStream input = context.getAssets().open("mascot/lumi-atlas.png")) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            atlas = BitmapFactory.decodeStream(input, null, options);
            if (atlas == null) return;
            for (int i = 0; i < 24; i++) {
                int col = i % 6, row = i / 6;
                frames[i] = new Rect(col*atlas.getWidth()/6, row*atlas.getHeight()/4,
                        (col+1)*atlas.getWidth()/6, (row+1)*atlas.getHeight()/4);
            }
        } catch (IOException ignored) { /* Static Canvas fallback if asset is missing. */ }
    }
    boolean hasAtlas() { return atlas != null; }
    int atlasBytes() { return atlas == null ? 0 : atlas.getAllocationByteCount(); }
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private final Path star = new Path();
    private final RectF bubble = new RectF();
    private String wrappedMessage, firstLine, secondLine;
    private static final int INK = Color.rgb(71, 62, 99);
    private static final int[] CONFETTI = {0xffefadc4, 0xffa7ddce, 0xffc3b0ef, 0xffefce86};

    VictoryMascot() {
        paint.setTypeface(Typeface.create("sans", Typeface.BOLD));
        for (int i = 0; i < 10; i++) {
            double angle = -Math.PI / 2 + i * Math.PI / 5;
            float radius = i % 2 == 0 ? 1 : .45f;
            float x = (float) Math.cos(angle) * radius;
            float y = (float) Math.sin(angle) * radius;
            if (i == 0) star.moveTo(x, y); else star.lineTo(x, y);
        }
        star.close();
    }

    private void fill(int color) { paint.setColor(color); paint.setStyle(Paint.Style.FILL); }
    private void oval(Canvas canvas, float x, float y, float rx, float ry, int color) {
        fill(color); canvas.drawOval(x-rx, y-ry, x+rx, y+ry, paint);
    }
    private void star(Canvas canvas, float x, float y, float size, int color) {
        canvas.save(); canvas.translate(x, y); canvas.scale(size, size);
        fill(color); canvas.drawPath(star, paint); canvas.restore();
    }

    void draw(Canvas canvas, RectF area, VictoryCelebration state, long now,
              boolean reduced, int accessoryColor, int world) {
        draw(canvas, area, state, now, reduced, accessoryColor, world, 1, 1);
    }
    void draw(Canvas canvas, RectF area, VictoryCelebration state, long now,
              boolean reduced, int accessoryColor, int world, float density, float fontScale) {
        if (!state.active) { bounds.setEmpty(); return; }
        long elapsed = Math.max(0, now - state.startedAt);
        long touch = state.reactionAt < 0 ? -1 : Math.max(0, now - state.reactionAt);
        animation.sample(elapsed, touch, state.reaction, reduced);
        float size = Math.min(area.height() * .66f, area.width() * .54f);
        float cx = area.centerX(), floor = area.bottom - size * .07f;
        bounds.set(cx-size*.55f, area.top+area.height()*.34f, cx+size*.55f, area.bottom);
        canvas.save();
        // Entrance and particles stay inside the reserved illustration zone.
        canvas.clipRect(area);
        bubble.set(area.left + 4*density, area.top + 2*density, area.right - 4*density,
                area.top + area.height()*.34f);
        fill(0xfff5f0ff);
        canvas.drawRoundRect(bubble, 12*density, 12*density, paint);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(18*density*fontScale);
        fill(INK);
        drawMessage(canvas, state.message, cx, density);
        fill(0xfff5f0ff);
        canvas.drawCircle(cx+size*.30f, bubble.bottom, 5, paint);
        if (!reduced && elapsed < UiStyle.CONFETTI_MS) {
            for (int i = 0; i < 14; i++) {
                float progress = (elapsed + i*117) / 1800f;
                float x = area.left + area.width()*((i*37%97)/97f);
                float y = area.top + area.height()*(progress % 1);
                int alpha = (int)(160*Math.min(1, (UiStyle.CONFETTI_MS-elapsed)/450f));
                int color = (CONFETTI[i%4] & 0xffffff) | (alpha << 24);
                if (i%3 == 0) star(canvas, x, y, 4, color);
                else oval(canvas, x, y, 2, 3, color);
            }
        }
        // Soft world-colored glow, no blur filters or flashing light.
        for (int i = 3; i >= 1; i--) {
            fill((accessoryColor & 0xffffff) | (8 << 24));
            canvas.drawOval(cx-size*(.25f+i*.055f), floor-size*(.62f+i*.03f),
                    cx+size*(.25f+i*.055f), floor, paint);
        }
        float shadow = 1-Math.max(0, animation.lift)*1.3f;
        oval(canvas, cx, floor, size*.27f*shadow, size*.024f, 0x206f598f);
        drawSprite(canvas, cx, floor, size);
        // Separate charm: future illustrated accessories need no atlas duplication.
        star(canvas, cx+size*.34f, floor-size*.43f, size*.06f, accessoryColor);
        if (!reduced && touch >= 0 && touch < MascotAnimation.TOUCH_MS) {
            float t = touch/(float)MascotAnimation.TOUCH_MS;
            for (int i = 0; i < 3; i++) {
                float x = cx+(i-1)*size*(.25f+t*.18f);
                float y = floor-size*(.62f+t*.22f);
                star(canvas, x, y, size*.045f*(1-t*.4f), (0xffd8a0)|((int)(180*(1-t))<<24));
            }
        }
        canvas.restore();
    }
    private void drawMessage(Canvas canvas, String message, float cx, float density) {
        float width=bubble.width()-16*density;
        if(paint.measureText(message)<=width) {
            canvas.drawText(message,cx,bubble.centerY()-(paint.ascent()+paint.descent())/2,paint);
            return;
        }
        if(!message.equals(wrappedMessage)) {
            wrappedMessage=message;
            int split=message.lastIndexOf(' ',message.length()/2);
            if(split<0) split=message.indexOf(' ');
            if(split<0) split=message.length()/2;
            firstLine=message.substring(0,split).trim(); secondLine=message.substring(split).trim();
        }
        float widest=Math.max(paint.measureText(firstLine),paint.measureText(secondLine));
        // Two lines preserve readable letters instead of shrinking the entire sentence.
        float lineHeight=Math.min(paint.getTextSize()*1.14f,(bubble.height()-4*density)/2);
        if(widest>width) paint.setTextSize(paint.getTextSize()*width/widest);
        if(paint.getTextSize()>lineHeight) paint.setTextSize(lineHeight);
        float baseline=bubble.centerY()-(paint.ascent()+paint.descent())/2;
        canvas.drawText(firstLine,cx,baseline-lineHeight/2,paint);
        canvas.drawText(secondLine,cx,baseline+lineHeight/2,paint);
    }
    void drawLoading(Canvas canvas, RectF area, long elapsed, boolean reduced, int accent) {
        animation.sampleLoading(elapsed, reduced);
        float size = Math.min(area.height(), area.width()*.76f);
        float cx = area.centerX(), floor = area.bottom-size*.09f;
        canvas.save(); canvas.clipRect(area);
        oval(canvas, cx, floor+size*.075f, size*.25f, size*.022f, 0x1b556960);
        // Register the closed-eye drawing to the same feet as the neutral drawing.
        if(animation.frame==4)canvas.translate(size*.035f,size*.008f);
        drawSprite(canvas, cx, floor, size);
        canvas.restore();
    }

    void drawMap(Canvas canvas, RectF area, long elapsed, long reaction, boolean reduced, boolean travelling,long arrival) {
        // Reuse actual atlas pose sequences, including blinks and occasional waves.
        long wave=elapsed%11000;
        long touch=reaction>=0 && reaction<1440 ? reaction : wave>=8500 && wave<9940 ? wave-8500 : -1;
        animation.sample(arrival>=0 ? MascotAnimation.APPEAR_MS+arrival : travelling ? 1300+elapsed%2100 : 6000+elapsed, touch, 0, reduced);
        float size=Math.min(area.height()*.93f,area.width());
        canvas.save();
        float shadow=Math.max(.65f,1-animation.lift*1.5f);
        oval(canvas,area.centerX(),area.bottom-size*.015f,size*.24f*shadow,size*.038f,0x3d544934);
        drawSprite(canvas,area.centerX(),area.bottom-size*.10f,size);
        canvas.restore();
    }

    private void drawSprite(Canvas canvas, float cx, float floor, float size) {
        // Feet are the pivot for landing squash and body stretch.
        canvas.save();
        canvas.translate(cx, floor-animation.lift*size);
        canvas.rotate(animation.tilt);
        canvas.scale(animation.scaleX, animation.scaleY);
        destination.set(-size*.5f, -size*.88f, size*.5f, size*.12f);
        fill(Color.WHITE);
        if (atlas != null) {
            SpritePose.draw(canvas,atlas,frames[animation.previousFrame],frames[animation.frame],destination,
                    animation.previousFrame==animation.frame?1:animation.blend,paint);
        } else {
            oval(canvas, 0, -size*.32f, size*.28f, size*.30f, 0xffb9e7dc);
            star(canvas, 0, -size*.35f, size*.14f, 0xffffedaf);
        }
        canvas.restore();
    }
}
