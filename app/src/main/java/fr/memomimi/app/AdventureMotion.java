package fr.memomimi.app;

/** Map-only animation state. It never writes or unlocks progression. */
final class AdventureMotion {
    static final long VALIDATE_MS=600,DEPART_MS=1200,MOVE_MS=2600,TRAVEL_MS=DEPART_MS+MOVE_MS,CELEBRATE_MS=1900;
    boolean validating(long now){return travelling(now)&&now-startedAt<VALIDATE_MS;}
    boolean unlocking(long now){return travelling(now)&&now-startedAt>=VALIDATE_MS&&now-startedAt<DEPART_MS;}
    int pendingFrom, from, to;
    long startedAt, reactionAt=-1, lastFrame;
    float velocity;
    void completed(int level) { if(level>=1 && level<40) pendingFrom=level; }
    void open(int current,long now,boolean reduced) {
        from=pendingFrom>=1 && pendingFrom==current-1 ? pendingFrom : current;to=current;
        pendingFrom=0;startedAt=now;reactionAt=-1;velocity=0;lastFrame=now;
        if(reduced)from=to;
    }
    float progress(long now) {
        float t=Math.max(0,Math.min(1,(now-startedAt-DEPART_MS)/(float)MOVE_MS));
        return t*t*(3-2*t);
    }
    boolean travelling(long now) {return from>=1 && to==from+1 && to<=40 && now-startedAt<TRAVEL_MS;}
    long arrival(long now){return from>=1 && to==from+1 && to<=40 && now-startedAt>=TRAVEL_MS && now-startedAt<TRAVEL_MS+CELEBRATE_MS ? now-startedAt-TRAVEL_MS : -1;}
    void cancelTravel() {from=to;}
    void stop() {velocity=0;cancelTravel();}
}
