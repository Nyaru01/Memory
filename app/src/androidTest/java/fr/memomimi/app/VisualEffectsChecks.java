package fr.memomimi.app;

import android.app.Instrumentation;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.MotionEvent;
import java.io.File;
import java.io.FileOutputStream;

/** Device regression checks using the Android framework, without external test libraries. */
public final class VisualEffectsChecks extends Instrumentation {
    private int checks;
    private String renderTiming = "";
    private Bundle updateArguments;

    @Override public void onCreate(Bundle arguments) {
        super.onCreate(arguments);
        if (arguments != null && arguments.containsKey("mode")) updateArguments = arguments;
        start();
    }

    @Override public void onStart() {
        if (updateArguments != null) {
            Bundle updateResult = new UpdateChecks(this, updateArguments).run();
            finish(updateResult.getString("stream", "").startsWith("PASS:") ? -1 : 0, updateResult);
            return;
        }
        Bundle result = new Bundle();
        try {
            runOnMainSync(() -> {
                GameView view = new GameView(getTargetContext());
                try {
                    require(view.victoryMascot.hasAtlas(), "Sprite atlas is bundled");
                    require(view.victoryMascot.atlasBytes() <= 8*1024*1024, "Atlas memory budget");
                    verify(view, 1, false);
                    verify(view, 2, false);
                    verify(view, 3, false);
                    verify(view, 1, true);
                    verifyV2(view);
                    verifyAdventure(view);
                } finally {
                    view.pauseMusic();
                    view.releaseNarration();
                }
            });
            captureLiveCelebration();
            result.putString("stream", "PASS: " + checks + " checks (solo, duel, cooperative, calm).\n" + renderTiming);
            finish(-1, result);
        } catch (Throwable failure) {
            result.putString("stream", "FAIL: " + failure + "\n");
            finish(0, result);
        }
    }

    private void verifyV2(GameView view) {
        float previousDensity=view.d; view.d=1; view.sound=false; view.haptics=false; view.activityActive=false;
        for(int[] size:new int[][]{{320,480},{360,640},{412,850}}) for(int pairs:new int[]{6,8,10,12}) {
            view.pairs=pairs; view.start(); view.tutorialActive=false;
            render(view,"v2-device-board-"+pairs+"-"+size[0],size[0],size[1]);
            require(view.cards.length==pairs*2,"V2 deck size");
            for(int i=0;i<view.cards.length;i++) {
                android.graphics.RectF rect=view.cardBounds[i];
                require(rect.top>=view.systemTop()+view.gameHeaderHeight() && rect.bottom<=size[1]-view.systemBottom()+.1f,"V2 board bounds");
                require(Math.min(rect.width(),rect.height())>=48,"V2 48dp card");
                require(view.hitCard(rect.centerX(),rect.centerY())==i,"V2 hit geometry");
            }
        }
        view.screen=0; render(view,"v2-device-home",360,640);
        click(view,view.chooseTheme); render(view,"v2-device-worlds",360,640);
        require(view.screen==4 && view.worldColumns()==3,"V2 normal world columns");
        for(int world=0;world<GameView.WORLD_COUNT;world++) require(view.themeSticker(world)!=null,"V2 existing artwork");
        for(int world=18;world<GameView.WORLD_COUNT;world++) {
            require(StoryLibrary.text(world).length()>1200,"New original story is present");
            Bitmap preview=Bitmap.createBitmap(480,480,Bitmap.Config.ARGB_8888);
            Canvas cards=new Canvas(preview); cards.drawColor(0xfffff8ec);
            for(int item=0;item<12;item++) {
                android.graphics.RectF cell=new android.graphics.RectF(item%4*120,item/4*160,item%4*120+120,item/4*160+160);
                view.drawAnimalFrom(cards,world,item,cell,12);
            }
            try(FileOutputStream out=new FileOutputStream(new File(getTargetContext().getExternalFilesDir(null),"v92-new-cards-"+world+".png"))) {
                preview.compress(Bitmap.CompressFormat.PNG,100,out);
            } catch(Exception failure) { throw new AssertionError(failure); }
            preview.recycle();
        }
        java.util.Arrays.fill(view.familyOpen,false); view.familyOpen[1]=true; view.worldsScroll=0;
        render(view,null,360,640);
        while(view.scrollPage(true)) render(view,null,360,640);
        click(view,view.themeTiles[17]); require(view.screen==0 && view.theme==17,"V2 last world selection");
        view.screen=5; view.albumTab=2; view.albumScroll=0;
        render(view,"v2-device-album-worlds",360,640); click(view,view.themeTiles[1]);
        require(view.albumPage==1 && view.albumTab==0,"V2 album world selection");
        render(view,"v2-device-album-images",360,640);
        view.screen=3; render(view,"v2-device-parents",360,640);
        view.d=previousDensity;
    }

