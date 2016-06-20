package com.AuroraByteSoftware.AuroraDMX;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
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

public class DMXContentProvider extends ContentProvider implements android.content.ContentProvider.PipeDataWriter<InputStream> {
    private static final String TAG = "AuroraDMX";

    public DMXContentProvider() {
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        //Adapt this to your code
        File file = new File(getContext().getCacheDir(), uri.getLastPathSegment());//#
        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        return null;
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

    public void writeDataToPipe(ParcelFileDescriptor arg0, Uri arg1,
                                String arg2, Bundle arg3, InputStream arg4) {
        // Transfer data from the asset to the pipe the client is reading.
        byte[] buffer = new byte[8192];
        int n;
        FileOutputStream fout = new FileOutputStream(arg0.getFileDescriptor());
        try {
            while ((n=arg4.read(buffer)) >= 0) {
                fout.write(buffer, 0, n);
            }
        } catch (IOException e) {
            Log.i("InstallApk", "Failed transferring", e);
        } finally {
            try {
                arg4.close();
            } catch (IOException e) {
            }
            try {
                fout.close();
            } catch (IOException e) {
            }
        }
    }
}
