package fr.memomimi.app;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import java.io.File;
import java.io.FileNotFoundException;

/** Exposes only the verified update file, read-only, through a temporary URI grant. */
public final class UpdateFileProvider extends ContentProvider {
    static File apk(android.content.Context context) {
        return new File(new File(context.getNoBackupFilesDir(), "updates"), "ready.apk");
    }
    private void check(Uri uri) {
        if (!"/update.apk".equals(uri.getPath())) throw new IllegalArgumentException("Unknown file");
    }
    @Override public boolean onCreate() { return true; }
    @Override public String getType(Uri uri) { check(uri); return "application/vnd.android.package-archive"; }
    @Override public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        check(uri);
        if (!"r".equals(mode)) throw new FileNotFoundException("Read only");
        return ParcelFileDescriptor.open(apk(getContext()), ParcelFileDescriptor.MODE_READ_ONLY);
    }
    @Override public Cursor query(Uri uri, String[] projection, String selection, String[] args, String order) {
        check(uri);
        String[] columns = projection == null ? new String[]{OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE} : projection;
        MatrixCursor cursor = new MatrixCursor(columns);
        Object[] values = new Object[columns.length];
        for (int i = 0; i < columns.length; i++) {
            if (OpenableColumns.DISPLAY_NAME.equals(columns[i])) values[i] = "MemoMondes.apk";
            if (OpenableColumns.SIZE.equals(columns[i])) values[i] = apk(getContext()).length();
        }
        cursor.addRow(values); return cursor;
    }
    @Override public Uri insert(Uri uri, ContentValues values) { throw new UnsupportedOperationException(); }
    @Override public int delete(Uri uri, String selection, String[] args) { throw new UnsupportedOperationException(); }
    @Override public int update(Uri uri, ContentValues values, String selection, String[] args) { throw new UnsupportedOperationException(); }
}
