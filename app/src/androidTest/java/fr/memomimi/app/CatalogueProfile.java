package fr.memomimi.app;

import android.app.Instrumentation;
import android.os.*;
import android.view.*;
import android.graphics.Bitmap;
import java.io.*;
import java.util.*;

/** The same real swipe workload can run against the old APK and this version. */
final class CatalogueProfile {
    final Instrumentation host;final Bundle args;
    CatalogueProfile(Instrumentation host,Bundle args){this.host=host;this.args=args;}
    Bundle run(){
        Bundle result=new Bundle();HandlerThread metrics=new HandlerThread("catalogue-frame-metrics");metrics.start();
        android.content.SharedPreferences prefs=host.getTargetContext().getSharedPreferences("m",0);Map<String,?> saved=prefs.getAll();
        List<Long> frames=Collections.synchronizedList(new ArrayList<>()),draws=Collections.synchronizedList(new ArrayList<>());
        MainActivity activity=null;
        try{
            activity=(MainActivity)host.startActivitySync(new android.content.Intent(host.getTargetContext(),MainActivity.class).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK));MainActivity live=activity;GameView v=live.game;
            host.runOnMainSync(()->{live.updates.close();v.sound=false;v.pauseMusic();v.activityActive=true;v.calm=false;v.systemReducedMotion=false;v.screen=4;v.showResumePrompt=false;Arrays.fill(v.familyOpen,true);v.worldsScroll=0;v.invalidate();});
            Thread.sleep(350);v.worldSelector.awaitReady();Thread.sleep(400);
            String prefix=args.getString("capturePrefix","catalogue-profile");Bitmap capture=host.getUiAutomation().takeScreenshot();
            if(capture==null)throw new AssertionError("Capture unavailable");
            try(FileOutputStream out=new FileOutputStream(new File(host.getTargetContext().getExternalFilesDir(null),prefix+".png"))){capture.compress(Bitmap.CompressFormat.PNG,100,out);}capture.recycle();
            Window.OnFrameMetricsAvailableListener listener=(window,frame,dropped)->{frames.add(frame.getMetric(FrameMetrics.TOTAL_DURATION));draws.add(frame.getMetric(FrameMetrics.DRAW_DURATION));};
            host.runOnMainSync(()->live.getWindow().addOnFrameMetricsAvailableListener(listener,new Handler(metrics.getLooper())));
            float x=v.getWidth()*.45f,low=v.getHeight()*.8f,high=v.getHeight()*.3f;
            for(int swipe=0;swipe<10;swipe++){
                float start=swipe%2==0?low:high,end=swipe%2==0?high:low;long down=SystemClock.uptimeMillis();send(down,MotionEvent.ACTION_DOWN,x,start);
                for(int step=1;step<=30;step++){Thread.sleep(16);send(down,MotionEvent.ACTION_MOVE,x,start+(end-start)*step/30f);}
                send(down,MotionEvent.ACTION_UP,x,end);Thread.sleep(80);
            }
            Thread.sleep(200);host.runOnMainSync(()->live.getWindow().removeOnFrameMetricsAvailableListener(listener));
            Long[] total,draw; synchronized(frames){total=frames.toArray(new Long[0]);}synchronized(draws){draw=draws.toArray(new Long[0]);}Arrays.sort(total);Arrays.sort(draw);
            if(total.length<50||draw.length==0||v.screen!=4)throw new AssertionError("Missing frames or accidental navigation");
            int late=0;for(long ns:total)if(ns>16666667L)late++;
            String report=String.format(Locale.ROOT,"PASS: catalogue %dx%d density=%.2f layer=%d version=%s; %d frames; total median=%.2fms p95=%.2fms; draw median=%.2fms p95=%.2fms; >16.67ms=%d (%.1f%%). Ten real alternating swipes, warm preview cache=%dB. Emulator only, not phone FPS.\n",v.getWidth(),v.getHeight(),v.d,v.getLayerType(),host.getTargetContext().getPackageManager().getPackageInfo("fr.memomimi.app",0).versionName,total.length,total[total.length/2]/1e6,total[(int)(total.length*.95)]/1e6,draw[draw.length/2]/1e6,draw[(int)(draw.length*.95)]/1e6,late,late*100d/total.length,v.worldSelector.bytes());
            result.putString("stream",report);
        }catch(Throwable error){result.putString("stream","FAIL: "+error+"\n");}
        finally{
            if(activity!=null){MainActivity live=activity;host.runOnMainSync(live::finish);}host.waitForIdleSync();metrics.quitSafely();
            android.content.SharedPreferences.Editor edit=prefs.edit().clear();for(Map.Entry<String,?> e:saved.entrySet()){Object value=e.getValue();String key=e.getKey();if(value instanceof Integer)edit.putInt(key,(Integer)value);else if(value instanceof Long)edit.putLong(key,(Long)value);else if(value instanceof Boolean)edit.putBoolean(key,(Boolean)value);else if(value instanceof Float)edit.putFloat(key,(Float)value);else if(value instanceof String)edit.putString(key,(String)value);else if(value instanceof Set)edit.putStringSet(key,(Set<String>)value);}edit.commit();
        }return result;
    }
    void send(long down,int action,float x,float y){MotionEvent e=MotionEvent.obtain(down,SystemClock.uptimeMillis(),action,x,y,0);host.sendPointerSync(e);e.recycle();}
}
