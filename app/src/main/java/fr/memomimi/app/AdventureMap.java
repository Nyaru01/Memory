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
    private final PathMeasure trail=new PathMeasure();
    private final float[] trailPoint=new float[2],trailTangent=new float[2];
    private VelocityTracker tracker;
    private long openedAt;
    private long pausedAt=-1;
    private float focusStart;
    private boolean pendingFocus;
    volatile boolean waitingForScenery;
    private final Matrix shaderMatrix=new Matrix();
    private LinearGradient headerFade;
    private float fadeTop=-1,fadeDensity;
    private float layoutWidth=360;
    int lockedLevel;
    long lockedAt=-1;
    private final LinearGradient[] nodeColors={new LinearGradient(0,0,0,1,0xffa9e9b6,0xff439b87,Shader.TileMode.CLAMP),new LinearGradient(0,0,0,1,0xffc7a6f2,0xff8163bd,Shader.TileMode.CLAMP),new LinearGradient(0,0,0,1,0xffffedc8,0xffbea16c,Shader.TileMode.CLAMP)};
    float scroll,maxScroll,top,bottom;
    private static final float SECTION=400;
    private Runnable repaint;
    boolean hardwareCanvas;
    void prepare(GameView view){
        if(repaint==null)repaint=view::postInvalidate;
        scenery.load(view.getContext());decor.request(view.getContext(),scenery.executor(),repaint);
    }
    void prewarm(GameView view){
        prepare(view);int section=(view.adventure.current()-1)/2;
        scenery.request(Math.max(0,section-1),Math.min(19,section+1),(int)view.W,(int)(SECTION*view.d),repaint);
    }
    AdventureMap(){
        for(int i=0;i<40;i++)nodes[i]=new RectF();
        for(int i=0;i<10;i++){double a=-Math.PI/2+i*Math.PI/5;float r=i%2==0?1:.46f;
            if(i==0)star.moveTo((float)Math.cos(a)*r,(float)Math.sin(a)*r);else star.lineTo((float)Math.cos(a)*r,(float)Math.sin(a)*r);}star.close();
    }
    float levelY(int level,float d){return AdventureRoute.y(level,layoutWidth,d);}
    static float levelX(int level,float w,float d){return AdventureRoute.x(level,w,d);}
    void focus(int level,float viewport,float d){pendingFocus=false;motion.stop();scroll=Math.max(0,levelY(level,d)-viewport*.42f);}
    void open(int level,float viewport,float d,boolean reduced,long now){
        pausedAt=-1;
        lockedAt=-1;motion.open(level,now,reduced);waitingForScenery=!reduced && motion.from>=1 && motion.to==motion.from+1;openedAt=now;scroll=Math.max(0,levelY(motion.from,d)-viewport*.42f);focusStart=scroll;pendingFocus=true;
    }
    void draw(Canvas c,GameView v){
        if(pausedAt>=0)return;
        hardwareCanvas=c.isHardwareAccelerated();
        float d=v.d,w=v.W;layoutWidth=w;long now=GameView.now();boolean reduced=v.animationsReduced();
        top=v.systemTop()+52*d;bottom=v.H-v.systemBottom()-54*d;
        maxScroll=Math.max(0,20*SECTION*d-(v.H-top));
        if(pendingFocus){pendingFocus=false;if(!reduced && motion.from>=1 && motion.to==motion.from+1)motion.startedAt=now;scroll=Math.max(0,levelY(motion.from,d)-(bottom-top)*.42f);focusStart=scroll;}
        if(reduced)motion.stop();
        if(waitingForScenery){
            if(motion.from<1 || motion.to!=motion.from+1 || reduced)waitingForScenery=false;
            else {
                motion.startedAt=now;
                if(scenery.tile((motion.from-1)/2)!=null && scenery.tile((motion.to-1)/2)!=null || now-openedAt>=8000)waitingForScenery=false;
            }
        }
        if(motion.travelling(now)){
            float goal=Math.max(0,levelY(motion.to,d)-(bottom-top)*.42f);scroll=focusStart+(goal-focusStart)*motion.progress(now);
        }else if(Math.abs(motion.velocity)>8*d && v.activityActive){
            float dt=Math.min(.05f,Math.max(0,(now-motion.lastFrame)/1000f));float old=scroll;
            scroll=Math.max(0,Math.min(maxScroll,scroll+motion.velocity*dt));motion.velocity*=(float)Math.exp(-5*dt);if(scroll==old)motion.velocity=0;
        }
        motion.lastFrame=now;scroll=Math.max(0,Math.min(scroll,maxScroll));prepare(v);
        c.save();c.clipRect(0,0,w,v.H);
        int first=Math.max(0,(int)(scroll/(SECTION*d))),last=Math.min(19,(int)((scroll+v.H-top)/(SECTION*d)));
        scenery.request(first,last,(int)w,(int)(SECTION*d),repaint);
        scenery.extendTop(c,first,top+first*SECTION*d-scroll,w,SECTION*d);
        for(int section=first;section<=last;section++){
            float y=top+section*SECTION*d-scroll;scenery.draw(c,section,y,w,SECTION*d);
        }
        for(int boundary=Math.max(1,first);boundary<=Math.min(19,last+1);boundary++)scenery.drawSeam(c,boundary,top+boundary*SECTION*d-scroll,w,d);
        for(int section=first;section<=last;section++){
            float y=top+section*SECTION*d-scroll;
            if(!reduced)drawAtmosphere(c,section,y,w,d,now-openedAt);
            decor.draw(c,section,y,w,d,now-openedAt,reduced);
        }
        for(int level=1;level<=40;level++){
            float x=levelX(level,w,d),y=top+levelY(level,d)-scroll;nodes[level-1].set(x-29*d,y-29*d,x+29*d,y+29*d);
        }
        paint.setStyle(Paint.Style.STROKE);paint.setStrokeCap(Paint.Cap.ROUND);
        for(int level=Math.max(2,first*2+1);level<=Math.min(40,last*2+3);level++){
            RectF a=nodes[level-2],b=nodes[level-1];path.reset();path.moveTo(a.centerX(),a.centerY());
            float span=b.centerY()-a.centerY();
            path.cubicTo(AdventureRoute.controlX(level-1,w,d,false),a.centerY()+span*.38f,AdventureRoute.controlX(level-1,w,d,true),b.centerY()-span*.38f,b.centerX(),b.centerY());
            boolean reached=level<=v.adventure.current();
            // Sparse footprints belong to the painted trail; no continuous artificial ribbon.
            trail.setPath(path,false);float length=trail.getLength();paint.setStyle(Paint.Style.FILL);
            for(float distance=12*d;distance<length;distance+=32*d){
                trail.getPosTan(distance,trailPoint,trailTangent);
                float side=((int)(distance/(32*d))%2==0?1:-1)*1.5f*d;
                float x=trailPoint[0]-trailTangent[1]*side,y=trailPoint[1]+trailTangent[0]*side;
                paint.setColor(reached?0x886e7950:0x406f765c);
                c.save();c.rotate((float)Math.toDegrees(Math.atan2(trailTangent[1],trailTangent[0])),x,y);
                c.drawOval(x-2.5f*d,y-1.3f*d,x+2.5f*d,y+1.3f*d,paint);c.restore();
            }
            paint.setStyle(Paint.Style.STROKE);
        }
        paint.setStyle(Paint.Style.FILL);
        for(int level=1;level<=40;level++){
            RectF n=nodes[level-1];if(n.bottom<top||n.top>bottom)continue;
            boolean done=level<=v.adventure.completed(),open=v.adventure.unlocked(level),current=level==v.adventure.current();
            boolean validating=motion.validating(now)&&level==motion.from;
            if(motion.validating(now)&&level==motion.to){open=false;current=false;}
            float pulse=reduced?0:(float)Math.sin((now-openedAt)/900d)*2*d;
            c.save();
            if(!reduced && level==lockedLevel && now-lockedAt<420)c.translate((float)Math.sin((now-lockedAt)/35d)*2*d,0);
            // Ground contact and a shallow stone bevel replace floating glossy discs.
            paint.setColor(0x32615837);c.drawOval(n.left-6*d,n.bottom-10*d,n.right+6*d,n.bottom+7*d,paint);
            paint.setColor(0x706f8060);c.drawOval(n.left-3*d,n.bottom-8*d,n.right+3*d,n.bottom+3*d,paint);
            if(current){paint.setColor(0x55fff1ba);c.drawOval(n.left-5*d-pulse,n.top-1*d,n.right+5*d+pulse,n.bottom+3*d,paint);}
            paint.setColor(open?0xff7b7460:0xffa09985);c.drawOval(n.left,n.top+6*d,n.right,n.bottom+3*d,paint);
            LinearGradient shade=nodeColors[done?0:open?1:2];shaderMatrix.setScale(1,n.height());shaderMatrix.postTranslate(0,n.top);shade.setLocalMatrix(shaderMatrix);paint.setColor(Color.WHITE);paint.setShader(shade);
            c.drawOval(n.left,n.top+4*d,n.right,n.bottom-2*d,paint);paint.setShader(null);
            paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(current?2*d:1*d);paint.setColor(current?0xfff6dda2:0x90f8f4dc);c.drawOval(n.left+3*d,n.top+7*d,n.right-3*d,n.bottom-5*d,paint);paint.setStyle(Paint.Style.FILL);
            if(!open){
                paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(1.4f*d);paint.setColor(0xff89754e);
                c.drawOval(n.left+5*d,n.top+9*d,n.right-5*d,n.bottom-7*d,paint);paint.setStyle(Paint.Style.FILL);
                for(int petal=0;petal<6;petal++){double angle=petal*Math.PI/3;float px=n.centerX()+(float)Math.cos(angle)*23*d,py=n.centerY()+(float)Math.sin(angle)*21*d;
                    paint.setColor(0xffb39156);c.drawCircle(px,py,1.5f*d,paint);}
            }
            if(validating||motion.unlocking(now)&&level==motion.to){
                paint.setColor(0x78fff0b0);c.drawOval(n.left-4*d,n.top,n.right+4*d,n.bottom,paint);
            }
            v.label(c,String.valueOf(level),n.centerX(),n.centerY()-3*d,21,open?Color.WHITE:0xff665f61,Paint.Align.CENTER);
            if(open)v.label(c,done?"✓":"▶",n.centerX(),n.centerY()+15*d,12,Color.WHITE,Paint.Align.CENTER);
            else drawLock(c,n.centerX(),n.centerY()+15*d,d);
            if(done)drawStar(c,n.right-3*d,n.top+5*d,6*d,0xffffd16a);
            c.restore();
        }
        drawLumi(c,v,now,reduced);c.restore();
        if(headerFade==null || fadeTop!=top || fadeDensity!=d){fadeTop=top;fadeDensity=d;headerFade=new LinearGradient(0,0,0,top+22*d,new int[]{0xe8f6f8ee,0xc8f6f8ee,0x00f6f8ee},new float[]{0,.6f,1},Shader.TileMode.CLAMP);}
        paint.setColor(Color.WHITE);paint.setShader(headerFade);c.drawRect(0,0,w,top+18*d,paint);paint.setShader(null);
        back.set(12*d,v.systemTop()+5*d,60*d,v.systemTop()+53*d);focusButton.set(w-60*d,v.systemTop()+5*d,w-12*d,v.systemTop()+53*d);
        surface(c,back,16*d,0xdffffbf5,d);v.label(c,"‹",back.centerX(),back.centerY(),28,UiStyle.TEXT,Paint.Align.CENTER);
        surface(c,focusButton,16*d,0xdffffbf5,d);v.label(c,"◎",focusButton.centerX(),focusButton.centerY(),25,UiStyle.ACCENT,Paint.Align.CENTER);
        int section=Math.min(19,Math.max(0,(int)((scroll+(bottom-top)*.42f)/(SECTION*d))));
        v.fitLabel(c,v.worldTitle(AdventureStore.world(section*2+1)),w/2,v.systemTop()+18*d,18,14,w-130*d,UiStyle.TEXT);
        v.fitLabel(c,v.mapHint.isEmpty()?"Niveau "+v.adventure.current()+" · "+v.adventure.completed()+" / 40":v.mapHint,w/2,v.systemTop()+38*d,12,10,w-144*d,UiStyle.MUTED);
        float buttonWidth=Math.min(w-32*d,240*d);continueButton.set((w-buttonWidth)/2,bottom+2*d,(w+buttonWidth)/2,bottom+50*d);surface(c,continueButton,18*d,UiStyle.REPLAY,d);
        v.fitLabel(c,v.adventure.completed()==40?"Rejouer le niveau 40":"Continuer · Niveau "+v.adventure.current(),w/2,continueButton.centerY(),17,14,buttonWidth-20*d,UiStyle.REPLAY_TEXT);
        if(v.activityActive && (!reduced || Math.abs(motion.velocity)>8*d)){
            if(v.dragging || motion.travelling(now) || motion.arrival(now)>=0 || Math.abs(motion.velocity)>8*d || motion.reactionAt>=0 && now-motion.reactionAt<1440)v.postInvalidateOnAnimation();
            else v.postInvalidateDelayed(33);
        }
    }
    /** Soft offset shadow avoids a large blur mask being rebuilt during every scroll frame. */
    private void surface(Canvas c,RectF bounds,float radius,int color,float d){
        paint.setColor(0x12605d68);c.drawRoundRect(bounds.left,bounds.top+2*d,bounds.right,bounds.bottom+2*d,radius,radius,paint);
        paint.setColor(color);c.drawRoundRect(bounds,radius,radius,paint);
    }
    private void drawLumi(Canvas c,GameView v,long now,boolean reduced){
        RectF dest=nodes[v.adventure.current()-1];float x=dest.centerX(),y=dest.centerY();
        if(motion.travelling(now)){
            RectF a=nodes[motion.from-1];float t=motion.progress(now),u=1-t;
            x=u*u*u*a.centerX()+3*u*u*t*AdventureRoute.controlX(motion.from,v.W,v.d,false)+3*u*t*t*AdventureRoute.controlX(motion.from,v.W,v.d,true)+t*t*t*x;
            float span=y-a.centerY();y=u*u*u*a.centerY()+3*u*u*t*(a.centerY()+span*.38f)+3*u*t*t*(y-span*.38f)+t*t*t*y;
        }
        lumi.set(x-42*v.d,y-105*v.d,x+42*v.d,y-21*v.d);
        if(lumi.bottom>=top&&lumi.top<=bottom){paint.setColor(0x30584f35);c.drawOval(x-19*v.d,y-26*v.d,x+19*v.d,y-19*v.d,paint);}
        if(lumi.bottom>=top&&lumi.top<=bottom)v.victoryMascot.drawMap(c,lumi,now-openedAt,motion.reactionAt<0?-1:now-motion.reactionAt,reduced,motion.travelling(now)&&now-motion.startedAt>=AdventureMotion.DEPART_MS,motion.arrival(now));
        if(!reduced && motion.arrival(now)>=0)arrivalStars(c,x,y,v.d,motion.arrival(now));
    }
    private void arrivalStars(Canvas c,float x,float y,float d,long elapsed){
        float t=elapsed/(float)AdventureMotion.CELEBRATE_MS;
        for(int i=0;i<7;i++){
            double angle=i*Math.PI*2/7;float spread=(30+28*t)*d;
            drawStar(c,x+(float)Math.cos(angle)*spread,y-59*d+(float)Math.sin(angle)*spread,3.5f*d,(Math.round(160*(1-t))<<24)|0xffd274);
        }
    }
    private void drawLock(Canvas c,float x,float y,float d){paint.setColor(0xff665f61);c.drawRoundRect(x-4*d,y-2*d,x+4*d,y+5*d,2*d,2*d,paint);paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(2*d);c.drawArc(x-3*d,y-8*d,x+3*d,y+1*d,180,180,false,paint);paint.setStyle(Paint.Style.FILL);}
    private void drawStar(Canvas c,float x,float y,float size,int color){c.save();c.translate(x,y);c.scale(size,size);paint.setColor(color);c.drawPath(star,paint);c.restore();}
    private void drawAtmosphere(Canvas c,int section,float y,float w,float d,long elapsed){
        int family=AdventureStore.island(section*2+1);
        // Small highlights sit on the existing painted water, not on a generic river overlay.
        float rx=section==0?.32f:section==1?.29f:section==2?.65f:section==3?.45f:section==5?.53f:section==9?.51f:section==12?.42f:section==13?.62f:section==17?.40f:-1;
        if(rx>=0 || family==1){
            if(rx<0)rx=.52f;
            float x=AdventureRoute.project(section,rx,.76f,w,d,true),cy=y+AdventureRoute.project(section,rx,.76f,w,d,false);
            paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(1.2f*d);
            for(int i=0;i<3;i++){
                float pulse=(float)Math.sin(elapsed/1300d+i*1.8);paint.setColor((Math.round(24+10*(pulse+1))<<24)|0xffffff);
                float span=(8+i*3+pulse*2)*d;c.drawArc(x-span,cy+i*10*d,x+span,cy+(i*10+5)*d,12,154,false,paint);
            }
            paint.setStyle(Paint.Style.FILL);
            float shimmer=(float)Math.max(0,Math.sin(elapsed/1100d+section));
            if(shimmer>.55f){paint.setColor((Math.round((shimmer-.55f)*150)<<24)|0xffffff);c.drawCircle(x+5*d,cy+3*d,1.6f*d,paint);}
        }
        for(int i=0;i<5;i++){
            float phase=(elapsed/9000f+i*.21f+section*.13f)%1,x=w*(.08f+i*.22f)+(float)Math.sin(elapsed/2100d+i)*6*d,py=y+75*d+phase*320*d;
            if(section==5){paint.setColor(0x70ffffff);c.drawCircle(x,py,(1.2f+i*.25f)*d,paint);}
            else if(family==1){paint.setColor(0x65ffffff);paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(1.3f*d);c.drawCircle(x,py,(3+i)*d,paint);paint.setStyle(Paint.Style.FILL);}
            else if(family==2||family==4)drawStar(c,x,py,3*d,0x99fff2b4);
            else {paint.setColor(0x8098ba76);c.save();c.rotate(25+(float)Math.sin(elapsed/1600d+i)*15,x,py);c.drawOval(x-3*d,py-6*d,x+3*d,py+6*d,paint);c.restore();}
        }
        // Fireflies stay beside the vegetation, away from the fixed header.
        if(family==0 || family==2 || family==4)for(int i=0;i<3;i++){
            float x=w*(i==1?.83f:.12f)+(float)Math.sin(elapsed/2600d+i+section)*4*d;
            float py=y+(170+i*64)*d+(float)Math.cos(elapsed/3100d+i)*6*d;
            int alpha=Math.round(30+30*(1+(float)Math.sin(elapsed/1900d+i*2)));
            paint.setColor((alpha<<24)|0xffe4a6);c.drawCircle(x,py,3*d,paint);
            paint.setColor((Math.min(160,alpha+40)<<24)|0xfff4ce);c.drawCircle(x,py,1.2f*d,paint);
        }
    }
    void touch(MotionEvent e,boolean dragging,float d,boolean reduced){
        if(e.getActionMasked()==MotionEvent.ACTION_DOWN){motion.velocity=0;if(tracker!=null)tracker.recycle();tracker=VelocityTracker.obtain();}
        if(dragging)motion.cancelTravel();
        if(tracker!=null)tracker.addMovement(e);
        if(e.getActionMasked()==MotionEvent.ACTION_UP||e.getActionMasked()==MotionEvent.ACTION_CANCEL){if(tracker!=null){if(dragging&&!reduced&&e.getActionMasked()==MotionEvent.ACTION_UP){tracker.computeCurrentVelocity(1000,2400*d);motion.velocity=-tracker.getYVelocity();motion.lastFrame=GameView.now();}tracker.recycle();tracker=null;}}
    }
    void release(){scenery.release();decor.release();motion.stop();if(tracker!=null){tracker.recycle();tracker=null;}}
    void pause(long now){
        if(pausedAt>=0)return;pausedAt=now;motion.velocity=0;
        scenery.release();decor.release();if(tracker!=null){tracker.recycle();tracker=null;}
    }
    void resume(long now){
        if(pausedAt<0)return;long gap=Math.max(0,now-pausedAt);openedAt+=gap;motion.startedAt+=gap;
        if(motion.reactionAt>=0)motion.reactionAt+=gap;if(lockedAt>=0)lockedAt+=gap;
        motion.lastFrame=now;pausedAt=-1;
    }
    boolean scrollBy(float delta){motion.stop();float old=scroll;scroll=Math.max(0,Math.min(maxScroll,scroll+delta));return old!=scroll;}
    int hit(float x,float y){if(y<top||y>bottom)return -1;for(int i=0;i<40;i++)if(nodes[i].contains(x,y))return i+1;return -1;}
}
