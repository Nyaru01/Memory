package fr.memomimi.app;

import android.content.Context;
import android.content.SharedPreferences;

/* JADX INFO: loaded from: classes.dex */
final class ProgressStore {
    private final SharedPreferences prefs;

    ProgressStore(Context context) {
        this.prefs = context.getSharedPreferences("m", 0);
        migrate();
    }

    SharedPreferences prefs() {
        return this.prefs;
    }

    private void migrate() {
        int i = this.prefs.getInt("data_version", 1);
        SharedPreferences.Editor editorEdit = this.prefs.edit();
        if (i < 5 && !this.prefs.contains("completed_games")) {
            editorEdit.putInt("completed_games", this.prefs.getInt("wins", 0));
        }
        editorEdit.putInt("data_version", 5).apply();
    }

    void recordCompleted(int i, int i2) {
        SharedPreferences.Editor editorPutInt = this.prefs.edit().putInt("completed_games", completedGames() + 1).putInt("level_count_" + i2, this.prefs.getInt("level_count_" + i2, 0) + 1);
        SharedPreferences sharedPreferences = this.prefs;
        editorPutInt.putInt("played_worlds", (1 << i) | sharedPreferences.getInt("played_worlds", sharedPreferences.getInt("won_worlds", 0))).apply();
    }

    int completedGames() {
        SharedPreferences sharedPreferences = this.prefs;
        return sharedPreferences.getInt("completed_games", sharedPreferences.getInt("wins", 0));
    }

    int exploredWorlds() {
        SharedPreferences sharedPreferences = this.prefs;
        return Integer.bitCount(sharedPreferences.getInt("played_worlds", sharedPreferences.getInt("won_worlds", 0)));
    }

    int preferredPairs() {
        int i = 6;
        int[] iArr = {6, 8, 10, 12};
        int i2 = -1;
        for (int i3 = 0; i3 < 4; i3++) {
            int i4 = iArr[i3];
            int i5 = this.prefs.getInt("level_count_" + i4, 0);
            if (i5 > i2) {
                i = i4;
                i2 = i5;
            }
        }
        return i;
    }

    void resetStatsAndRecords() {
        SharedPreferences.Editor editorPutInt = this.prefs.edit().remove("best_6").remove("best_8").remove("best_10").remove("best_12").remove("best_15").putInt("completed_games", 0).putInt("played_worlds", 0);
        int[] iArr = {6, 8, 10, 12, 15};
        for (int i = 0; i < 5; i++) {
            editorPutInt.remove("level_count_" + iArr[i]);
        }
        editorPutInt.apply();
    }
}
