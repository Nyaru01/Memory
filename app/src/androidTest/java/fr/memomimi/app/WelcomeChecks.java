package fr.memomimi.app;

import android.app.Instrumentation;
import android.graphics.Bitmap;
import android.os.Bundle;
import java.io.*;

/** Attached startup captures and checks, preserving the real timed exit to home. */
final class WelcomeChecks {
    final Instrumentation host;int checks;
    WelcomeChecks(Instrumentation host){this.host=host;}
    Bundle run(){
        Bundle result=new Bundle();MainActivity activity=null;
        try{
            android.content.Intent intent=new android.content.Intent(host.getTargetContext(),MainActivity.class).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
            activity=(MainActivity)host.startActivitySync(intent);MainActivity live=activity;GameView v=live.game;
            host.runOnMainSync(()->{live.updates.close();v.sound=false;v.pauseMusic();v.activityActive=true;v.calm=false;v.systemReducedMotion=false;});
            for(int age:new int[]{180,650,1030,1400}){
                host.runOnMainSync(()->{v.screen=-1;v.splashStart=GameView.now()-age;v.invalidate();});
                Thread.sleep(75);
                Bitmap image=host.getUiAutomation().takeScreenshot();require(image!=null,"Actual startup capture");
                try(FileOutputStream out=new FileOutputStream(new File(host.getTargetContext().getExternalFilesDir(null),"welcome-"+age+".png"))){image.compress(Bitmap.CompressFormat.PNG,100,out);}image.recycle();
                host.runOnMainSync(()->{require(!v.celebration.active,"Loading never starts victory");require(v.splashMascotArea.bottom<v.splashBadge.top,"Character and copy do not overlap");require(v.victoryMascot.animation.lift==0 && v.victoryMascot.animation.tilt==0,"Feet remain grounded");});
            }
            host.runOnMainSync(()->{v.calm=true;v.screen=-1;v.splashStart=GameView.now()-1100;v.invalidate();});Thread.sleep(90);
            host.runOnMainSync(()->{require(v.victoryMascot.animation.frame==0 && v.victoryMascot.animation.scaleY==1,"Reduced welcome stays still");v.splashStart=GameView.now()-1800;v.invalidate();});Thread.sleep(90);
            host.runOnMainSync(()->require(v.screen==0,"Loading exits to home without extra wait"));
            result.putString("stream","PASS: "+checks+" attached welcome checks, startup captures, grounded character, reduced motion, automatic home. Captures do not constitute video review.\n");
        }catch(Throwable error){result.putString("stream","FAIL: "+error+"\n");}
        finally{if(activity!=null){MainActivity live=activity;host.runOnMainSync(live::finish);}}
        return result;
    }
    void require(boolean value,String label){if(!value)throw new AssertionError(label);checks++;}
}
