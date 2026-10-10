package fr.memomimi.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import java.io.InputStream;
import java.util.Locale;
import java.util.Random;

/* JADX INFO: compiled from: MainActivity.java */
/* JADX INFO: loaded from: classes.dex */
class GameView extends View {
    static long now() { return android.os.SystemClock.uptimeMillis(); }
    final VictoryCelebration celebration = new VictoryCelebration();
    final VictoryMascot victoryMascot = new VictoryMascot();
    final RectF victoryArea = new RectF();
    final RectF splashPanel = new RectF(), splashMascotArea = new RectF();
    final RectF splashBadge = new RectF(), splashBar = new RectF(), splashFill = new RectF();
    boolean systemReducedMotion;
    RectF pressedTarget;
    LinearGradient worldGradient;
    float backgroundWidth, backgroundHeight;
    int backgroundTheme = -1;
    final Path backgroundWave = new Path();
    final Path cardShinePath = new Path();
    int matchFeedbackGeneration;
    volatile AudioTrack activeSound;
    final java.util.concurrent.ThreadPoolExecutor soundExecutor = new java.util.concurrent.ThreadPoolExecutor(
            1, 1, 0L, java.util.concurrent.TimeUnit.MILLISECONDS,
            new java.util.concurrent.ArrayBlockingQueue<Runnable>(4),
            runnable -> { Thread thread = new Thread(runnable, "MemoMondes-audio"); thread.setDaemon(true); return thread; },
            new java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy());
    final GameAccessibility accessible = new GameAccessibility(this);
    boolean animationsReduced() { return this.calm || this.systemReducedMotion; }

    @Override public android.view.accessibility.AccessibilityNodeProvider getAccessibilityNodeProvider() {
        return this.accessible;
    }

    @Override public boolean dispatchHoverEvent(MotionEvent event) {
        return this.accessible.hover(event) || super.dispatchHoverEvent(event);
    }

    @Override public boolean performClick() {
        super.performClick();
        return true;
    }
    final RectF[] cardBounds = new RectF[30];
    final RectF[] albumImageBounds = new RectF[12];
    final RectF cardImageBounds = new RectF();
    final RectF cardBorderBounds = new RectF();
    int[] themeColors;
    static final String[] THEME_NAMES = {"ANIMAUX", "OCÉAN", "ESPACE", "DINOSAURES", "FERME", "VÉHICULES", "MAGIE", "SIRÈNES", "JUNGLE", "GOURMANDISES", "PIRATES", "BANQUISE", "MUSIQUE", "MÉTIERS", "FÉES", "MAISON", "LÉGUMES", "DÉTECTIVE"};
    static final long MATCH_DURATION_MS = 1050L;
    long pendingWinAt;
    static final int ALBUM_TOTAL = 216;
    static final int WORLD_COUNT = 18;
    final RectF[] themeTiles = new RectF[WORLD_COUNT];
    final Bitmap[] themeStickers = new Bitmap[WORLD_COUNT];
    final RectF worldsTab = new RectF();
    float H;
    float W;
    boolean activityActive;
    RectF albumBtn;
    RectF albumNext;
    int albumPage;
    RectF albumPrev;
    int albumReturnScreen;
    int albumTab;
    RectF animalsTheme;
    RectF arcticTheme;
    final String[] assetNames;
    MediaPlayer backgroundMusic;
    RectF badgesTab;
    final int bg;
    boolean calm;
    RectF calmBtn;
    int[] cards;
    String[] cheers;
    RectF chooseTheme;
    int closeA;
    int closeB;
    long closeStart;
    int cols;
    RectF coopPlayer;
    final int coral;
    float d;
    RectF dinoTheme;
    String earnedBadge;
    RectF easy;
    RectF enlargedClose;
    int enlargedItem;
    int errors;
    RectF expert;
    RectF farmTheme;
    int first;
    int flipIndex;
    long flipStart;
    int found;
    RectF hapticBtn;
    boolean haptics;
    RectF hard;
    long hideAt;
    int hintA;
    int hintB;
    RectF hintBtn;
    long hintUntil;
    boolean hintUsed;
    RectF homeBtn;
    RectF imagesTab;
    final int ink;
    RectF jobsTheme;
    RectF jungleTheme;
    int lastA;
    int lastB;
    RectF longDelay;
    RectF magicTheme;
    boolean[] matched;
    RectF medium;
    RectF mega;
    RectF mermaidTheme;
    final int mint;
    int mismatchDelay;
    int moves;
    final String[] musicAssets;
    int musicIndex;
    RectF musicTheme;
    MediaPlayer narration;
    RectF newGameBtn;
    boolean newRecord;
    int newUnlocks;
    RectF normalDelay;
    RectF oceanTheme;
    RectF onePlayer;
    int[] owners;
    final Paint p;
    int pairs;
    RectF parentBtn;
    boolean parentHolding;
    String parentNotice;
    RectF pirateTheme;
    RectF play;
    int players;
    long previewUntil;
    ProgressStore progress;
    final int purple;
    RectF quitConfirmBtn;
    RectF quitContinueBtn;
    RectF resetBtn;
    RectF resetCancelBtn;
    RectF resetConfirmBtn;
    long resetDoneUntil;
    boolean resumeAvailable;
    RectF resumeBtn;
    final Random rng;
    int rows;
    int score1;
    int score2;
    int screen;
    int second;
    Bitmap[] sheets;
    RectF shortDelay;
    boolean showQuit;
    boolean showResetConfirm;
    boolean showResumePrompt;
    boolean sound;
    RectF soundBtn;
    RectF spaceTheme;
    long splashStart;
    RectF storyBtn;
    RectF storyPlayBtn;
    boolean storyPlaying;
    long successUntil;
    int suggestedPairs;
    boolean switchPending;
    final Paint text;
    int theme;
    RectF treatsTheme;
    TextToSpeech tts;
    int turn;
    boolean tutorialActive;
    RectF tutorialDoneBtn;
    RectF tutorialResetBtn;
    int tutorialStep;
    RectF twoPlayers;
    RectF vehiclesTheme;
    long winAt;