    private void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
        checks++;
    }
    private void verifyAdventure(GameView view) {
        getTargetContext().getSharedPreferences("m",0).edit().remove("adventure_completed").commit();
        view.calm=false;view.systemReducedMotion=false;view.d=1;view.layout(0,0,360,640);
        for(int opening=0;opening<3;opening++) {
            view.openAdventure();render(view,"adventure-first-open-animated",360,640);
            require(view.adventureMap.motion.from==1 && !view.adventureMap.motion.travelling(GameView.now()),"Fresh animated map never travels from level zero");
            require(view.screen==7 && view.adventure.completed()==0,"Fresh map opens without changing progression");
            view.screen=0;
        }
        view.calm=true;view.openAdventure();render(view,"adventure-device-start",360,640);
        require(!view.startAdventure(2),"Locked level cannot start");
        require(view.startAdventure(1),"Level one available");view.tutorialActive=false;
        for(int i=0;i<view.cards.length;i++)view.cards[i]=i/2;
        render(view,null,360,640);
        for(int i=0;i<view.cards.length;i+=2){click(view,view.cardBounds[i]);click(view,view.cardBounds[i+1]);}
        require(view.adventure.completed()==1,"Real complete board unlocks exactly level two");
        require(!view.startAdventure(3),"Cannot skip next level");
        view.pendingWinAt=GameView.now()-1;render(view,"adventure-device-win",360,640);
        click(view,view.play);require(view.screen==7 && view.adventure.current()==2,"Victory continues through Lumi journey map");render(view,null,360,640);click(view,view.adventureMap.continueButton);require(view.adventureLevel==2 && view.screen==1,"Continue starts level two");
        view.saveForPause();GameView restored=new GameView(getTargetContext());restored.sound=false;
        try {restored.loadGame();require(restored.adventureLevel==2 && restored.pairs==6,"Resume keeps adventure level");} finally {restored.releaseAudio();}
        view.openAdventure();render(view,"adventure-device-unlocked",360,640);
        view.calm=false;
        for(int level=1;level<=40;level++) {
            view.adventureMap.focus(level,488,1);render(view,level==13?"adventure-illustrated-ocean":level==21?"adventure-illustrated-space":null,360,640);
            require(view.adventureMap.scenery.retainedTiles()<=3,"Illustrated tile cache bounded");
            require(view.adventureMap.scenery.retainedBytes()+view.adventureMap.decor.bytes()<6*1024*1024,"Map bitmap memory bounded");
            require(view.adventureMap.scenery.seamBytes()<=AdventureScenery.MAX_SEAM_BYTES,"Join strip cache bounded");
        }
        require(view.adventureMap.decor.bytes()>0,"Animated creatures bundled");
        view.openAdventure();render(view,"adventure-illustrated-device",360,640);
        click(view,view.adventureMap.lumi);require(view.screen==7 && view.adventureMap.motion.reactionAt>=0,"Lumi reacts without starting a game");
        require(view.accessible.targets().stream().anyMatch(t->t.label.equals("Revenir au niveau actuel")),"Accessible current-level shortcut");
        Bitmap timingBitmap=Bitmap.createBitmap(360,640,Bitmap.Config.ARGB_8888);Canvas timingCanvas=new Canvas(timingBitmap);
        long[] timing=new long[120];
        for(int frame=0;frame<120;frame++){long start=System.nanoTime();view.adventureMap.draw(timingCanvas,view);timing[frame]=System.nanoTime()-start;}
        java.util.Arrays.sort(timing);renderTiming+="\nWarm map Canvas software draw: median="+timing[60]/1000000f+"ms p95="+timing[114]/1000000f+"ms; tiles="+view.adventureMap.scenery.retainedBytes()+"B creatures="+view.adventureMap.decor.bytes()+"B (not device FPS).";
        timingBitmap.recycle();
        view.adventureMap.scrollBy(5000);render(view,null,360,640);click(view,view.adventureMap.focusButton);render(view,null,360,640);
        require(view.adventureMap.nodes[view.adventure.current()-1].centerY()>=view.adventureMap.top && view.adventureMap.nodes[view.adventure.current()-1].centerY()<=view.adventureMap.bottom,"Shortcut centers current level");
        while(view.scrollPage(true))render(view,null,360,640);
        render(view,"adventure-device-end",360,640);require(view.adventureMap.nodes[39].top<view.adventureMap.bottom,"Map reaches level forty");
        view.clearSavedGame();view.adventureLevel=0;
    }

    private void render(GameView view, String name) {
        render(view, name, 480, 900);
    }

    private void render(GameView view, String name, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        view.layout(0, 0, width, height);
        view.onDraw(new Canvas(bitmap));
        if(view.screen==7){view.adventureMap.scenery.awaitReady();view.onDraw(new Canvas(bitmap));}
        if (name != null) {
            try (FileOutputStream output = new FileOutputStream(new File(getTargetContext().getExternalFilesDir(null), name + ".png"))) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            } catch (Exception failure) { throw new RuntimeException(failure); }
        }
        bitmap.recycle();
    }

    private void tap(GameView view, int index) {
        for (int y = 120; y < 880; y += 5) {
            for (int x = 15; x < 465; x += 5) {
                if (view.hitCard(x, y) == index) {
                    long now = GameView.now();
                    MotionEvent event = MotionEvent.obtain(now, now, MotionEvent.ACTION_UP, x, y, 0);
                    view.onTouchEvent(event);
                    event.recycle();
                    return;
                }
            }
        }
        throw new AssertionError("Card not found: " + index);
    }

    private void ready(GameView view) {
        view.flipIndex = -1;
        view.closeA = -1;
        view.closeB = -1;
        view.hideAt = 0;
    }

    private void verify(GameView view, int players, boolean calm) {
        view.pairs = 3;
        view.players = players;
        view.calm = calm;
        view.start();
        view.tutorialActive = false;
        view.previewUntil = 0;
        view.hintUntil = 0;
        view.cards = new int[]{0, 0, 1, 1, 2, 2};
        render(view, null);
        tap(view, 0);
        if (players == 1 && !calm) {
            view.flipStart = GameView.now()-90;
            render(view, "card-turn-90ms-8.10");
            view.flipStart = GameView.now()-170;
            render(view, "card-turn-170ms-8.10");
            require(view.p.getAlpha() == 255, "Card turn restores paint alpha");
        }
        ready(view);
        tap(view, 1);
        require(view.found == 1 && view.matched[0] && view.matched[1], "Matched pair and score");
        require(view.screen == 1 && view.pendingWinAt == 0, "Ordinary pair stays in game");
        view.flipIndex = -1;
        view.successUntil = GameView.now() + 700;
        render(view, "match-" + players + "-" + calm);
        require(view.p.getAlpha() == 255 && view.p.getStyle() == android.graphics.Paint.Style.FILL, "Paint restored after effects");
        if (players == 2) require(view.owners[0] == 1 && view.owners[1] == 1 && view.score1 == 1, "Duel ownership");
        ready(view);
        tap(view, 2);
        ready(view);
        tap(view, 4);
        require(view.hideAt > 0 && !view.matched[2] && !view.matched[4], "Mismatch stays unmatched");
        view.flipIndex = -1;
        view.hideAt = GameView.now() - 1;
        render(view, null);
        view.closeStart = GameView.now() - 400;
        render(view, null);
        require(view.first == -1 && view.second == -1 && view.closeA == -1, "Mismatch closes");
        if (players == 2) require(view.turn == 2, "Duel switches after mismatch");
        ready(view);
        tap(view, 2);
        ready(view);
        tap(view, 3);
        ready(view);
        tap(view, 4);
        ready(view);
        tap(view, 5);
        require(view.found == 3 && view.screen == 1 && view.pendingWinAt > 0, "Last pair is visible before win");
        require(view.pendingWinAt - GameView.now() <= (calm ? 450 : GameView.MATCH_DURATION_MS), "Calm-aware delay");
        require(view.handleBack() && !view.showQuit, "Back absorbed during completion");
        int moves = view.moves;
        tap(view, 0);
        require(view.moves == moves, "Tap absorbed during completion");
        view.saveForPause();
        require(!getTargetContext().getSharedPreferences("m", 0).getBoolean("game_active", true), "Completed game is not saved");
        view.pendingWinAt = GameView.now() - 1;
        render(view, "win-" + players + "-" + calm);
        require(view.screen == 2 && view.pendingWinAt == 0, "Win transition completes");
        require(view.celebration.active, "Mascot starts on completed game");
        view.celebration.startedAt = GameView.now() - 1000;
        render(view, "mascot-joy-" + players + "-" + calm);
        view.celebration.startedAt = GameView.now() - 4200;
        render(view, "mascot-idle-" + players + "-" + calm);
        require(view.victoryMascot.bounds.top > view.H * .345f
                && view.victoryMascot.bounds.bottom < view.storyBtn.top, "Mascot leaves results and actions clear");
        long idleStart = view.celebration.startedAt;
        render(view, null);
        require(view.celebration.startedAt == idleStart, "Drawing does not restart mascot");
        click(view, view.victoryMascot.bounds);
        require(view.celebration.reactionAt >= 0, "Mascot touch reacts");
        view.celebration.reactionAt = GameView.now() - 250;
        render(view, "mascot-touch-" + players + "-" + calm);
        if (players == 1 && !calm) {
            render(view, "win-small", 320, 616);
            measureRenderer(view);
            render(view, null, 320, 616);
        }
        click(view, view.homeBtn);
        require(view.screen == 4, "World choice usable during celebration");
        view.screen = 2;
        click(view, view.play);
        require(!view.celebration.active, "Replay clears mascot state");
        require(view.pendingWinAt == 0 && view.found == 0 && view.screen == 1, "New game resets completion");
    }

    private void click(GameView view, android.graphics.RectF bounds) {
        long now = GameView.now();
        MotionEvent event = MotionEvent.obtain(now, now, MotionEvent.ACTION_UP, bounds.centerX(), bounds.centerY(), 0);
        view.onTouchEvent(event); event.recycle();
    }

    private void measureRenderer(GameView view) {
        Bitmap bitmap = Bitmap.createBitmap(480, 900, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        long[] timings = new long[180];
        android.graphics.RectF area = new android.graphics.RectF(30, 324, 450, 522);
        long start = view.celebration.startedAt;
        for (int i = -30; i < timings.length; i++) {
            long before = System.nanoTime();
            view.victoryMascot.draw(canvas, area, view.celebration, start + Math.max(0, i)*33L,
                    false, view.themeColor(view.theme), view.theme);
            if (i >= 0) timings[i] = System.nanoTime()-before;
        }
        java.util.Arrays.sort(timings);
        renderTiming = String.format(java.util.Locale.ROOT,
                "Mascot software Canvas, 180 frames: median %.2f ms, p95 %.2f ms, max %.2f ms (emulator).\n",
                timings[90]/1e6, timings[171]/1e6, timings[179]/1e6);
        bitmap.recycle();
    }

    /** Exercise the real attached Canvas view, then capture GPU-composited device frames. */
    private void captureLiveCelebration() throws Exception {
        android.content.Intent intent = new android.content.Intent(getTargetContext(), MainActivity.class);
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
        MainActivity activity = (MainActivity)startActivitySync(intent);
        runOnMainSync(() -> {
            GameView view = activity.game;
            view.sound = false; view.calm = false; view.systemReducedMotion = false;
            view.screen = -1; view.splashStart = GameView.now(); view.invalidate();
        });
        Thread.sleep(650); liveScreenshot("loading-lumi-salut-8.11");
        Thread.sleep(400); liveScreenshot("loading-lumi-rebond-8.11");
        Thread.sleep(800);
        int savedAdventure=getTargetContext().getSharedPreferences("m",0).getInt("adventure_completed",0);
        runOnMainSync(() -> {
            getTargetContext().getSharedPreferences("m",0).edit().putInt("adventure_completed",0).commit();
            activity.game.openAdventure();
        });
        Thread.sleep(500);liveScreenshot("map-first-open-live-9.5");
        runOnMainSync(() -> {
            require(activity.game.screen==7,"Attached animated first map stays open");
            require(activity.game.adventureMap.motion.from==1 && !activity.game.adventureMap.motion.travelling(GameView.now()),"Attached map starts Lumi at one");
            getTargetContext().getSharedPreferences("m",0).edit().putInt("adventure_completed",savedAdventure).commit();
            activity.game.screen=0;
        });
        runOnMainSync(() -> {
            GameView view = activity.game;
            require(view.screen == 0, "Loading completes with mascot");
            view.players = 1; view.pairs = 3; view.start();
            try {
                activity.updates.ready = new org.json.JSONObject().put("versionName", "test");
                activity.updates.announced = false;
                activity.updates.announceWhenSafe();
                require(!activity.updates.announced, "Update does not interrupt an active game");
                activity.updates.ready = null;
            } catch (org.json.JSONException failure) { throw new AssertionError(failure); }
            view.tutorialActive = false; view.found = view.pairs; view.moves = 6;
            java.util.Arrays.fill(view.matched, true);
            view.pendingWinAt = GameView.now() + 20;
            view.invalidate();
        });
        Thread.sleep(400); liveScreenshot("mascot-live-appear-8.11");
        Thread.sleep(950); liveScreenshot("mascot-live-victory-8.11");
        Thread.sleep(3200); liveScreenshot("mascot-live-idle-8.11");
        runOnMainSync(() -> {
            require(activity.game.screen == 2, "Attached view reached victory");
            click(activity.game, activity.game.victoryMascot.bounds);
            require(activity.game.celebration.reactionAt >= 0, "Attached mascot reacts");
        });
        Thread.sleep(400); liveScreenshot("mascot-live-touch-8.11");
        runOnMainSync(() -> {
            GameView view=activity.game; view.celebration.reactionAt=-1;
            view.celebration.startedAt=GameView.now()-4500; view.theme=18; view.invalidate();
        });
        Thread.sleep(200); liveScreenshot("v92-victory-readable");
        runOnMainSync(() -> {
            GameView view=activity.game; view.screen=4; view.worldsScroll=0;
            java.util.Arrays.fill(view.familyOpen,false); view.familyOpen[4]=true; view.invalidate();
        });
        Thread.sleep(200); liveScreenshot("v92-worlds-robots");
        runOnMainSync(() -> {
            GameView view=activity.game;
            require(!view.themeTiles[18].isEmpty() && view.themeTiles[0].isEmpty(),"Family hides other worlds");
            click(view,view.themeTiles[18]); require(view.theme==18 && view.screen==0,"New world is selectable");
            view.pairs=12; view.start(); view.tutorialActive=false;
            require(view.cards.length==24 && view.worldSheet(18)!=null,"New robot deck has twelve pairs");
            view.invalidate();
        });
        Thread.sleep(200); liveScreenshot("v92-robots-board");
        runOnMainSync(() -> { activity.game.theme=19; activity.game.start(); activity.game.tutorialActive=false; activity.game.invalidate(); });
        Thread.sleep(200); liveScreenshot("v92-garden-board");
        runOnMainSync(() -> {activity.game.openAdventure();});
        Thread.sleep(200); liveScreenshot("adventure-live-map");
        runOnMainSync(() -> {
            activity.game.screen=2;
            click(activity.game, activity.game.homeBtn);
            require(activity.game.screen == 4, "Attached world button navigates");
            activity.finish();
        });
    }

    private void liveScreenshot(String name) throws Exception {
        Bitmap capture = getUiAutomation().takeScreenshot();
        require(capture != null, "Live device screenshot");
        try (FileOutputStream output = new FileOutputStream(new File(getTargetContext().getExternalFilesDir(null), name+".png"))) {
            capture.compress(Bitmap.CompressFormat.PNG, 100, output);
        }
        capture.recycle();
    }
}
