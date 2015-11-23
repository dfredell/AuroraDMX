package com.AuroraByteSoftware.AuroraDMX.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.AuroraByteSoftware.AuroraDMX.MainActivity;
import com.AuroraByteSoftware.AuroraDMX.R;
import com.AuroraByteSoftware.AuroraDMX.fixture.Fixture;
import com.AuroraByteSoftware.AuroraDMX.fixture.FixtureUtility;

import java.util.ArrayList;
import java.util.List;

public class EditColumnMenu extends MainActivity {

    private static final String TAG = "AuroraDMX";

    public static void createEditColumnMenu(View v, final MainActivity context, final Fixture fixture, String chText, int chLevel) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.action_about);

        builder.setInverseBackgroundForced(true);
        // Create the Save button
        builder.setPositiveButton(R.string.Save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                EditText editColumnName = (EditText) ((AlertDialog) dialog).findViewById(R.id.editColumnName);
                Switch rgbSwitch = (Switch) ((AlertDialog) dialog).findViewById(R.id.chanel_rgb);

                if (rgbSwitch.isChecked() && !fixture.isRGB()) {
                    FixtureUtility.switchToRGB(fixture, context);
                } else if (!rgbSwitch.isChecked() && fixture.isRGB()) {
                    FixtureUtility.switchToStandard(fixture, context);
                }

                try {
                    String columnName = editColumnName.getText().toString();
                    Log.d(TAG, "Col name: " + columnName);
                    fixture.setColumnText(columnName);

                    List<Integer> specifiedLevel = new ArrayList<>();
                    if (fixture.isRGB()) {
                        EditText editRLevel = (EditText) ((AlertDialog) dialog).findViewById(R.id.editRLevel);
                        EditText editGLevel = (EditText) ((AlertDialog) dialog).findViewById(R.id.editGLevel);
                        EditText editBLevel = (EditText) ((AlertDialog) dialog).findViewById(R.id.editBLevel);
                        specifiedLevel.add(Integer.parseInt(editRLevel.getText().toString()));
                        specifiedLevel.add(Integer.parseInt(editGLevel.getText().toString()));
                        specifiedLevel.add(Integer.parseInt(editBLevel.getText().toString()));
                    } else {
                        EditText editColumnLevel = (EditText) ((AlertDialog) dialog).findViewById(R.id.editColumnLevel);
                        specifiedLevel.add(Integer.parseInt(editColumnLevel.getText().toString()));
                    }
                    fixture.setChLevels(specifiedLevel);

                } catch (NumberFormatException n) {
                    Toast.makeText(context, R.string.errNumConv, Toast.LENGTH_SHORT).show();
                } catch (Throwable t) {
                    Toast.makeText(context, R.string.Error, Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
            }
        });
        // Create the Cancel button
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // set prompts.xml to alertdialog builder
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView;


        //Set previous text
        if (fixture.isRGB()) {
            promptsView = li.inflate(R.layout.dialog_column_rgb, (ViewGroup) v.getParent(), false);
            ((EditText) promptsView.findViewById(R.id.editRLevel)).setText(String.format("%1$s", fixture.getChLevels().get(0)));
            ((EditText) promptsView.findViewById(R.id.editGLevel)).setText(String.format("%1$s", fixture.getChLevels().get(1)));
            ((EditText) promptsView.findViewById(R.id.editBLevel)).setText(String.format("%1$s", fixture.getChLevels().get(2)));
        } else {
            promptsView = li.inflate(R.layout.dialog_column, (ViewGroup) v.getParent(), false);
            ((EditText) promptsView.findViewById(R.id.editColumnLevel)).setText(String.format("%1$s", chLevel));
        }
        ((Switch) promptsView.findViewById(R.id.chanel_rgb)).setChecked(fixture.isRGB());
        ((EditText) promptsView.findViewById(R.id.editColumnName)).setText(chText);

        builder.setView(promptsView);
        AlertDialog alert = builder.create();
        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        alert.show();
    }
}
