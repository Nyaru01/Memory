package fr.memomimi.app;

import android.content.Context;
import android.content.SharedPreferences;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk={28,35})
public class SavedGameTest {
    Context context; SharedPreferences prefs; AdventureStore adventure;
    @Before public void setup() {
        context=RuntimeEnvironment.getApplication(); prefs=context.getSharedPreferences("m",0);
        prefs.edit().clear().putInt("saved_pairs",3).putString("saved_cards","2,0,1,2,1,0")
                .putInt("album_0",63).putInt("best_6",10).putInt("adventure_completed",4)
                .putBoolean("haptics",false).putBoolean("game_active",true).commit();
        adventure=new AdventureStore(context);
    }
    SavedGame read() { return new SavedGame(prefs,adventure); }
    void rejected() {
        try { read(); fail("Corrupt save accepted"); } catch(IllegalArgumentException expected) { }
    }
    @Test public void validDeckAndLegacyDuelKeepTheirFormat() {
        assertArrayEquals(new int[]{2,0,1,2,1,0},read().cards);
        prefs.edit().putInt("saved_players",2).putLong("saved_matched",43)
                .putInt("saved_found",2).putInt("saved_moves",3)
                .putInt("saved_score1",1).putInt("saved_score2",1).commit();
        SavedGame saved=read();
        assertArrayEquals(new int[]{1,2,0,1,0,2},saved.owners);
        prefs.edit().putLong("saved_owner1",9).putLong("saved_owner2",34).commit();
        assertArrayEquals(saved.owners,read().owners);
    }
    @Test public void invalidDecksAreRejectedBeforeAllocation() {
        for(int pairs:new int[]{-1,0,1,7,16,Integer.MAX_VALUE}) {
            prefs.edit().putInt("saved_pairs",pairs).commit(); rejected();
        }
        prefs.edit().putInt("saved_pairs",3).commit();
        for(String cards:new String[]{"", "0,0,1,1,2", "0,0,1,1,2,2,", "0,0,0,1,2,2", "-1,0,1,1,2,2", "3,0,1,1,2,2", "x,0,1,1,2,2"}) {
            prefs.edit().putString("saved_cards",cards).commit(); rejected();
        }
    }
    @Test public void partialPairsAndInvalidMasksAreRejected() {
        for(long mask:new long[]{1,64,-1}) { prefs.edit().putLong("saved_matched",mask).commit(); rejected(); }
        prefs.edit().putLong("saved_matched",0).putLong("saved_owner1",1).commit(); rejected();
    }
    @Test public void invalidScalarsAndTypesAreRejected() {
        String[] keys={"saved_players","saved_theme","saved_turn","saved_score1","saved_score2","saved_found","saved_moves","saved_errors","saved_adventure_level"};
        for(String key:keys) {
            prefs.edit().putInt(key,-1).commit(); rejected(); prefs.edit().remove(key).commit();
        }
        prefs.edit().putInt("saved_found",1).commit(); rejected(); prefs.edit().remove("saved_found").commit();
        prefs.edit().putString("saved_pairs","six").commit();
        try { read(); fail(); } catch(ClassCastException expected) { }
    }
    @Test public void duelOwnersMustAgreeWithPairsAndScores() {
        prefs.edit().putInt("saved_players",2).putLong("saved_matched",9)
                .putInt("saved_found",1).putInt("saved_moves",1).putInt("saved_score1",1)
                .putLong("saved_owner1",1).putLong("saved_owner2",8).commit(); rejected();
        prefs.edit().putLong("saved_owner1",9).putLong("saved_owner2",9).commit(); rejected();
        prefs.edit().putLong("saved_owner1",0).putLong("saved_owner2",9).commit(); rejected();
    }
    @Test public void corruptResumePreservesCollectionsAndRawSave() {
        GameView view=new GameView(context);
        try {
            int[] previous=view.cards;
            prefs.edit().putInt("saved_pairs",Integer.MAX_VALUE).commit(); view.loadGame();
            assertEquals(0,view.screen); assertSame(previous,view.cards);
            assertFalse(prefs.getBoolean("game_active",true));
            assertEquals("2,0,1,2,1,0",prefs.getString("saved_cards",""));
            assertEquals(63,prefs.getInt("album_0",0)); assertEquals(10,prefs.getInt("best_6",0));
            assertEquals(4,prefs.getInt("adventure_completed",0)); assertFalse(prefs.getBoolean("haptics",true));
        } finally { view.releaseAudio(); }
    }
}
