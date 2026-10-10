package fr.memomimi.app;

import android.graphics.*;
import android.view.MotionEvent;
import android.view.VelocityTracker;

/** Illustrated renderer and hit geometry. AdventureStore exclusively owns progression. */
final class AdventureMap {
    final RectF[] nodes=new RectF[40];
    final RectF back=new RectF(),continueButton=new RectF(),focusButton=new RectF(),lumi=new RectF();
    final AdventureScenery scenery=new AdventureScenery();
    final AdventureMotion motion=new AdventureMotion();
    final AdventureDecor decor=new AdventureDecor();
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    private final Path path=new Path(),star=new Path();
    private final RectF area=new RectF();
    private VelocityTracker tracker;
    private long openedAt;
    private float focusStart;
    private boolean pendingFocus;
    private final Matrix shaderMatrix=new Matrix();
    private final LinearGradient mist=new LinearGradient(0,0,0,1,0x99fff8eb,0x00fff8eb,Shader.TileMode.CLAMP);
    private final LinearGradient[] nodeColors={new LinearGradient(0,0,0,1,0xffa9e9b6,0xff439b87,Shader.TileMode.CLAMP),new LinearGradient(0,0,0,1,0xffc7a6f2,0xff8163bd,Shader.TileMode.CLAMP),new LinearGradient(0,0,0,1,0xffede3d6,0xffb0a798,Shader.TileMode.CLAMP)};
    float scroll,maxScroll,top,bottom;
    private static final float SECTION=400;
    AdventureMap(){
        for(int i=0;i<40;i++)nodes[i]=new RectF();
        for(int i=0;i<10;i++){double a=-Math.PI/2+i*Math.PI/5;float r=i%2==0?1:.46f;
            if(i==0)star.moveTo((float)Math.cos(a)*r,(float)Math.sin(a)*r);else star.lineTo((float)Math.cos(a)*r,(float)Math.sin(a)*r);}star.close();
    }
    static float levelY(int level,float d){return (((level-1)/2)*SECTION+(level%2==1?150:310))*d;}
    static float levelX(int level,float w,float d){return w*(.5f+.21f*(float)Math.sin(level*1.37+.4));}
    void focus(int level,float viewport,float d){pendingFocus=false;motion.stop();scroll=Math.max(0,levelY(level,d)-viewport*.42f);}
    void open(int level,float viewport,float d,boolean reduced,long now){
        motion.open(level,now,reduced);openedAt=now;scroll=Math.max(0,levelY(motion.from,d)-viewport*.42f);focusStart=scroll;pendingFocus=true;
    }
    void draw(Canvas c,GameView v){
        float d=v.d,w=v.W;long now=GameView.now();boolean reduced=v.animationsReduced();
        top=v.systemTop()+86*d;bottom=v.H-v.systemBottom()-66*d;
        maxScroll=Math.max(0,20*SECTION*d-(bottom-top));
        if(pendingFocus){pendingFocus=false;scroll=Math.max(0,levelY(motion.from,d)-(bottom-top)*.42f);focusStart=scroll;}
        if(reduced)motion.stop();
        if(motion.travelling(now)){
            float goal=Math.max(0,levelY(motion.to,d)-(bottom-top)*.42f);scroll=focusStart+(goal-focusStart)*motion.progress(now);
        }else if(Math.abs(motion.velocity)>8*d && v.activityActive){
            float dt=Math.min(.05f,Math.max(0,(now-motion.lastFrame)/1000f));float old=scroll;
            scroll=Math.max(0,Math.min(maxScroll,scroll+motion.velocity*dt));motion.velocity*=(float)Math.exp(-5*dt);if(scroll==old)motion.velocity=0;
        }
        motion.lastFrame=now;scroll=Math.max(0,Math.min(scroll,maxScroll));scenery.load(v.getContext());decor.load(v.getContext());
        c.save();c.clipRect(0,top,w,bottom);
        int first=Math.max(0,(int)(scroll/(SECTION*d))),last=Math.min(19,(int)((scroll+bottom-top)/(SECTION*d)));
        for(int section=first;section<=last;section++){
            float y=top+section*SECTION*d-scroll;scenery.draw(c,section,y,w,SECTION*d);
            shaderMatrix.setScale(1,30*d);shaderMatrix.postTranslate(0,y);mist.setLocalMatrix(shaderMatrix);paint.setColor(Color.WHITE);paint.setShader(mist);c.drawRect(0,y,w,y+30*d,paint);paint.setShader(null);
            area.set(14*d,y+12*d,w-14*d,y+46*d);v.round(c,area,14,0xdffffbf5);
            v.fitLabel(c,v.worldTitle(AdventureStore.world(section*2+1)),w/2,y+29*d,17,14,w-48*d,UiStyle.TEXT);
            if(!reduced)drawAtmosphere(c,section,y,w,d,now-openedAt);
            decor.draw(c,section,y,w,d,now-openedAt,reduced);
            if(section%3==2){paint.setColor(0x40fff8dc);c.drawCircle(w-38*d,y+225*d,25*d,paint);drawStar(c,w-38*d,y+225*d,14*d,0xffffd17b);}
        }
        for(int level=1;level<=40;level++){
            float x=levelX(level,w,d),y=top+levelY(level,d)-scroll;nodes[level-1].set(x-29*d,y-29*d,x+29*d,y+29*d);
        }
        paint.setStyle(Paint.Style.STROKE);paint.setStrokeCap(Paint.Cap.ROUND);
        for(int level=Math.max(2,first*2+1);level<=Math.min(40,last*2+3);level++){
            RectF a=nodes[level-2],b=nodes[level-1];path.reset();path.moveTo(a.centerX(),a.centerY());
            path.cubicTo(a.centerX(),a.centerY()+a.height()*1.5f,b.centerX(),b.centerY()-b.height()*1.5f,b.centerX(),b.centerY());
            paint.setStrokeWidth(18*d);paint.setColor(0x606a5370);c.drawPath(path,paint);
            paint.setStrokeWidth(13*d);paint.setColor(level<=v.adventure.current()?0xffffe9a8:0xcffff8df);c.drawPath(path,paint);
            paint.setStrokeWidth(2*d);paint.setColor(0x80ffffff);c.drawPath(path,paint);
        }
        paint.setStyle(Paint.Style.FILL);
        for(int level=1;level<=40;level++){
            RectF n=nodes[level-1];if(n.bottom<top||n.top>bottom)continue;
            boolean done=level<=v.adventure.completed(),open=v.adventure.unlocked(level),current=level==v.adventure.current();
            float pulse=reduced?0:(float)Math.sin((now-openedAt)/900d)*2*d;
            if(current){paint.setColor(0x65fff8d4);c.drawCircle(n.centerX(),n.centerY(),37*d+pulse,paint);}
            paint.setColor(0x506c5266);c.drawOval(n.left,n.top+6*d,n.right,n.bottom+6*d,paint);
            LinearGradient shade=nodeColors[done?0:open?1:2];shaderMatrix.setScale(1,n.height());shaderMatrix.postTranslate(0,n.top);shade.setLocalMatrix(shaderMatrix);paint.setColor(Color.WHITE);paint.setShader(shade);
            c.drawCircle(n.centerX(),n.centerY(),29*d,paint);paint.setShader(null);
            paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(3*d);paint.setColor(0xeafffff4);c.drawCircle(n.centerX(),n.centerY(),26*d,paint);paint.setStyle(Paint.Style.FILL);
            v.label(c,String.valueOf(level),n.centerX(),n.centerY()-3*d,22,open?Color.WHITE:0xff665f61,Paint.Align.CENTER);
            if(open)v.label(c,done?"✓":"▶",n.centerX(),n.centerY()+18*d,12,Color.WHITE,Paint.Align.CENTER);
            else drawLock(c,n.centerX(),n.centerY()+15*d,d);
            if(done)drawStar(c,n.right-3*d,n.top+2*d,8*d,0xffffd16a);
        }
        drawLumi(c,v,now,reduced);c.restore();
        back.set(12*d,v.systemTop()+5*d,60*d,v.systemTop()+53*d);focusButton.set(w-60*d,v.systemTop()+5*d,w-12*d,v.systemTop()+53*d);
        v.round(c,back,16,UiStyle.SURFACE);v.label(c,"‹",back.centerX(),back.centerY(),30,UiStyle.TEXT,Paint.Align.CENTER);
        v.round(c,focusButton,16,UiStyle.SURFACE);v.label(c,"◎",focusButton.centerX(),focusButton.centerY(),29,UiStyle.ACCENT,Paint.Align.CENTER);
        int section=Math.min(19,Math.max(0,(int)((scroll+(bottom-top)*.42f)/(SECTION*d))));
        v.fitLabel(c,v.worldTitle(AdventureStore.world(section*2+1)),w/2,v.systemTop()+26*d,20,14,w-130*d,UiStyle.TEXT);
        v.fitLabel(c,v.mapHint.isEmpty()?v.adventure.completed()+" / 40 · Mon aventure":v.mapHint,w/2,v.systemTop()+64*d,14,12,w-24*d,UiStyle.MUTED);
        continueButton.set(16*d,bottom+7*d,w-16*d,bottom+59*d);v.round(c,continueButton,18,UiStyle.REPLAY);
        v.label(c,v.adventure.completed()==40?"Rejouer le niveau 40":"Continuer · Niveau "+v.adventure.current(),w/2,continueButton.centerY(),18,UiStyle.REPLAY_TEXT,Paint.Align.CENTER);
        if(v.activityActive && (!reduced || Math.abs(motion.velocity)>8*d))v.postInvalidateOnAnimation();
    }
    private void drawLumi(Canvas c,GameView v,long now,boolean reduced){
        RectF dest=nodes[v.adventure.current()-1];float x=dest.centerX(),y=dest.centerY();
        if(motion.travelling(now)){
            RectF a=nodes[motion.from-1];float t=motion.progress(now),u=1-t;
            x=u*u*u*a.centerX()+3*u*u*t*a.centerX()+3*u*t*t*x+t*t*t*x;
            y=u*u*u*a.centerY()+3*u*u*t*(a.centerY()+a.height()*1.5f)+3*u*t*t*(y-dest.height()*1.5f)+t*t*t*y;
        }
        lumi.set(x-47*v.d,y-119*v.d,x+47*v.d,y-25*v.d);
        if(lumi.bottom>=top&&lumi.top<=bottom)v.victoryMascot.drawMap(c,lumi,now-openedAt,motion.reactionAt<0?-1:now-motion.reactionAt,reduced,motion.travelling(now));
    }
    private void drawLock(Canvas c,float x,float y,float d){paint.setColor(0xff665f61);c.drawRoundRect(x-4*d,y-2*d,x+4*d,y+5*d,2*d,2*d,paint);paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(2*d);c.drawArc(x-3*d,y-8*d,x+3*d,y+1*d,180,180,false,paint);paint.setStyle(Paint.Style.FILL);}
    private void drawStar(Canvas c,float x,float y,float size,int color){c.save();c.translate(x,y);c.scale(size,size);paint.setColor(color);c.drawPath(star,paint);c.restore();}
    private void drawAtmosphere(Canvas c,int section,float y,float w,float d,long elapsed){
        int family=AdventureStore.island(section*2+1);
        for(int i=0;i<5;i++){
            float phase=(elapsed/9000f+i*.21f+section*.13f)%1,x=w*(.08f+i*.22f)+(float)Math.sin(elapsed/2100d+i)*6*d,py=y+75*d+phase*320*d;
            if(family==1){paint.setColor(0x65ffffff);paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(1.3f*d);c.drawCircle(x,py,(3+i)*d,paint);paint.setStyle(Paint.Style.FILL);}
            else if(family==2||family==4)drawStar(c,x,py,3*d,0x99fff2b4);
            else {paint.setColor(0x8098ba76);c.save();c.rotate(25+(float)Math.sin(elapsed/1600d+i)*15,x,py);c.drawOval(x-3*d,py-6*d,x+3*d,py+6*d,paint);c.restore();}
        }
        float cx=((elapsed/180f+section*73)%(w+100*d))-50*d;
        paint.setColor(0x65ffffff);c.drawOval(cx-35*d,y+63*d,cx+35*d,y+77*d,paint);c.drawOval(cx-12*d,y+54*d,cx+20*d,y+77*d,paint);
    }
    void touch(MotionEvent e,boolean dragging,float d,boolean reduced){
        if(e.getActionMasked()==MotionEvent.ACTION_DOWN){motion.velocity=0;motion.cancelTravel();if(tracker!=null)tracker.recycle();tracker=VelocityTracker.obtain();}
        if(tracker!=null)tracker.addMovement(e);
        if(e.getActionMasked()==MotionEvent.ACTION_UP||e.getActionMasked()==MotionEvent.ACTION_CANCEL){if(tracker!=null){if(dragging&&!reduced&&e.getActionMasked()==MotionEvent.ACTION_UP){tracker.computeCurrentVelocity(1000,2400*d);motion.velocity=-tracker.getYVelocity();motion.lastFrame=GameView.now();}tracker.recycle();tracker=null;}}
    }
    void release(){scenery.release();decor.release();motion.stop();if(tracker!=null){tracker.recycle();tracker=null;}}
    boolean scrollBy(float delta){motion.stop();float old=scroll;scroll=Math.max(0,Math.min(maxScroll,scroll+delta));return old!=scroll;}
    int hit(float x,float y){if(y<top||y>bottom)return -1;for(int i=0;i<40;i++)if(nodes[i].contains(x,y))return i+1;return -1;}
}
