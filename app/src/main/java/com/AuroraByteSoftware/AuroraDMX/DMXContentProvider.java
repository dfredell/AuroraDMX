package com.AuroraByteSoftware.AuroraDMX;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DMXContentProvider extends ContentProvider implements ContentProvider.PipeDataWriter<InputStream> {

    public DMXContentProvider() {
    }

    /**
     * Used to load the file when sharing
     *
     * @param uri  file to attach to an email
     * @param mode
     * @return
     * @throws FileNotFoundException
     */
    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        File file = new File(getContext().getCacheDir(), uri.getLastPathSegment());//#
        try {
            if (!file.getCanonicalPath().startsWith(this.getContext().getCacheDir().getCanonicalPath())) {
                // https://support.google.com/faqs/answer/7496913
                throw new IllegalArgumentException();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        return "application/octet-stream";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return 0;
    }

    @Override
    public void writeDataToPipe(ParcelFileDescriptor arg0, Uri arg1,
                                String arg2, Bundle arg3, InputStream arg4) {
        // Transfer data from the asset to the pipe the client is reading.
        byte[] buffer = new byte[8192];
        int n;
        FileOutputStream fout = new FileOutputStream(arg0.getFileDescriptor());
        try {
            while ((n = arg4.read(buffer)) >= 0) {
                fout.write(buffer, 0, n);
            }
        } catch (IOException e) {
            Log.i(getClass().getSimpleName(), "Failed transferring", e);
        } finally {
            try {
                arg4.close();
            } catch (IOException e) {
                Log.i(getClass().getSimpleName(), "Can't close", e);
            }
            try {
                fout.close();
            } catch (IOException e) {
                Log.i(getClass().getSimpleName(), "Can't close", e);
            }
        }
    }
}
