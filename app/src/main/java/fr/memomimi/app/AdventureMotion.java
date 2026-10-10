package fr.memomimi.app;

/** Map-only animation state. It never writes or unlocks progression. */
final class AdventureMotion {
    static final long TRAVEL_MS=2200;
    int pendingFrom, from, to;
    long startedAt, reactionAt=-1, lastFrame;
    float velocity;
    void completed(int level) { if(level<40) pendingFrom=level; }
    void open(int current,long now,boolean reduced) {
        from=pendingFrom==current-1 ? pendingFrom : current;to=current;
        pendingFrom=0;startedAt=now;reactionAt=-1;velocity=0;lastFrame=now;
        if(reduced)from=to;
    }
    float progress(long now) {
        float t=Math.max(0,Math.min(1,(now-startedAt)/(float)TRAVEL_MS));
        return t*t*(3-2*t);
    }
    boolean travelling(long now) {return from!=to && now-startedAt<TRAVEL_MS;}
    void cancelTravel() {from=to;}
    void stop() {velocity=0;cancelTravel();}
}
