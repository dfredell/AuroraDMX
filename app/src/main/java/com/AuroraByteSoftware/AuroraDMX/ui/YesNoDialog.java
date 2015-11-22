package com.AuroraByteSoftware.AuroraDMX.ui;


import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import com.AuroraByteSoftware.AuroraDMX.MainActivity;
import com.AuroraByteSoftware.AuroraDMX.SettingsActivity;

/**
 * The OptionDialogPreference will display a dialog, and will persist the
 * <code>true</code> when pressing the positive button and <code>false</code>
 * otherwise. It will persist to the android:key specified in xml-preference.
 */
public class YesNoDialog extends DialogPreference {
    /**
     * constructor
     *
     * @param context parent context
     * @param attrs   data attributes
     */
    public YesNoDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * deals with action to do once dialog is closed
     *
     * @param positiveResult true or false
     */
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (callChangeListener(positiveResult)) {
            //System.out.println(positiveResult);
            MainActivity.getSharedPref().edit().putBoolean(SettingsActivity.restoredefaults, positiveResult).commit();
        }
    }
}