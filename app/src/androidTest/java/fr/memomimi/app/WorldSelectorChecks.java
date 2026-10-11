package fr.memomimi.app;

import android.app.Instrumentation;
import android.os.Bundle;
import android.graphics.*;
import android.view.MotionEvent;
import java.io.*;
import java.util.*;

/** Real Canvas, scroll/tap and free-play checks. Fictitious preferences restored afterwards. */
final class WorldSelectorChecks {
    final Instrumentation host;int checks;
    WorldSelectorChecks(Instrumentation host){this.host=host;}
    Bundle run(){Bundle result=new Bundle();android.content.SharedPreferences prefs=host.getTargetContext().getSharedPreferences("m",0);Map<String,?> saved=prefs.getAll();MainActivity activity=null;
        try{
            prefs.edit().putInt("adventure_completed",17).putInt("album_18",63).putInt("best_12",19).putInt("free_theme",18).commit();
            activity=(MainActivity)host.startActivitySync(new android.content.Intent(host.getTargetContext(),MainActivity.class).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK));MainActivity live=activity;GameView v=activity.game;
            host.runOnMainSync(()->{live.updates.close();v.sound=false;v.pauseMusic();v.activityActive=true;v.calm=false;v.systemReducedMotion=false;v.openFreePlay();});Thread.sleep(350);
            host.runOnMainSync(()->{tap(v,v.chooseTheme);require(v.screen==4,"World selector reached from free play");v.worldsScroll=0;Arrays.fill(v.familyOpen,false);v.familyOpen[0]=true;v.invalidate();});Thread.sleep(450);
            require(v.worldSelector.columns==(v.W/v.d<600?2:3),"Responsive columns");
            require(v.getLayerType()==android.view.View.LAYER_TYPE_NONE,"Catalogue avoids software layer");
            for(int family=0;family<5;family++){
                final int current=family;
                host.runOnMainSync(()->{Arrays.fill(v.familyOpen,false);v.familyOpen[current]=true;v.worldsScroll=0;v.invalidate();});Thread.sleep(250);v.worldSelector.awaitReady();capture("catalogue-family-"+family);
                host.runOnMainSync(()->{for(int world:WorldCatalog.WORLDS[current]){require(!v.themeTiles[world].isEmpty(),"Family exposes real world ID");require(v.themeTiles[world].width()/v.d>=130,"Large child touch area");require(v.worldSelector.image(world)!=null,"Existing landscape preview loaded");}require(v.adventure.completed()==17&&v.albumMask(18)==63,"Browsing preserves progression and collection");});
            }
            host.runOnMainSync(()->{v.worldsScroll=0;tap(v,v.familyTiles[0]);v.invalidate();});Thread.sleep(35);
            host.runOnMainSync(()->{require(v.worldSelector.moving(v),"Family animation active");require(v.themeTiles[0].isEmpty(),"Moving cards cannot be tapped");});Thread.sleep(250);
            host.runOnMainSync(()->{require(!v.themeTiles[0].isEmpty(),"Targets restored after expansion");});
            host.runOnMainSync(()->{Arrays.fill(v.familyOpen,true);v.worldsScroll=0;v.invalidate();});Thread.sleep(200);
            host.runOnMainSync(()->event(v,MotionEvent.ACTION_DOWN,v.W/2,v.H-90*v.d));
            for(int i=1;i<=10;i++){final int step=i;Thread.sleep(18);host.runOnMainSync(()->event(v,MotionEvent.ACTION_MOVE,v.W/2,v.H-(90+20*step)*v.d));}
            host.runOnMainSync(()->{event(v,MotionEvent.ACTION_UP,v.W/2,v.H-290*v.d);require(v.screen==4&&v.worldsScroll>0,"Swipe cannot select a world");Arrays.fill(v.familyOpen,false);v.familyOpen[4]=true;v.worldsScroll=0;v.invalidate();});Thread.sleep(200);
            while(v.themeTiles[18].centerY()>v.H-v.systemBottom()){host.runOnMainSync(()->v.scrollPage(true));Thread.sleep(100);}
            host.runOnMainSync(()->{tap(v,v.themeTiles[18]);require(v.screen==8&&v.theme==18,"Selection returns directly to pairs");v.invalidate();});Thread.sleep(150);
            host.runOnMainSync(()->{tap(v,v.medium);require(v.pairs==8,"Pairs remain available");tap(v,v.play);require(v.screen==1&&v.adventureLevel==0,"Free game launches directly");require(v.adventure.completed()==17&&v.albumMask(18)==63&&prefs.getInt("best_12",0)==19,"Adventure collection records preserved");});
            result.putString("stream","PASS: "+checks+" attached catalogue checks: five families, twenty worlds, preview cache, columns, animated hit targets, swipe, selection, pairs, free game and preserved adventure.\n");
        }catch(Throwable error){result.putString("stream","FAIL: "+error+"\n");}
        finally{if(activity!=null){MainActivity live=activity;host.runOnMainSync(live::finish);}host.waitForIdleSync();android.content.SharedPreferences.Editor edit=prefs.edit().clear();for(Map.Entry<String,?> e:saved.entrySet()){Object value=e.getValue();String key=e.getKey();if(value instanceof Integer)edit.putInt(key,(Integer)value);else if(value instanceof Long)edit.putLong(key,(Long)value);else if(value instanceof Boolean)edit.putBoolean(key,(Boolean)value);else if(value instanceof Float)edit.putFloat(key,(Float)value);else if(value instanceof String)edit.putString(key,(String)value);else if(value instanceof Set)edit.putStringSet(key,(Set<String>)value);}edit.commit();}return result;
    }
    void require(boolean value,String name){if(!value)throw new AssertionError(name);checks++;}
    void event(GameView v,int action,float x,float y){MotionEvent e=MotionEvent.obtain(GameView.now(),GameView.now(),action,x,y,0);v.onTouchEvent(e);e.recycle();}
    void tap(GameView v,RectF area){event(v,MotionEvent.ACTION_DOWN,area.centerX(),area.centerY());event(v,MotionEvent.ACTION_UP,area.centerX(),area.centerY());}
    void capture(String name)throws Exception{Bitmap b=host.getUiAutomation().takeScreenshot();require(b!=null,"GPU capture");try(FileOutputStream out=new FileOutputStream(new File(host.getTargetContext().getExternalFilesDir(null),name+".png"))){b.compress(Bitmap.CompressFormat.PNG,100,out);}b.recycle();}
}
