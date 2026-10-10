package fr.memomimi.app;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

/** Original pastel island map drawn entirely with cached Canvas geometry. */
final class AdventureMap {
    final RectF[] nodes=new RectF[AdventureStore.LEVEL_COUNT];
    final RectF back=new RectF(), continueButton=new RectF();
    private final RectF islandArea=new RectF();
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path=new Path();
    private final Path coastline=new Path();
    float scroll, maxScroll, top, bottom;
    private static final int[] LAND={0xffd1e7b4,0xfff6dca4,0xffe2cff0,0xfff6d1bb,0xffbfdde8};
    private void coast(RectF r,float d) {
        float l=r.left,t=r.top,w=r.width(),h=r.height();
        coastline.reset();coastline.moveTo(l+55*d,t);
        coastline.cubicTo(l+w*.48f,t+10*d,l+w-45*d,t-8*d,l+w-12*d,t+36*d);
        coastline.cubicTo(l+w+6*d,t+h*.28f,l+w-35*d,t+h*.36f,l+w-8*d,t+h*.52f);
        coastline.cubicTo(l+w+4*d,t+h*.7f,l+w-14*d,t+h*.78f,l+w-22*d,t+h-35*d);
        coastline.cubicTo(l+w-52*d,t+h+8*d,l+42*d,t+h+4*d,l+19*d,t+h-25*d);
        coastline.cubicTo(l-7*d,t+h*.73f,l+26*d,t+h*.7f,l+10*d,t+h*.5f);
        coastline.cubicTo(l-6*d,t+h*.3f,l-5*d,t+22*d,l+55*d,t);coastline.close();
    }
    AdventureMap() { for(int i=0;i<nodes.length;i++)nodes[i]=new RectF(); }
    private float levelY(int level,float d) {
        int gaps=0;
        for(int n=2;n<=level;n++) if(AdventureStore.island(n)!=AdventureStore.island(n-1)) gaps++;
        return (94+(level-1)*82+gaps*92)*d;
    }
    void focus(int level,float viewport,float d) { scroll=Math.max(0,levelY(level,d)-viewport*.4f); }
    void draw(Canvas canvas,GameView view) {
        float d=view.d,w=view.W; top=view.systemTop()+98*d; bottom=view.H-view.systemBottom()-70*d;
        maxScroll=Math.max(0,levelY(40,d)+72*d-(bottom-top)); scroll=Math.max(0,Math.min(scroll,maxScroll));
        back.set(16*d,view.systemTop()+4*d,64*d,view.systemTop()+52*d);
        view.round(canvas,back,18,UiStyle.SURFACE);
        view.label(canvas,"‹",back.centerX(),back.centerY(),32,UiStyle.TEXT,Paint.Align.CENTER);
        view.fitLabel(canvas,"Mon aventure",(64*d+w)/2,view.systemTop()+28*d,24,18,w-88*d,UiStyle.TEXT);
        view.fitLabel(canvas,view.mapHint.isEmpty() ? view.adventure.completed()+" / 40 étapes terminées" : view.mapHint,w/2,view.systemTop()+70*d,15,13,w-24*d,UiStyle.MUTED);
        paint.setColor(0xffdff3f5);canvas.drawRect(0,top,w,bottom,paint);
        canvas.save();canvas.clipRect(0,top,w,bottom);
        for(int first=1;first<=40;) {
            int family=AdventureStore.island(first),last=first;
            while(last<40 && AdventureStore.island(last+1)==family) last++;
            float y=top+levelY(first,d)-70*d-scroll;
            islandArea.set(14*d,y,w-14*d,top+levelY(last,d)+45*d-scroll);
            coast(islandArea,d);paint.setColor(0x35516d71);canvas.drawPath(coastline,paint);
            islandArea.offset(0,-7*d);coast(islandArea,d);paint.setColor(LAND[family]);canvas.drawPath(coastline,paint);
            view.fitLabel(canvas,WorldCatalog.FAMILIES[family],w/2,y+23*d,17,14,w-56*d,UiStyle.TEXT);
            // Rounded trees, flowers and ponds keep scenery quieter than the route.
            for(int k=0;k<4;k++) {
                float x=k%2==0 ? 36*d : w-36*d;
                float treeY=y+80*d+k*(islandArea.height()-120*d)/4;
                if(treeY<islandArea.bottom-26*d) {
                    paint.setColor(0x3573a180);canvas.drawCircle(x,treeY,13*d,paint);
                    paint.setColor(0x2273a180);canvas.drawCircle(x+8*d,treeY-9*d,10*d,paint);
                }
            }
            first=last+1;
        }
        for(int level=1;level<=40;level++) {
            float x=w/2+(float)Math.sin((level-1)*Math.PI/2)*Math.min(76*d,w*.23f);
            float y=top+levelY(level,d)-scroll;
            nodes[level-1].set(x-27*d,y-27*d,x+27*d,y+27*d);
        }
        paint.setStyle(Paint.Style.STROKE);paint.setStrokeCap(Paint.Cap.ROUND);paint.setStrokeWidth(10*d);
        for(int level=2;level<=40;level++) {
            RectF a=nodes[level-2],b=nodes[level-1];path.reset();path.moveTo(a.centerX(),a.centerY());
            path.cubicTo(a.centerX(),(a.centerY()+b.centerY())/2,b.centerX(),(a.centerY()+b.centerY())/2,b.centerX(),b.centerY());
            paint.setColor(level<=view.adventure.current() ? 0xfffff4c7 : 0x65ffffff);canvas.drawPath(path,paint);
        }
        paint.setStyle(Paint.Style.FILL);
        for(int level=1;level<=40;level++) {
            RectF node=nodes[level-1]; if(node.bottom<top || node.top>bottom)continue;
            boolean done=level<=view.adventure.completed(),open=view.adventure.unlocked(level),current=level==view.adventure.current();
            if(current) {paint.setColor(0x407156aa);canvas.drawCircle(node.centerX(),node.centerY(),32*d,paint);}
            paint.setColor(done ? UiStyle.SUCCESS : open ? UiStyle.ACCENT : 0xffd9dedb);
            canvas.drawCircle(node.centerX(),node.centerY(),27*d,paint);
            view.label(canvas,String.valueOf(level),node.centerX(),node.centerY()-3*d,21,open ? 0xffffffff : UiStyle.MUTED,Paint.Align.CENTER);
            if(open) view.label(canvas,done ? "✓" : "▶",node.centerX(),node.centerY()+17*d,12,0xffffffff,Paint.Align.CENTER);
            else {
                paint.setColor(UiStyle.MUTED);canvas.drawRoundRect(node.centerX()-4*d,node.centerY()+12*d,node.centerX()+4*d,node.centerY()+20*d,2*d,2*d,paint);
                paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(2*d);
                canvas.drawArc(node.centerX()-3*d,node.centerY()+6*d,node.centerX()+3*d,node.centerY()+17*d,180,180,false,paint);paint.setStyle(Paint.Style.FILL);
            }
            if(current) {
                view.label(canvas,"Lumi t'attend ici",w/2,node.bottom+17*d,12,UiStyle.ACCENT,Paint.Align.CENTER);
                float x=node.centerX()<=w/2 ? node.right+8*d : node.left-68*d;
                islandArea.set(x,node.centerY()-36*d,x+60*d,node.centerY()+24*d);
                view.victoryMascot.drawLoading(canvas,islandArea,0,true,UiStyle.ACCENT);
            }
        }
        canvas.restore();
        continueButton.set(16*d,bottom+10*d,w-16*d,bottom+62*d);
        view.round(canvas,continueButton,18,UiStyle.REPLAY);
        view.label(canvas,view.adventure.completed()==40 ? "Rejouer le niveau 40" : "Continuer · Niveau "+view.adventure.current(),w/2,continueButton.centerY(),18,UiStyle.REPLAY_TEXT,Paint.Align.CENTER);
    }
    boolean scrollBy(float delta) { float old=scroll;scroll=Math.max(0,Math.min(maxScroll,scroll+delta));return old!=scroll; }
    int hit(float x,float y) { if(y<top || y>bottom)return -1;for(int i=0;i<nodes.length;i++) if(nodes[i].contains(x,y))return i+1;return -1; }
}
