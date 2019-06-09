package com.AuroraByteSoftware.AuroraDMX.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.AuroraByteSoftware.AuroraDMX.R;

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
     * Give the dialog and the view id of the EditText return the text
     *
     * @param view dialog with the viewId
     * @param viewId the viewId of an EditText
     * @return typed value
     */
    public static String getTextFromDialog(View view, int viewId) {
        return ((EditText) view.findViewById(viewId)).getText().toString();
    }

    /**
     * Give the dialog and the view id of the EditText return the int
     * If empty then 0
     * @param view dialog with the viewId
     * @param viewId the viewId of an EditText
     * @return typed int value
     */
    public static int getIntFromDialog(DialogInterface dialog, int viewId, View view) {
        final String textFromDialog = getTextFromDialog(dialog, viewId);
        if ("".equals(textFromDialog.trim())){
            return 0;
        }
        try {
            return Integer.parseInt(textFromDialog);
        } catch (NumberFormatException n) {
            Toast.makeText(view.getContext(), R.string.errNumConv, Toast.LENGTH_SHORT).show();
        } catch (Throwable t) {
            Toast.makeText(view.getContext(), R.string.Error, Toast.LENGTH_SHORT).show();
        }
        return 0;
    }


    /**
     * Give the dialog and the view id of the EditText return the int
     * If empty then 0
     * @param view dialog with the viewId
     * @param viewId the viewId of an EditText
     * @return typed int value
     */
    public static int getIntFromDialog(View view, int viewId) {
        final String textFromDialog = getTextFromDialog(view, viewId);
        if ("".equals(textFromDialog.trim())){
            return 0;
        }
        try {
            return Integer.parseInt(textFromDialog);
        } catch (NumberFormatException n) {
            Toast.makeText(view.getContext(), R.string.errNumConv, Toast.LENGTH_SHORT).show();
        } catch (Throwable t) {
            Toast.makeText(view.getContext(), R.string.Error, Toast.LENGTH_SHORT).show();
        }
        return 0;
    }

    /**
     * Give the dialog and the view id of the EditText return the Double
     * If empty then 0
     * @param view dialog with the viewId
     * @param viewId the viewId of an EditText
     * @return typed Double value
     */
    public static Double getDoubleFromDialog(View view, int viewId) {
        final String textFromDialog = getTextFromDialog(view, viewId);
        if ("".equals(textFromDialog.trim())){
            return 0.0;
        }
        try {
            return Double.parseDouble(textFromDialog);
        } catch (NumberFormatException n) {
            Toast.makeText(view.getContext(), R.string.errNumConv, Toast.LENGTH_SHORT).show();
        } catch (Throwable t) {
            Toast.makeText(view.getContext(), R.string.Error, Toast.LENGTH_SHORT).show();
        }
        return 0.0;
    }
}
