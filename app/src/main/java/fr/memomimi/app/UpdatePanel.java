package fr.memomimi.app;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/** Native, accessible controls styled like the Canvas game, including small screens. */
final class UpdatePanel {
    static final int INK = 0xff403743, CORAL = 0xffff6a50, PURPLE = 0xff8064be, CREAM = 0xfffff8ec;
    static int dp(Activity activity, float value) { return Math.round(value * activity.getResources().getDisplayMetrics().density); }
    static GradientDrawable background(Activity activity, int color, int border, int radius) {
        GradientDrawable shape = new GradientDrawable(); shape.setColor(color); shape.setCornerRadius(dp(activity, radius));
        if (border != 0) shape.setStroke(dp(activity, 1), border);
        return shape;
    }
    static TextView text(Activity activity, String value, int size, int color, boolean bold) {
        TextView text = new TextView(activity); text.setText(value); text.setTextSize(size); text.setTextColor(color);
        if (bold) text.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        text.setGravity(Gravity.CENTER); return text;
    }
    static Button button(Activity activity, String label, boolean primary) {
        Button button = new Button(activity); button.setText(label); button.setAllCaps(false); button.setTextSize(14);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD); button.setTextColor(primary ? Color.WHITE : PURPLE);
        button.setBackground(background(activity, primary ? CORAL : Color.WHITE, primary ? 0 : 0xffded5ef, 18));
        button.setMinHeight(dp(activity, 48)); button.setMinimumHeight(dp(activity, 48));
        button.setPadding(dp(activity, 10), dp(activity, 8), dp(activity, 10), dp(activity, 8));
        button.setElevation(dp(activity, primary ? 3 : 1));
        button.setStateListAnimator(null);
        button.setOnTouchListener((view, event) -> {
            if (event.getActionMasked() == android.view.MotionEvent.ACTION_DOWN) view.setAlpha(0.78f);
            else if (event.getActionMasked() == android.view.MotionEvent.ACTION_UP || event.getActionMasked() == android.view.MotionEvent.ACTION_CANCEL) view.setAlpha(1f);
            return false;
        });
        return button;
    }
    static void gap(Activity activity, LinearLayout layout, int height) {
        layout.addView(new View(activity), new LinearLayout.LayoutParams(1, dp(activity, height)));
    }
    static Dialog show(Activity activity, String version, Runnable install) {
        return dialog(activity, "↑", "Une mise à jour est prête !", "VERSION " + version,
                "Installe la nouvelle version quand tu veux.", "✓  Ta progression est conservée", "Installer", install, () -> { });
    }
    static Dialog permission(Activity activity, Runnable settings, Runnable later) {
        return dialog(activity, "✓", "Encore une petite étape", "AUTORISATION ANDROID",
                "Autorise MémoMondes à installer sa mise à jour dans les réglages Android, puis reviens au jeu.",
                "✓  Le jeu et ton album restent sur l’appareil", "Réglages", settings, later);
    }
    static Dialog dialog(Activity activity, String symbol, String title, String badge, String description,
                         String reassurance, String action, Runnable positive, Runnable negative) {
        Dialog dialog = new Dialog(activity); dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout content = new LinearLayout(activity); content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER_HORIZONTAL); content.setPadding(dp(activity, 22), dp(activity, 24), dp(activity, 22), dp(activity, 22));
        content.setBackground(background(activity, CREAM, 0, 28));
        TextView icon = text(activity, symbol, 34, PURPLE, true);
        icon.setBackground(background(activity, 0xffe8e0f5, 0, 24));
        content.addView(icon, new LinearLayout.LayoutParams(dp(activity, 56), dp(activity, 56)));
        gap(activity, content, 16);
        TextView heading = text(activity, title, 23, INK, true); if (android.os.Build.VERSION.SDK_INT >= 28) heading.setAccessibilityHeading(true);
        content.addView(heading); gap(activity, content, 12);
        TextView version = text(activity, badge, 12, PURPLE, true);
        version.setPadding(dp(activity, 14), dp(activity, 8), dp(activity, 14), dp(activity, 8));
        version.setBackground(background(activity, 0xffede6f7, 0, 12)); content.addView(version);
        gap(activity, content, 18);
        TextView body = text(activity, description, 15, INK, false); body.setLineSpacing(dp(activity, 3), 1f); content.addView(body);
        gap(activity, content, 14);
        TextView keep = text(activity, reassurance, 12, 0xff287c69, true); keep.setPadding(dp(activity, 8), dp(activity, 10), dp(activity, 8), dp(activity, 10));
        keep.setBackground(background(activity, 0xffdcf1e9, 0, 14)); content.addView(keep);
        gap(activity, content, 22);
        LinearLayout actions = new LinearLayout(activity);
        Button later = button(activity, "Plus tard", false), update = button(activity, action, true);
        actions.addView(later, new LinearLayout.LayoutParams(0, dp(activity, 50), 1));
        actions.addView(new View(activity), new LinearLayout.LayoutParams(dp(activity, 10), 1));
        actions.addView(update, new LinearLayout.LayoutParams(0, dp(activity, 50), 1));
        content.addView(actions, new LinearLayout.LayoutParams(-1, -2));
        later.setOnClickListener(view -> { dialog.dismiss(); negative.run(); });
        update.setOnClickListener(view -> { dialog.dismiss(); positive.run(); });
        dialog.setOnCancelListener(ignored -> negative.run());
        ScrollView scroll = new ScrollView(activity); scroll.setFillViewport(false); scroll.setClipToPadding(false); scroll.addView(content);
        dialog.setContentView(scroll); dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams params = window.getAttributes(); params.dimAmount = 0.42f;
            window.setAttributes(params);
            int width = Math.min(activity.getResources().getDisplayMetrics().widthPixels - dp(activity, 32), dp(activity, 360));
            window.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        }
        return dialog;
    }
}
