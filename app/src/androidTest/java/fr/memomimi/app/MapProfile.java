package fr.memomimi.app;

import android.app.Instrumentation;
import android.graphics.Bitmap;
import android.os.*;
import android.view.*;
import java.io.*;
import java.util.*;

/** Identical attached-view swipe workload usable against the previous and current APK. */
final class MapProfile {
    final Instrumentation host;final Bundle args;
    MapProfile(Instrumentation host,Bundle args){this.host=host;this.args=args;}
    Bundle run(){
        Bundle result=new Bundle();HandlerThread metrics=new HandlerThread("map-frame-metrics");
        metrics.start();MainActivity activity=null;
        List<Long> frames=Collections.synchronizedList(new ArrayList<>());
        List<Long> draws=Collections.synchronizedList(new ArrayList<>());
        try {
            android.content.Intent intent=new android.content.Intent(host.getTargetContext(),MainActivity.class).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
            activity=(MainActivity)host.startActivitySync(intent);MainActivity live=activity;
            int previous=host.getTargetContext().getSharedPreferences("m",0).getInt("adventure_completed",0);
            host.runOnMainSync(()->{live.updates.close();live.game.sound=false;live.game.pauseMusic();live.game.calm=false;live.game.systemReducedMotion=false;host.getTargetContext().getSharedPreferences("m",0).edit().putInt("adventure_completed",0).commit();live.game.openAdventure();});
            Thread.sleep(1800);
            Bitmap capture=host.getUiAutomation().takeScreenshot();
            String prefix=args.getString("capturePrefix","map-profile");
            try(FileOutputStream out=new FileOutputStream(new File(host.getTargetContext().getExternalFilesDir(null),prefix+".png"))){capture.compress(Bitmap.CompressFormat.PNG,100,out);}capture.recycle();
            Window.OnFrameMetricsAvailableListener listener=(window,frame,dropped)->{frames.add(frame.getMetric(FrameMetrics.TOTAL_DURATION));draws.add(frame.getMetric(FrameMetrics.DRAW_DURATION));};
            host.runOnMainSync(()->live.getWindow().addOnFrameMetricsAvailableListener(listener,new Handler(metrics.getLooper())));
            float width=live.game.getWidth(),height=live.game.getHeight();
            for(int swipe=0;swipe<12;swipe++){
                float start=height*.72f,end=height*.32f;
                long down=SystemClock.uptimeMillis();
                send(down,MotionEvent.ACTION_DOWN,width*.45f,start);
                for(int step=1;step<=40;step++){Thread.sleep(16);send(down,MotionEvent.ACTION_MOVE,width*.45f,start+(end-start)*step/40f);}
                send(down,MotionEvent.ACTION_UP,width*.45f,end);Thread.sleep(160);
            }
            Thread.sleep(400);host.runOnMainSync(()->live.getWindow().removeOnFrameMetricsAvailableListener(listener));
            Long[] values; synchronized(frames){values=frames.toArray(new Long[0]);}Arrays.sort(values);
            if(values.length<100)throw new AssertionError("Too few rendered frames: "+values.length);
            int late=0;for(long ns:values)if(ns>16666667L)late++;
            String report=String.format(Locale.ROOT,"PASS: attached Canvas %dx%d density=%.2f layer=%d; %d frames; median=%.2fms p95=%.2fms >16.67ms=%d (%.1f%%); 12 real injected swipes, emulator, includes image uploads.\n",live.game.getWidth(),live.game.getHeight(),live.game.d,live.game.getLayerType(),values.length,values[values.length/2]/1e6,values[(int)(values.length*.95)]/1e6,late,late*100d/values.length);
            Long[] drawValues; synchronized(draws){drawValues=draws.toArray(new Long[0]);}Arrays.sort(drawValues);
            report+=String.format(Locale.ROOT,"Canvas recording: median=%.2fms p95=%.2fms; actual hardware canvas=%s; tile memory=%dB, creatures=%dB. Total duration includes synchronization and is not a phone FPS guarantee.\n",drawValues[drawValues.length/2]/1e6,drawValues[(int)(drawValues.length*.95)]/1e6,live.game.adventureMap.hardwareCanvas,live.game.adventureMap.scenery.retainedBytes(),live.game.adventureMap.decor.bytes());
            if(!live.game.adventureMap.hardwareCanvas)throw new AssertionError("Map is not hardware rendered");
            try(FileWriter out=new FileWriter(new File(host.getTargetContext().getExternalFilesDir(null),prefix+".txt"))){out.write(report);}
            host.runOnMainSync(()->{if(live.game.screen!=7)throw new AssertionError("Map navigated unexpectedly");host.getTargetContext().getSharedPreferences("m",0).edit().putInt("adventure_completed",previous).commit();live.finish();});
            result.putString("stream",report);
        } catch(Throwable failure){result.putString("stream","FAIL: "+failure+"\n");if(activity!=null){MainActivity live=activity;host.runOnMainSync(live::finish);}}
        finally{metrics.quitSafely();}
        return result;
    }
    void send(long down,int action,float x,float y){MotionEvent e=MotionEvent.obtain(down,SystemClock.uptimeMillis(),action,x,y,0);host.sendPointerSync(e);e.recycle();}
}
