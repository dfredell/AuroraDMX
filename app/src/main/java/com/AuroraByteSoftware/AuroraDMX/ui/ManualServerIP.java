package com.AuroraByteSoftware.AuroraDMX.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.preference.CheckBoxPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.AuroraByteSoftware.AuroraDMX.MainActivity;
import com.AuroraByteSoftware.AuroraDMX.R;
import com.AuroraByteSoftware.AuroraDMX.SettingsActivity;

public class ManualServerIP {
    public static void askForString(Activity activity, final CheckBoxPreference checkboxPrefManual) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(true);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                //Do nothing
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                EditText editTextServerIP = ((AlertDialog) arg0)
                        .findViewById(R.id.editTextServerIP);
                MainActivity.getSharedPref().edit().putString(
                        SettingsActivity.manualserver, editTextServerIP.getText().toString()).apply();
                checkboxPrefManual.setSummary(editTextServerIP.getText().toString());
                MainActivity.getSharedPref().edit().putString(SettingsActivity.serveraddress, editTextServerIP.getText().toString()).apply();
            }
        });
        // set prompts.xml to alertdialog builder
        LayoutInflater li = LayoutInflater.from(activity);
        View promptsView = li.inflate(R.layout.dialog_server, null);
        EditText editTextServerIP = promptsView.findViewById(R.id.editTextServerIP);
        String addr = MainActivity.getSharedPref().getString(SettingsActivity.manualserver, "192.168.0.0:0");
        editTextServerIP.setText(addr);
        editTextServerIP.setSelection(addr.length());
        builder.setView(promptsView);

        AlertDialog alert = builder.create();
        alert.show();
    }

}
