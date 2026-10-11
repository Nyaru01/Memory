package fr.memomimi.app;

import android.graphics.*;

/** Local scene signs and scenic milestones. None of these objects grants a reward. */
final class AdventureChapters {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF sign=new RectF();
    private final String[] steps=new String[20];
    AdventureChapters(){for(int i=0;i<20;i++)steps[i]="Étapes "+(i*2+1)+"–"+(i*2+2);}
    void draw(Canvas c,GameView v,int section,float y){
        float d=v.d,nodeX=AdventureRoute.x(section*2+1,v.W,d);
        boolean onLeft=nodeX>v.W*.5f;
        float room=(onLeft?nodeX:v.W-nodeX)-66*d,width=Math.min(174*d,Math.max(112*d,room));
        float left=onLeft?12*d:v.W-width-12*d;
        sign.set(left,y+70*d,left+width,y+112*d);
        paint.setColor(0xff826a4c);c.drawRoundRect(sign.centerX()-3*d,sign.top-7*d,sign.centerX()+3*d,sign.bottom+8*d,2*d,2*d,paint);
        paint.setColor(0x34634e31);c.drawRoundRect(sign.left,sign.top+2*d,sign.right,sign.bottom+2*d,10*d,10*d,paint);
        paint.setColor(0xedfaf0d6);c.drawRoundRect(sign,10*d,10*d,paint);
        paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(1*d);paint.setColor(0xffb79a69);c.drawRoundRect(sign,10*d,10*d,paint);paint.setStyle(Paint.Style.FILL);
        v.fitLabel(c,v.worldTitle(AdventureStore.world(section*2+1)),sign.centerX(),sign.top+14*d,14,11,width-12*d,UiStyle.TEXT);
        v.fitLabel(c,steps[section],sign.centerX(),sign.top+31*d,10,9,width-12*d,UiStyle.MUTED);
    }
    static String landmark(int level){
        switch(level){case 5:return "Petit monde";case 11:return "Passage polaire";case 17:return "Le trésor";
            case 21:return "Vers les étoiles";case 29:return "Découverte";case 40:return "Fin du voyage";default:return "";}
    }
    void milestone(Canvas c,GameView v,int level,RectF n){
        String label=landmark(level);if(label.isEmpty())return;float d=v.d;
        paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(2*d);paint.setColor(0xffd6b16d);
        c.drawArc(n.left-3*d,n.top+1*d,n.right+3*d,n.bottom+1*d,195,150,false,paint);paint.setStyle(Paint.Style.FILL);
        sign.set(n.centerX()-47*d,n.bottom+3*d,n.centerX()+47*d,n.bottom+20*d);
        paint.setColor(0xedfaf0d6);c.drawRoundRect(sign,6*d,6*d,paint);
        v.fitLabel(c,label,n.centerX(),sign.centerY(),10,9,sign.width()-8*d,UiStyle.TEXT);
    }
    static int visible(float scroll,float top,float bottom,float d){
        float start=scroll,end=scroll+Math.max(0,bottom-top),best=-1;int chosen=0;
        for(int section=0;section<20;section++){float overlap=Math.max(0,Math.min(end,(section+1)*400*d)-Math.max(start,section*400*d));if(overlap>best){best=overlap;chosen=section;}}
        return chosen;
    }
}
