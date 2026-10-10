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
            host.runOnMainSync(()->{v.screen=-1;v.splashStart=GameView.now();v.invalidate();});
            long deadline=GameView.now()+3500;
            while(!v.welcomeScreen.hasArtwork() && GameView.now()<deadline)Thread.sleep(20);
            require(v.welcomeScreen.hasArtwork(),"Dedicated illustration loaded off UI");require(v.welcomeScreen.bytes()<=4*1024*1024,"Welcome bitmap memory bounded");
            for(int age:new int[]{180,1050,1550,2400,4200}){
                host.runOnMainSync(()->{v.screen=-1;v.splashStart=GameView.now()-age;v.invalidate();});
                Thread.sleep(75);
                host.runOnMainSync(()->{require(!v.celebration.active,"Loading never starts victory");require(v.splashMascotArea.bottom<v.splashBadge.top,"Character and copy do not overlap");require(v.welcomeScreen.greeting!=null && v.welcomeScreen.greeting.atlas.getAllocationByteCount()<=8*1024*1024,"Dedicated paw atlas bounded");});
                Bitmap image=host.getUiAutomation().takeScreenshot();require(image!=null,"Actual startup capture");
                try(FileOutputStream out=new FileOutputStream(new File(host.getTargetContext().getExternalFilesDir(null),"welcome-"+age+".png"))){image.compress(Bitmap.CompressFormat.PNG,100,out);}image.recycle();

            }
            host.runOnMainSync(()->{v.calm=true;v.screen=-1;v.splashStart=GameView.now();v.invalidate();});
            long reducedDeadline=GameView.now()+3500;while(v.welcomeScreen.greeting==null && GameView.now()<reducedDeadline)Thread.sleep(20);
            require(v.welcomeScreen.greeting!=null,"Reduced restart reloads released atlas");
            host.runOnMainSync(()->{v.splashStart=GameView.now()-1100;v.invalidate();});Thread.sleep(90);
            host.runOnMainSync(()->{require(v.welcomeScreen.greeting.frame==0,"Reduced welcome stays still");v.splashStart=GameView.now()-5100;v.invalidate();});Thread.sleep(90);
            host.runOnMainSync(()->require(v.screen==0,"Loading exits to home after complete welcome"));
            result.putString("stream","PASS: "+checks+" attached welcome checks, startup captures, grounded character, reduced motion, automatic home. Captures do not constitute video review.\n");
        }catch(Throwable error){result.putString("stream","FAIL: "+error+"\n");}
        finally{if(activity!=null){MainActivity live=activity;host.runOnMainSync(live::finish);}}
        return result;
    }
    void require(boolean value,String label){if(!value)throw new AssertionError(label);checks++;}
}
