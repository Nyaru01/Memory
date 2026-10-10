package fr.memomimi.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Public GitHub releases only. A missing delta never triggers a full-APK download. */
final class UpdateManager {
    static final String RELEASE_ROOT = "https://github.com/Nyaru01/Memory/releases/";
    static final String FEED = RELEASE_ROOT + "latest/download/update.json";
    static final long MAX_PATCH = 64L * 1024 * 1024;
    final Activity activity;
    final Runnable readyListener;
    final UpdatePoller poller;
    final ExecutorService worker = Executors.newSingleThreadExecutor();
    volatile boolean closed;
    boolean resumed, announced, permissionRequested, busy;
    JSONObject ready;
    String message = "Vérification des mises à jour…";

    UpdateManager(Activity activity, Runnable listener) {
        this.activity = activity; readyListener = listener;
        poller = new UpdatePoller(() -> {
            if (closed) return;
            if (ready == null) check();
            else announceWhenSafe();
        });
    }
    static long version(PackageInfo info) { return Build.VERSION.SDK_INT >= 28 ? info.getLongVersionCode() : info.versionCode; }
    static void hashField(String value) throws IOException {
        if (!value.matches("[a-f0-9]{64}")) throw new IOException("Invalid digest");
    }
    static void assetUrl(String value) throws IOException {
        URL url = new URL(value);
        if (!"https".equals(url.getProtocol()) || !"github.com".equals(url.getHost())
                || url.getPort() != -1 || url.getUserInfo() != null
                || !url.getPath().startsWith("/Nyaru01/Memory/releases/download/")) throw new IOException("Untrusted asset URL");
    }
    static void validateManifest(JSONObject manifest) throws Exception {
        if (manifest.getInt("schema") != 1 || !"fr.memomimi.app".equals(manifest.getString("packageName"))) throw new IOException("Manifest identity");
        if (manifest.getLong("versionCode") <= 0 || manifest.getString("versionName").length() > 40) throw new IOException("Version");
        long size = manifest.getLong("apkSize");
        if (size <= 0 || size > ApkDelta.MAX_APK) throw new IOException("Size");
        hashField(manifest.getString("apkSha256"));
        JSONArray patches = manifest.getJSONArray("patches");
        if (patches.length() > 100) throw new IOException("Too many patches");
        for (int i = 0; i < patches.length(); i++) {
            JSONObject patch = patches.getJSONObject(i);
            hashField(patch.getString("baseSha256")); hashField(patch.getString("sha256"));
            assetUrl(patch.getString("url"));
            long bytes = patch.getLong("size");
            if (bytes <= 0 || bytes > MAX_PATCH || bytes >= size) throw new IOException("Patch size");
        }
    }
    static JSONObject selectPatch(JSONObject manifest, String baseHash) throws Exception {
        JSONArray patches = manifest.getJSONArray("patches");
        for (int i = 0; i < patches.length(); i++) {
            JSONObject patch = patches.getJSONObject(i);
            if (patch.getString("baseSha256").equals(baseHash)) return patch;
        }
        return null;
    }
    static long download(String address, File output, long max) throws Exception {
        URL url = new URL(address);
        HttpURLConnection connection = null;
        boolean success = false;
        try {
            for (int redirects = 0; redirects < 6; redirects++) {
                String host = url.getHost();
                if (!"https".equals(url.getProtocol()) || url.getUserInfo() != null || url.getPort() != -1
                        || !(host.equals("github.com") || host.endsWith(".githubusercontent.com"))) throw new IOException("Redirect rejected");
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(12000); connection.setReadTimeout(20000);
                connection.setUseCaches(false);
                connection.setRequestProperty("Cache-Control", "no-cache");
                connection.setInstanceFollowRedirects(false); connection.setRequestProperty("User-Agent", "MemoMondes-Updater/1");
                int status = connection.getResponseCode();
                if (status == 301 || status == 302 || status == 303 || status == 307 || status == 308) {
                    String next = connection.getHeaderField("Location");
                    if (next == null) throw new IOException("Missing redirect");
                    url = new URL(url, next); connection.disconnect(); connection = null; continue;
                }
                if (status != 200) throw new IOException("HTTP " + status);
                if (connection.getContentLength() > max) throw new IOException("Download too large");
                long bytes = 0;
                byte[] buffer = new byte[65536];
                try (InputStream input = connection.getInputStream(); OutputStream target = new FileOutputStream(output)) {
                    int n;
                    while ((n = input.read(buffer)) != -1) {
                        ApkDelta.interrupted();
                        bytes += n; if (bytes > max) throw new IOException("Download too large");
                        target.write(buffer, 0, n);
                    }
                }
                success = true; return bytes;
            }
            throw new IOException("Too many redirects");
        } finally {
            if (connection != null) connection.disconnect();
            if (!success) output.delete();
        }
    }
    static JSONObject readJson(File file) throws Exception {
        if (file.length() > 262144) throw new IOException("Manifest too large");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (InputStream input = new FileInputStream(file)) {
            byte[] buffer = new byte[8192]; int n;
            while ((n = input.read(buffer)) != -1) { bytes.write(buffer, 0, n); if (bytes.size() > 262144) throw new IOException("Manifest too large"); }
        }
        return new JSONObject(bytes.toString("UTF-8"));
    }
    static void validateApk(android.content.Context context, File apk, JSONObject manifest, long installedVersion) throws Exception {
        validateManifest(manifest);
        if (apk.length() != manifest.getLong("apkSize") || !ApkDelta.sha256(apk).equals(manifest.getString("apkSha256"))) throw new IOException("APK digest");
        PackageManager pm = context.getPackageManager();
        PackageInfo target = pm.getPackageArchiveInfo(apk.getAbsolutePath(), PackageManager.GET_SIGNATURES);
        PackageInfo current = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
        if (target == null || !current.packageName.equals(target.packageName) || version(target) != manifest.getLong("versionCode")
                || version(target) <= installedVersion || !manifest.getString("versionName").equals(target.versionName)
                || target.signatures == null || target.signatures.length == 0 || !Arrays.equals(current.signatures, target.signatures)) throw new IOException("APK identity or signature");
        if (Build.VERSION.SDK_INT >= 24 && target.applicationInfo.minSdkVersion > Build.VERSION.SDK_INT) throw new IOException("Android version");
    }
    void check() {
        if (closed || busy) return;
        busy = true;
        worker.execute(() -> {
            File directory = UpdateFileProvider.apk(activity).getParentFile();
            File feed = new File(directory, "feed.part");
            File patchFile = new File(directory, "delta.part");
            File reconstructed = new File(directory, "apk.part");
            File storedManifest = new File(directory, "ready.json");
            try {
                if (!directory.isDirectory() && !directory.mkdirs()) throw new IOException("Storage");
                PackageInfo installed = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
                long currentVersion = version(installed);
                File readyApk = UpdateFileProvider.apk(activity);
                if (readyApk.isFile() && storedManifest.isFile()) {
                    try {
                        JSONObject cached = readJson(storedManifest);
                        validateApk(activity, readyApk, cached, currentVersion);
                        publish(cached, "Mise à jour " + cached.getString("versionName") + " prête"); return;
                    } catch (Exception stale) { readyApk.delete(); storedManifest.delete(); }
                }
                download(FEED, feed, 262144);
                JSONObject manifest = readJson(feed); validateManifest(manifest);
                if (manifest.getLong("versionCode") <= currentVersion) { publish(null, "Le jeu est à jour"); return; }
                if (manifest.optInt("minSdk", 23) > Build.VERSION.SDK_INT) { publish(null, "Cette mise à jour nécessite un Android plus récent"); return; }
                File base = new File(activity.getApplicationInfo().sourceDir);
                JSONObject patch = selectPatch(manifest, ApkDelta.sha256(base));
                if (patch == null) { publish(null, "Aucun correctif disponible pour cette installation"); return; }
                if (directory.getUsableSpace() < manifest.getLong("apkSize") + patch.getLong("size") + 16 * 1024 * 1024) { publish(null, "Espace insuffisant pour la mise à jour"); return; }
                publish(null, "Téléchargement du correctif…");
                long bytes = download(patch.getString("url"), patchFile, patch.getLong("size"));
                if (bytes != patch.getLong("size") || !ApkDelta.sha256(patchFile).equals(patch.getString("sha256"))) throw new IOException("Patch digest");
                publish(null, "Préparation de la mise à jour…");
                ApkDelta.apply(base, patchFile, reconstructed, manifest.getLong("apkSize"), manifest.getString("apkSha256"));
                validateApk(activity, reconstructed, manifest, currentVersion);
                ApkDelta.interrupted();
                if (!reconstructed.renameTo(readyApk) || !feed.renameTo(storedManifest)) throw new IOException("Save update");
                publish(manifest, "Mise à jour " + manifest.getString("versionName") + " prête");
            } catch (Exception failure) {
                if (!closed) publish(null, "Mise à jour indisponible ; le jeu reste utilisable hors ligne");
                android.util.Log.w("MemoMondesUpdate", "Update not prepared", failure);
            } finally {
                feed.delete(); patchFile.delete(); reconstructed.delete();
                activity.runOnUiThread(() -> busy = false);
            }
        });
    }
    void publish(JSONObject manifest, String text) {
        activity.runOnUiThread(() -> {
            if (closed) return;
            ready = manifest; message = text;
            readyListener.run();
            announceWhenSafe();
        });
    }
    void resume() {
        if (closed) return;
        resumed = true;
        poller.resume();
        if (permissionRequested) {
            permissionRequested = false;
            if (Build.VERSION.SDK_INT < 26 || activity.getPackageManager().canRequestPackageInstalls()) install();
        } else announceWhenSafe();
    }
    void pause() { resumed = false; poller.pause(); }
    void announceWhenSafe() {
        if (ready == null || !resumed || announced || closed) return;
        if (activity instanceof MainActivity) {
            GameView game = ((MainActivity)activity).game;
            if (game != null && (game.screen < 0 || game.screen == 1 || game.screen == 6)) return;
        }
        announced = true;
        show();
    }
    void show() {
        if (closed || !resumed) return;
        if (ready == null) { check(); return; }
        UpdatePanel.show(activity, ready.optString("versionName"), () -> install());
    }
    void install() {
        if (ready == null || closed) return;
        if (Build.VERSION.SDK_INT >= 26 && !activity.getPackageManager().canRequestPackageInstalls()) {
            permissionRequested = true;
            UpdatePanel.permission(activity, () -> {
                        try { activity.startActivity(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + activity.getPackageName()))); }
                        catch (Exception unavailable) { permissionRequested = false; }
                    }, () -> permissionRequested = false);
            return;
        }
        worker.execute(() -> {
            try {
                validateApk(activity, UpdateFileProvider.apk(activity), ready,
                        version(activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0)));
                activity.runOnUiThread(() -> {
                    if (closed || !resumed) return;
                    Uri uri = Uri.parse("content://" + activity.getPackageName() + ".updates/update.apk");
                    Intent intent = new Intent(Intent.ACTION_VIEW).setDataAndType(uri, "application/vnd.android.package-archive")
                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    try { activity.startActivity(intent); }
                    catch (Exception unavailable) { new AlertDialog.Builder(activity).setMessage("Impossible d’ouvrir l’installateur Android.").setPositiveButton("OK", null).show(); }
                });
            } catch (Exception invalid) { publish(null, "La mise à jour n’est plus valide ; réessaie"); }
        });
    }
    void close() { closed = true; pause(); worker.shutdownNow(); }
}
