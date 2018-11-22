package com.AuroraByteSoftware.AuroraDMX.ui;

import android.app.Activity;
import android.content.Intent;

public class ImportFile {


    public static final int READ_REQUEST_CODE = 42;

    public void onImport(Activity activity) {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("application/octet-stream");

        activity.startActivityForResult(intent, READ_REQUEST_CODE);
    }


}
