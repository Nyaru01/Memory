package fr.memomimi.app;

import android.content.Context;
import android.content.SharedPreferences;

/** Append-only route and independent sequential progress. Level numbers are one-based. */
final class AdventureStore {
    static final int LEVEL_COUNT=40;
    private static final int[] ROUTE={0,4,19,8,3,11,1,7,10,17,2,6,14,9,15,16,5,12,13,18};
    private final SharedPreferences prefs;
    AdventureStore(Context context) { prefs=context.getSharedPreferences("m",0); }
    int completed() { return Math.max(0,Math.min(LEVEL_COUNT,prefs.getInt("adventure_completed",0))); }
    int current() { return Math.min(LEVEL_COUNT,completed()+1); }
    boolean unlocked(int level) { return level>=1 && level<=LEVEL_COUNT && level<=completed()+1; }
    boolean complete(int level) {
        if(level!=completed()+1 || level>LEVEL_COUNT) return false;
        return prefs.edit().putInt("adventure_completed",level).commit();
    }
    boolean reset() {
        SharedPreferences.Editor edit=prefs.edit().putInt("adventure_completed",0);
        // Keep any free-play resume, collections, statistics and parent settings.
        if(prefs.getInt("saved_adventure_level",0)>0) {
            edit.putBoolean("game_active",false).remove("saved_adventure_level")
                    .remove("saved_cards").remove("saved_matched").remove("saved_owner1")
                    .remove("saved_owner2").remove("saved_first");
        }
        return edit.commit();
    }
    static int world(int level) { return ROUTE[(level-1)/2]; }
    static int pairs(int level) { return level<=6 ? 6 : level<=16 ? 8 : level<=28 ? 10 : 12; }
    static int island(int level) { return WorldCatalog.familyOf(world(level)); }
}
