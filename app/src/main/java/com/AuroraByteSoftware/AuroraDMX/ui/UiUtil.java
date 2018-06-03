package com.AuroraByteSoftware.AuroraDMX.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.widget.EditText;

/**
 * Some helpful utils for the UI
 * Created by furtchet on 3/17/18.
 */

public class UiUtil {

    /**
     * Give the dialog and the view id of the EditText return the text
     *
     * @param dialog dialog with the viewId
     * @param viewId the viewId of an EditText
     * @return typed value
     */
    public static String getTextFromDialog(DialogInterface dialog, int viewId) {
        Dialog alertDialog = (Dialog) dialog;
        return ((EditText) alertDialog.findViewById(viewId)).getText().toString();
    }

    /**
     * Give the dialog and the view id of the EditText return the int
     *
     * @param dialog dialog with the viewId
     * @param viewId the viewId of an EditText
     * @return typed int value
     */
    public static int getIntFromDialog(DialogInterface dialog, int viewId) {
        return Integer.parseInt(getTextFromDialog(dialog, viewId));
    }
}
