package fr.memomimi.app;

import android.app.Instrumentation;
import android.os.Bundle;
import org.json.JSONObject;
import java.io.File;

/** Dedicated emulator checks against public GitHub assets and a supplied base APK. */
public final class UpdateChecks {
    final Instrumentation host;
    Bundle arguments;
    UpdateChecks(Instrumentation host, Bundle args) { this.host = host; arguments = args; }
    android.content.Context getTargetContext() { return host.getTargetContext(); }
    Bundle run() {
        Bundle result = new Bundle();
        try {
            String mode = arguments == null ? "network" : arguments.getString("mode", "network");
            if("mapprofile".equals(mode)) {
                return new MapProfile(host,arguments).run();
            } else if("mapseed".equals(mode)) {
                if(UpdateManager.version(getTargetContext().getPackageManager().getPackageInfo("fr.memomimi.app",0))!=44)throw new AssertionError("Expected 9.3 base");
                getTargetContext().getSharedPreferences("m",0).edit().putInt("adventure_completed",17).putInt("album_17",63).putInt("best_12",19).putBoolean("sound",false).putBoolean("calm",true).commit();
                GameView view=new GameView(getTargetContext());
                try{if(!view.startAdventure(18))throw new AssertionError("Seed level unavailable");view.saveForPause();getTargetContext().getSharedPreferences("m",0).edit().putBoolean("map_seed_ready",true).commit();if(getTargetContext().getSharedPreferences("m",0).getInt("saved_adventure_level",0)!=18)throw new AssertionError("Seed not saved");}finally{view.releaseAudio();}
                result.putString("stream","PASS: 9.3 progression at level18 and resumable game seeded\n");
            } else if("mapverify".equals(mode)) {
                if(UpdateManager.version(getTargetContext().getPackageManager().getPackageInfo("fr.memomimi.app",0))!=45)throw new AssertionError("Expected 9.4");
                android.content.SharedPreferences prefs=getTargetContext().getSharedPreferences("m",0);
                GameView view=new GameView(getTargetContext());
                try{view.loadGame();if(view.adventure.completed()!=17||view.adventureLevel!=18||view.theme!=AdventureStore.world(18)||view.pairs!=AdventureStore.pairs(18)||view.screen!=1||prefs.getInt("album_17",0)!=63||prefs.getInt("best_12",0)!=19||prefs.getBoolean("sound",true)||!prefs.getBoolean("calm",false))throw new AssertionError("Upgrade changed progress or resume: completed="+view.adventure.completed()+" savedLevel="+prefs.getInt("saved_adventure_level",-1)+" resumed="+view.adventureLevel+" world="+view.theme+" pairs="+view.pairs+" screen="+view.screen);}finally{view.releaseAudio();}
                result.putString("stream","PASS: signed upgrade9.3 to9.4 keeps17 completed levels, level18 saved game, albums, record, sound and calm\n");
            } else if ("v2before".equals(mode) || "v2after".equals(mode)) {
                android.content.Intent intent=new android.content.Intent(getTargetContext(),MainActivity.class).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                MainActivity activity=(MainActivity)host.startActivitySync(intent);
                host.runOnMainSync(() -> {
                    activity.updates.close(); GameView view=activity.game;
                    view.sound=false; view.pauseMusic(); view.calm=true; view.screen=0; view.showResumePrompt=false; view.theme=1; view.pairs=6; view.invalidate();
                });
                String prefix=arguments.getString("capturePrefix", "v2before".equals(mode) ? "v2-before" : "v2-after");
                capture(prefix+"-home.png");
                host.runOnMainSync(() -> { activity.game.screen=4; activity.game.invalidate(); });
                capture(prefix+"-worlds.png");
                host.runOnMainSync(() -> { activity.game.screen=0; activity.game.pairs=12; activity.game.start(); activity.game.tutorialActive=false; activity.game.invalidate(); });
                capture(prefix+"-board12.png");
                host.runOnMainSync(() -> { activity.game.clearSavedGame(); activity.finish(); });
                result.putString("stream","PASS: "+prefix+" real activity captures saved\n");
            } else if ("v2seed".equals(mode)) {
                if (!getTargetContext().getSharedPreferences("m",0).edit().putInt("theme",17).putInt("pairs",12)
                        .putInt("album_17",63).putInt("best_12",19).putInt("data_version",5)
                        .putBoolean("audio_default_v7",true).putBoolean("sound",false).putBoolean("calm",true)
                        .putBoolean("tutorial_done",true).commit()) throw new AssertionError("Seed failed");
                result.putString("stream","PASS: V2 upgrade data written on version "+UpdateManager.version(getTargetContext().getPackageManager().getPackageInfo("fr.memomimi.app",0))+"\n");
            } else if ("adventureverify".equals(mode)) {
                android.content.SharedPreferences prefs=getTargetContext().getSharedPreferences("m",0);
                if(UpdateManager.version(getTargetContext().getPackageManager().getPackageInfo("fr.memomimi.app",0))!=44
                        || prefs.getInt("theme",-1)!=17 || prefs.getInt("pairs",-1)!=12 || prefs.getInt("album_17",0)!=63
                        || prefs.getInt("best_12",0)!=19 || prefs.getBoolean("sound",true) || !prefs.getBoolean("calm",false)
                        || new AdventureStore(getTargetContext()).completed()!=0) throw new AssertionError("Upgrade data changed");
                result.putString("stream","PASS: signed upgrade 9.2 to 9.3 preserves album, record, world, sound and calm; adventure starts at level 1\n");
            } else if ("v2verify".equals(mode)) {
                android.content.SharedPreferences prefs=getTargetContext().getSharedPreferences("m",0);
                if (UpdateManager.version(getTargetContext().getPackageManager().getPackageInfo("fr.memomimi.app",0))!=41
                        || prefs.getInt("theme",-1)!=17 || prefs.getInt("pairs",-1)!=12 || prefs.getInt("album_17",0)!=63
                        || prefs.getInt("best_12",0)!=19 || prefs.getBoolean("sound",true) || !prefs.getBoolean("calm",false))
                    throw new AssertionError("Old data changed during upgrade");
                result.putString("stream","PASS: signed upgrade 8.11 to V2, world, difficulty, album, record, sound and calm preserved\n");
            } else if ("panel".equals(mode)) {
                android.content.Intent intent = new android.content.Intent(getTargetContext(), MainActivity.class);
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                MainActivity activity = (MainActivity) host.startActivitySync(intent);
                final android.app.Dialog[] dialog = new android.app.Dialog[1];
                host.runOnMainSync(() -> {
                    activity.updates.close();
                    try { activity.updates.ready = new JSONObject().put("versionName", "8.8"); }
                    catch (Exception failure) { throw new RuntimeException(failure); }
                    activity.updates.readyListener.run();
                    dialog[0] = UpdatePanel.show(activity, "8.8", () -> { });
                });
                capture("update-panel.png");
                host.runOnMainSync(() -> dialog[0].dismiss());
                capture("update-banner.png");
                host.runOnMainSync(() -> dialog[0] = UpdatePanel.permission(activity, () -> { }, () -> { }));
                capture("update-permission.png");
                host.runOnMainSync(() -> { dialog[0].dismiss(); activity.finish(); });
                result.putString("stream", "PASS: native update dialog, banner and permission previews captured\n");
            } else if ("seed".equals(mode)) {
                if (!getTargetContext().getSharedPreferences("update-proof", 0).edit().putInt("sentinel", 20261010).commit()) throw new AssertionError("Seed");
                result.putString("stream", "PASS: progression marker written\n");
            } else if ("verify".equals(mode)) {
                if (getTargetContext().getSharedPreferences("update-proof", 0).getInt("sentinel", 0) != 20261010) throw new AssertionError("Progress lost");
                long version = UpdateManager.version(getTargetContext().getPackageManager().getPackageInfo("fr.memomimi.app", 0));
                if (version != 36) throw new AssertionError("Update not installed: " + version);
                result.putString("stream", "PASS: version 36 installed, progression preserved\n");
            } else {
                File directory = getTargetContext().getExternalFilesDir(null);
                File base = new File(directory, "update-base.apk");
                File manifestFile = new File(directory, "update-test.json");
                File patchFile = new File(directory, "update-test.mmd.gz");
                File rebuilt = new File(directory, "update-rebuilt.apk");
                try {
                    UpdateManager.download(UpdateManager.FEED, manifestFile, 262144);
                    JSONObject manifest = UpdateManager.readJson(manifestFile);
                    UpdateManager.validateManifest(manifest);
                    JSONObject patch = UpdateManager.selectPatch(manifest, ApkDelta.sha256(base));
                    if (patch == null) throw new AssertionError("No patch for test base");
                    long bytes = UpdateManager.download(patch.getString("url"), patchFile, patch.getLong("size"));
                    if (bytes != patch.getLong("size") || !ApkDelta.sha256(patchFile).equals(patch.getString("sha256"))) throw new AssertionError("Download integrity");
                    ApkDelta.apply(base, patchFile, rebuilt, manifest.getLong("apkSize"), manifest.getString("apkSha256"));
                    UpdateManager.validateApk(getTargetContext(), rebuilt, manifest, 34);
                    try {
                        getTargetContext().getContentResolver().openFileDescriptor(android.net.Uri.parse("content://fr.memomimi.app.updates/update.apk"), "w");
                        throw new AssertionError("Provider accepts writes");
                    } catch (java.io.FileNotFoundException expected) { }
                    result.putString("stream", "PASS: GitHub HTTPS download " + bytes + " bytes, exact reconstructed APK " + rebuilt.length()
                            + " bytes, package/version/signature verified, provider read-only\n");
                } finally { manifestFile.delete(); patchFile.delete(); rebuilt.delete(); }
            }
        } catch (Throwable failure) {
            result.putString("stream", "FAIL: " + failure + "\n");
        }
        return result;
    }
    void capture(String name) throws Exception {
        host.waitForIdleSync();
        Thread.sleep(350);
        android.graphics.Bitmap bitmap = host.getUiAutomation().takeScreenshot();
        if (bitmap == null) throw new AssertionError("Screenshot unavailable");
        try (java.io.FileOutputStream output = new java.io.FileOutputStream(new File(getTargetContext().getExternalFilesDir(null), name))) {
            bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, output);
        } finally { bitmap.recycle(); }
    }
}
