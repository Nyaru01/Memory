package fr.memomimi.app;

import org.json.JSONObject;
import org.json.JSONArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28, 35})
public class UpdateManifestTest {
    static final String HASH = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
    JSONObject manifest() throws Exception {
        JSONObject patch = new JSONObject().put("baseSha256", HASH).put("sha256", HASH).put("size", 100)
                .put("url", "https://github.com/Nyaru01/Memory/releases/download/v8.6/from-test.mmd.gz");
        return new JSONObject().put("schema", 1).put("packageName", "fr.memomimi.app").put("versionCode", 35)
                .put("versionName", "8.6").put("apkSize", 1000).put("apkSha256", HASH).put("patches", new JSONArray().put(patch));
    }
    @Test public void selectsOnlyExactInstalledApk() throws Exception {
        JSONObject manifest = manifest(); UpdateManager.validateManifest(manifest);
        assertNotNull(UpdateManager.selectPatch(manifest, HASH));
        assertNull(UpdateManager.selectPatch(manifest, "0000000000000000000000000000000000000000000000000000000000000000"));
    }
    @Test public void rejectsExternalRepoAndPlainHttp() throws Exception {
        for (String url : new String[]{"http://github.com/Nyaru01/Memory/releases/download/v1/x",
                "https://github.com/other/Memory/releases/download/v1/x", "https://github.com.evil.invalid/Nyaru01/Memory/releases/download/v1/x"}) {
            try { UpdateManager.assetUrl(url); fail(url); } catch (java.io.IOException expected) { }
        }
    }
    @Test public void rejectsFullApkDisguisedAsPatch() throws Exception {
        JSONObject manifest = manifest(); manifest.getJSONArray("patches").getJSONObject(0).put("size", 1000);
        try { UpdateManager.validateManifest(manifest); fail(); } catch (java.io.IOException expected) { }
    }
    @Test public void rejectsDifferentPackage() throws Exception {
        JSONObject manifest = manifest().put("packageName", "other.app");
        try { UpdateManager.validateManifest(manifest); fail(); } catch (java.io.IOException expected) { }
    }
    @Test public void rejectsInvalidHash() throws Exception {
        JSONObject manifest = manifest().put("apkSha256", "not-a-hash");
        try { UpdateManager.validateManifest(manifest); fail(); } catch (java.io.IOException expected) { }
    }
}
