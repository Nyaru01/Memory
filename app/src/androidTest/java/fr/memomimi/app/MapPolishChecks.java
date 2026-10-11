package fr.memomimi.app;

import android.app.Instrumentation;
import android.graphics.*;
import android.os.Bundle;
import android.view.MotionEvent;
import java.io.*;

/** Real completion -> victory action -> animated route -> next game, on the attached GPU view. */
final class MapPolishChecks {
    final Instrumentation host;int checks;
    MapPolishChecks(Instrumentation host){this.host=host;}
    Bundle run(){
        Bundle result=new Bundle();MainActivity activity=null;
        android.content.SharedPreferences prefs=host.getTargetContext().getSharedPreferences("m",0);java.util.Map<String,?> saved=prefs.getAll();
        try {
            android.content.Intent intent=new android.content.Intent(host.getTargetContext(),MainActivity.class).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
            activity=(MainActivity)host.startActivitySync(intent);MainActivity live=activity;GameView v=live.game;
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
            waitUntil(start,AdventureMotion.DEPART_MS+AdventureMotion.MOVE_MS/2);capture("polish-travel");
            host.runOnMainSync(()->{require(v.adventureMap.motion.progress(GameView.now())>.4f && v.adventureMap.motion.travelling(GameView.now()),"Mid-journey visible");float x=v.adventureMap.lumi.centerX(),y=v.adventureMap.lumi.centerY();touch(v,MotionEvent.ACTION_DOWN,x,y);touch(v,MotionEvent.ACTION_UP,x,y);require(v.adventureMap.motion.reactionAt>=0 && v.screen==7,"Touch reacts during journey");require(v.adventureMap.motion.from==1,"A tap does not cancel travel");});
            Thread.sleep(120);capture("polish-touch");
            waitUntil(start,AdventureMotion.TRAVEL_MS+650);capture("polish-arrival");
            host.runOnMainSync(()->{require(v.adventureMap.motion.arrival(GameView.now())>=0,"Arrival celebrates");require(v.victoryMascot.animation.state==MascotAnimation.State.VICTORY,"Arrival uses victory sprite poses");require(v.adventure.completed()==1,"Animation cannot unlock another level");});
            host.runOnMainSync(()->{click(v,v.adventureMap.continueButton);require(v.screen==1 && v.adventureLevel==2,"Continue launches next playable level");v.clearSavedGame();prefs.edit().putInt("adventure_completed",17).commit();v.openAdventure();});
            Thread.sleep(600);capture("polish-level18");
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
                host.runOnMainSync(()->{require(v.adventureMap.scenery.retainedTiles()<=3,"Three landscape limit");require(v.adventureMap.scenery.seamBytes()<=AdventureScenery.MAX_SEAM_BYTES,"Bounded join strips");require(v.adventure.completed()==17,"Browsing does not unlock");});
            }
            result.putString("stream","PASS: "+checks+" attached Android polish checks: real level1 victory, depart/travel/touch/arrival sprite poses, Continue, current-level camera, four landscape joins and bounded caches. Screenshots are captures, not a manual video review.\n");
        }catch(Throwable error){result.putString("stream","FAIL: "+error+"\n");}
        finally {
            if(activity!=null){MainActivity live=activity;host.runOnMainSync(live::finish);}host.waitForIdleSync();
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
