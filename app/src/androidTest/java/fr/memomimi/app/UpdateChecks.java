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
            if ("panel".equals(mode)) {
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
