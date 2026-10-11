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
            prefs.edit().putBoolean("tutorial_done",true).putInt("album_18",63).putInt("best_12",19).commit();
            activity=(MainActivity)host.startActivitySync(new android.content.Intent(host.getTargetContext(),MainActivity.class).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK));MainActivity live=activity;GameView v=activity.game;
            host.runOnMainSync(()->{live.updates.close();v.sound=false;v.haptics=false;v.calm=true;});
            int checks=0;StringBuilder sizes=new StringBuilder();
            for(int[] stage:new int[][]{{1,6},{7,8},{17,10},{29,12},{40,12}}){
                prefs.edit().putInt("adventure_completed",stage[0]-1).commit();
                host.runOnMainSync(()->{v.clearSavedGame();v.startAdventure(stage[0]);v.tutorialActive=false;v.invalidate();});Thread.sleep(400);
                if(v.pairs!=stage[1]||v.cards.length!=stage[1]*2||v.adventureLevel!=stage[0]||v.first!=-1)throw new AssertionError("Deck changed at "+stage[0]);checks++;
                for(int i=0;i<v.cards.length;i++){RectF b=v.cardBounds[i];if(b.width()/v.d<48||b.top<v.systemTop()+v.gameHeaderHeight()||b.bottom>v.H-v.systemBottom()+1||v.hitCard(b.centerX(),b.centerY())!=i)throw new AssertionError("Card bounds "+stage[0]+" / "+i);checks++;}
                if(v.adventure.completed()!=stage[0]-1||v.albumMask(18)!=63||prefs.getInt("best_12",0)!=19)throw new AssertionError("Progress/collection/record changed");checks++;
                Bitmap bitmap=host.getUiAutomation().takeScreenshot();if(bitmap==null)throw new AssertionError("Capture");try(FileOutputStream out=new FileOutputStream(new File(host.getTargetContext().getExternalFilesDir(null),"board-920-level"+stage[0]+".png"))){bitmap.compress(Bitmap.CompressFormat.PNG,100,out);}bitmap.recycle();checks++;
                int last=v.cards.length-1;host.runOnMainSync(()->{RectF b=v.cardBounds[last];MotionEvent e=MotionEvent.obtain(GameView.now(),GameView.now(),MotionEvent.ACTION_UP,b.centerX(),b.centerY(),0);v.onTouchEvent(e);e.recycle();});if(v.first!=last)throw new AssertionError("Last card tap "+stage[0]);checks++;
                sizes.append("Level ").append(stage[0]).append(": ").append(v.cols).append(" columns, ").append(v.boardLayout.width/v.d).append(" x ").append(v.boardLayout.height/v.d).append(" dp.\n");
            }
            result.putString("stream","PASS: "+checks+" attached board checks; 6/8/10/12 pairs preserved; bounds, final-card taps, collections, records and progression verified.\n"+sizes);
        }catch(Throwable error){result.putString("stream","FAIL: "+error+"\n");}
        finally{if(activity!=null){MainActivity live=activity;host.runOnMainSync(live::finish);}host.waitForIdleSync();android.content.SharedPreferences.Editor edit=prefs.edit().clear();for(Map.Entry<String,?> e:saved.entrySet()){Object value=e.getValue();String key=e.getKey();if(value instanceof Integer)edit.putInt(key,(Integer)value);else if(value instanceof Long)edit.putLong(key,(Long)value);else if(value instanceof Boolean)edit.putBoolean(key,(Boolean)value);else if(value instanceof Float)edit.putFloat(key,(Float)value);else if(value instanceof String)edit.putString(key,(String)value);else if(value instanceof Set)edit.putStringSet(key,(Set<String>)value);}edit.commit();}return result;
    }
}
