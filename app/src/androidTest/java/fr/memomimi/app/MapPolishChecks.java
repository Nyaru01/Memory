package fr.memomimi.app;

import android.app.Instrumentation;
import android.graphics.*;
import android.os.Bundle;
import android.view.MotionEvent;
import java.io.*;

/** Real completion -> victory action -> animated route -> next game, on the attached GPU view. */
final class MapPolishChecks {
    final Instrumentation host;int checks;
    private android.view.Window.OnFrameMetricsAvailableListener metricsListener;
    private long frameNanos,worstNanos;
    private int measuredFrames,slowFrames;
    MapPolishChecks(Instrumentation host){this.host=host;}
    Bundle run(){
        Bundle result=new Bundle();MainActivity activity=null;
        android.content.SharedPreferences prefs=host.getTargetContext().getSharedPreferences("m",0);java.util.Map<String,?> saved=prefs.getAll();
        try {
            android.content.Intent intent=new android.content.Intent(host.getTargetContext(),MainActivity.class).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
            activity=(MainActivity)host.startActivitySync(intent);MainActivity live=activity;GameView v=live.game;
            if(android.os.Build.VERSION.SDK_INT>=24)host.runOnMainSync(()->{
                metricsListener=(window,metrics,dropped)->{if(v.screen==7){long duration=metrics.getMetric(android.view.FrameMetrics.TOTAL_DURATION);if(duration>=0){measuredFrames++;frameNanos+=duration;worstNanos=Math.max(worstNanos,duration);if(duration>16666667)slowFrames++;}}};
                live.getWindow().addOnFrameMetricsAvailableListener(metricsListener,new android.os.Handler(android.os.Looper.getMainLooper()));
            });
            host.runOnMainSync(()->{live.updates.close();v.sound=false;v.pauseMusic();v.activityActive=true;v.calm=false;v.systemReducedMotion=false;v.haptics=false;prefs.edit().putInt("adventure_completed",0).commit();v.startAdventure(1);v.tutorialActive=false;v.previewUntil=0;for(int i=0;i<v.cards.length;i++)v.cards[i]=i/2;});
            Thread.sleep(250);
            host.runOnMainSync(()->{for(int i=0;i<v.cards.length;i++){v.flipIndex=-1;click(v,v.cardBounds[i]);}require(v.found==v.pairs && v.adventure.completed()==1,"Real board unlocks one level");});
            Thread.sleep(1100);
            host.runOnMainSync(()->{require(v.screen==2,"Real victory reached");click(v,v.play);require(v.screen==7,"Victory action opens journey");});
            long deadline=GameView.now()+15000;
            while((v.adventureMap.waitingForScenery || v.adventureMap.scenery.tile(0)==null) && GameView.now()<deadline)Thread.sleep(30);
            require(!v.adventureMap.waitingForScenery && v.adventureMap.scenery.tile(0)!=null,"Journey waits for the painted landscape");
            Thread.sleep(100);capture("polish-depart");
            long start=v.adventureMap.motion.startedAt;
            host.runOnMainSync(()->{require(v.adventureMap.motion.from==1 && v.adventureMap.motion.to==2,"Journey starts from completed level");require(v.adventureMap.hardwareCanvas,"Attached hardware Canvas");require(v.victoryMascot.hasAtlas(),"Real Lumi poses bundled");});
            final float[] pausedProgress=new float[1];
            host.runOnMainSync(()->{require(v.victoryMascot.hasMapAtlas(),"Dedicated breathing/ears/blink/hop atlas loaded");pausedProgress[0]=v.adventureMap.motion.progress(GameView.now());v.pauseMusic();});
            Thread.sleep(600);
            host.runOnMainSync(()->{v.resumeMusic();require(Math.abs(v.adventureMap.motion.progress(GameView.now())-pausedProgress[0])<.02f,"Lifecycle pause preserves journey position");});
            start=v.adventureMap.motion.startedAt;
            waitUntil(start,AdventureMotion.DEPART_MS+AdventureMotion.MOVE_MS/2);capture("polish-travel");
            host.runOnMainSync(()->{require(v.adventureMap.motion.progress(GameView.now())>.4f && v.adventureMap.motion.travelling(GameView.now()),"Mid-journey visible");float x=v.adventureMap.lumi.centerX(),y=v.adventureMap.lumi.centerY();touch(v,MotionEvent.ACTION_DOWN,x,y);touch(v,MotionEvent.ACTION_UP,x,y);require(v.adventureMap.motion.reactionAt>=0 && v.screen==7,"Touch reacts during journey");require(v.adventureMap.motion.from==1,"A tap does not cancel travel");});
            Thread.sleep(120);capture("polish-touch");
            waitUntil(start,AdventureMotion.TRAVEL_MS+650);capture("polish-arrival");
            host.runOnMainSync(()->{require(v.adventureMap.motion.arrival(GameView.now())>=0,"Arrival celebrates");require(v.victoryMascot.animation.state==MascotAnimation.State.VICTORY,"Arrival uses victory sprite poses");require(v.adventure.completed()==1,"Animation cannot unlock another level");});
            host.runOnMainSync(()->{click(v,v.adventureMap.continueButton);require(v.screen==1 && v.adventureLevel==2,"Continue launches next playable level");v.clearSavedGame();prefs.edit().putInt("adventure_completed",17).commit();v.openAdventure();});
            Thread.sleep(600);capture("polish-level18");
            final int[] idleFrame=new int[1];host.runOnMainSync(()->idleFrame[0]=v.victoryMascot.animation.frame);
            Thread.sleep(1200);capture("polish-idle-breath");
            host.runOnMainSync(()->{require(v.victoryMascot.animation.frame!=idleFrame[0],"Idle changes real sprite frames");require(v.victoryMascot.animation.scaleY==1,"Breathing is drawn in sprite frames");});
            host.runOnMainSync(()->{RectF n=v.adventureMap.nodes[17];require(n.centerY()>v.adventureMap.top && n.centerY()<v.adventureMap.bottom,"Opening focuses level18");require(!v.adventureMap.motion.travelling(GameView.now()),"Ordinary opening has no replayed journey");});
            for(int level:new int[]{6,13,21,40}){
                host.runOnMainSync(()->{v.adventureMap.focus(level,v.adventureMap.bottom-v.adventureMap.top,v.d);v.invalidate();});
                Thread.sleep(700);capture("polish-zone-level"+level);
                if(level==21)host.runOnMainSync(()->{
                    RectF node=v.adventureMap.nodes[20];click(v,node);
                    require(v.screen==7&&v.adventure.completed()==17,"Locked tap preserves progress and screen");
                    require(v.adventureMap.lockedLevel==21&&v.adventureMap.lockedAt>=0,"Locked feedback registered");
                    require(node.width()/v.d>=48&&node.height()/v.d>=48,"Stone keeps accessible target");
                });
                host.runOnMainSync(()->{require(v.adventureMap.scenery.retainedTiles()<=AdventureScenery.MAX_TILES,"Bounded viewport and neighbour tiles");require(v.adventureMap.scenery.seamBytes()<=AdventureScenery.MAX_SEAM_BYTES,"Bounded join strips");require(v.adventure.completed()==17,"Browsing does not unlock");});
            }
            for(int completed:new int[]{2,10,20,38}){
                host.runOnMainSync(()->{prefs.edit().putInt("adventure_completed",completed).commit();v.adventureMap.motion.completed(completed);v.openAdventure();});
                long readyBy=GameView.now()+15000;
                while((v.adventureMap.waitingForScenery||v.adventureMap.scenery.seam(completed/2)==null)&&GameView.now()<readyBy)Thread.sleep(30);
                long departure=v.adventureMap.motion.startedAt;
                host.runOnMainSync(()->{require(v.adventureMap.scenery.passage.bytes()>0,"Illustrated passage atlas decoded off-thread");require(v.adventureMap.scenery.passage.bytes()<=2*1024*1024,"Passage atlas bounded");});
                waitUntil(departure,AdventureMotion.DEPART_MS+AdventureMotion.MOVE_MS/2);
                host.runOnMainSync(()->{float t=v.adventureMap.motion.progress(GameView.now());v.adventureMap.journey.sample(completed,t);require(Math.abs(v.adventureMap.lumi.centerX()-v.adventureMap.journey.position[0])<6*v.d,"Lumi follows the shared path through the passage");require(v.adventure.completed()==completed,"Scenic passage never unlocks a level");});
                capture("world-passage-"+completed);
                waitUntil(departure,AdventureMotion.TRAVEL_MS+200);
                host.runOnMainSync(()->{require(v.adventureMap.motion.to==completed+1,"Arrival position is next saved level");});
            }
            final float[] beforeScroll=new float[1];
            host.runOnMainSync(()->{v.adventureMap.focus(18,v.adventureMap.bottom-v.adventureMap.top,v.d);beforeScroll[0]=v.adventureMap.scroll;touch(v,MotionEvent.ACTION_DOWN,v.W/2,v.adventureMap.bottom-80*v.d);});
            for(int step=1;step<=12;step++){final int index=step;Thread.sleep(18);host.runOnMainSync(()->touch(v,MotionEvent.ACTION_MOVE,v.W/2,v.adventureMap.bottom-(80+index*20)*v.d));}
            host.runOnMainSync(()->touch(v,MotionEvent.ACTION_UP,v.W/2,v.adventureMap.bottom-320*v.d));Thread.sleep(350);
            host.runOnMainSync(()->{require(v.adventureMap.scroll>beforeScroll[0]+100*v.d,"Real swipe scrolls the attached map");require(v.adventureMap.scenery.retainedBytes()<=AdventureScenery.MAX_BYTES,"Swipe cache stays within memory budget");require(v.adventure.completed()==38,"Exploration swipe preserves saved position");click(v,v.adventureMap.focusButton);});
            host.runOnMainSync(()->result.putString("stream","PASS: "+checks+" attached Android map checks: victory, real sprite frames, lifecycle pause, shared path/camera, four illustrated cross-zone passages, Continue, focus, browsing and bounded caches.\nMap FrameMetrics: "+measuredFrames+" frames, mean "+(measuredFrames==0?0:frameNanos/1000000d/measuredFrames)+" ms, worst "+worstNanos/1000000d+" ms, "+slowFrames+" over 16.7 ms. Emulator measurements include loading/captures; no physical-device or manual-video claim.\n"));
        }catch(Throwable error){result.putString("stream","FAIL: "+error+"\n");}
        finally {
            if(activity!=null){MainActivity live=activity;host.runOnMainSync(()->{if(android.os.Build.VERSION.SDK_INT>=24&&metricsListener!=null)live.getWindow().removeOnFrameMetricsAvailableListener(metricsListener);live.finish();});}host.waitForIdleSync();
            android.content.SharedPreferences.Editor edit=prefs.edit().clear();
            for(java.util.Map.Entry<String,?> entry:saved.entrySet()){
                String key=entry.getKey();Object value=entry.getValue();
                if(value instanceof Integer)edit.putInt(key,(Integer)value);else if(value instanceof Long)edit.putLong(key,(Long)value);
                else if(value instanceof Boolean)edit.putBoolean(key,(Boolean)value);else if(value instanceof Float)edit.putFloat(key,(Float)value);
                else if(value instanceof String)edit.putString(key,(String)value);
            }edit.commit();
        }
        return result;
    }
    void require(boolean condition,String label){if(!condition)throw new AssertionError(label);checks++;}
    void click(GameView v,RectF r){touch(v,MotionEvent.ACTION_UP,r.centerX(),r.centerY());}
    void touch(GameView v,int action,float x,float y){long now=GameView.now();MotionEvent e=MotionEvent.obtain(now,now,action,x,y,0);v.onTouchEvent(e);e.recycle();}
    void waitUntil(long start,long age)throws InterruptedException{Thread.sleep(Math.max(1,start+age-GameView.now()));}
    void capture(String name)throws Exception{Bitmap b=host.getUiAutomation().takeScreenshot();require(b!=null,"GPU screenshot");try(FileOutputStream out=new FileOutputStream(new File(host.getTargetContext().getExternalFilesDir(null),name+".png"))){b.compress(Bitmap.CompressFormat.PNG,100,out);}b.recycle();}
}
