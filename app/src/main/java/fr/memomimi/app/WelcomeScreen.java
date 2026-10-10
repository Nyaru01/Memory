package fr.memomimi.app;

import android.graphics.*;

/** Quiet brand composition: cached light, a grounded character, understated loading feedback. */
final class WelcomeScreen {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private LinearGradient background;
    private RadialGradient light;
    private float width=-1,height=-1,origin=-1;
    void draw(Canvas canvas,GameView view,long elapsed,float progress){
        float d=view.d,w=view.W,top=view.systemTop(),bottom=view.H-view.systemBottom(),h=bottom-top;
        if(width!=w || height!=h || origin!=top){
            width=w;height=h;origin=top;
            background=new LinearGradient(0,0,w*.35f,view.H,0xfffffbf5,0xffdef0e8,Shader.TileMode.CLAMP);
            light=new RadialGradient(w/2,top+h*.49f,Math.max(1,Math.min(w*.66f,h*.43f)),0xcaffffff,0x00ffffff,Shader.TileMode.CLAMP);
        }
        paint.setColor(Color.WHITE);paint.setShader(background);canvas.drawRect(0,0,w,view.H,paint);
        paint.setShader(light);canvas.drawRect(0,top,w,bottom,paint);paint.setShader(null);
        view.splashPanel.set(18*d,top,w-18*d,bottom);
        view.fitLabel(canvas,"MémoMondes",w/2,top+h*.17f,35,25,w-36*d,0xff493f50);
        view.fitLabel(canvas,"De petites paires, de grandes aventures",w/2,top+h*.235f,13,10,w-36*d,0xff736c7b);
        view.splashMascotArea.set(24*d,top+h*.33f,w-24*d,top+h*.69f);
        boolean reduced=view.animationsReduced();
        float t=Math.max(0,Math.min(1,elapsed/240f));t=t*t*(3-2*t);
        int layer=!reduced && t<1 ? canvas.saveLayerAlpha(view.splashMascotArea,Math.round(t*255)) : -1;
        view.victoryMascot.drawLoading(canvas,view.splashMascotArea,elapsed,reduced,0xff68b8a7);
        if(layer>=0)canvas.restoreToCount(layer);
        view.splashBadge.set(18*d,top+h*.75f,w-18*d,top+h*.80f);
        view.fitLabel(canvas,GameView.WORLD_COUNT+" mondes à découvrir",w/2,view.splashBadge.centerY(),12,10,w-36*d,0xff698179);
        float barWidth=Math.min(132*d,w*.42f),y=top+h*.85f;
        view.splashBar.set((w-barWidth)/2,y,(w+barWidth)/2,y+4*d);
        paint.setColor(0xffcddfd8);canvas.drawRoundRect(view.splashBar,2*d,2*d,paint);
        view.splashFill.set(view.splashBar);view.splashFill.right=view.splashBar.left+view.splashBar.width()*progress;
        paint.setColor(0xff62bda7);canvas.drawRoundRect(view.splashFill,2*d,2*d,paint);
        view.fitLabel(canvas,progress<.94f?"Les aventures se préparent…":"Bienvenue !",w/2,top+h*.92f,12,10,w-36*d,0xff57736b);
    }
}
