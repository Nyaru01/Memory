package fr.memomimi.app;

import android.app.Instrumentation;
import android.graphics.*;
import android.os.Bundle;
import android.view.MotionEvent;
import android.content.SharedPreferences;
import java.io.*;
import java.util.Map;

/** Real attached menus; restore emulator preferences after the scenario. */
final class HomeChecks {
    private final Instrumentation host;private int checks;
    HomeChecks(Instrumentation h){host=h;}
    void require(boolean b,String label){if(!b)throw new AssertionError(label);checks++;}
    void tap(GameView v,RectF r){long now=GameView.now();MotionEvent e=MotionEvent.obtain(now,now,MotionEvent.ACTION_UP,r.centerX(),r.centerY(),0);v.onTouchEvent(e);e.recycle();}
    void capture(String name)throws Exception{
        host.waitForIdleSync();Thread.sleep(550);Bitmap b=host.getUiAutomation().takeScreenshot();require(b!=null,"Attached capture");
        try(FileOutputStream out=new FileOutputStream(new File(host.getTargetContext().getExternalFilesDir(null),name+".png"))){b.compress(Bitmap.CompressFormat.PNG,100,out);}b.recycle();
    }
    Bundle run(){
        Bundle result=new Bundle();MainActivity activity=null;
        SharedPreferences prefs=host.getTargetContext().getSharedPreferences("m",0);Map<String,?> previous=prefs.getAll();
        try{
            activity=(MainActivity)host.startActivitySync(new android.content.Intent(host.getTargetContext(),MainActivity.class).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK));
            MainActivity live=activity;GameView v=live.game;
            host.runOnMainSync(()->{live.updates.close();v.sound=false;v.calm=true;v.screen=0;v.showResumePrompt=false;v.homeScroll=0;v.invalidate();});
            v.adventureMap.scenery.awaitReady();capture("home-910-live");
            host.runOnMainSync(()->{
                require(v.homeScreen.bytes()>0&&v.homeScreen.bytes()<=12*1024*1024,"Two bounded illustrations");
                require(v.easy.isEmpty()&&v.chooseTheme.isEmpty(),"Home has no configuration controls");
                tap(v,v.adventureButton);require(v.screen==7,"Adventure entrance opens map");
                v.handleBack();require(v.screen==0,"Map back to home");v.invalidate();
            });Thread.sleep(100);
            host.runOnMainSync(()->{
                require(v.animationsReduced(),"Calm mode reduces animations");
                for(GameAccessibility.Target target:v.accessible.targets()) if(target.bounds==v.freePlayButton) {
                    require(v.accessible.performAction(target.id,android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK,null),"Virtual free play action");break;
                }
                require(v.screen==8,"Free entrance opens configuration");
                v.invalidate();
            });capture("free-910-live");
            host.runOnMainSync(()->{require(v.homeBtn.width()/v.d>=48,"Back touch target");tap(v,v.medium);require(v.pairs==8&&v.adventureLevel==0,"Free difficulty isolated");tap(v,v.chooseTheme);require(v.screen==4,"Free world selector");v.familyOpen[0]=true;v.worldsScroll=0;v.invalidate();});capture("worlds-910-live");
            host.runOnMainSync(()->{tap(v,v.themeTiles[0]);require(v.screen==8,"Selected world returns to configuration");require(prefs.getInt("free_pairs",0)==8,"Free choice persisted");v.invalidate();});Thread.sleep(100);
            host.runOnMainSync(()->{tap(v,v.play);require(v.screen==1&&v.adventureLevel==0&&v.cards.length==16,"Free game starts selected deck");v.handleBack();require(v.showQuit,"Android game back asks to quit");v.showQuit=false;v.screen=8;v.handleBack();require(v.screen==0,"Configuration back to home");v.invalidate();});Thread.sleep(100);
            host.runOnMainSync(()->{boolean sound=v.sound;tap(v,v.soundBtn);require(v.sound!=sound&&prefs.getBoolean("sound",sound)!=sound,"Music toggle persists");tap(v,v.albumBtn);require(v.screen==5&&v.albumReturnScreen==0,"Home album opens");v.handleBack();require(v.screen==0,"Album back to home");});
            host.runOnMainSync(()->{v.homeScroll=v.homeScrollMax;v.invalidate();});Thread.sleep(150);
            host.runOnMainSync(()->{
                long now=GameView.now();MotionEvent down=MotionEvent.obtain(now,now,MotionEvent.ACTION_DOWN,v.parentBtn.centerX(),v.parentBtn.centerY(),0);
                v.onTouchEvent(down);down.recycle();
                MotionEvent move=MotionEvent.obtain(now,now+30,MotionEvent.ACTION_MOVE,v.parentBtn.centerX(),v.parentBtn.centerY()+12*v.d,0);
                v.onTouchEvent(move);move.recycle();require(v.parentHolding,"Parent hold tolerates jitter");
            });Thread.sleep(GameView.PARENT_HOLD_MS+150);
            host.runOnMainSync(()->{require(v.screen==3,"Real timed parent hold opens settings");tap(v,v.parentBtn);v.invalidate();});capture("parents-912-live");
            host.runOnMainSync(()->{
                prefs.edit().putInt("adventure_completed",4).putInt("saved_adventure_level",1).putBoolean("game_active",true).commit();
                v.parentsScroll=v.parentsScrollMax;v.invalidate();
            });Thread.sleep(150);
            host.runOnMainSync(()->{tap(v,v.adventureResetBtn);require(v.showResetConfirm,"Adventure reset asks confirmation");v.invalidate();});capture("adventure-reset-912-live");
            host.runOnMainSync(()->{
                v.handleBack();require(!v.showResetConfirm&&v.adventure.completed()==4,"Android back cancels adventure reset");
                tap(v,v.adventureResetBtn);v.invalidate();
            });Thread.sleep(150);
            host.runOnMainSync(()->{
                int album=v.albumCount(),record=prefs.getInt("best_6",0);boolean haptics=prefs.getBoolean("haptics",true);
                tap(v,v.resetConfirmBtn);require(v.adventure.completed()==0&&v.adventure.current()==1,"Reset returns to first level");
                require(!prefs.getBoolean("game_active",true),"Adventure resume discarded");
                require(v.albumCount()==album&&prefs.getInt("best_6",0)==record&&prefs.getBoolean("haptics",true)==haptics,"Album records settings preserved");
                v.invalidate();
            });
            result.putString("stream","PASS: "+checks+" attached home checks, two illustrated modes, map, free configuration/worlds/game/back, album, music and settings. Preferences restored; no manual video or physical phone validation.\n");
        }catch(Throwable error){result.putString("stream","FAIL: "+error+"\n");}
        finally{
            if(activity!=null){MainActivity live=activity;host.runOnMainSync(live::finish);}
            host.waitForIdleSync();SharedPreferences.Editor edit=prefs.edit().clear();
            for(Map.Entry<String,?> entry:previous.entrySet()){
                Object value=entry.getValue();String key=entry.getKey();
                if(value instanceof Integer)edit.putInt(key,(Integer)value);else if(value instanceof Long)edit.putLong(key,(Long)value);
                else if(value instanceof Boolean)edit.putBoolean(key,(Boolean)value);else if(value instanceof Float)edit.putFloat(key,(Float)value);
                else if(value instanceof String)edit.putString(key,(String)value);
            }edit.commit();
        }return result;
    }
}
