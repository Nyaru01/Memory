package fr.memomimi.app;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

/* JADX INFO: loaded from: classes.dex */
public class MainActivity extends Activity {
    GameView game;
    UpdateManager updates;
    android.widget.Button updateButton;

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setStatusBarColor(Color.rgb(255, 248, 236));
        getWindow().getDecorView().setSystemUiVisibility(8192);
        GameView gameView = new GameView(this);
        this.game = gameView;
        android.widget.FrameLayout layout = new android.widget.FrameLayout(this);
        android.widget.FrameLayout.LayoutParams gameLayout = new android.widget.FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT, android.widget.FrameLayout.LayoutParams.MATCH_PARENT);
        layout.addView(gameView, gameLayout);
        android.widget.LinearLayout updateCard = new android.widget.LinearLayout(this);
        updateCard.setGravity(android.view.Gravity.CENTER_VERTICAL);
        updateCard.setPadding(UpdatePanel.dp(this, 14), UpdatePanel.dp(this, 10), UpdatePanel.dp(this, 14), UpdatePanel.dp(this, 10));
        updateCard.setBackground(UpdatePanel.background(this, UpdatePanel.CREAM, 0xffd8d0e6, 22));
        updateCard.setElevation(UpdatePanel.dp(this, 4));
        android.widget.LinearLayout copy = new android.widget.LinearLayout(this);
        copy.setOrientation(android.widget.LinearLayout.VERTICAL);
        android.widget.TextView updateTitle = UpdatePanel.text(this, "Mise à jour prête", 14, UpdatePanel.INK, true);
        android.widget.TextView updateDetail = UpdatePanel.text(this, "Progression conservée", 11, 0xff287c69, false);
        updateTitle.setGravity(android.view.Gravity.START);
        updateDetail.setGravity(android.view.Gravity.START);
        copy.addView(updateTitle); copy.addView(updateDetail);
        updateCard.addView(copy, new android.widget.LinearLayout.LayoutParams(0, -2, 1));
        updateButton = UpdatePanel.button(this, "Installer", true);
        updateButton.setContentDescription("Installer la mise à jour de MémoMondes");
        android.widget.LinearLayout.LayoutParams action = new android.widget.LinearLayout.LayoutParams(UpdatePanel.dp(this, 92), UpdatePanel.dp(this, 48));
        action.leftMargin = UpdatePanel.dp(this, 10);
        updateCard.addView(updateButton, action);
        updateCard.setVisibility(android.view.View.GONE);
        android.widget.FrameLayout.LayoutParams cardLayout = new android.widget.FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT, android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                android.view.Gravity.BOTTOM);
        cardLayout.setMargins(UpdatePanel.dp(this, 16), 0, UpdatePanel.dp(this, 16), UpdatePanel.dp(this, 30));
        layout.addView(updateCard, cardLayout);
        layout.setBackgroundColor(0xffe0f1e9);
        setContentView(layout);
        updates = new UpdateManager(this, () -> {
            boolean ready = updates.ready != null;
            updateCard.setVisibility(ready ? android.view.View.VISIBLE : android.view.View.GONE);
            if (ready) updateTitle.setText("Version " + updates.ready.optString("versionName") + " prête");
            gameLayout.bottomMargin = ready ? UpdatePanel.dp(this, 108) : 0;
            gameView.setLayoutParams(gameLayout);
        });
        updateButton.setOnClickListener(view -> updates.show());
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        if (updates != null) updates.pause();
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
        if (updates != null) updates.resume();
        GameView gameView = this.game;
        if (gameView != null) {
            gameView.resumeMusic();
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        if (updates != null) updates.close();
        GameView gameView = this.game;
        if (gameView != null) {
            gameView.releaseAudio();
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
