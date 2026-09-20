package fr.memomimi.app;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

/* JADX INFO: loaded from: classes.dex */
public class MainActivity extends Activity {
    GameView game;

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setStatusBarColor(Color.rgb(255, 248, 236));
        getWindow().getDecorView().setSystemUiVisibility(8192);
        GameView gameView = new GameView(this);
        this.game = gameView;
        setContentView(gameView);
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        GameView gameView = this.game;
        if (gameView != null) {
            gameView.saveForPause();
            this.game.pauseMusic();
            this.game.pauseNarration();
        }
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        GameView gameView = this.game;
        if (gameView != null) {
            gameView.resumeMusic();
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        GameView gameView = this.game;
        if (gameView != null) {
            gameView.releaseNarration();
        }
        super.onDestroy();
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        GameView gameView = this.game;
        if (gameView == null || !gameView.handleBack()) {
            super.onBackPressed();
        }
    }
}
