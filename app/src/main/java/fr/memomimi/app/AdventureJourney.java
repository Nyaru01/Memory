package fr.memomimi.app;

import android.graphics.*;

/** Cached world-space paths shared by footprints, Lumi and the camera. */
final class AdventureJourney {
    final Path[] paths=new Path[39];
    final PathMeasure[] measures=new PathMeasure[39];
    final float[] position=new float[2],tangent=new float[2];
    private float width=-1,density;
    void layout(float w,float d){
        if(width==w&&density==d)return;width=w;density=d;
        for(int i=0;i<39;i++){
            if(paths[i]==null){paths[i]=new Path();measures[i]=new PathMeasure();}
            AdventureRoute.fill(paths[i],i+1,w,d);measures[i].setPath(paths[i],false);
        }
    }
    void sample(int from,float fraction){PathMeasure measure=measures[from-1];measure.getPosTan(Math.max(0,Math.min(1,fraction))*measure.getLength(),position,tangent);}
}