    GameView(Context context) {
        super(context);
        this.victoryMascot.load(context);
        for (int index = 0; index < this.cardBounds.length; index++) this.cardBounds[index] = new RectF();
        for (int index = 0; index < this.albumImageBounds.length; index++) this.albumImageBounds[index] = new RectF();
        this.p = new Paint(3);
        Paint paint = new Paint(3);
        this.text = paint;
        this.rng = new Random();
        this.sheets = new Bitmap[WORLD_COUNT];
        this.assetNames = new String[]{"animals.png", "ocean.png", "space.png", "dinosaurs.png", "farm.png", "vehicles.png", "magic.png", "mermaids.png", "jungle.png", "treats.png", "pirates.png", "arctic.png", "music_world.png", "jobs.png", "fairies.png", "home_world.png", "vegetables.png", "detective.png"};
        for (int tile = 0; tile < WORLD_COUNT; tile++) this.themeTiles[tile] = new RectF();
        boolean z = false;
        this.theme = 0;
        this.albumPage = 0;
        this.albumReturnScreen = 0;
        this.albumTab = 2;
        this.enlargedItem = -1;
        this.screen = -1;
        this.pairs = 6;
        this.players = 1;
        this.turn = 1;
        this.score1 = 0;
        this.score2 = 0;
        this.cols = 3;
        this.rows = 4;
        this.moves = 0;
        this.found = 0;
        this.first = -1;
        this.second = -1;
        this.lastA = -1;
        this.lastB = -1;
        this.mismatchDelay = 1300;
        this.newUnlocks = 0;
        this.errors = 0;
        this.suggestedPairs = 0;
        this.tutorialStep = 0;
        this.hintA = -1;
        this.hintB = -1;
        this.hideAt = 0L;
        this.previewUntil = 0L;
        this.winAt = 0L;
        this.successUntil = 0L;
        this.resetDoneUntil = 0L;
        this.flipStart = 0L;
        this.closeStart = 0L;
        this.hintUntil = 0L;
        this.splashStart = now();
        this.flipIndex = -1;
        this.closeA = -1;
        this.closeB = -1;
        this.newRecord = false;
        this.switchPending = false;
        this.calm = false;
        this.haptics = true;
        this.parentHolding = false;
        this.tutorialActive = false;
        this.hintUsed = false;
        this.showQuit = false;
        this.resumeAvailable = false;
        this.showResumePrompt = false;
        this.showResetConfirm = false;
        this.earnedBadge = "";
        this.play = new RectF();
        this.easy = new RectF();
        this.medium = new RectF();
        this.hard = new RectF();
        this.expert = new RectF();
        this.mega = new RectF();
        this.onePlayer = new RectF();
        this.coopPlayer = new RectF();
        this.twoPlayers = new RectF();
        this.chooseTheme = new RectF();
        this.animalsTheme = new RectF();
        this.oceanTheme = new RectF();
        this.spaceTheme = new RectF();
        this.dinoTheme = new RectF();
        this.farmTheme = new RectF();
        this.vehiclesTheme = new RectF();
        this.magicTheme = new RectF();
        this.mermaidTheme = new RectF();
        this.jungleTheme = new RectF();
        this.treatsTheme = new RectF();
        this.pirateTheme = new RectF();
        this.arcticTheme = new RectF();
        this.musicTheme = new RectF();
        this.jobsTheme = new RectF();
        this.soundBtn = new RectF();
        this.albumBtn = new RectF();
        this.storyBtn = new RectF();
        this.storyPlayBtn = new RectF();
        this.albumPrev = new RectF();
        this.albumNext = new RectF();
        this.imagesTab = new RectF();
        this.badgesTab = new RectF();
        this.enlargedClose = new RectF();
        this.parentBtn = new RectF();
        this.homeBtn = new RectF();
        this.hintBtn = new RectF();
        this.tutorialDoneBtn = new RectF();
        this.resumeBtn = new RectF();
        this.newGameBtn = new RectF();
        this.quitContinueBtn = new RectF();
        this.quitConfirmBtn = new RectF();
        this.resetCancelBtn = new RectF();
        this.resetConfirmBtn = new RectF();
        this.shortDelay = new RectF();
        this.normalDelay = new RectF();
        this.longDelay = new RectF();
        this.calmBtn = new RectF();
        this.hapticBtn = new RectF();
        this.resetBtn = new RectF();
        this.tutorialResetBtn = new RectF();
        this.sound = true;
        this.activityActive = true;
        this.storyPlaying = false;
        this.musicIndex = -1;
        this.musicAssets = new String[]{"music/mountain_kids.mp3", "music/kids_background.mp3", "music/sub_clair_kids.mp3", "music/gentle_lullaby.mp3", "music/star_garden.wav", "music/little_steps.wav"};
        this.parentNotice = "";
        this.bg = Color.rgb(255, 248, 236);
        this.ink = Color.rgb(61, 53, 64);
        this.coral = Color.rgb(255, 112, 84);
        this.mint = Color.rgb(92, 203, 174);
        this.purple = Color.rgb(125, 102, 190);
        this.cheers = new String[]{"Bravo !", "Super !", "Bien joué !", "Youpi !"};
        this.d = getResources().getDisplayMetrics().density;
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ProgressStore progressStore = new ProgressStore(context);
        this.progress = progressStore;
        SharedPreferences sharedPreferencesPrefs = progressStore.prefs();
        boolean z2 = sharedPreferencesPrefs.getBoolean("audio_default_v7", false);
        this.sound = !z2 || sharedPreferencesPrefs.getBoolean("sound", true);
        if (!z2) {
            sharedPreferencesPrefs.edit().putBoolean("sound", true).putBoolean("audio_default_v7", true).apply();
        }
        this.haptics = sharedPreferencesPrefs.getBoolean("haptics", true);
        this.players = 1;
        sharedPreferencesPrefs.edit().putInt("players", 1).apply();
        this.theme = Math.max(0, Math.min(WORLD_COUNT - 1, sharedPreferencesPrefs.getInt("theme", 0)));
        int i = sharedPreferencesPrefs.getInt("pairs", 6);
        this.pairs = i;
        if (i != 6 && i != 8 && i != 10 && i != 12) {
            this.pairs = 6;
        }
        this.mismatchDelay = sharedPreferencesPrefs.getInt("delay", 1300);
        this.calm = sharedPreferencesPrefs.getBoolean("calm", false);
        int i2 = sharedPreferencesPrefs.getInt("suggested_pairs", 0);
        this.suggestedPairs = i2;
        if (i2 != 6 && i2 != 8 && i2 != 10 && i2 != 12) {
            this.suggestedPairs = 0;
        }
        if (sharedPreferencesPrefs.getBoolean("game_active", false) && sharedPreferencesPrefs.getInt("saved_players", 1) == 1) {
            z = true;
        }
        this.resumeAvailable = z;
        this.showResumePrompt = z;
        paint.setTypeface(Typeface.create("sans", Typeface.BOLD));
        setFocusable(true);
        setImportantForAccessibility(1);
        setContentDescription("MémoMondes, jeu de mémoire illustré");
        post(new Runnable() { // from class: fr.memomimi.app.GameView$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                GameView.this.updateMusic();
            }
        });
    }

    Bitmap loadAsset(Context context, String str) {
        try (InputStream input = context.getAssets().open(str)) {
            return BitmapFactory.decodeStream(input);
        } catch (Exception unused) {
            return null;
        }
    }

    Bitmap loadSmallAsset(String name) {
        try (InputStream input = getContext().getAssets().open(name)) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            return BitmapFactory.decodeStream(input, null, options);
        } catch (Exception ignored) {
            return null;
        }
    }

    void color(int i) {
        this.p.setColor(i);
        this.p.setStyle(Paint.Style.FILL);
    }

    void label(Canvas canvas, String str, float f, float f2, float f3, int i, Paint.Align align) {
        this.text.setTextSize(f3 * this.d);
        this.text.setColor(i);
        this.text.setTextAlign(align);
        this.text.setTypeface(Typeface.create("sans", Typeface.BOLD));
        canvas.drawText(str, f, f2 - ((this.text.ascent() + this.text.descent()) / 2.0f), this.text);
    }

    void round(Canvas canvas, RectF rectF, float f, int i) {
        boolean action = rectF == this.play || rectF == this.homeBtn || rectF == this.albumBtn
                || rectF == this.storyBtn || rectF == this.soundBtn || rectF == this.parentBtn
                || rectF == this.hintBtn || rectF == this.resumeBtn || rectF == this.newGameBtn
                || rectF == this.quitConfirmBtn || rectF == this.quitContinueBtn;
        if (action) f = 18f;
        if (this.pressedTarget == rectF) i = blend(i, this.ink, 0.12f);
        color(i);
        Paint paint = this.p;
        float f2 = this.d;
        paint.setShadowLayer(8.0f * f2, 0.0f, f2 * 4.0f, 570425344);
        float f3 = this.d;
        canvas.drawRoundRect(rectF, f * f3, f * f3, this.p);
        this.p.clearShadowLayer();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.screen == 1 && this.found == this.pairs && this.pendingWinAt > 0
                && now() >= this.pendingWinAt && !this.showQuit) {
            this.pendingWinAt = 0L;
            this.winAt = now();
            this.celebration.begin(this.winAt, this.rng);
            this.screen = 2;
            this.accessible.changed();
            announceForAccessibility(this.players == 3 ? "Bravo, vous avez gagné ensemble !"
                    : this.players == 2 ? "Bravo, la partie est terminée !"
                    : "Bravo, toutes les paires sont retrouvées !");
            performHapticFeedback(0);
            playSound(100);
        }
        this.W = getWidth();
        this.H = getHeight();
        drawWorldBackground(canvas, this.screen == 5 ? this.albumPage : this.theme);
        int i = this.screen;
        if (i == -1) {
            drawSplash(canvas);
            return;
        }
        if (i == 0) {
            drawHome(canvas);
        } else if (i == 1) {
            drawGame(canvas);
        } else if (i == 2) {
            drawWin(canvas);
        } else if (i == 3) {
            drawParents(canvas);
        } else if (i == 4) {
            drawThemes(canvas);
        } else if (i == 5) {
            drawAlbum(canvas);
        } else {
            drawStory(canvas);
        }
        this.accessible.drawFocus(canvas);
        long j = this.hideAt;
        if (j > 0) {
            long jCurrentTimeMillis = j - now();
            if (jCurrentTimeMillis <= 0) {
                this.closeA = this.first;
                this.closeB = this.second;
                this.closeStart = now();
                this.second = -1;
                this.first = -1;
                this.hideAt = 0L;
                invalidate();
            } else {
                postInvalidateDelayed(jCurrentTimeMillis);
            }
        }
        if (this.showQuit) {
            drawQuitOverlay(canvas);
            return;
        }
        if (this.showResetConfirm) {
            drawResetConfirm(canvas);
        } else if (this.screen == 0 && this.showResumePrompt && this.resumeAvailable) {
            drawResumeOverlay(canvas);
        }
    }

    void drawSplash(Canvas canvas) {
        long elapsed = Math.max(0, now()-this.splashStart);
        float progress = Math.min(1f, elapsed/1750f);
        float top = systemTop()+22f*this.d;
        this.splashPanel.set(20f*this.d, top, this.W-20f*this.d, this.H*.80f);
        round(canvas, this.splashPanel, 34f, -134218244);
        float height = this.splashPanel.height();
        int accent = themeColor(this.theme);
        this.splashMascotArea.set(this.splashPanel.left+12f*this.d, top+height*.035f,
                this.splashPanel.right-12f*this.d, top+height*.50f);
        this.victoryMascot.drawLoading(canvas, this.splashMascotArea, elapsed, animationsReduced(), accent);
        fitLabel(canvas, "MémoMondes", this.W/2f, top+height*.61f, 37f, 24f,
                this.splashPanel.width()-24f*this.d, this.ink);
        fitLabel(canvas, "Éveille ta mémoire en t'amusant", this.W/2f, top+height*.70f, 14f, 10f,
                this.splashPanel.width()-24f*this.d, -8950152);
        this.splashBadge.set(this.W/2f-82f*this.d, top+height*.77f,
                this.W/2f+82f*this.d, top+height*.84f);
        round(canvas, this.splashBadge, 15f, (accent & 0xffffff) | 0x22000000);
        label(canvas, "★  " + WORLD_COUNT + " MONDES À EXPLORER", this.W/2f,
                this.splashBadge.centerY(), 10f, accent, Paint.Align.CENTER);
        this.splashBar.set(this.splashPanel.left+35f*this.d, top+height*.92f,
                this.splashPanel.right-35f*this.d, top+height*.92f+12f*this.d);
        round(canvas, this.splashBar, 8f, -1580827);
        this.splashFill.set(this.splashBar);
        this.splashFill.right = this.splashBar.left+this.splashBar.width()*progress;
        if (progress > 0) round(canvas, this.splashFill, 8f, this.mint);
        label(canvas, progress < .92f ? "Lumi prépare les aventures…" : "C'est prêt !",
                this.W/2f, this.splashPanel.bottom+this.H*.06f, 12f, accent, Paint.Align.CENTER);
        if (progress < 1f) {
            if (animationsReduced()) postInvalidateDelayed(120L);
            else postInvalidateOnAnimation();
            return;
        }
        this.screen = 0;
        this.accessible.changed();
        announceForAccessibility("Bienvenue dans MémoMondes");
        updateMusic();
        invalidate();
    }

    void drawWorldBackground(Canvas canvas, int i) {
        Path path = null;
        int accent = themeColor(i);
        if (worldGradient == null || backgroundTheme != i || backgroundWidth != W || backgroundHeight != H) {
            worldGradient = new LinearGradient(0, 0, W * 0.65f, H,
                    new int[]{blend(accent, Color.WHITE, 0.80f), blend(this.bg, accent, 0.10f),
                            blend(accent, Color.WHITE, 0.68f)}, new float[]{0f, 0.48f, 1f}, Shader.TileMode.CLAMP);
            backgroundTheme = i;
            backgroundWidth = W;
            backgroundHeight = H;
        }
        color(Color.WHITE);
        this.p.setShader(worldGradient);
        canvas.drawRect(0, 0, W, H, this.p);
        this.p.setShader(null);
        // Des courbes fixes, sans animation, donnent de la profondeur sans distraire du jeu.
        backgroundWave.reset();
        backgroundWave.moveTo(0, H * 0.76f);
        backgroundWave.cubicTo(W * 0.30f, H * 0.64f, W * 0.65f, H * 0.96f, W, H * 0.80f);
        backgroundWave.lineTo(W, H);
        backgroundWave.lineTo(0, H);
        backgroundWave.close();
        color((accent & 0x00ffffff) | 0x12000000);
        canvas.drawPath(backgroundWave, this.p);
        color((accent & 0x00ffffff) | 0x24000000);
        for (int i2 = 0; i2 < 15; i2++) {
            float f = ((((i2 * 83) + (i * 29)) % 101) / 101.0f) * this.W;
            float f2 = ((((i2 * 149) + (i * 47)) % 103) / 103.0f) * this.H;
            float f3 = (((i2 % 4) * 4) + 8) * this.d;
            if (i == 1 || i == 7) {
                this.p.setStyle(Paint.Style.STROKE);
                this.p.setStrokeWidth(this.d * 2.0f);
                canvas.drawCircle(f, f2, f3, this.p);
                this.p.setStyle(Paint.Style.FILL);
            } else {
                if (i == 2 || i == 6) {
                    path = new Path();
                    for (int i3 = 0; i3 < 8; i3++) {
                        double d = ((((double) i3) * 3.141592653589793d) / 4.0d) - 1.5707963267948966d;
                        float f4 = i3 % 2 == 0 ? f3 : 0.38f * f3;
                        float fCos = (((float) Math.cos(d)) * f4) + f;
                        float fSin = (((float) Math.sin(d)) * f4) + f2;
                        if (i3 == 0) {
                            path.moveTo(fCos, fSin);
                        } else {
                            path.lineTo(fCos, fSin);
                        }
                    }
                } else if (i == 8 || i == 4) {
                    canvas.save();
                    canvas.rotate((i2 * 31) % 180, f, f2);
                    float f5 = 0.45f * f3;
                    canvas.drawOval(new RectF(f - f3, f2 - f5, f + f3, f2 + f5), this.p);
                    canvas.restore();
                } else if (i == 3) {
                    path = new Path();
                    path.moveTo(f, f2 - f3);
                    float f6 = f2 + f3;
                    path.lineTo(f + f3, f6);
                    path.lineTo(f - f3, f6);
                } else {
                    canvas.drawCircle(f, f2, f3 * 0.55f, this.p);
                }
                if (i == 2 || i == 6 || i == 3) {
                    path.close();
                    canvas.drawPath(path, this.p);
                }
            }
        }
    }

    float systemTop() {
        WindowInsets rootWindowInsets = getRootWindowInsets();
        return rootWindowInsets == null ? this.d * 16.0f : Math.max(this.d * 8.0f, rootWindowInsets.getSystemWindowInsetTop());
    }

    void fitLabel(Canvas canvas, String str, float f, float f2, float f3, float f4, float f5, int i) {
        this.text.setTypeface(Typeface.create("sans", Typeface.BOLD));
        float f6 = f3;
        do {
            this.text.setTextSize(this.d * f6);
            if (this.text.measureText(str) <= f5 || f6 <= f4) {
                break;
            } else {
                f6 -= 0.5f;
            }
        } while (f6 > f4);
        label(canvas, str, f, f2, f6, i, Paint.Align.CENTER);
    }

    void drawHome(Canvas canvas) {
        this.players = 1;
        this.onePlayer.setEmpty();
        this.coopPlayer.setEmpty();
        this.twoPlayers.setEmpty();
        this.mega.setEmpty();
        color(861719470);
        canvas.drawCircle(this.W * 0.08f, this.H * 0.12f, this.d * 44.0f, this.p);
        color(872396373);
        canvas.drawCircle(this.W * 0.94f, this.H * 0.23f, this.d * 58.0f, this.p);
        color(578643646);
        canvas.drawCircle(this.W * 0.04f, this.H * 0.8f, this.d * 54.0f, this.p);
        label(canvas, "MémoMondes", this.W / 2.0f, 0.075f * this.H, 34.0f, this.ink, Paint.Align.CENTER);
        label(canvas, "Explore, retourne, retrouve !", this.W / 2.0f, this.H * 0.12f, 14.0f, -8950152, Paint.Align.CENTER);
        label(canvas, "Choisis ton aventure", this.W / 2.0f, this.H * 0.175f, 18.0f, this.ink, Paint.Align.CENTER);
        float f = this.H * 0.205f;
        RectF rectF = this.chooseTheme;
        float f2 = this.d;
        rectF.set(f2 * 22.0f, f, this.W - (22.0f * f2), (f2 * 106.0f) + f);
        selectedThemeChoice(canvas, this.chooseTheme);
        float f3 = (this.d * 31.0f) + this.chooseTheme.bottom;
        label(canvas, "Combien de paires ?", this.W / 2.0f, f3, 17.0f, this.ink, Paint.Align.CENTER);
        float f4 = this.d;
        float f5 = f4 * 10.0f;
        float f6 = ((this.W - (40.0f * f4)) - f5) / 2.0f;
        float f7 = f3 + (25.0f * f4);
        float f8 = 56.0f * f4;
        float f9 = f7 + f8;
        this.easy.set(f4 * 20.0f, f7, (f4 * 20.0f) + f6, f9);
        this.medium.set(this.easy.right + f5, f7, this.W - (this.d * 20.0f), f9);
        RectF rectF2 = this.hard;
        float f10 = this.d * 20.0f;
        float f11 = this.easy.bottom;
        float f12 = this.d;
        rectF2.set(f10, f11 + (f12 * 10.0f), (f12 * 20.0f) + f6, this.easy.bottom + (this.d * 10.0f) + f8);
        this.expert.set(this.hard.right + f5, this.hard.top, this.W - (this.d * 20.0f), this.hard.bottom);
        compactLevel(canvas, this.easy, "6 PAIRES", this.suggestedPairs == 6 ? "★ CONSEILLÉ" : "POUR COMMENCER", this.pairs == 6, this.mint);
        compactLevel(canvas, this.medium, "8 PAIRES", this.suggestedPairs == 8 ? "★ CONSEILLÉ" : "JE M'ENTRAÎNE", this.pairs == 8, -18857);
        compactLevel(canvas, this.hard, "10 PAIRES", this.suggestedPairs == 10 ? "★ CONSEILLÉ" : "JE PROGRESSE", this.pairs == 10, this.purple);
        compactLevel(canvas, this.expert, "12 PAIRES", this.suggestedPairs != 12 ? "GRAND DÉFI" : "★ CONSEILLÉ", this.pairs == 12, this.coral);
        float f13 = this.expert.bottom;
        float f14 = this.d;
        float f15 = f13 + (28.0f * f14);
        this.play.set(f14 * 34.0f, f15, this.W - (34.0f * f14), (f14 * 60.0f) + f15);
        round(canvas, this.play, 27.0f, this.coral);
        label(canvas, "JOUER !   ▶", this.W / 2.0f, this.play.centerY(), 21.0f, -1, Paint.Align.CENTER);
        float f16 = this.play.bottom;
        float f17 = this.d;
        float f18 = f16 + (24.0f * f17);
        this.soundBtn.set(f17 * 26.0f, f18, (this.W / 2.0f) - (f17 * 5.0f), (f17 * 46.0f) + f18);
        RectF rectF3 = this.albumBtn;
        float f19 = this.W;
        float f20 = this.d;
        rectF3.set((f19 / 2.0f) + (5.0f * f20), f18, f19 - (26.0f * f20), (f20 * 46.0f) + f18);
        round(canvas, this.soundBtn, 19.0f, this.sound ? -1968143 : -516);
        round(canvas, this.albumBtn, 19.0f, -516);
        label(canvas, this.sound ? "♪  MUSIQUE OUI" : "♪  MUSIQUE NON", this.soundBtn.centerX(), this.soundBtn.centerY(), 11.0f, this.sound ? -13530251 : -8950152, Paint.Align.CENTER);
        label(canvas, "★  MON ALBUM  " + albumCount() + "/" + ALBUM_TOTAL, this.albumBtn.centerX(), this.albumBtn.centerY(), 10.0f, this.purple, Paint.Align.CENTER);
        float f21 = this.soundBtn.bottom;
        float f22 = this.d;
        float f23 = f21 + (18.0f * f22);
        RectF rectF4 = this.parentBtn;
        float f24 = this.W;
        rectF4.set((f24 / 2.0f) - (f22 * 100.0f), f23, (f24 / 2.0f) + (100.0f * f22), (f22 * 36.0f) + f23);
        label(canvas, "⚙  Espace parents — maintenir", this.W / 2.0f, this.parentBtn.centerY(), 11.0f, -7107695, Paint.Align.CENTER);
    }

    void modeChoice(Canvas canvas, RectF rectF, String str, boolean z, int i) {
        if (!z) {
            i = -1;
        }
        round(canvas, rectF, 18.0f, i);
        label(canvas, str, rectF.centerX(), rectF.centerY(), 10.0f, z ? -1 : this.ink, Paint.Align.CENTER);
        if (z) {
            this.p.setStyle(Paint.Style.STROKE);
            this.p.setStrokeWidth(this.d * 3.0f);
            this.p.setColor(-1);
            RectF rectF2 = new RectF(rectF.left + (this.d * 4.0f), rectF.top + (this.d * 4.0f), rectF.right - (this.d * 4.0f), rectF.bottom - (this.d * 4.0f));
            float f = this.d;
            canvas.drawRoundRect(rectF2, f * 14.0f, f * 14.0f, this.p);
            this.p.setStyle(Paint.Style.FILL);
        }
    }

    int themeColor(int i) {
        if (this.themeColors == null) this.themeColors = new int[]{this.mint, -12013624, this.purple, -1008827, -8537246, -1414047, -2263628, -11159117, -10901413, -1475427, -12617560, -9912368, -1989061, -11625599, -5417494, -10042165, -12684698, -9411760};
        return this.themeColors[i];
    }

    String themeName(int i) {
        return THEME_NAMES[i];
    }

    void selectedThemeChoice(Canvas canvas, RectF rectF) {
        int iThemeColor = themeColor(this.theme);
        round(canvas, rectF, 27.0f, iThemeColor);
        this.p.setStyle(Paint.Style.STROKE);
        this.p.setStrokeWidth(this.d * 2.0f);
        this.p.setColor(-1711276033);
        RectF rectF2 = new RectF(rectF.left + (this.d * 4.0f), rectF.top + (this.d * 4.0f), rectF.right - (this.d * 4.0f), rectF.bottom - (this.d * 4.0f));
        float f = this.d;
        canvas.drawRoundRect(rectF2, f * 23.0f, f * 23.0f, this.p);
        this.p.setStyle(Paint.Style.FILL);
        RectF rectF3 = new RectF(rectF.left + (this.d * 10.0f), rectF.top + (this.d * 7.0f), rectF.left + (this.d * 10.0f) + ((rectF.height() - (this.d * 14.0f)) * 0.76f), rectF.bottom - (this.d * 7.0f));
        round(canvas, new RectF(rectF3.left - (this.d * 2.0f), rectF3.top - (this.d * 2.0f), rectF3.right + (this.d * 2.0f), rectF3.bottom + (this.d * 2.0f)), 20.0f, 1157627903);
        drawThemeSticker(canvas, this.theme, rectF3);
        float f2 = ((rectF3.right + rectF.right) - (this.d * 48.0f)) / 2.0f;
        label(canvas, "TON MONDE", f2, rectF.top + (this.d * 25.0f), 10.0f, -570425345, Paint.Align.CENTER);
        fitLabel(canvas, themeName(this.theme), f2, (this.d * 52.0f) + rectF.top, 20.0f, 14.0f, (rectF.right - rectF3.right) - (this.d * 52.0f), -1);
        label(canvas, "12 IMAGES À RETROUVER", f2, (this.d * 76.0f) + rectF.top, 9.0f, -285212673, Paint.Align.CENTER);
        label(canvas, "TOUCHE POUR CHANGER", f2, (this.d * 94.0f) + rectF.top, 8.0f, -855638017, Paint.Align.CENTER);
        color(-1);
        canvas.drawCircle(rectF.right - (this.d * 25.0f), rectF.centerY(), this.d * 18.0f, this.p);
        label(canvas, "›", rectF.right - (this.d * 25.0f), rectF.centerY() - (this.d * 2.0f), 27.0f, iThemeColor, Paint.Align.CENTER);
    }

    void themeChoice(Canvas canvas, RectF rectF, int i, String str, int i2) {
        boolean z = this.theme == i;
        round(canvas, rectF, 17.0f, z ? i2 : -1);
        RectF rectF2 = new RectF(rectF.left + (this.d * 6.0f), rectF.top + (this.d * 4.0f), rectF.left + (this.d * 48.0f), rectF.bottom - (this.d * 4.0f));
        drawThemeSticker(canvas, i, rectF2);
        label(canvas, str, ((rectF2.right + rectF.right) / 2.0f) - (this.d * 2.0f), rectF.centerY(), 10.0f, z ? -1 : this.ink, Paint.Align.CENTER);
        if (z) {
            color(-1);
            float f = rectF.right - (this.d * 12.0f);
            float f2 = rectF.top;
            float f3 = this.d;
            canvas.drawCircle(f, f2 + (f3 * 12.0f), f3 * 8.0f, this.p);
            label(canvas, "✓", rectF.right - (this.d * 12.0f), rectF.top + (this.d * 11.0f), 9.0f, i2, Paint.Align.CENTER);
        }
    }

    void level(Canvas canvas, RectF rectF, String str, String str2, boolean z, int i) {
        String str3;
        round(canvas, rectF, 18.0f, z ? i : -1);
        if (str.equals("6")) {
            str3 = "PETIT";
        } else {
            str3 = str.equals("8") ? "MOYEN" : "GRAND";
        }
        label(canvas, str3, rectF.centerX(), rectF.top + (this.d * 14.0f), 9.0f, z ? -1 : -7700085, Paint.Align.CENTER);
        label(canvas, str, rectF.centerX(), (this.d * 35.0f) + rectF.top, 20.0f, z ? -1 : this.ink, Paint.Align.CENTER);
        label(canvas, str2, rectF.centerX(), (this.d * 54.0f) + rectF.top, 10.0f, z ? -1 : -7700085, Paint.Align.CENTER);
        if (z) {
            label(canvas, "✓", rectF.right - (this.d * 12.0f), (this.d * 11.0f) + rectF.top, 10.0f, -1, Paint.Align.CENTER);
        }
    }

    void compactLevel(Canvas canvas, RectF rectF, String str, String str2, boolean z, int i) {
        round(canvas, rectF, 19.0f, z ? i : -1);
        if (z) {
            this.p.setStyle(Paint.Style.STROKE);
            this.p.setStrokeWidth(this.d * 2.0f);
            this.p.setColor(-1711276033);
            RectF rectF2 = new RectF(rectF.left + (this.d * 4.0f), rectF.top + (this.d * 4.0f), rectF.right - (this.d * 4.0f), rectF.bottom - (this.d * 4.0f));
            float f = this.d;
            canvas.drawRoundRect(rectF2, f * 15.0f, f * 15.0f, this.p);
            this.p.setStyle(Paint.Style.FILL);
        }
        label(canvas, str, rectF.centerX(), rectF.top + (this.d * 20.0f), 15.0f, z ? -1 : this.ink, Paint.Align.CENTER);
        label(canvas, str2, rectF.centerX(), (this.d * 42.0f) + rectF.top, 9.0f, z ? -1 : -7700085, Paint.Align.CENTER);
        if (z) {
            label(canvas, "✓", rectF.right - (this.d * 13.0f), (this.d * 12.0f) + rectF.top, 10.0f, -1, Paint.Align.CENTER);
        }
    }

    void start() {
        this.celebration.reset();
        this.victoryMascot.bounds.setEmpty();
        this.matchFeedbackGeneration++;
        this.pendingWinAt = 0L;
        String str;
        int iColumns = GameModel.columns(this.pairs);
        this.cols = iColumns;
        int i = this.pairs;
        this.rows = (((i * 2) + iColumns) - 1) / iColumns;
        int[] iArrShuffledDeck = GameModel.shuffledDeck(i, this.rng);
        this.cards = iArrShuffledDeck;
        this.matched = new boolean[iArrShuffledDeck.length];
        this.owners = new int[iArrShuffledDeck.length];
        this.errors = 0;
        this.newUnlocks = 0;
        this.score2 = 0;
        this.score1 = 0;
        this.found = 0;
        this.moves = 0;
        this.turn = 1;
        this.hintB = -1;
        this.hintA = -1;
        this.closeB = -1;
        this.closeA = -1;
        this.flipIndex = -1;
        this.lastB = -1;
        this.lastA = -1;
        this.second = -1;
        this.first = -1;
        this.hintUntil = 0L;
        this.closeStart = 0L;
        this.flipStart = 0L;
        this.previewUntil = 0L;
        this.successUntil = 0L;
        this.hideAt = 0L;
        this.newRecord = false;
        this.hintUsed = false;
        this.switchPending = false;
        this.earnedBadge = "";
        this.tutorialActive = !getContext().getSharedPreferences("m", 0).getBoolean("tutorial_done", false);
        this.tutorialStep = 0;
        this.showResumePrompt = false;
        this.resumeAvailable = true;
        this.screen = 1;
        saveGame();
        playSound(70);
        if (this.tutorialActive) {
            str = "Lumi va te montrer comment jouer";
        } else {
            int i2 = this.players;
            if (i2 == 2) {
                str = "Au tour du joueur 1. Retourne une première carte";
            } else {
                str = i2 == 3 ? "Jouez ensemble. Retournez une première carte" : "Retourne une première carte";
            }
        }
        announceForAccessibility(str);
        invalidate();
    }

    int best() {
        return getContext().getSharedPreferences("m", 0).getInt("best_" + this.pairs, 0);
    }

    void drawGame(Canvas canvas) {
        String str;
        String str2;
        float fCenterX;
        float fCenterY;
        int i;
        Paint.Align align;
        float f;
        long jCurrentTimeMillis = now();
        int i2 = 2;
        boolean z = true;
        boolean z2 = this.first < 0 && this.errors >= 3 && !this.hintUsed && this.players != 2 && jCurrentTimeMillis >= this.hintUntil;
        float fSystemTop = systemTop();
        RectF rectF = this.homeBtn;
        float f2 = this.d;
        rectF.set(f2 * 16.0f, (f2 * 14.0f) + fSystemTop, f2 * 62.0f, (f2 * 58.0f) + fSystemTop);
        round(canvas, this.homeBtn, 16.0f, -1);
        label(canvas, "‹", this.homeBtn.centerX(), this.homeBtn.centerY() - (this.d * 2.0f), 32.0f, this.ink, Paint.Align.CENTER);
        drawInstruction(canvas, fSystemTop);
        int i3 = this.players;
        if (i3 == 2) {
            drawDuelScore(canvas, fSystemTop);
        } else {
            StringBuilder sb = i3 == 3 ? new StringBuilder("♥  ") : new StringBuilder("★  ");
            sb.append(this.found);
            sb.append(" / ");
            sb.append(this.pairs);
            label(canvas, sb.toString(), this.W * 0.2f, (this.d * 80.0f) + fSystemTop, 16.0f, this.players == 3 ? this.coral : this.mint, Paint.Align.CENTER);
            Canvas canvas2 = canvas;
            label(canvas2, "Coups  " + this.moves, 0.5f * this.W, fSystemTop + (this.d * 80.0f), 15.0f, -8950152, Paint.Align.CENTER);
            if (z2) {
                RectF rectF2 = this.hintBtn;
                float f3 = this.W;
                float f4 = this.d;
                rectF2.set(0.7f * f3, fSystemTop + (62.0f * f4), f3 - (12.0f * f4), (f4 * 99.0f) + fSystemTop);
                round(canvas2, this.hintBtn, 15.0f, -6492);
                fCenterX = this.hintBtn.centerX();
                fCenterY = this.hintBtn.centerY();
                i = this.purple;
                align = Paint.Align.CENTER;
                str2 = "★  AIDE";
                f = 11.0f;
            } else {
                this.hintBtn.setEmpty();
                int iBest = best();
                if (iBest > 0) {
                    str = "Record  " + iBest;
                } else {
                    str = "";
                }
                str2 = str;
                fCenterX = 0.81f * this.W;
                fCenterY = fSystemTop + (this.d * 80.0f);
                i = this.purple;
                align = Paint.Align.CENTER;
                f = 13.0f;
                canvas2 = canvas;
            }
            label(canvas2, str2, fCenterX, fCenterY, f, i, align);
        }
        int i4 = this.pairs >= 10 ? 7 : 9;
        float f5 = this.d;
        float f6 = i4 * f5;
        float f7 = 14.0f * f5;
        float f8 = fSystemTop + (112.0f * f5);
        float f9 = f5 * 24.0f;
        float f10 = this.W - (f7 * 2.0f);
        int i5 = this.cols;
        float f11 = (f10 - ((i5 - 1) * f6)) / i5;
        float f12 = (this.H - f8) - f9;
        int i6 = this.rows;
        float fMin = Math.min((f12 - ((i6 - 1) * f6)) / i6, 1.18f * f11);
        int i7 = this.rows;
        float f13 = f8 + ((((this.H - f8) - f9) - ((i7 * fMin) + ((i7 - 1) * f6))) / 2.0f);
        int i8 = 0;
        while (i8 < this.cards.length) {
            int i9 = this.cols;
            int i10 = i8 / i9;
            float f14 = f7 + ((i8 % i9) * (f11 + f6));
            float f15 = (i10 * (fMin + f6)) + f13;
            RectF rectF3 = this.cardBounds[i8];
            rectF3.set(f14, f15, f14 + f11, f15 + fMin);
            long j = jCurrentTimeMillis;
            drawAnimatedCard(canvas, i8, rectF3, (this.matched[i8] || i8 == this.first || i8 == this.second || (jCurrentTimeMillis < this.hintUntil && (i8 == this.hintA || i8 == this.hintB))) ? z : false, j);
            int i11 = i8;
            if (this.tutorialActive && this.tutorialStep < i2 && i11 == tutorialTarget()) {
                this.p.setStyle(Paint.Style.STROKE);
                this.p.setStrokeWidth((Math.abs(((j / 180) % 3) - 1) + 4) * this.d);
                this.p.setColor(-14249);
                RectF rectF4 = new RectF(rectF3.left - (this.d * 3.0f), rectF3.top - (this.d * 3.0f), rectF3.right + (this.d * 3.0f), rectF3.bottom + (this.d * 3.0f));
                float f16 = this.d;
                canvas.drawRoundRect(rectF4, f16 * 21.0f, f16 * 21.0f, this.p);
                this.p.setStyle(Paint.Style.FILL);
                postInvalidateDelayed(40L);
            }
            jCurrentTimeMillis = j;
            i8 = i11 + 1;
            fMin = fMin;
            i2 = 2;
            z = true;
        }
        long j2 = jCurrentTimeMillis;
        float f17 = fMin;
        this.p.setAlpha(255);
        this.p.setStyle(Paint.Style.FILL);
        if (!animationsReduced() && j2 < this.successUntil) {
            drawSuccessSparkles(canvas, j2, f7, f13, f11, f17, f6);
        }
        if (j2 < this.successUntil || this.pendingWinAt > 0 || this.flipIndex >= 0 || this.closeA >= 0) {
            postInvalidateOnAnimation();
        }
        if (j2 < this.hintUntil) {
            postInvalidateDelayed(40L);
        }
        if (!this.tutorialActive || this.tutorialStep < 2) {
            return;
        }
        drawTutorialDone(canvas);
    }

    // Particles follow elapsed match time, with no random allocation in a frame.
    void drawSuccessSparkles(Canvas canvas, long now, float left, float top,
                             float width, float height, float gap) {
        float progress = Math.max(0f, Math.min(1f,
                (now - (this.successUntil - MATCH_DURATION_MS)) / (float) MATCH_DURATION_MS));
        // Wait until the second card has revealed its illustration.
        float burst = Math.max(0f, (progress - 0.16f) / 0.84f);
        if (burst <= 0f) return;
        for (int pairCard = 0; pairCard < 2; pairCard++) {
            int index = pairCard == 0 ? this.lastA : this.lastB;
            if (index < 0 || index >= this.cards.length) continue;
            float cx = left + (index % this.cols) * (width + gap) + width / 2f;
            float cy = top + (index / this.cols) * (height + gap) + height / 2f;
            float radius = Math.min(width, height) * (0.3f + 0.18f * burst);
            this.p.setStyle(Paint.Style.STROKE);
            this.p.setStrokeWidth(this.d * (2f - burst));
            this.p.setColor(this.mint);
            this.p.setAlpha((int) (130f * (1f - burst)));
            canvas.drawCircle(cx, cy, radius, this.p);
            this.p.setStyle(Paint.Style.FILL);
            for (int particle = 0; particle < 12; particle++) {
                double angle = particle * Math.PI / 6d + pairCard * 0.25d;
                float travel = Math.min(Math.min(width, height) * (0.38f + 0.17f * burst),
                        Math.min(width, height) * 0.5f + gap * 0.4f);
                float px = cx + (float) Math.cos(angle) * travel;
                float py = cy + (float) Math.sin(angle) * travel + gap * 0.6f * burst * burst;
                color(particle % 3 == 0 ? this.coral : particle % 3 == 1 ? this.mint : -14249);
                this.p.setAlpha((int) (230f * (1f - burst)));
                float size = this.d * (3f - 1.5f * burst);
                if (particle % 2 == 0) {
                    canvas.save();
                    canvas.rotate(45f + burst * 100f, px, py);
                    canvas.drawRect(px - size, py - size, px + size, py + size, this.p);
                    canvas.restore();
                } else {
                    canvas.drawCircle(px, py, size, this.p);
                }
            }
        }
        this.p.setAlpha(255);
    }

    void drawDuelScore(Canvas canvas, float f) {
        float f2 = this.d;
        RectF rectF = new RectF(f2 * 12.0f, (f2 * 62.0f) + f, this.W * 0.3f, (f2 * 100.0f) + f);
        float f3 = this.W;
        float f4 = this.d;
        RectF rectF2 = new RectF(0.32f * f3, (f4 * 62.0f) + f, f3 * 0.68f, (f4 * 100.0f) + f);
        float f5 = this.W;
        float f6 = this.d;
        RectF rectF3 = new RectF(0.7f * f5, (62.0f * f6) + f, f5 - (12.0f * f6), f + (f6 * 100.0f));
        round(canvas, rectF, 15.0f, this.turn == 1 ? this.mint : -1968143);
        round(canvas, rectF2, 15.0f, -1);
        round(canvas, rectF3, 15.0f, this.turn == 2 ? this.purple : -1185542);
        label(canvas, "J1  ★ " + this.score1, rectF.centerX(), rectF.centerY(), 12.0f, this.turn == 1 ? -1 : this.mint, Paint.Align.CENTER);
        label(canvas, this.found + "/" + this.pairs + "  •  " + this.moves + " coups", rectF2.centerX(), rectF2.centerY(), 10.0f, -8950152, Paint.Align.CENTER);
        label(canvas, "J2  ★ " + this.score2, rectF3.centerX(), rectF3.centerY(), 12.0f, this.turn != 2 ? this.purple : -1, Paint.Align.CENTER);
    }

    int tutorialTarget() {
        int i = this.tutorialStep;
        int i2 = 0;
        if (i == 0) {
            return 0;
        }
        if (i != 1 || this.first < 0) {
            return -1;
        }
        while (true) {
            int[] iArr = this.cards;
            if (i2 >= iArr.length) {
                return -1;
            }
            int i3 = this.first;
            if (i2 != i3 && !this.matched[i2] && iArr[i2] != iArr[i3]) {
                return i2;
            }
            i2++;
        }
    }

    void drawTutorialDone(Canvas canvas) {
        color(-1728053248);
        float f = this.H;
        canvas.drawRect(0.0f, f - (this.d * 112.0f), this.W, f, this.p);
        label(canvas, "★  Lumi : Bravo, tu sais jouer !", this.W / 2.0f, this.H - (this.d * 87.0f), 14.0f, -1, Paint.Align.CENTER);
        RectF rectF = this.tutorialDoneBtn;
        float f2 = this.W;
        float f3 = this.d;
        float f4 = this.H;
        rectF.set((f2 / 2.0f) - (f3 * 88.0f), f4 - (70.0f * f3), (f2 / 2.0f) + (88.0f * f3), f4 - (f3 * 22.0f));
        round(canvas, this.tutorialDoneBtn, 20.0f, this.coral);
        label(canvas, "J'AI COMPRIS !", this.tutorialDoneBtn.centerX(), this.tutorialDoneBtn.centerY(), 16.0f, -1, Paint.Align.CENTER);
    }

    void drawInstruction(Canvas canvas, float f) {
        boolean z = this.hideAt > 0 || this.closeA >= 0;
        String message;
        int background = -3895;
        int accent = this.coral;
        if (this.tutorialActive) {
            message = this.tutorialStep == 0 ? "Touche la carte brillante" :
                    this.tutorialStep == 1 ? "Touche l'autre carte" : "Elles vont se cacher";
        } else if (now() < this.successUntil && this.first < 0 && !z) {
            message = this.pendingWinAt > 0 ? "Toutes les paires ! Bravo !" : "Bravo, une paire retrouvée !";
            accent = this.mint;
            background = blend(this.mint, -1, 0.9f);
        } else if (now() < this.hintUntil) {
            message = "Regarde : voici une paire !";
        } else if (z) {
            message = "Regarde bien ces deux cartes…";
        } else if (this.first >= 0) {
            message = "Trouve sa jumelle !";
            accent = this.purple;
            background = -1185542;
        } else if (this.players == 2) {
            message = "Joueur " + this.turn + ", à toi !";
            accent = this.turn == 1 ? this.mint : this.purple;
            background = this.turn == 1 ? -1968143 : -1185542;
        } else if (this.players == 3) {
            message = "Jouez ensemble !";
        } else {
            message = "À toi de jouer !";
            accent = this.mint;
        }
        RectF banner = new RectF(74.0f * this.d, f + (this.d * 14.0f),
                this.W - (this.d * 16.0f), f + (this.d * 58.0f));
        round(canvas, banner, 16.0f, background);
        color(accent);
        canvas.drawCircle(banner.left + (this.d * 21.0f), banner.centerY(), this.d * 12.0f, this.p);
        label(canvas, "★", banner.left + (this.d * 21.0f), banner.centerY() - this.d, 12.0f, -1, Paint.Align.CENTER);
        fitLabel(canvas, message, (banner.left + (this.d * 43.0f) + banner.right) / 2.0f,
                banner.centerY(), 15.0f, 11.0f, banner.width() - (this.d * 53.0f), this.ink);
    }

    void drawAnimatedCard(Canvas canvas, int i, RectF rectF, boolean z, long j) {
        float fAbs;
        float turnProgress = -1f;
        boolean z2;
        Paint paint;
        int i2;
        int[] iArr;
        int i3;
        boolean z3 = false;
        if (i == this.flipIndex) {
            float fMin = animationsReduced() ? 1f : Math.min(1.0f, (j - this.flipStart) / (float)CardMotion.OPEN_MS);
            turnProgress = fMin;
            fAbs = CardMotion.width(fMin);
            z2 = fMin >= 0.5f;
            if (fMin >= 1.0f) {
                this.flipIndex = -1;
                this.accessible.changed();
            }
        } else if (i == this.closeA || i == this.closeB) {
            float fMin2 = animationsReduced() ? 1f : Math.min(1.0f, (j - this.closeStart) / (float)CardMotion.CLOSE_MS);
            turnProgress = fMin2;
            fAbs = CardMotion.width(fMin2);
            z2 = fMin2 < 0.5f;
            if (fMin2 >= 1.0f && i == this.closeB) {
                this.closeB = -1;
                this.closeA = -1;
                this.accessible.changed();
                if (this.switchPending && this.players == 2) {
                    this.turn = this.turn == 1 ? 2 : 1;
                    this.switchPending = false;
                    announceForAccessibility("Au tour du joueur " + this.turn);
                    saveGame();
                }
            }
        } else {
            z2 = z;
            fAbs = 1.0f;
        }
        canvas.save();
        if (turnProgress >= 0 && !animationsReduced()) {
            canvas.translate(0, -this.d*2f*(float)Math.sin(Math.PI*turnProgress));
        }
        canvas.scale(Math.max(0.025f, fAbs), 1.0f, rectF.centerX(), rectF.centerY());
        if (!animationsReduced() && this.matched[i] && j < this.successUntil
                && (i == this.lastA || i == this.lastB)) {
            float elapsed = (j - (this.successUntil - MATCH_DURATION_MS)) / (float) MATCH_DURATION_MS;
            float pulse = Math.max(0f, Math.min(1f, (elapsed - 0.16f) / 0.6f));
            float scale = 1f + 0.045f * (float) Math.sin(pulse * Math.PI);
            canvas.scale(scale, scale, rectF.centerX(), rectF.centerY());
        }
        if (z2) {
            boolean waitingMismatch = this.hideAt > 0 && (i == this.first || i == this.second);
            round(canvas, rectF, 20.0f, this.matched[i] ? blend(this.mint, -1, 0.93f)
                    : waitingMismatch ? blend(this.coral, -1, 0.94f) : -516);
            this.cardImageBounds.set(rectF.left + this.d * 5f, rectF.top + this.d * 5f, rectF.right - this.d * 5f, rectF.bottom - this.d * 5f);
            drawCardAnimal(canvas, this.cards[i], this.cardImageBounds, 16.0f);
            this.p.setStyle(Paint.Style.STROKE);
            this.p.setStrokeWidth(this.d * 1.5f);
            this.p.setColor((themeColor(this.theme) & 16777215) | 1426063360);
            RectF rectF2 = this.cardBorderBounds;
            rectF2.set(rectF.left + this.d * 3f, rectF.top + this.d * 3f, rectF.right - this.d * 3f, rectF.bottom - this.d * 3f);
            float f = this.d;
            canvas.drawRoundRect(rectF2, f * 17.0f, f * 17.0f, this.p);
            this.p.setStyle(Paint.Style.FILL);
            if (this.players != 2 || !this.matched[i] || (iArr = this.owners) == null || (i3 = iArr[i]) <= 0) {
                if (i == this.first || i == this.second || i == this.closeA || i == this.closeB) {
                    this.p.setStyle(Paint.Style.STROKE);
                    this.p.setStrokeWidth(this.d * 4.0f);
                    paint = this.p;
                    if (this.hideAt > 0 || this.closeA >= 0) {
                        i2 = this.coral;
                    } else {
                        i2 = this.players == 2 && this.turn == 1 ? this.mint : this.purple;
                    }
                    paint.setColor(i2);
                    float f2 = this.d;
                    canvas.drawRoundRect(rectF, f2 * 18.0f, f2 * 18.0f, this.p);
                    this.p.setStyle(Paint.Style.FILL);
                } else if (j < this.successUntil && (i == this.lastA || i == this.lastB)) {
                    this.p.setStyle(Paint.Style.STROKE);
                    this.p.setStrokeWidth(this.d * 4.0f);
                    this.p.setColor(this.mint);
                    canvas.drawRoundRect(rectF, 18.0f * this.d, 18.0f * this.d, this.p);
                    this.p.setStyle(Paint.Style.FILL);
                }
            } else {
                int i4 = i3 == 1 ? this.mint : this.purple;
                if (j < this.successUntil && (i == this.lastA || i == this.lastB)) {
                    z3 = true;
                }
                this.p.setStyle(Paint.Style.STROKE);
                this.p.setStrokeWidth((z3 ? 5 : 3) * this.d);
                this.p.setColor(i4);
                float f4 = this.d;
                canvas.drawRoundRect(rectF, f4 * 18.0f, f4 * 18.0f, this.p);
                this.p.setStyle(Paint.Style.FILL);
                color(i4);
                float f5 = rectF.right - (this.d * 11.0f);
                float f6 = rectF.top;
                float f7 = this.d;
                canvas.drawCircle(f5, f6 + (f7 * 11.0f), f7 * 8.0f, this.p);
                label(canvas, String.valueOf(this.owners[i]), rectF.right - (this.d * 11.0f), rectF.top + (this.d * 10.0f), 8.0f, -1, Paint.Align.CENTER);
            }
        } else {
            drawBack(canvas, rectF);
        }
        if (z2 && this.matched[i] && this.players != 2) {
            color(this.mint);
            float badgeX = rectF.right - this.d * 12f;
            float badgeY = rectF.top + this.d * 12f;
            canvas.drawCircle(badgeX, badgeY, this.d * 9f, this.p);
            this.p.setStyle(Paint.Style.STROKE);
            this.p.setStrokeWidth(this.d * 2f);
            this.p.setColor(Color.WHITE);
            canvas.drawLine(badgeX - this.d * 4f, badgeY, badgeX - this.d, badgeY + this.d * 3f, this.p);
            canvas.drawLine(badgeX - this.d, badgeY + this.d * 3f, badgeX + this.d * 4f, badgeY - this.d * 3f, this.p);
            this.p.setStyle(Paint.Style.FILL);
        }
        if (turnProgress > 0 && turnProgress < 1 && !animationsReduced()) {
            // Soft shading and a moving highlight, clipped to the rounded card.
            float shine = (float)Math.sin(Math.PI*turnProgress);
            color(((int)(32*shine)<<24) | (this.ink & 0xffffff));
            canvas.drawRoundRect(rectF, this.d*18f, this.d*18f, this.p);
            this.cardShinePath.reset();
            this.cardShinePath.addRoundRect(rectF, this.d*18f, this.d*18f, Path.Direction.CW);
            canvas.save(); canvas.clipPath(this.cardShinePath);
            float x = rectF.left + rectF.width()*turnProgress;
            color((int)(70*shine)<<24 | 0xffffff);
            canvas.drawOval(x-rectF.width()*.13f, rectF.top-rectF.height()*.2f,
                    x+rectF.width()*.13f, rectF.bottom+rectF.height()*.2f, this.p);
            canvas.restore();
            this.p.setAlpha(255); this.p.setStyle(Paint.Style.FILL);
        }
        canvas.restore();
    }

    int blend(int i, int i2, float f) {
        int iRed = Color.red(i);
        int iGreen = Color.green(i);
        int iBlue = Color.blue(i);
        return Color.rgb((int) (iRed + ((Color.red(i2) - iRed) * f)), (int) (iGreen + ((Color.green(i2) - iGreen) * f)), (int) (iBlue + ((Color.blue(i2) - iBlue) * f)));
    }

    void drawBack(Canvas canvas, RectF rectF) {
        int iThemeColor = themeColor(this.theme);
        int iBlend = blend(iThemeColor, -1, 0.22f);
        this.p.setStyle(Paint.Style.FILL);
        this.p.setColor(-1);
        this.p.setAlpha(255);
        Paint paint = this.p;
        float f = this.d;
        paint.setShadowLayer(9.0f * f, 0.0f, f * 4.0f, 855638016);
        this.p.setShader(new LinearGradient(rectF.left, rectF.top, rectF.right, rectF.bottom, iBlend, iThemeColor, Shader.TileMode.CLAMP));
        float f2 = this.d;
        canvas.drawRoundRect(rectF, f2 * 20.0f, f2 * 20.0f, this.p);
        this.p.setShader(null);
        this.p.setAlpha(255);
        this.p.clearShadowLayer();
        this.p.setStyle(Paint.Style.STROKE);
        this.p.setStrokeWidth(this.d * 2.0f);
        this.p.setColor(-1996488705);
        RectF rectF2 = new RectF(rectF.left + (this.d * 5.0f), rectF.top + (this.d * 5.0f), rectF.right - (this.d * 5.0f), rectF.bottom - (this.d * 5.0f));
        float f3 = this.d;
        canvas.drawRoundRect(rectF2, f3 * 16.0f, f3 * 16.0f, this.p);
        this.p.setStyle(Paint.Style.FILL);
        color(587202559);
        float f4 = rectF.top;
        float f5 = this.d * 15.0f;
        while (true) {
            f4 += f5;
            if (f4 >= rectF.bottom) {
                color(-10390);
                canvas.drawCircle(rectF.centerX(), rectF.centerY(), this.d * 18.0f, this.p);
                color(1157627903);
                float fCenterX = rectF.centerX() - (this.d * 5.0f);
                float fCenterY = rectF.centerY();
                float f6 = this.d;
                canvas.drawCircle(fCenterX, fCenterY - (f6 * 6.0f), f6 * 6.0f, this.p);
                label(canvas, new String[]{"●", "≈", "✦", "▲", "♦", "■", "✧", "♥", "❧", "•", "◆", "❄", "♪", "★", "✿", "⌂", "♥", "?"}[this.theme], rectF.centerX(), rectF.centerY() - (this.d * 1.0f), 18.0f, iThemeColor, Paint.Align.CENTER);
                return;
            }
            float f7 = rectF.left;
            float f8 = this.d * 15.0f;
            while (true) {
                f7 += f8;
                if (f7 >= rectF.right) break;
                canvas.drawCircle(f7, f4, this.d * 2.6f, this.p);
                f8 = this.d * 22.0f;
            }
            f5 = this.d * 22.0f;
        }
    }

    void drawAnimal(Canvas canvas, int i, RectF rectF, float f) {
        drawAnimalFrom(canvas, this.theme, i % 12, rectF, f);
    }

    void drawCardAnimal(Canvas canvas, int i, RectF rectF, float f) {
        drawAnimal(canvas, i, rectF, f);
    }

    void drawAnimalFrom(Canvas canvas, int i, int i2, RectF rectF, float f) {
        Bitmap bitmapLoadAsset = this.sheets[i];
        if (bitmapLoadAsset == null) {
            bitmapLoadAsset = loadAsset(getContext(), this.assetNames[i]);
            this.sheets[i] = bitmapLoadAsset;
        }
        if (bitmapLoadAsset == null) {
            return;
        }
        int width = bitmapLoadAsset.getWidth() / 4;
        int height = bitmapLoadAsset.getHeight() / 3;
        int i3 = (i2 % 4) * width;
        int i4 = (i2 / 4) * height;
        Path path = new Path();
        float f2 = this.d;
        path.addRoundRect(rectF, f * f2, f * f2, Path.Direction.CW);
        canvas.save();
        canvas.clipPath(path);
        // The sprite cells are portrait-shaped. Preserve their proportions in
        // square album tiles and differently sized game cards instead of stretching.
        float scale = Math.min(rectF.width() / width, rectF.height() / height);
        float fittedWidth = width * scale;
        float fittedHeight = height * scale;
        RectF fitted = new RectF(rectF.centerX() - fittedWidth / 2.0f,
                rectF.centerY() - fittedHeight / 2.0f,
                rectF.centerX() + fittedWidth / 2.0f,
                rectF.centerY() + fittedHeight / 2.0f);
        canvas.drawBitmap(bitmapLoadAsset, new Rect(i3, i4, width + i3, height + i4), fitted, this.p);
        canvas.restore();
    }

    boolean paleIconBackground(int pixel) {
        int red = Color.red(pixel);
        int green = Color.green(pixel);
        int blue = Color.blue(pixel);
        int highest = Math.max(red, Math.max(green, blue));
        int lowest = Math.min(red, Math.min(green, blue));
        return red >= 222 && green >= 210 && blue >= 186 && highest - lowest <= 65;
    }

    int addBackgroundPixel(int position, int[] pixels, boolean[] background, int[] queue, int tail) {
        if (!background[position] && paleIconBackground(pixels[position])) {
            background[position] = true;
            queue[tail++] = position;
        }
        return tail;
    }

    Bitmap themeSticker(int world) {
        Bitmap cached = this.themeStickers[world];
        if (cached != null) return cached;
        if (world == 11 || world == 13) {
            Bitmap cutout = loadSmallAsset(world == 11 ?
                    "ui_icons/arctic_cutout.png" : "ui_icons/jobs_cutout.png");
            if (cutout != null) {
                this.themeStickers[world] = cutout;
                return cutout;
            }
        }
        Bitmap sheet = this.sheets[world];
        Bitmap region = null;
        if (sheet == null) {
            // The world picker needs only the first sprite, not all 12 cards.
            // This keeps the 18-world screen light on memory and quick to open.
            try (InputStream input = getContext().getAssets().open(this.assetNames[world])) {
                BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(input, false);
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    region = decoder.decodeRegion(new Rect(0, 0,
                            decoder.getWidth() / 4, decoder.getHeight() / 3), options);
                } finally {
                    decoder.recycle();
                }
            } catch (Exception ignored) {
            }
        }
        if (sheet == null && region == null) {
            sheet = loadAsset(getContext(), this.assetNames[world]);
            this.sheets[world] = sheet;
        }
        if (sheet == null && region == null) return null;
        int width = region != null ? region.getWidth() : sheet.getWidth() / 4;
        int height = region != null ? region.getHeight() : sheet.getHeight() / 3;
        int size = width * height;
        int[] pixels = new int[size];
        (region != null ? region : sheet).getPixels(pixels, 0, width, 0, 0, width, height);
        if (region != null) region.recycle();
        // The white bear and firefighter have dedicated transparent cutouts.
        // If they are unavailable, feather the original without erasing them.
        boolean keyBackground = world != 11 && world != 13;
        boolean[] background = new boolean[size];
        if (keyBackground) {
            int[] queue = new int[size];
            int tail = 0;
            for (int x = 0; x < width; x++) {
                tail = addBackgroundPixel(x, pixels, background, queue, tail);
                tail = addBackgroundPixel((height - 1) * width + x, pixels, background, queue, tail);
            }
            for (int y = 0; y < height; y++) {
                tail = addBackgroundPixel(y * width, pixels, background, queue, tail);
                tail = addBackgroundPixel(y * width + width - 1, pixels, background, queue, tail);
            }
            for (int head = 0; head < tail; head++) {
                int position = queue[head];
                int x = position % width;
                if (x > 0) tail = addBackgroundPixel(position - 1, pixels, background, queue, tail);
                if (x + 1 < width) tail = addBackgroundPixel(position + 1, pixels, background, queue, tail);
                if (position >= width) tail = addBackgroundPixel(position - width, pixels, background, queue, tail);
                if (position + width < size) tail = addBackgroundPixel(position + width, pixels, background, queue, tail);
            }
        }
        float feather = keyBackground ? 0.10f : 0.18f;
        for (int y = 0; y < height; y++) {
            float edgeY = Math.min(1.0f, Math.min(y, height - 1 - y) / (height * feather));
            for (int x = 0; x < width; x++) {
                int position = y * width + x;
                if (background[position]) {
                    pixels[position] = 0;
                    continue;
                }
                float edgeX = Math.min(1.0f, Math.min(x, width - 1 - x) / (width * feather));
                float edge = Math.min(edgeX, edgeY);
                edge = edge * edge * (3.0f - 2.0f * edge);
                int alpha = Math.round(Color.alpha(pixels[position]) * edge);
                pixels[position] = (pixels[position] & 0x00ffffff) | (alpha << 24);
            }
        }
        Bitmap sticker = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        sticker.setPixels(pixels, 0, width, 0, 0, width, height);
        this.themeStickers[world] = sticker;
        return sticker;
    }

    void drawThemeSticker(Canvas canvas, int world, RectF area) {
        Bitmap sticker = themeSticker(world);
        if (sticker == null) return;
        float scale = Math.min(area.width() / sticker.getWidth(), area.height() / sticker.getHeight());
        float width = sticker.getWidth() * scale;
        float height = sticker.getHeight() * scale;
        RectF fitted = new RectF(area.centerX() - width / 2.0f, area.centerY() - height / 2.0f,
                area.centerX() + width / 2.0f, area.centerY() + height / 2.0f);
        this.p.setAlpha(255);
        this.p.setFilterBitmap(true);
        canvas.drawBitmap(sticker, null, fitted, this.p);
    }

    void drawWin(Canvas canvas) {
        int i;
        String str;
        String str2;
        StringBuilder sb;
        String str3;
        StringBuilder sb2;
        String strThemeName;
        Canvas canvas2;
        long jCurrentTimeMillis = now() - this.winAt;
        int i4 = this.players;
        boolean z = i4 == 2;
        boolean z2 = i4 == 3;
        int i5 = this.score1;
        int i6 = this.score2;
        if (i5 == i6) {
            str = "Égalité !";
        } else {
            str = i5 > i6 ? "Joueur 1 gagne !" : "Joueur 2 gagne !";
        }
        String str4 = str;
        canvas.save();
        if (!animationsReduced() && jCurrentTimeMillis < 650L) {
            float entrance = Math.max(0f, jCurrentTimeMillis / 650f);
            float scale = 0.8f + 0.2f * (1f - (float) Math.pow(1f - entrance, 3d));
            canvas.scale(scale, scale, this.W / 2f, this.H * 0.1f);
        }
        label(canvas, "★", (this.W / 2.0f) - (this.d * 48.0f), this.H * 0.1f, 38.0f, -14249, Paint.Align.CENTER);
        label(canvas, "★", this.W / 2.0f, this.H * 0.085f, 46.0f, this.coral, Paint.Align.CENTER);
        label(canvas, "★", (this.W / 2.0f) + (this.d * 48.0f), this.H * 0.1f, 38.0f, this.mint, Paint.Align.CENTER);
        canvas.restore();
        float f4 = this.d;
        float f5 = this.H;
        round(canvas, new RectF(f4 * 20.0f, 0.145f * f5, this.W - (f4 * 20.0f), f5 * 0.585f), 28.0f, -516);
        if (!z) {
            if (z2) {
                str4 = "Gagné ensemble !";
            } else {
                String[] strArr = this.cheers;
                str4 = strArr[this.moves % strArr.length];
            }
        }
        label(canvas, str4, this.W / 2.0f, this.H * 0.205f, z ? 27.0f : 31.0f, this.ink, Paint.Align.CENTER);
        if (z) {
            str2 = "Bravo à vous deux !";
        } else {
            str2 = this.newRecord ? "Nouveau record !" : "Toutes les paires sont retrouvées !";
        }
        label(canvas, str2, this.W / 2.0f, 0.255f * this.H, 14.0f, this.newRecord ? this.purple : -8950152, Paint.Align.CENTER);
        float f6 = this.d;
        float f7 = this.H;
        RectF rectF = new RectF(f6 * 48.0f, 0.285f * f7, this.W - (f6 * 48.0f), f7 * 0.345f);
        round(canvas, rectF, 18.0f, z ? -1185542 : -5918);
        if (z) {
            sb = new StringBuilder("J1  ★ ");
            sb.append(this.score1);
            sb.append("   •   ");
            sb.append(this.score2);
            str3 = " ★  J2";
        } else {
            sb = new StringBuilder();
            sb.append(this.pairs);
            sb.append(" paires  •  ");
            sb.append(this.moves);
            str3 = " coups";
        }
        sb.append(str3);
        label(canvas, sb.toString(), rectF.centerX(), rectF.centerY(), 15.0f, z ? this.purple : this.coral, Paint.Align.CENTER);
        this.victoryArea.set(this.d * 30f, this.H * .36f,
                this.W - this.d * 30f, this.H * .58f);
        this.victoryMascot.draw(canvas, this.victoryArea, this.celebration, now(),
                animationsReduced(), themeColor(this.theme), this.theme);
        float f15 = this.d;
        float f16 = this.H;
        RectF rectF4 = new RectF(f15 * 20.0f, 0.60f * f16, this.W - (f15 * 20.0f), f16 * 0.72f);
        this.storyBtn.set(rectF4);
        round(canvas, this.storyBtn, 18.0f, themeColor(this.theme));
        color(872415231);
        float iconRadius = Math.min(this.d * 24f, rectF4.height() * 0.32f);
        canvas.drawCircle(rectF4.left + (this.d * 34.0f), rectF4.centerY(), iconRadius, this.p);
        label(canvas, "♪", (this.d * 34.0f) + rectF4.left, rectF4.centerY(), 26.0f, -1, Paint.Align.CENTER);
        float f17 = ((rectF4.left + (this.d * 64.0f)) + rectF4.right) / 2.0f;
        if (this.earnedBadge.isEmpty()) {
            sb2 = new StringBuilder("CADEAU DU MONDE ");
            strThemeName = themeName(this.theme);
        } else {
            sb2 = new StringBuilder("★  NOUVEAU BADGE : ");
            strThemeName = this.earnedBadge;
        }
        sb2.append(strThemeName);
        fitLabel(canvas, sb2.toString(), f17, rectF4.top + rectF4.height() * 0.27f, 10.0f, 8.0f, rectF4.width() - (this.d * 80.0f), -285212673);
        fitLabel(canvas, "ÉCOUTER L’HISTOIRE  ▶", f17, rectF4.top + rectF4.height() * 0.65f, 17.0f, 12.0f, rectF4.width() - (this.d * 80.0f), -1);
        RectF rectF5 = this.play;
        float f18 = this.d;
        float f19 = this.H;
        rectF5.set(f18 * 20.0f, 0.74f * f19, this.W - (f18 * 20.0f), f19 * 0.825f);
        round(canvas, this.play, 18.0f, this.coral);
        label(canvas, "REJOUER  ↻", this.play.centerX(), this.play.centerY(), 19.0f, -1, Paint.Align.CENTER);
        if (this.suggestedPairs > 0) {
            canvas2 = canvas;
            label(canvas2, "★  Lumi propose ensuite : " + this.suggestedPairs + " paires", this.W / 2.0f, 0.843f * this.H, 10.0f, this.purple, Paint.Align.CENTER);
        } else {
            canvas2 = canvas;
        }
        float f20 = this.d;
        float f21 = 10.0f * f20;
        float f22 = ((this.W - (40.0f * f20)) - f21) / 2.0f;
        float f23 = this.H;
        this.albumBtn.set(f20 * 20.0f, f23 * 0.865f, (f20 * 20.0f) + f22, f23 * 0.945f);
        RectF rectF6 = this.homeBtn;
        float f24 = this.albumBtn.right + f21;
        float f25 = this.H;
        rectF6.set(f24, 0.865f * f25, this.W - (this.d * 20.0f), f25 * 0.945f);
        round(canvas2, this.albumBtn, 18.0f, -1);
        round(canvas2, this.homeBtn, 18.0f, -1);
        label(canvas2, "★  MON ALBUM", this.albumBtn.centerX(), this.albumBtn.centerY(), 12.0f, this.purple, Paint.Align.CENTER);
        fitLabel(canvas, "CHOISIR UN MONDE", this.homeBtn.centerX(), this.homeBtn.centerY(), 12f, 9f, this.homeBtn.width() - 12f * this.d, this.ink);
        if (animationsReduced() || !this.activityActive) {
            return;
        }
        postInvalidateDelayed(33L);
    }

    void drawStory(Canvas canvas) {
        float fSystemTop = systemTop();
        RectF rectF = this.homeBtn;
        float f = this.d;
        rectF.set(f * 18.0f, (8.0f * f) + fSystemTop, 66.0f * f, (f * 54.0f) + fSystemTop);
        round(canvas, this.homeBtn, 16.0f, -1);
        label(canvas, "‹", this.homeBtn.centerX(), this.homeBtn.centerY() - (this.d * 2.0f), 32.0f, this.ink, Paint.Align.CENTER);
        label(canvas, this.storyPlaying ? "Chut… écoute bien !" : "Prêt pour une histoire ?", (this.homeBtn.right + this.W) / 2.0f, (this.d * 31.0f) + fSystemTop, 21.0f, this.ink, Paint.Align.CENTER);
        float f2 = this.d;
        RectF rectF2 = new RectF(f2 * 20.0f, fSystemTop + (72.0f * f2), this.W - (f2 * 20.0f), this.H * 0.79f);
        round(canvas, rectF2, 30.0f, -516);
        RectF rectF3 = new RectF(rectF2.left + (this.d * 18.0f), rectF2.top + (this.d * 15.0f), rectF2.right - (this.d * 18.0f), rectF2.top + (this.d * 45.0f));
        round(canvas, rectF3, 14.0f, (themeColor(this.theme) & 16777215) | 570425344);
        label(canvas, "★  MONDE " + themeName(this.theme), rectF3.centerX(), rectF3.centerY(), 10.0f, themeColor(this.theme), Paint.Align.CENTER);
        float f3 = (this.W / 2.0f) - (this.d * 70.0f);
        float f4 = rectF2.top;
        float f5 = this.d;
        drawAnimalFrom(canvas, this.theme, (this.moves + 3) % 12, new RectF(f3, f4 + (57.0f * f5), (this.W / 2.0f) + (f5 * 70.0f), rectF2.top + (this.d * 190.0f)), 26.0f);
        fitLabel(canvas, StoryLibrary.title(this.theme), this.W / 2.0f, rectF2.top + (this.d * 218.0f), 22.0f, 15.0f, rectF2.width() - (this.d * 30.0f), themeColor(this.theme));
        String str = StoryLibrary.summary(this.theme);
        float f6 = (this.d * 24.0f) + rectF2.left;
        float f7 = rectF2.top + (this.d * 253.0f);
        float fWidth = rectF2.width();
        float f8 = this.d;
        drawWrappedText(canvas, str, f6, f7, fWidth - (48.0f * f8), 15.0f, f8 * 25.0f, this.ink);
        label(canvas, "Une histoire longue racontée rien que pour toi", this.W / 2.0f, rectF2.bottom - (this.d * 24.0f), 10.0f, -7107695, Paint.Align.CENTER);
        RectF rectF4 = this.storyPlayBtn;
        float f9 = this.d;
        float f10 = this.H;
        rectF4.set(f9 * 32.0f, 0.82f * f10, this.W - (f9 * 32.0f), f10 * 0.9f);
        round(canvas, this.storyPlayBtn, 25.0f, this.storyPlaying ? this.coral : themeColor(this.theme));
        label(canvas, this.storyPlaying ? "■  ARRÊTER L'HISTOIRE" : "▶  ÉCOUTER MON HISTOIRE", this.storyPlayBtn.centerX(), this.storyPlayBtn.centerY(), 16.0f, -1, Paint.Align.CENTER);
        label(canvas, this.storyPlaying ? "La voix magique raconte…" : "Installe-toi bien, puis touche le bouton", this.W / 2.0f, 0.935f * this.H, 11.0f, -8950152, Paint.Align.CENTER);
    }

    void drawWrappedText(Canvas canvas, String str, float f, float f2, float f3, float f4, float f5, int i) {
        this.text.setTextSize(f4 * this.d);
        this.text.setTypeface(Typeface.create("sans", Typeface.NORMAL));
        String str2 = "";
        for (String str3 : str.split(" ")) {
            String str4 = str2.isEmpty() ? str3 : str2 + " " + str3;
            if (this.text.measureText(str4) <= f3 || str2.isEmpty()) {
                str2 = str4;
            } else {
                this.text.setColor(i);
                this.text.setTextAlign(Paint.Align.LEFT);
                canvas.drawText(str2, f, f2, this.text);
                f2 += f5;
                str2 = str3;
            }
        }
        if (!str2.isEmpty()) {
            this.text.setColor(i);
            this.text.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(str2, f, f2, this.text);
        }
        this.text.setTypeface(Typeface.create("sans", Typeface.BOLD));
    }

    /* JADX INFO: renamed from: lambda$toggleStory$0$fr-memomimi-app-GameView, reason: not valid java name */
    /* synthetic */ void m4lambda$toggleStory$0$frmemomimiappGameView(MediaPlayer mediaPlayer) {
        this.storyPlaying = false;
        mediaPlayer.release();
        this.narration = null;
        updateMusic();
        invalidate();
    }

    /* JADX INFO: renamed from: lambda$toggleStory$1$fr-memomimi-app-GameView, reason: not valid java name */
    /* synthetic */ void m5lambda$toggleStory$1$frmemomimiappGameView(int i) {
        if (i != 0 || !this.storyPlaying) {
            finishDeviceStory();
            return;
        }
        this.tts.setLanguage(Locale.FRANCE);
        this.tts.setSpeechRate(0.88f);
        this.tts.setPitch(1.08f);
        this.tts.setOnUtteranceProgressListener(new UtteranceProgressListener() { // from class: fr.memomimi.app.GameView.1
            @Override // android.speech.tts.UtteranceProgressListener
            public void onStart(String str) {
            }

            @Override // android.speech.tts.UtteranceProgressListener
            public void onDone(String str) {
                GameView.this.finishDeviceStory();
            }

            @Override // android.speech.tts.UtteranceProgressListener
            public void onError(String str) {
                GameView.this.finishDeviceStory();
            }
        });
        this.tts.speak(StoryLibrary.text(this.theme), 0, null, "story");
    }

    void toggleStory() {
        if (this.storyPlaying) {
            stopNarration();
            return;
        }
        stopMusic();
        try {
            AssetFileDescriptor assetFileDescriptorOpenFd = getContext().getAssets().openFd(StoryLibrary.assetName(this.theme));
            MediaPlayer mediaPlayer = new MediaPlayer();
            this.narration = mediaPlayer;
            mediaPlayer.setDataSource(assetFileDescriptorOpenFd.getFileDescriptor(), assetFileDescriptorOpenFd.getStartOffset(), assetFileDescriptorOpenFd.getLength());
            assetFileDescriptorOpenFd.close();
            this.narration.prepare();
            this.narration.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: fr.memomimi.app.GameView$$ExternalSyntheticLambda2
                @Override // android.media.MediaPlayer.OnCompletionListener
                public final void onCompletion(MediaPlayer mediaPlayer2) {
                    GameView.this.m4lambda$toggleStory$0$frmemomimiappGameView(mediaPlayer2);
                }
            });
            this.narration.start();
            this.storyPlaying = true;
            invalidate();
        } catch (Exception unused) {
            this.storyPlaying = true;
            TextToSpeech textToSpeech = this.tts;
            if (textToSpeech == null) {
                this.tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() { // from class: fr.memomimi.app.GameView$$ExternalSyntheticLambda3
                    @Override // android.speech.tts.TextToSpeech.OnInitListener
                    public final void onInit(int i) {
                        GameView.this.m5lambda$toggleStory$1$frmemomimiappGameView(i);
                    }
                });
            } else {
                textToSpeech.speak(StoryLibrary.text(this.theme), 0, null, "story");
            }
            invalidate();
        }
    }

    void finishDeviceStory() {
        post(new Runnable() { // from class: fr.memomimi.app.GameView$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                GameView.this.m0lambda$finishDeviceStory$2$frmemomimiappGameView();
            }
        });
    }

    /* JADX INFO: renamed from: lambda$finishDeviceStory$2$fr-memomimi-app-GameView, reason: not valid java name */
    /* synthetic */ void m0lambda$finishDeviceStory$2$frmemomimiappGameView() {
        this.storyPlaying = false;
        updateMusic();
        invalidate();
    }

    void stopNarration() {
        MediaPlayer mediaPlayer = this.narration;
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
            } catch (Exception unused) {
            }
            this.narration.release();
            this.narration = null;
        }
        TextToSpeech textToSpeech = this.tts;
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
        this.storyPlaying = false;
        updateMusic();
        invalidate();
    }

    void pauseNarration() {
        MediaPlayer mediaPlayer = this.narration;
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
            } catch (Exception unused) {
            }
            this.narration.release();
            this.narration = null;
        }
        TextToSpeech textToSpeech = this.tts;
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
        this.storyPlaying = false;
    }

    void releaseNarration() {
        stopNarration();
        TextToSpeech textToSpeech = this.tts;
        if (textToSpeech != null) {
            textToSpeech.shutdown();
            this.tts = null;
        }
    }

    int albumMask(int i) {
        return getContext().getSharedPreferences("m", 0).getInt("album_" + i, 0);
    }

    int albumCount() {
        int iBitCount = 0;
        for (int i = 0; i < WORLD_COUNT; i++) {
            iBitCount += Integer.bitCount(albumMask(i));
        }
        return iBitCount;
    }

    int badgeCount() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("m", 0);
        int first = sharedPreferences.getBoolean("badge_first", false) ? 1 : 0;
        int i = first;
        if (sharedPreferences.getBoolean("badge_explorer", false)) {
            i = first + 1;
        }
        int i2 = i;
        if (sharedPreferences.getBoolean("badge_five", false)) {
            i2 = i + 1;
        }
        return sharedPreferences.getBoolean("badge_mega", false) ? i2 + 1 : i2;
    }

    void unlockProgress() {
        this.progress.recordCompleted(this.theme, this.pairs);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("m", 0);
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        this.newUnlocks = 0;
        int i = sharedPreferences.getInt("album_" + this.theme, 0);
        int i2 = 12;
        int iMin = ((1 << Math.min(12, this.pairs)) - 1) | i;
        this.newUnlocks = Integer.bitCount(i ^ iMin);
        editorEdit.putInt("album_" + this.theme, iMin);
        int i3 = sharedPreferences.getInt("wins", 0) + 1;
        int i4 = sharedPreferences.getInt("won_worlds", 0) | (1 << this.theme);
        int i5 = this.pairs;
        if (i5 == 6) {
            i2 = 8;
        } else if (i5 == 8) {
            i2 = 10;
        } else if (i5 != 10) {
            i2 = 0;
        }
        this.suggestedPairs = i2;
        editorEdit.putInt("wins", i3).putInt("won_worlds", i4).putInt("suggested_pairs", this.suggestedPairs);
        if (!sharedPreferences.getBoolean("badge_first", false)) {
            editorEdit.putBoolean("badge_first", true);
            this.earnedBadge = "Première victoire";
        }
        if (i3 >= 5 && !sharedPreferences.getBoolean("badge_five", false)) {
            editorEdit.putBoolean("badge_five", true);
            this.earnedBadge = "Super mémoire";
        }
        if (Integer.bitCount(i4) >= 3 && !sharedPreferences.getBoolean("badge_explorer", false)) {
            editorEdit.putBoolean("badge_explorer", true);
            this.earnedBadge = "Grand explorateur";
        }
        if (Integer.bitCount(i4) >= WORLD_COUNT && !sharedPreferences.getBoolean("badge_mega", false)) {
            editorEdit.putBoolean("badge_mega", true);
            this.earnedBadge = "Ami de tous les mondes";
        }
        editorEdit.apply();
    }

    void drawAlbum(Canvas canvas) {
        float fSystemTop = systemTop();
        RectF rectF = this.homeBtn;
        float f = this.d;
        rectF.set(18.0f * f, (8.0f * f) + fSystemTop, 66.0f * f, (f * 54.0f) + fSystemTop);
        round(canvas, this.homeBtn, 16.0f, -1);
        label(canvas, "‹", this.homeBtn.centerX(), this.homeBtn.centerY() - (this.d * 2.0f), 32.0f, this.ink, Paint.Align.CENTER);
        label(canvas, "Mon album", this.W / 2.0f, fSystemTop + (this.d * 31.0f), 27.0f, this.ink, Paint.Align.CENTER);
        RectF rectF2 = this.albumPrev;
        float f2 = this.d;
        rectF2.set(f2 * 20.0f, (f2 * 64.0f) + fSystemTop, f2 * 64.0f, (f2 * 106.0f) + fSystemTop);
        RectF rectF3 = this.albumNext;
        float f3 = this.W;
        float f4 = this.d;
        rectF3.set(f3 - (f4 * 64.0f), (64.0f * f4) + fSystemTop, f3 - (20.0f * f4), (f4 * 106.0f) + fSystemTop);
        round(canvas, this.albumPrev, 15.0f, -1);
        round(canvas, this.albumNext, 15.0f, -1);
        label(canvas, "‹", this.albumPrev.centerX(), this.albumPrev.centerY() - (this.d * 2.0f), 28.0f, this.ink, Paint.Align.CENTER);
        label(canvas, "›", this.albumNext.centerX(), this.albumNext.centerY() - (this.d * 2.0f), 28.0f, this.ink, Paint.Align.CENTER);
        label(canvas, themeName(this.albumPage) + "  " + Integer.bitCount(albumMask(this.albumPage)) + " / 12", this.W / 2.0f, (this.d * 85.0f) + fSystemTop, 16.0f, themeColor(this.albumPage), Paint.Align.CENTER);
        float f5 = this.d;
        float f6 = fSystemTop + (118.0f * f5);
        float tabGap = 6.0f * f5;
        float tabWidth = (this.W - 2 * tabGap - 2 * (28.0f * f5)) / 3.0f;
        this.worldsTab.set(28.0f * f5, f6, 28.0f * f5 + tabWidth, f6 + 42.0f * f5);
        this.imagesTab.set(this.worldsTab.right + tabGap, f6,
                this.worldsTab.right + tabGap + tabWidth, f6 + 42.0f * f5);
        this.badgesTab.set(this.imagesTab.right + tabGap, f6,
                this.imagesTab.right + tabGap + tabWidth, f6 + 42.0f * f5);
        round(canvas, this.worldsTab, 16.0f, this.albumTab == 2 ? this.mint : -1);
        round(canvas, this.imagesTab, 16.0f, this.albumTab == 0 ? themeColor(this.albumPage) : -1);
        round(canvas, this.badgesTab, 16.0f, this.albumTab == 1 ? this.purple : -1);
        label(canvas, "MONDES", this.worldsTab.centerX(), this.worldsTab.centerY(), 10.0f, this.albumTab == 2 ? -1 : this.ink, Paint.Align.CENTER);
        label(canvas, "IMAGES", this.imagesTab.centerX(), this.imagesTab.centerY(), 10.0f, this.albumTab == 0 ? -1 : this.ink, Paint.Align.CENTER);
        label(canvas, "BADGES", this.badgesTab.centerX(), this.badgesTab.centerY(), 10.0f, this.albumTab == 1 ? -1 : this.ink, Paint.Align.CENTER);
        float f9 = f6 + (this.d * 56.0f);
        float f10 = this.d;
        RectF rectF5 = new RectF(f10 * 35.0f, f9, this.W - (35.0f * f10), (f10 * 12.0f) + f9);
        round(canvas, rectF5, 6.0f, -1580827);
        round(canvas, new RectF(rectF5.left, rectF5.top, rectF5.left + ((rectF5.width() * albumCount()) / ALBUM_TOTAL), rectF5.bottom), 6.0f, this.mint);
        label(canvas, albumCount() + " / " + ALBUM_TOTAL + " images  •  " + badgeCount() + " / 4 badges", this.W / 2.0f, (this.d * 31.0f) + f9, 11.0f, -8950152, Paint.Align.CENTER);
        if (this.albumTab == 2) {
            drawAlbumWorlds(canvas, f9 + (this.d * 48.0f));
        } else if (this.albumTab == 0) {
            drawAlbumImages(canvas, f9 + (this.d * 48.0f));
        } else {
            drawBadgeCards(canvas, f9 + (this.d * 53.0f));
        }
        if (this.enlargedItem >= 0) {
            drawLargeAlbumImage(canvas);
        }
    }

    void drawAlbumImages(Canvas canvas, float f) {
        GameView gameView = this;
        int iAlbumMask = gameView.albumMask(gameView.albumPage);
        float f4 = gameView.d;
        float f5 = f4 * 9.0f;
        float f7 = f4 * 22.0f;
        float f8 = ((gameView.W - (f7 * 2.0f)) - (2.0f * f5)) / 3.0f;
        float fMin = Math.min(f8, f4 * 105.0f);
        int i3 = 0;
        while (i3 < 12) {
            float f9 = ((i3 % 3) * (f8 + f5)) + f7;
            float f10 = f + ((i3 / 3) * (fMin + f5));
            RectF rectF = gameView.albumImageBounds[i3];
            rectF.set(f9, f10, f9 + f8, f10 + fMin);
            if (((1 << i3) & iAlbumMask) != 0) {
                gameView.round(canvas, rectF, 17.0f, -1);
                gameView.drawAnimalFrom(canvas, gameView.albumPage, i3, new RectF(rectF.left + (gameView.d * 4.0f), rectF.top + (gameView.d * 4.0f), rectF.right - (gameView.d * 4.0f), rectF.bottom - (gameView.d * 4.0f)), 14.0f);
                if (gameView.newUnlocks > 0 && gameView.albumPage == gameView.theme && now() - gameView.winAt < 120000) {
                    gameView.label(canvas, "✦", rectF.right - (gameView.d * 10.0f),
                            rectF.top + (gameView.d * 10.0f), 12.0f, -18881, Paint.Align.CENTER);
                }
            } else {
                gameView.round(canvas, rectF, 17.0f, -1448729);
                gameView.label(canvas, "★", rectF.centerX(), rectF.centerY() - (gameView.d * 7.0f), 22.0f, -4344643, Paint.Align.CENTER);
                gameView.label(canvas, "À trouver", rectF.centerX(),
                        rectF.centerY() + (gameView.d * 22.0f), 9.0f, -7107695, Paint.Align.CENTER);
            }
            i3++;
        }
    }

    void drawBadgeCards(Canvas canvas, float f) {
        GameView gameView = this;
        Canvas canvas2 = canvas;
        String[] strArr = {"PREMIÈRE VICTOIRE", "GRAND EXPLORATEUR", "SUPER MÉMOIRE", "AMI DE TOUS"};
        String[] strArr2 = {"Termine une partie", "Explore 3 mondes", "Gagne 5 parties", "Explore les 18 mondes"};
        String[] strArr3 = {"badge_first", "badge_explorer", "badge_five", "badge_mega"};
        SharedPreferences sharedPreferencesPrefs = gameView.progress.prefs();
        float f2 = gameView.d;
        float f3 = f2 * 12.0f;
        float f4 = f2 * 24.0f;
        float f5 = ((gameView.W - (f4 * 2.0f)) - f3) / 2.0f;
        float f6 = f2 * 150.0f;
        boolean z = false;
        int i = 0;
        while (i < 4) {
            float f7 = ((i % 2) * (f5 + f3)) + f4;
            float f8 = f + ((i / 2) * (f6 + f3));
            RectF rectF = new RectF(f7, f8, f7 + f5, f8 + f6);
            boolean z2 = sharedPreferencesPrefs.getBoolean(strArr3[i], z);
            gameView.round(canvas2, rectF, 22.0f, z2 ? -1 : -1448729);
            gameView.color(z2 ? -14249 : -4344643);
            float fCenterX = rectF.centerX();
            float f9 = rectF.top;
            float f10 = gameView.d;
            canvas2.drawCircle(fCenterX, f9 + (42.0f * f10), f10 * 25.0f, gameView.p);
            int i2 = i;
            gameView.label(canvas2, z2 ? "★" : "?", rectF.centerX(), rectF.top + (gameView.d * 41.0f), 26.0f, -1, Paint.Align.CENTER);
            boolean z3 = z;
            gameView.fitLabel(canvas, strArr[i2], rectF.centerX(), rectF.top + (gameView.d * 87.0f), 11.0f, 8.0f, rectF.width() - (gameView.d * 14.0f), z2 ? gameView.ink : -7107695);
            gameView.fitLabel(canvas, strArr2[i2], rectF.centerX(), rectF.top + (gameView.d * 116.0f), 10.0f, 8.0f, rectF.width() - (gameView.d * 12.0f), -8950152);
            canvas2 = canvas;
            gameView.label(canvas2, z2 ? "✓ GAGNÉ" : "À DÉBLOQUER", rectF.centerX(), rectF.top + (gameView.d * 139.0f), 9.0f, z2 ? gameView.mint : -7107695, Paint.Align.CENTER);
            i = i2 + 1;
            gameView = this;
            z = z3;
        }
    }

    int albumImageAt(float x, float y) {
        for (int index = 0; index < this.albumImageBounds.length; index++) {
            if (this.albumImageBounds[index].contains(x, y) && (albumMask(this.albumPage) & (1 << index)) != 0) return index;
        }
        return -1;
    }

    void drawLargeAlbumImage(Canvas canvas) {
        color(-1442840576);
        canvas.drawRect(0.0f, 0.0f, this.W, this.H, this.p);
        float f = this.d;
        float f2 = this.H;
        round(canvas, new RectF(f * 34.0f, 0.18f * f2, this.W - (f * 34.0f), f2 * 0.76f), 28.0f, -516);
        label(canvas, themeName(this.albumPage), this.W / 2.0f, this.H * 0.245f, 21.0f, themeColor(this.albumPage), Paint.Align.CENTER);
        float f3 = this.d;
        float f4 = this.H;
        drawAnimalFrom(canvas, this.albumPage, this.enlargedItem, new RectF(f3 * 58.0f, 0.29f * f4, this.W - (f3 * 58.0f), f4 * 0.62f), 28.0f);
        label(canvas, "★  Image retrouvée !", this.W / 2.0f, this.H * 0.675f, 16.0f, this.purple, Paint.Align.CENTER);
        RectF rectF = this.enlargedClose;
        float f5 = this.W;
        float f6 = this.d;
        float f7 = this.H;
        rectF.set((f5 / 2.0f) - (f6 * 78.0f), 0.7f * f7, (f5 / 2.0f) + (f6 * 78.0f), f7 * 0.75f);
        round(canvas, this.enlargedClose, 18.0f, this.mint);
        label(canvas, "RETOUR À L'ALBUM", this.enlargedClose.centerX(), this.enlargedClose.centerY(), 12.0f, -1, Paint.Align.CENTER);
    }

    String badgeSymbols() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("m", 0);
        String str = sharedPreferences.getBoolean("badge_first", false) ? "★ " : "○ ";
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(sharedPreferences.getBoolean("badge_explorer", false) ? "★ " : "○ ");
        String string = sb.toString();
        StringBuilder sb2 = new StringBuilder();
        sb2.append(string);
        sb2.append(sharedPreferences.getBoolean("badge_five", false) ? "★ " : "○ ");
        String string2 = sb2.toString();
        StringBuilder sb3 = new StringBuilder();
        sb3.append(string2);
        sb3.append(sharedPreferences.getBoolean("badge_mega", false) ? "★" : "○");
        return sb3.toString();
    }

    void drawParents(Canvas canvas) {
        float fSystemTop = systemTop();
        RectF rectF = this.homeBtn;
        float f = this.d;
        rectF.set(f * 18.0f, (f * 8.0f) + fSystemTop, 66.0f * f, (f * 54.0f) + fSystemTop);
        round(canvas, this.homeBtn, 16.0f, -1);
        label(canvas, "‹", this.homeBtn.centerX(), this.homeBtn.centerY() - (this.d * 2.0f), 32.0f, this.ink, Paint.Align.CENTER);
        label(canvas, "Espace Parents", this.W / 2.0f, fSystemTop + (this.d * 31.0f), 26.0f, this.ink, Paint.Align.CENTER);
        label(canvas, "Tout reste sur cet appareil", this.W / 2.0f, fSystemTop + (this.d * 72.0f), 13.0f, -8950152, Paint.Align.CENTER);
        float f2 = this.d;
        float f3 = (92.0f * f2) + fSystemTop;
        float f4 = 8.0f * f2;
        float f5 = f4 * 2.0f;
        float f6 = ((this.W - (f2 * 40.0f)) - f5) / 3.0f;
        float f7 = this.d;
        statCard(canvas, new RectF(f7 * 20.0f, f3, (f7 * 20.0f) + f6, (f7 * 62.0f) + f3), String.valueOf(this.progress.completedGames()), "PARTIES");
        float f8 = this.d;
        float f9 = (f8 * 20.0f) + f6 + f4;
        float f10 = f6 * 2.0f;
        statCard(canvas, new RectF(f9, f3, (f8 * 20.0f) + f10 + f4, (f8 * 62.0f) + f3), this.progress.exploredWorlds() + " / " + WORLD_COUNT, "MONDES");
        float f11 = this.d;
        statCard(canvas, new RectF((f11 * 20.0f) + f10 + f5, f3, this.W - (f11 * 20.0f), (f11 * 62.0f) + f3), albumCount() + " / " + ALBUM_TOTAL, "ALBUM");
        label(canvas, "Niveau le plus joué : " + this.progress.preferredPairs() + " paires", this.W / 2.0f, fSystemTop + (this.d * 174.0f), 12.0f, this.purple, Paint.Align.CENTER);
        label(canvas, "Les cartes différentes restent visibles", this.W / 2.0f, fSystemTop + (this.d * 212.0f), 14.0f, this.ink, Paint.Align.CENTER);
        float f12 = this.W;
        float f13 = this.d;
        float f14 = ((f12 - (40.0f * f13)) - f5) / 3.0f;
        float f15 = (235.0f * f13) + fSystemTop;
        this.shortDelay.set(f13 * 20.0f, f15, (20.0f * f13) + f14, (f13 * 56.0f) + f15);
        this.normalDelay.set(this.shortDelay.right + f4, f15, this.shortDelay.right + f4 + f14, (this.d * 56.0f) + f15);
        this.longDelay.set(this.normalDelay.right + f4, f15, this.normalDelay.right + f4 + f14, (this.d * 56.0f) + f15);
        parentChoice(canvas, this.shortDelay, "COURT", "0,8 s", this.mismatchDelay == 800);
        parentChoice(canvas, this.normalDelay, "NORMAL", "1,3 s", this.mismatchDelay == 1300);
        parentChoice(canvas, this.longDelay, "LONG", "2 s", this.mismatchDelay == 2000);
        RectF rectF2 = this.calmBtn;
        float f16 = this.d;
        rectF2.set(f16 * 28.0f, (310.0f * f16) + fSystemTop, this.W - (f16 * 28.0f), (f16 * 363.0f) + fSystemTop);
        round(canvas, this.calmBtn, 18.0f, this.calm ? this.mint : -1);
        label(canvas, "☁  MODE CALME  ".concat(this.calm ? "OUI" : "NON"), this.calmBtn.centerX(), this.calmBtn.centerY(), 14.0f, this.calm ? -1 : this.ink, Paint.Align.CENTER);
        RectF rectF3 = this.hapticBtn;
        float f17 = this.d;
        rectF3.set(f17 * 28.0f, (375.0f * f17) + fSystemTop, this.W - (f17 * 28.0f), (f17 * 428.0f) + fSystemTop);
        round(canvas, this.hapticBtn, 18.0f, this.haptics ? this.purple : -1);
        label(canvas, "✦  VIBRATIONS  ".concat(this.haptics ? "OUI" : "NON"), this.hapticBtn.centerX(), this.hapticBtn.centerY(), 14.0f, this.haptics ? -1 : this.ink, Paint.Align.CENTER);
        RectF rectF4 = this.resetBtn;
        float f18 = this.d;
        rectF4.set(f18 * 32.0f, (448.0f * f18) + fSystemTop, this.W - (f18 * 32.0f), (f18 * 498.0f) + fSystemTop);
        round(canvas, this.resetBtn, 18.0f, -1);
        label(canvas, "EFFACER RECORDS ET STATISTIQUES", this.resetBtn.centerX(), this.resetBtn.centerY(), 11.0f, this.coral, Paint.Align.CENTER);
        RectF rectF5 = this.tutorialResetBtn;
        float f19 = this.d;
        rectF5.set(f19 * 32.0f, (510.0f * f19) + fSystemTop, this.W - (32.0f * f19), (f19 * 560.0f) + fSystemTop);
        round(canvas, this.tutorialResetBtn, 18.0f, -1);
        label(canvas, "★  REVOIR LE TUTORIEL DE LUMI", this.tutorialResetBtn.centerX(), this.tutorialResetBtn.centerY(), 12.0f, this.purple, Paint.Align.CENTER);
        if (now() < this.resetDoneUntil) {
            label(canvas, "✓  " + this.parentNotice, this.W / 2.0f, (this.d * 588.0f) + fSystemTop, 12.0f, this.mint, Paint.Align.CENTER);
            postInvalidateDelayed(200L);
        }
        label(canvas, "Sans compte  •  Sans publicité  •  Hors ligne", this.W / 2.0f, this.H - (this.d * 28.0f), 11.0f, -7107695, Paint.Align.CENTER);
    }

    void statCard(Canvas canvas, RectF rectF, String str, String str2) {
        round(canvas, rectF, 17.0f, -1);
        label(canvas, str, rectF.centerX(), (this.d * 23.0f) + rectF.top, 17.0f, themeColor(this.theme), Paint.Align.CENTER);
        label(canvas, str2, rectF.centerX(), (this.d * 45.0f) + rectF.top, 8.0f, -8950152, Paint.Align.CENTER);
    }

    void parentChoice(Canvas canvas, RectF rectF, String str, String str2, boolean z) {
        round(canvas, rectF, 18.0f, z ? this.purple : -1);
        label(canvas, str, rectF.centerX(), (this.d * 21.0f) + rectF.top, 10.0f, z ? -1 : -8950152, Paint.Align.CENTER);
        label(canvas, str2, rectF.centerX(), (this.d * 43.0f) + rectF.top, 15.0f, z ? -1 : this.ink, Paint.Align.CENTER);
    }

    void drawThemesGrid(Canvas canvas) {
        float topInset = systemTop();
        float scale = this.d;
        this.homeBtn.set(18 * scale, topInset + 8 * scale, 66 * scale, topInset + 54 * scale);
        round(canvas, this.homeBtn, 16, -1);
        label(canvas, "‹", this.homeBtn.centerX(), this.homeBtn.centerY() - 2 * scale, 32, this.ink, Paint.Align.CENTER);
        label(canvas, "Choisis ton monde", this.W / 2, topInset + 32 * scale, 24, this.ink, Paint.Align.CENTER);
        label(canvas, "18 aventures à découvrir", this.W / 2, topInset + 68 * scale, 13, -8950152, Paint.Align.CENTER);
        float gap = 8 * scale;
        float left = 16 * scale;
        float width = (this.W - 2 * left - 2 * gap) / 3;
        float startY = topInset + 88 * scale;
        float bottomInset = getRootWindowInsets() == null ? 26 * scale :
                Math.max(26 * scale, getRootWindowInsets().getSystemWindowInsetBottom() + 12 * scale);
        float height = (this.H - startY - bottomInset - 5 * gap) / 6;
        for (int world = 0; world < WORLD_COUNT; world++) {
            float x = left + (world % 3) * (width + gap);
            float y = startY + (world / 3) * (height + gap);
            RectF tile = this.themeTiles[world];
            tile.set(x, y, x + width, y + height);
            drawThemeGridTile(canvas, tile, world);
        }
    }

    void drawThemeGridTile(Canvas canvas, RectF tile, int world) {
        boolean selected = this.theme == world;
        int accent = themeColor(world);
        round(canvas, tile, 17, selected ? blend(accent, -1, 0.84f) : -1);
        if (selected) {
            this.p.setStyle(Paint.Style.STROKE);
            this.p.setStrokeWidth(3 * this.d);
            this.p.setColor(accent);
            canvas.drawRoundRect(tile, 17 * this.d, 17 * this.d, this.p);
            this.p.setStyle(Paint.Style.FILL);
        }
        float imageSize = Math.min(tile.width() - 16 * this.d, tile.height() * 0.60f);
        RectF image = new RectF(tile.centerX() - imageSize / 2,
                tile.top + 3 * this.d,
                tile.centerX() + imageSize / 2,
                tile.top + 3 * this.d + imageSize);
        drawThemeSticker(canvas, world, image);
        fitLabel(canvas, themeName(world), tile.centerX(), tile.bottom - 13 * this.d,
                10, 7.5f, tile.width() - 8 * this.d, this.ink);
        if (selected) {
            color(accent);
            canvas.drawCircle(tile.right - 12 * this.d, tile.top + 12 * this.d, 9 * this.d, this.p);
            label(canvas, "✓", tile.right - 12 * this.d, tile.top + 11 * this.d,
                    9, -1, Paint.Align.CENTER);
        }
    }

    RectF albumWorldRect(int world, float top) {
        float margin = 20.0f * this.d;
        float gap = 8.0f * this.d;
        float width = (this.W - 2 * margin - 2 * gap) / 3.0f;
        float height = Math.min(84.0f * this.d,
                (this.H - top - 22.0f * this.d - 5 * gap) / 6.0f);
        float x = margin + (world % 3) * (width + gap);
        float y = top + (world / 3) * (height + gap);
        return new RectF(x, y, x + width, y + height);
    }

    void drawAlbumWorlds(Canvas canvas, float top) {
        for (int world = 0; world < WORLD_COUNT; world++) {
            RectF tile = albumWorldRect(world, top);
            round(canvas, tile, 16.0f, -1);
            float imageSize = Math.min(48.0f * this.d, tile.height() * 0.59f);
            RectF image = new RectF(tile.centerX() - imageSize / 2, tile.top + 3.0f * this.d,
                    tile.centerX() + imageSize / 2, tile.top + 3.0f * this.d + imageSize);
            drawThemeSticker(canvas, world, image);
            fitLabel(canvas, themeName(world), tile.centerX(), tile.bottom - 17.0f * this.d,
                    10.0f, 7.0f, tile.width() - 8.0f * this.d, this.ink);
            label(canvas, Integer.bitCount(albumMask(world)) + " / 12", tile.centerX(),
                    tile.bottom - 5.0f * this.d, 9.0f, themeColor(world), Paint.Align.CENTER);
        }
    }

    int albumWorldAt(float x, float y) {
        float top = systemTop() + (118.0f + 56.0f + 48.0f) * this.d;
        for (int world = 0; world < WORLD_COUNT; world++) {
            if (albumWorldRect(world, top).contains(x, y)) return world;
        }
        return -1;
    }

    void drawThemes(Canvas canvas) {
        if (this.themeTiles.length > 0) {
            drawThemesGrid(canvas);
            return;
        }
        float fSystemTop = systemTop();
        RectF rectF = this.homeBtn;
        float f = this.d;
        rectF.set(f * 18.0f, (8.0f * f) + fSystemTop, 66.0f * f, (f * 54.0f) + fSystemTop);
        round(canvas, this.homeBtn, 16.0f, -1);
        label(canvas, "‹", this.homeBtn.centerX(), this.homeBtn.centerY() - (this.d * 2.0f), 32.0f, this.ink, Paint.Align.CENTER);
        label(canvas, "Choisis ton monde", this.W / 2.0f, fSystemTop + (this.d * 31.0f), 27.0f, this.ink, Paint.Align.CENTER);
        label(canvas, "Touche celui que tu préfères", this.W / 2.0f, fSystemTop + (this.d * 72.0f), 14.0f, -8950152, Paint.Align.CENTER);
        float f2 = this.d;
        float f3 = 10.0f * f2;
        float f4 = ((this.W - (42.0f * f2)) - f3) / 2.0f;
        float f5 = fSystemTop + (91.0f * f2);
        float f6 = f2 * 7.0f;
        float fMax = Math.max(48.0f * f2, Math.min(61.0f * f2, (((this.H - f5) - (f2 * 18.0f)) - (6.0f * f6)) / 7.0f));
        RectF rectF2 = this.animalsTheme;
        float f7 = this.d;
        float f8 = f5 + fMax;
        rectF2.set(f7 * 21.0f, f5, (f7 * 21.0f) + f4, f8);
        this.oceanTheme.set(this.animalsTheme.right + f3, f5, this.W - (this.d * 21.0f), f8);
        this.spaceTheme.set(this.d * 21.0f, this.animalsTheme.bottom + f6, (this.d * 21.0f) + f4, this.animalsTheme.bottom + f6 + fMax);
        this.dinoTheme.set(this.spaceTheme.right + f3, this.spaceTheme.top, this.W - (this.d * 21.0f), this.spaceTheme.bottom);
        this.farmTheme.set(this.d * 21.0f, this.spaceTheme.bottom + f6, (this.d * 21.0f) + f4, this.spaceTheme.bottom + f6 + fMax);
        this.vehiclesTheme.set(this.farmTheme.right + f3, this.farmTheme.top, this.W - (this.d * 21.0f), this.farmTheme.bottom);
        this.magicTheme.set(this.d * 21.0f, this.farmTheme.bottom + f6, (this.d * 21.0f) + f4, this.farmTheme.bottom + f6 + fMax);
        this.mermaidTheme.set(this.magicTheme.right + f3, this.magicTheme.top, this.W - (this.d * 21.0f), this.magicTheme.bottom);
        this.jungleTheme.set(this.d * 21.0f, this.magicTheme.bottom + f6, (this.d * 21.0f) + f4, this.magicTheme.bottom + f6 + fMax);
        this.treatsTheme.set(this.jungleTheme.right + f3, this.jungleTheme.top, this.W - (this.d * 21.0f), this.jungleTheme.bottom);
        this.pirateTheme.set(this.d * 21.0f, this.jungleTheme.bottom + f6, (this.d * 21.0f) + f4, this.jungleTheme.bottom + f6 + fMax);
        this.arcticTheme.set(this.pirateTheme.right + f3, this.pirateTheme.top, this.W - (this.d * 21.0f), this.pirateTheme.bottom);
        this.musicTheme.set(this.d * 21.0f, this.pirateTheme.bottom + f6, (this.d * 21.0f) + f4, this.pirateTheme.bottom + f6 + fMax);
        this.jobsTheme.set(this.musicTheme.right + f3, this.musicTheme.top, this.W - (this.d * 21.0f), this.musicTheme.bottom);
        themeChoice(canvas, this.animalsTheme, 0, "ANIMAUX", themeColor(0));
        themeChoice(canvas, this.oceanTheme, 1, "OCÉAN", themeColor(1));
        themeChoice(canvas, this.spaceTheme, 2, "ESPACE", themeColor(2));
        themeChoice(canvas, this.dinoTheme, 3, "DINOSAURES", themeColor(3));
        themeChoice(canvas, this.farmTheme, 4, "FERME", themeColor(4));
        themeChoice(canvas, this.vehiclesTheme, 5, "VÉHICULES", themeColor(5));
        themeChoice(canvas, this.magicTheme, 6, "MAGIE", themeColor(6));
        themeChoice(canvas, this.mermaidTheme, 7, "SIRÈNES", themeColor(7));
        themeChoice(canvas, this.jungleTheme, 8, "JUNGLE", themeColor(8));
        themeChoice(canvas, this.treatsTheme, 9, "GOURMANDISES", themeColor(9));
        themeChoice(canvas, this.pirateTheme, 10, "PIRATES", themeColor(10));
        themeChoice(canvas, this.arcticTheme, 11, "BANQUISE", themeColor(11));
        themeChoice(canvas, this.musicTheme, 12, "MUSIQUE", themeColor(12));
        themeChoice(canvas, this.jobsTheme, 13, "MÉTIERS", themeColor(13));
    }

    void drawResumeOverlay(Canvas canvas) {
        color(-2013265920);
        canvas.drawRect(0.0f, 0.0f, this.W, this.H, this.p);
        float f = this.d;
        float f2 = this.H;
        round(canvas, new RectF(f * 28.0f, 0.2f * f2, this.W - (f * 28.0f), f2 * 0.75f), 28.0f, -516);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("m", 0);
        int iMax = Math.max(0, Math.min(WORLD_COUNT - 1, sharedPreferences.getInt("saved_theme", this.theme)));
        int i = sharedPreferences.getInt("saved_found", 0);
        int i2 = sharedPreferences.getInt("saved_pairs", 6);
        label(canvas, "★", this.W / 2.0f, 0.265f * this.H, 42.0f, -14249, Paint.Align.CENTER);
        label(canvas, "Ta partie t'attend !", this.W / 2.0f, this.H * 0.34f, 25.0f, this.ink, Paint.Align.CENTER);
        float f3 = this.W;
        float f4 = this.d;
        float f5 = this.H;
        drawAnimalFrom(canvas, iMax, (i + 2) % 12, new RectF((f3 / 2.0f) - (f4 * 62.0f), 0.375f * f5, (f3 / 2.0f) + (f4 * 62.0f), f5 * 0.52f), 22.0f);
        label(canvas, themeName(iMax) + "  •  " + i + " / " + i2 + " paires", this.W / 2.0f, this.H * 0.565f, 14.0f, themeColor(iMax), Paint.Align.CENTER);
        RectF rectF = this.resumeBtn;
        float f6 = this.d;
        float f7 = this.H;
        rectF.set(f6 * 52.0f, 0.61f * f7, this.W - (f6 * 52.0f), f7 * 0.68f);
        round(canvas, this.resumeBtn, 22.0f, this.mint);
        label(canvas, "CONTINUER  ▶", this.resumeBtn.centerX(), this.resumeBtn.centerY(), 17.0f, -1, Paint.Align.CENTER);
        RectF rectF2 = this.newGameBtn;
        float f8 = this.d;
        float f9 = this.H;
        rectF2.set(f8 * 70.0f, 0.69f * f9, this.W - (f8 * 70.0f), f9 * 0.735f);
        label(canvas, "Choisir une nouvelle partie", this.newGameBtn.centerX(), this.newGameBtn.centerY(), 13.0f, this.purple, Paint.Align.CENTER);
    }

    void drawQuitOverlay(Canvas canvas) {
        color(-2013265920);
        canvas.drawRect(0.0f, 0.0f, this.W, this.H, this.p);
        float f = this.d;
        float f2 = this.H;
        round(canvas, new RectF(f * 30.0f, 0.29f * f2, this.W - (f * 30.0f), f2 * 0.7f), 28.0f, -516);
        label(canvas, "★", this.W / 2.0f, 0.36f * this.H, 38.0f, -14249, Paint.Align.CENTER);
        label(canvas, "Tu veux quitter ?", this.W / 2.0f, this.H * 0.43f, 25.0f, this.ink, Paint.Align.CENTER);
        label(canvas, "Ta partie sera effacée", this.W / 2.0f, this.H * 0.48f, 14.0f, -8950152, Paint.Align.CENTER);
        RectF rectF = this.quitContinueBtn;
        float f3 = this.d;
        float f4 = this.H;
        rectF.set(f3 * 52.0f, 0.535f * f4, this.W - (f3 * 52.0f), f4 * 0.6f);
        round(canvas, this.quitContinueBtn, 22.0f, this.mint);
        label(canvas, "CONTINUER À JOUER", this.quitContinueBtn.centerX(), this.quitContinueBtn.centerY(), 15.0f, -1, Paint.Align.CENTER);
        RectF rectF2 = this.quitConfirmBtn;
        float f5 = this.d;
        float f6 = this.H;
        rectF2.set(f5 * 70.0f, 0.625f * f6, this.W - (f5 * 70.0f), f6 * 0.675f);
        round(canvas, this.quitConfirmBtn, 18.0f, -5918);
        label(canvas, "QUITTER LA PARTIE", this.quitConfirmBtn.centerX(), this.quitConfirmBtn.centerY(), 13.0f, this.coral, Paint.Align.CENTER);
    }

    void drawResetConfirm(Canvas canvas) {
        color(-2013265920);
        canvas.drawRect(0.0f, 0.0f, this.W, this.H, this.p);
        float f = this.d;
        float f2 = this.H;
        round(canvas, new RectF(f * 30.0f, 0.28f * f2, this.W - (f * 30.0f), f2 * 0.7f), 28.0f, -516);
        label(canvas, "Espace Parents", this.W / 2.0f, 0.36f * this.H, 22.0f, this.ink, Paint.Align.CENTER);
        label(canvas, "Effacer records et statistiques ?", this.W / 2.0f, this.H * 0.43f, 15.0f, this.coral, Paint.Align.CENTER);
        label(canvas, "L'album et les badges seront conservés", this.W / 2.0f, this.H * 0.48f, 11.0f, -8950152, Paint.Align.CENTER);
        RectF rectF = this.resetCancelBtn;
        float f3 = this.d;
        float f4 = this.H;
        rectF.set(f3 * 48.0f, 0.54f * f4, this.W - (f3 * 48.0f), f4 * 0.6f);
        round(canvas, this.resetCancelBtn, 20.0f, this.mint);
        label(canvas, "ANNULER", this.resetCancelBtn.centerX(), this.resetCancelBtn.centerY(), 14.0f, -1, Paint.Align.CENTER);
        RectF rectF2 = this.resetConfirmBtn;
        float f5 = this.d;
        float f6 = this.H;
        rectF2.set(f5 * 62.0f, 0.625f * f6, this.W - (f5 * 62.0f), f6 * 0.68f);
        round(canvas, this.resetConfirmBtn, 18.0f, -5918);
        label(canvas, "OUI, EFFACER", this.resetConfirmBtn.centerX(), this.resetConfirmBtn.centerY(), 12.0f, this.coral, Paint.Align.CENTER);
    }

    /* JADX WARN: Code duplicated, block: B:150:0x0267  */
    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getActionMasked();
        if (action == MotionEvent.ACTION_CANCEL) {
            this.pressedTarget = null;
            this.parentHolding = false;
            invalidate();
            return true;
        }
        if (action == MotionEvent.ACTION_DOWN) {
            this.pressedTarget = this.accessible.targetAt(motionEvent.getX(), motionEvent.getY());
            invalidate();
        } else if (action == MotionEvent.ACTION_MOVE && this.pressedTarget != null
                && !this.pressedTarget.contains(motionEvent.getX(), motionEvent.getY())) {
            this.pressedTarget = null;
            invalidate();
        } else if (action == MotionEvent.ACTION_UP) {
            this.pressedTarget = null;
            performClick();
            this.accessible.changed();
            invalidate();
        }
        StringBuilder sb;
        int i;
        String str;
        int iAlbumImageAt;
        int i2 = this.albumPage;
        int i3 = this.mismatchDelay;
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        int i4 = this.screen;
        int i5 = -1;
        if (i4 == 1 && this.pendingWinAt > 0) return true;
        if (i4 == -1) {
            return true;
        }
        if (this.showQuit) {
            if (motionEvent.getAction() == 1) {
                if (this.quitContinueBtn.contains(x, y)) {
                    this.showQuit = false;
                    buzz(4);
                } else if (this.quitConfirmBtn.contains(x, y)) {
                    this.showQuit = false;
                    clearSavedGame();
                    this.screen = 0;
                    buzz(0);
                }
                invalidate();
            }
            return true;
        }
        if (this.showResetConfirm) {
            if (motionEvent.getAction() == 1) {
                if (this.resetCancelBtn.contains(x, y)) {
                    this.showResetConfirm = false;
                } else if (this.resetConfirmBtn.contains(x, y)) {
                    this.progress.resetStatsAndRecords();
                    this.showResetConfirm = false;
                    this.parentNotice = "Records et statistiques effacés";
                    this.resetDoneUntil = now() + 2400;
                }
                buzz(4);
                invalidate();
            }
            return true;
        }
        if (i4 == 0 && this.showResumePrompt && this.resumeAvailable) {
            if (motionEvent.getAction() == 1) {
                if (this.resumeBtn.contains(x, y)) {
                    loadGame();
                } else if (this.newGameBtn.contains(x, y)) {
                    this.showResumePrompt = false;
                }
                buzz(4);
                invalidate();
            }
            return true;
        }
        if (i4 == 0 && motionEvent.getAction() == 0 && this.parentBtn.contains(x, y)) {
            this.parentHolding = true;
            postDelayed(new Runnable() { // from class: fr.memomimi.app.GameView$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    GameView.this.m1lambda$onTouchEvent$3$frmemomimiappGameView();
                }
            }, 2500L);
            return true;
        }
        if (motionEvent.getAction() == 2) {
            if (this.parentHolding && !this.parentBtn.contains(x, y)) {
                this.parentHolding = false;
            }
            return true;
        }
        if (motionEvent.getAction() != 1) {
            return true;
        }
        this.parentHolding = false;
        int i6 = this.screen;
        if (i6 == 3) {
            if (this.homeBtn.contains(x, y)) {
                this.screen = 0;
            } else {
                if (this.shortDelay.contains(x, y)) {
                    i3 = 800;
                } else if (this.normalDelay.contains(x, y)) {
                    i3 = 1300;
                } else if (this.longDelay.contains(x, y)) {
                    i3 = 2000;
                } else if (this.calmBtn.contains(x, y)) {
                    this.calm = !this.calm;
                    saveParents();
                } else if (this.hapticBtn.contains(x, y)) {
                    this.haptics = !this.haptics;
                    saveParents();
                    buzz(4);
                } else if (this.resetBtn.contains(x, y)) {
                    this.showResetConfirm = true;
                } else if (this.tutorialResetBtn.contains(x, y)) {
                    getContext().getSharedPreferences("m", 0).edit().remove("tutorial_done").apply();
                    this.parentNotice = "Lumi reviendra à la prochaine partie";
                    this.resetDoneUntil = now() + 2400;
                }
                this.mismatchDelay = i3;
                saveParents();
            }
            invalidate();
            return true;
        }
        if (i6 == 4) {
            if (this.themeTiles.length > 0) {
                if (this.homeBtn.contains(x, y)) {
                    this.screen = 0;
                } else {
                    for (int world = 0; world < WORLD_COUNT; world++) {
                        if (this.themeTiles[world].contains(x, y)) {
                            this.theme = world;
                            remember();
                            stopMusic();
                            updateMusic();
                            playSound(92);
                            this.screen = 0;
                            break;
                        }
                    }
                }
                buzz(4);
                invalidate();
                return true;
            }
            if (this.homeBtn.contains(x, y)) {
                this.screen = 0;
            } else {
                if (this.animalsTheme.contains(x, y)) {
                    i5 = 0;
                } else if (this.oceanTheme.contains(x, y)) {
                    i5 = 1;
                } else if (this.spaceTheme.contains(x, y)) {
                    i5 = 2;
                } else if (this.dinoTheme.contains(x, y)) {
                    i5 = 3;
                } else if (this.farmTheme.contains(x, y)) {
                    i5 = 4;
                } else if (this.vehiclesTheme.contains(x, y)) {
                    i5 = 5;
                } else if (this.magicTheme.contains(x, y)) {
                    i5 = 6;
                } else if (this.mermaidTheme.contains(x, y)) {
                    i5 = 7;
                } else if (this.jungleTheme.contains(x, y)) {
                    i5 = 8;
                } else if (this.treatsTheme.contains(x, y)) {
                    i5 = 9;
                } else if (this.pirateTheme.contains(x, y)) {
                    i5 = 10;
                } else if (this.arcticTheme.contains(x, y)) {
                    i5 = 11;
                } else if (this.musicTheme.contains(x, y)) {
                    i5 = 12;
                } else if (this.jobsTheme.contains(x, y)) {
                    i5 = 13;
                }
                if (i5 >= 0) {
                    this.theme = i5;
                    remember();
                    stopMusic();
                    updateMusic();
                    playSound(92);
                    this.screen = 0;
                }
            }
            buzz(4);
            invalidate();
            return true;
        }
        if (i6 == 5) {
            if (this.enlargedItem >= 0) {
                if (this.enlargedClose.contains(x, y)) {
                    this.enlargedItem = -1;
                } else {
                    float f = this.d;
                    float f2 = this.H;
                    if (!new RectF(f * 34.0f, 0.18f * f2, this.W - (f * 34.0f), f2 * 0.76f).contains(x, y)) {
                        this.enlargedItem = -1;
                    }
                }
            } else if (this.homeBtn.contains(x, y)) {
                this.screen = this.albumReturnScreen;
            } else if (this.worldsTab.contains(x, y)) {
                this.albumTab = 2;
            } else if (this.imagesTab.contains(x, y)) {
                this.albumTab = 0;
            } else if (this.badgesTab.contains(x, y)) {
                this.albumTab = 1;
            } else {
                if (this.albumPrev.contains(x, y)) {
                    i2 = this.albumPage + WORLD_COUNT - 1;
                    this.albumTab = 0;
                } else if (this.albumNext.contains(x, y)) {
                    i2 = this.albumPage + 1;
                    this.albumTab = 0;
                } else if (this.albumTab == 2) {
                    int selected = albumWorldAt(x, y);
                    if (selected >= 0) {
                        this.albumPage = selected;
                        this.albumTab = 0;
                    }
                } else if (this.albumTab == 0 && (iAlbumImageAt = albumImageAt(x, y)) >= 0) {
                    this.enlargedItem = iAlbumImageAt;
                }
                this.albumPage = (i2 + WORLD_COUNT) % WORLD_COUNT;
            }
            buzz(4);
            invalidate();
            return true;
        }
        if (i6 == 0) {
            if (this.chooseTheme.contains(x, y)) {
                this.screen = 4;
            } else if (this.easy.contains(x, y)) {
                choosePairs(6);
            } else if (this.medium.contains(x, y)) {
                choosePairs(8);
            } else if (this.hard.contains(x, y)) {
                choosePairs(10);
            } else if (this.expert.contains(x, y)) {
                choosePairs(12);
            } else {
                if (this.play.contains(x, y)) {
                    start();
                    return true;
                }
                if (this.albumBtn.contains(x, y)) {
                    this.albumPage = this.theme;
                    this.albumReturnScreen = 0;
                    this.screen = 5;
                } else if (this.soundBtn.contains(x, y)) {
                    this.sound = !this.sound;
                    getContext().getSharedPreferences("m", 0).edit().putBoolean("sound", this.sound).apply();
                    updateMusic();
                    playSound(82);
                }
            }
            buzz(4);
            invalidate();
            return true;
        }
        if (i6 == 2) {
            if (this.victoryMascot.bounds.contains(x, y)) {
                if (this.celebration.touch(now())) {
                    playSound(92);
                    if (animationsReduced()) announceForAccessibility("Lumi te sourit !");
                }
                invalidate();
                return true;
            }
            if (!this.storyBtn.contains(x, y)) {
                if (this.play.contains(x, y)) {
                    start();
                } else {
                    if (this.albumBtn.contains(x, y)) {
                        this.albumPage = this.theme;
                        this.albumReturnScreen = 2;
                        this.screen = 5;
                    } else if (this.homeBtn.contains(x, y)) {
                        this.screen = 4;
                    }
                    buzz(4);
                }
                return true;
            }
            this.screen = 6;
            buzz(4);
            toggleStory();
            invalidate();
            return true;
        }
        if (i6 == 6) {
            if (this.homeBtn.contains(x, y)) {
                stopNarration();
                this.screen = 2;
            } else if (this.storyPlayBtn.contains(x, y)) {
                toggleStory();
            }
            buzz(4);
            invalidate();
            return true;
        }
        if (this.tutorialActive && this.tutorialStep >= 2) {
            if (this.tutorialDoneBtn.contains(x, y)) {
                this.tutorialActive = false;
                getContext().getSharedPreferences("m", 0).edit().putBoolean("tutorial_done", true).apply();
                announceForAccessibility("Bravo, tu sais jouer");
                saveGame();
                invalidate();
            }
            return true;
        }
        if (this.homeBtn.contains(x, y)) {
            this.showQuit = true;
            invalidate();
            return true;
        }
        if (!this.hintBtn.isEmpty() && this.hintBtn.contains(x, y)) {
            useHint();
            return true;
        }
        if (now() >= this.hintUntil && this.hideAt <= 0 && this.second < 0 && this.closeA < 0 && (this.flipIndex < 0 || animationsReduced() || now() - this.flipStart >= CardMotion.OPEN_MS)) {
            int iHitCard = hitCard(x, y);
            if ((!this.tutorialActive || this.tutorialStep >= 2 || iHitCard == tutorialTarget()) && iHitCard >= 0 && !this.matched[iHitCard] && iHitCard != this.first) {
                performHapticFeedback(4);
                this.flipIndex = iHitCard;
                this.flipStart = now();
                int i7 = this.first;
                if (i7 < 0) {
                    this.first = iHitCard;
                    if (this.tutorialActive) {
                        this.tutorialStep = 1;
                    }
                    announceForAccessibility("Première carte retournée. Elle reste ouverte. Choisis une deuxième carte");
                    playSound(78);
                    saveGame();
                    invalidate();
                    return true;
                }
                this.second = iHitCard;
                this.moves++;
                int[] iArr = this.cards;
                if (iArr[i7] == iArr[iHitCard]) {
                    this.lastA = i7;
                    this.lastB = iHitCard;
                    this.successUntil = now() + MATCH_DURATION_MS;
                    boolean[] zArr = this.matched;
                    int i8 = this.first;
                    int i9 = this.second;
                    zArr[i9] = true;
                    zArr[i8] = true;
                    this.errors = 0;
                    if (this.tutorialActive) {
                        this.tutorialStep = 2;
                    }
                    if (this.players == 2) {
                        int[] iArr2 = this.owners;
                        int i10 = this.turn;
                        iArr2[i9] = i10;
                        iArr2[i8] = i10;
                        this.score1 = ownedPairs(1);
                        int iOwnedPairs = ownedPairs(2);
                        this.score2 = iOwnedPairs;
                        this.found = this.score1 + iOwnedPairs;
                    } else {
                        this.found++;
                    }
                    this.second = -1;
                    this.first = -1;
                    playMatchSound();
                    int i11 = this.players;
                    if (i11 == 2) {
                        sb = new StringBuilder("Paire pour le joueur ");
                        sb.append(this.turn);
                        sb.append(". Il rejoue. Score ");
                        sb.append(this.score1);
                        sb.append(" à ");
                        i = this.score2;
                    } else {
                        sb = i11 == 3 ? new StringBuilder("Paire trouvée ensemble. ") : new StringBuilder("Paire trouvée. ");
                        sb.append(this.found);
                        sb.append(" sur ");
                        i = this.pairs;
                    }
                    sb.append(i);
                    announceForAccessibility(sb.toString());
                    if (this.found == this.pairs) {
                        if (this.players != 2) {
                            int iBest = best();
                            boolean z = iBest == 0 || this.moves < iBest;
                            this.newRecord = z;
                            if (z) {
                                getContext().getSharedPreferences("m", 0).edit().putInt("best_" + this.pairs, this.moves).apply();
                            }
                        }
                        unlockProgress();
                        clearSavedGame();
                        this.pendingWinAt = now() + (animationsReduced() ? 450L : MATCH_DURATION_MS);
                    }
                    invalidate();
                } else {
                    if (this.tutorialActive) {
                        this.tutorialStep = 2;
                    } else {
                        this.errors++;
                    }
                    this.hideAt = now() + ((long) this.mismatchDelay);
                    this.switchPending = this.players == 2;
                    playSound(55);
                }
                saveGame();
                invalidate();
            }
        }
        return true;
    }

    /* JADX INFO: renamed from: lambda$onTouchEvent$3$fr-memomimi-app-GameView, reason: not valid java name */
    /* synthetic */ void m1lambda$onTouchEvent$3$frmemomimiappGameView() {
        if (this.parentHolding && this.screen == 0) {
            this.parentHolding = false;
            this.screen = 3;
            buzz(0);
            announceForAccessibility("Espace Parents");
            invalidate();
        }
    }

    void saveParents() {
        getContext().getSharedPreferences("m", 0).edit().putInt("delay", this.mismatchDelay).putBoolean("calm", this.calm).putBoolean("haptics", this.haptics).apply();
    }

    void buzz(int i) {
        if (this.haptics) {
            performHapticFeedback(i);
        }
    }

    @Override // android.view.View
    public boolean performHapticFeedback(int i) {
        return this.haptics && super.performHapticFeedback(i);
    }

    void remember() {
        getContext().getSharedPreferences("m", 0).edit().putInt("players", this.players).putInt("theme", this.theme).putInt("pairs", this.pairs).apply();
    }

    void choosePairs(int i) {
        this.pairs = i;
        if (this.suggestedPairs == i) {
            this.suggestedPairs = 0;
            getContext().getSharedPreferences("m", 0).edit().putInt("suggested_pairs", 0).apply();
        }
        remember();
    }

    void saveForPause() {
        if (this.screen != 1 || this.cards == null) {
            return;
        }
        saveGame();
    }

    void saveGame() {
        // Do not restore a completed board during its final celebration.
        if (this.pendingWinAt > 0 || (this.cards != null && this.found == this.pairs)) return;
        int i;
        int i2;
        if (this.cards == null || this.matched == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        long j = 0;
        int i3 = 0;
        long j2 = 0;
        long j3 = 0;
        while (true) {
            i = 2;
            if (i3 >= this.cards.length) {
                break;
            }
            if (i3 > 0) {
                sb.append(',');
            }
            sb.append(this.cards[i3]);
            if (this.matched[i3]) {
                j |= 1 << i3;
            }
            int[] iArr = this.owners;
            if (iArr != null && iArr[i3] == 1) {
                j2 |= 1 << i3;
            } else if (iArr != null && iArr[i3] == 2) {
                j3 |= 1 << i3;
            }
            i3++;
        }
        int i4 = this.second;
        if (i4 >= 0 || (i2 = this.first) < 0 || this.matched[i2]) {
            i2 = -1;
        }
        if (i4 < 0 || !this.switchPending || this.players != 2) {
            i = this.turn;
        } else if (this.turn != 1) {
            i = 1;
        }
        getContext().getSharedPreferences("m", 0).edit().putBoolean("game_active", true).putString("saved_cards", sb.toString()).putLong("saved_matched", j).putLong("saved_owner1", j2).putLong("saved_owner2", j3).putInt("saved_theme", this.theme).putInt("saved_pairs", this.pairs).putInt("saved_players", this.players).putInt("saved_turn", i).putInt("saved_score1", this.score1).putInt("saved_score2", this.score2).putInt("saved_moves", this.moves).putInt("saved_found", this.found).putInt("saved_first", i2).putInt("saved_errors", this.errors).putBoolean("saved_hint_used", this.hintUsed).apply();
        this.resumeAvailable = true;
    }

    void loadGame() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("m", 0);
        try {
            this.pairs = sharedPreferences.getInt("saved_pairs", 6);
            this.players = sharedPreferences.getInt("saved_players", 1);
            this.theme = Math.max(0, Math.min(WORLD_COUNT - 1, sharedPreferences.getInt("saved_theme", 0)));
            String[] strArrSplit = sharedPreferences.getString("saved_cards", "").split(",");
            if (strArrSplit.length != this.pairs * 2) {
                throw new IllegalStateException();
            }
            this.cards = new int[strArrSplit.length];
            this.matched = new boolean[strArrSplit.length];
            this.owners = new int[strArrSplit.length];
            long j = sharedPreferences.getLong("saved_matched", 0L);
            long j2 = sharedPreferences.getLong("saved_owner1", 0L);
            long j3 = sharedPreferences.getLong("saved_owner2", 0L);
            for (int i = 0; i < strArrSplit.length; i++) {
                this.cards[i] = Integer.parseInt(strArrSplit[i]);
                long j4 = 1 << i;
                this.matched[i] = (j & j4) != 0;
                this.owners[i] = (j2 & j4) != 0 ? 1 : (j3 & j4) != 0 ? 2 : 0;
            }
            int i2 = this.pairs;
            int i3 = GameModel.columns(i2);
            this.cols = i3;
            this.rows = (((i2 * 2) + i3) - 1) / i3;
            this.turn = sharedPreferences.getInt("saved_turn", 1);
            this.score1 = sharedPreferences.getInt("saved_score1", 0);
            this.score2 = sharedPreferences.getInt("saved_score2", 0);
            this.moves = sharedPreferences.getInt("saved_moves", 0);
            int i4 = sharedPreferences.getInt("saved_found", 0);
            this.found = i4;
            if (this.players == 2 && j2 == 0 && j3 == 0 && i4 > 0) {
                migrateOwnersFromScores();
            } else if (j2 != 0 || j3 != 0) {
                this.score1 = ownedPairs(1);
                int iOwnedPairs = ownedPairs(2);
                this.score2 = iOwnedPairs;
                this.found = this.score1 + iOwnedPairs;
            }
            this.errors = sharedPreferences.getInt("saved_errors", 0);
            this.hintUsed = sharedPreferences.getBoolean("saved_hint_used", false);
            int i5 = sharedPreferences.getInt("saved_first", -1);
            this.first = i5;
            if (i5 < 0 || i5 >= this.cards.length || this.matched[i5]) {
                this.first = -1;
            }
            this.hintB = -1;
            this.hintA = -1;
            this.closeB = -1;
            this.closeA = -1;
            this.flipIndex = -1;
            this.lastB = -1;
            this.lastA = -1;
            this.second = -1;
            this.hintUntil = 0L;
            this.closeStart = 0L;
            this.flipStart = 0L;
            this.successUntil = 0L;
            this.hideAt = 0L;
            this.switchPending = false;
            this.tutorialActive = !sharedPreferences.getBoolean("tutorial_done", false) && this.moves == 0 && this.found == 0;
            this.tutorialStep = this.first >= 0 ? 1 : 0;
            this.showResumePrompt = false;
            this.showQuit = false;
            this.screen = 1;
            remember();
            announceForAccessibility("Partie reprise. " + this.found + " paires sur " + this.pairs);
            invalidate();
        } catch (Exception unused) {
            clearSavedGame();
            this.screen = 0;
            this.showResumePrompt = false;
            invalidate();
        }
    }

    void migrateOwnersFromScores() {
        int i = this.score1;
        for (int i2 = 0; i2 < this.cards.length; i2++) {
            if (this.matched[i2] && this.owners[i2] == 0) {
                int i3 = i2 + 1;
                while (true) {
                    int[] iArr = this.cards;
                    if (i3 >= iArr.length) {
                        break;
                    }
                    if (this.matched[i3]) {
                        int[] iArr2 = this.owners;
                        if (iArr2[i3] == 0 && iArr[i2] == iArr[i3]) {
                            int i4 = i > 0 ? 1 : 2;
                            iArr2[i3] = i4;
                            iArr2[i2] = i4;
                            if (i <= 0) {
                                break;
                            }
                            i--;
                            break;
                        }
                    }
                    i3++;
                }
            }
        }
    }

    int ownedPairs(int i) {
        return GameModel.ownedPairs(this.owners, i);
    }

    void clearSavedGame() {
        getContext().getSharedPreferences("m", 0).edit().putBoolean("game_active", false).remove("saved_cards").remove("saved_matched").remove("saved_owner1").remove("saved_owner2").remove("saved_first").apply();
        this.resumeAvailable = false;
        this.showResumePrompt = false;
    }

    boolean handleBack() {
        if (this.pendingWinAt > 0) return true;
        if (this.showQuit) {
            this.showQuit = false;
        } else {
            int i2 = this.screen;
            if (i2 == 0 && this.showResumePrompt) {
                this.showResumePrompt = false;
            } else if (i2 == 1) {
                this.showQuit = true;
            } else {
                if (i2 == 5) {
                    this.screen = this.albumReturnScreen;
                } else if (i2 == 6) {
                    stopNarration();
                    this.screen = 2;
                } else {
                    if (i2 == 0) {
                        return false;
                    }
                    this.screen = 0;
                }
            }
        }
        invalidate();
        return true;
    }

    void useHint() {
        this.hintB = -1;
        this.hintA = -1;
        for (int i = 0; i < this.cards.length && this.hintA < 0; i++) {
            if (!this.matched[i]) {
                int i2 = i + 1;
                while (true) {
                    int[] iArr = this.cards;
                    if (i2 >= iArr.length) {
                        break;
                    }
                    if (!this.matched[i2] && iArr[i] == iArr[i2]) {
                        this.hintA = i;
                        this.hintB = i2;
                        break;
                    }
                    i2++;
                }
            }
        }
        if (this.hintA >= 0) {
            this.hintUsed = true;
            this.errors = 0;
            this.hintUntil = now() + 1250;
            playSound(92);
            announceForAccessibility("Coup de pouce. Une paire est montrée");
            saveGame();
            invalidate();
        }
    }

    int hitCard(float x, float y) {
        if (this.cards == null) return -1;
        for (int index = 0; index < this.cards.length; index++) {
            if (this.cardBounds[index].contains(x, y)) return index;
        }
        return -1;
    }

    synchronized void updateMusic() {
        if (!this.sound || !this.activityActive || this.storyPlaying || this.screen < 0) {
            stopMusic();
        } else if (this.backgroundMusic == null) {
            startMusic();
        }
    }

    void pauseMusic() {
        this.activityActive = false;
        this.parentHolding = false;
        this.pressedTarget = null;
        this.matchFeedbackGeneration++;
        stopEffectSound();
        stopMusic();
    }

    void resumeMusic() {
        this.systemReducedMotion = android.provider.Settings.Global.getFloat(
                getContext().getContentResolver(), android.provider.Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f;
        this.activityActive = true;
        updateMusic();
    }

    synchronized void startMusic() {
        int i;
        int length;
        if (this.backgroundMusic == null && this.sound && this.activityActive && !this.storyPlaying) {
            int i2 = this.musicIndex;
            if (i2 < 0) {
                i = this.theme;
                length = this.musicAssets.length;
            } else {
                i = i2 + 1;
                length = this.musicAssets.length;
            }
            this.musicIndex = i % length;
            try {
                AssetFileDescriptor assetFileDescriptorOpenFd = getContext().getAssets().openFd(this.musicAssets[this.musicIndex]);
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(3);
                mediaPlayer.setDataSource(assetFileDescriptorOpenFd.getFileDescriptor(), assetFileDescriptorOpenFd.getStartOffset(), assetFileDescriptorOpenFd.getLength());
                assetFileDescriptorOpenFd.close();
                mediaPlayer.setVolume(0.13f, 0.13f);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: fr.memomimi.app.GameView$$ExternalSyntheticLambda0
                    @Override // android.media.MediaPlayer.OnCompletionListener
                    public final void onCompletion(MediaPlayer mediaPlayer2) {
                        GameView.this.m3lambda$startMusic$4$frmemomimiappGameView(mediaPlayer2);
                    }
                });
                mediaPlayer.prepare();
                mediaPlayer.start();
                this.backgroundMusic = mediaPlayer;
            } catch (Exception unused) {
                this.backgroundMusic = null;
            }
        }
    }

    /* JADX INFO: renamed from: lambda$startMusic$4$fr-memomimi-app-GameView, reason: not valid java name */
    /* synthetic */ void m3lambda$startMusic$4$frmemomimiappGameView(MediaPlayer mediaPlayer) {
        synchronized (this) {
            if (this.backgroundMusic == mediaPlayer) {
                this.backgroundMusic = null;
            }
        }
        mediaPlayer.release();
        post(new Runnable() { // from class: fr.memomimi.app.GameView$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                GameView.this.startMusic();
            }
        });
    }

    synchronized void stopMusic() {
        MediaPlayer mediaPlayer = this.backgroundMusic;
        if (mediaPlayer == null) {
            return;
        }
        this.backgroundMusic = null;
        mediaPlayer.setOnCompletionListener(null);
        try {
            mediaPlayer.stop();
        } catch (Exception unused) {
        }
        try {
            mediaPlayer.release();
        } catch (Exception unused2) {
        }
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        this.activityActive = false;
        this.parentHolding = false;
        this.pressedTarget = null;
        this.matchFeedbackGeneration++;
        stopEffectSound();
        stopMusic();
        super.onDetachedFromWindow();
    }

    void playSound(int i) {
        final int i2;
        final double[] dArr;
        final float f;
        if (this.sound && this.activityActive && !this.soundExecutor.isShutdown()) {
            if (i >= 100) {
                dArr = new double[]{523.25d, 659.25d, 783.99d, 1046.5d};
                i2 = 105;
                f = 0.15f;
            } else {
                if (i >= 90) {
                    dArr = new double[]{659.25d, 783.99d, 987.77d};
                    i2 = 90;
                    f = 0.13f;
                } else if (i < 60) {
                    dArr = new double[]{392.0d, 329.63d};
                    i2 = 145;
                    f = 0.09f;
                } else if (i == 82) {
                    dArr = new double[]{587.33d, 698.46d};
                    i2 = 55;
                    f = 0.08f;
                } else {
                    dArr = new double[]{523.25d};
                    i2 = 48;
                    f = 0.065f;
                }
            }
            this.soundExecutor.execute(new Runnable() {
                @Override // java.lang.Runnable
                public final void run() {
                    GameView.this.m2lambda$playSound$5$frmemomimiappGameView(dArr, i2, f);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX INFO: renamed from: synth, reason: merged with bridge method [inline-methods] */
    public void m2lambda$playSound$5$frmemomimiappGameView(double[] dArr, int i, float f) {
        double d = 22050;
        int i2 = (int) (0.025d * d);
        int i3 = (int) (((double) (i * 22050)) / 1000.0d);
        int length = dArr.length * (i3 + i2);
        short[] sArr = new short[length];
        int i4 = 0;
        for (double d2 : dArr) {
            int i5 = 0;
            while (i5 < i3) {
                double d3 = i5;
                double d4 = d3 / d;
                double d5 = d;
                sArr[i4] = (short) (((double) (32767.0f * f)) * Math.min(1.0d, Math.min(d3 / (0.012d * d), ((double) (i3 - i5)) / (0.035d * d5))) * ((Math.sin(6.283185307179586d * d2 * d4) * 0.82d) + (Math.sin(12.566370614359172d * d2 * d4) * 0.18d)));
                i5++;
                i4++;
                i2 = i2;
                d = d5;
            }
            i4 += i2;
        }
        AudioTrack audioTrack = null;
        try {
            if (!this.activityActive || !this.sound) return;
            audioTrack = new AudioTrack(3, 22050, 4, 2, length * 2, 0);
            this.activeSound = audioTrack;
            audioTrack.write(sArr, 0, length);
            audioTrack.play();
            Thread.sleep((((long) dArr.length) * ((long) (i + 25))) + 40);
            audioTrack.stop();
        } catch (Exception unused) {
        } finally {
            if (this.activeSound == audioTrack) this.activeSound = null;
            if (audioTrack != null) {
                try { audioTrack.release(); } catch (Exception ignored) { }
            }
        }
    }

    void playMatchSound() {
        final int generation = ++this.matchFeedbackGeneration;
        postDelayed(() -> {
            if (generation == this.matchFeedbackGeneration && this.screen == 1) playSound(92);
        }, animationsReduced() ? 0L : 170L);
    }

    void stopEffectSound() {
        this.soundExecutor.getQueue().clear();
        AudioTrack track = this.activeSound;
        this.activeSound = null;
        if (track != null) {
            try { track.stop(); } catch (Exception ignored) { }
        }
    }

    void releaseAudio() {
        pauseMusic();
        releaseNarration();
        this.soundExecutor.shutdownNow();
    }
}
