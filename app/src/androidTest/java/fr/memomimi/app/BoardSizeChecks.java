package fr.memomimi.app;

import android.app.Instrumentation;
import android.os.Bundle;
import android.graphics.*;
import android.view.MotionEvent;
import java.io.*;
import java.util.*;

final class BoardSizeChecks {
    final Instrumentation host;
    BoardSizeChecks(Instrumentation host){this.host=host;}
    Bundle run(){Bundle result=new Bundle();android.content.SharedPreferences prefs=host.getTargetContext().getSharedPreferences("m",0);Map<String,?> saved=prefs.getAll();MainActivity activity=null;
        try{
            prefs.edit().putInt("adventure_completed",6).putBoolean("tutorial_done",true).commit();
            activity=(MainActivity)host.startActivitySync(new android.content.Intent(host.getTargetContext(),MainActivity.class).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK));MainActivity live=activity;GameView v=activity.game;
            host.runOnMainSync(()->{live.updates.close();v.sound=false;v.calm=true;v.startAdventure(7);v.tutorialActive=false;v.invalidate();});Thread.sleep(400);
            int checks=0;if(v.pairs!=8||v.cards.length!=16||v.adventureLevel!=7)throw new AssertionError("Level7 deck changed");checks++;
            for(int i=0;i<16;i++){RectF b=v.cardBounds[i];if(b.width()/v.d<48||b.top<v.systemTop()+v.gameHeaderHeight()||b.bottom>v.H-v.systemBottom()+1||v.hitCard(b.centerX(),b.centerY())!=i)throw new AssertionError("Card bounds "+i);checks++;}
            if(v.adventure.completed()!=6)throw new AssertionError("Progress changed");checks++;
            if(v.H/v.d>700&&v.boardLayout.columns==4){float old=(v.W-56*v.d)/4;float gain=v.boardLayout.width*v.boardLayout.height/(old*old*1.12f);if(gain<1.15f)throw new AssertionError("Insufficient size gain");checks++;}
            Bitmap bitmap=host.getUiAutomation().takeScreenshot();if(bitmap==null)throw new AssertionError("Capture");try(FileOutputStream out=new FileOutputStream(new File(host.getTargetContext().getExternalFilesDir(null),"level7-919-live.png"))){bitmap.compress(Bitmap.CompressFormat.PNG,100,out);}bitmap.recycle();checks++;
            host.runOnMainSync(()->{RectF b=v.cardBounds[15];MotionEvent e=MotionEvent.obtain(GameView.now(),GameView.now(),MotionEvent.ACTION_UP,b.centerX(),b.centerY(),0);v.onTouchEvent(e);e.recycle();});if(v.first!=15)throw new AssertionError("Last card tap");checks++;
            result.putString("stream","PASS: "+checks+" attached level7 board checks; 8 pairs/16 cards preserved; "+v.cols+" columns, card "+v.boardLayout.width/v.d+" x "+v.boardLayout.height/v.d+" dp; bounds, final-card tap and progression verified.\n");
        }catch(Throwable error){result.putString("stream","FAIL: "+error+"\n");}
        finally{if(activity!=null){MainActivity live=activity;host.runOnMainSync(live::finish);}host.waitForIdleSync();android.content.SharedPreferences.Editor edit=prefs.edit().clear();for(Map.Entry<String,?> e:saved.entrySet()){Object value=e.getValue();String key=e.getKey();if(value instanceof Integer)edit.putInt(key,(Integer)value);else if(value instanceof Long)edit.putLong(key,(Long)value);else if(value instanceof Boolean)edit.putBoolean(key,(Boolean)value);else if(value instanceof Float)edit.putFloat(key,(Float)value);else if(value instanceof String)edit.putString(key,(String)value);else if(value instanceof Set)edit.putStringSet(key,(Set<String>)value);}edit.commit();}return result;
    }
}
