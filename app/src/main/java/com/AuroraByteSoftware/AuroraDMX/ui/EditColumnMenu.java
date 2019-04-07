package com.AuroraByteSoftware.AuroraDMX.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


    public static void createEditColumnMenu(final View v, final MainActivity context, final Fixture fixture,
                                            String chText, int chLevel, String chValuePresets) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);

        // Create the Save button
        builder.setPositiveButton(R.string.Save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    String columnName = UiUtil.getTextFromDialog(dialog, R.id.editColumnName);
                    Log.d(getClass().getSimpleName(), "Col name: " + columnName);
                    fixture.setColumnText(columnName);

                    String valuePresets = UiUtil.getTextFromDialog(dialog, R.id.editValuePresets);
                    Log.d(getClass().getSimpleName(), "Value presets: " + valuePresets);
                    fixture.setValuePresets(valuePresets);

                    List<Integer> specifiedLevel = new ArrayList<>();
                    if (fixture.isRGB()) {
                        specifiedLevel.add(UiUtil.getIntFromDialog(dialog, R.id.editRLevel, v));
                        specifiedLevel.add(UiUtil.getIntFromDialog(dialog, R.id.editGLevel, v));
                        specifiedLevel.add(UiUtil.getIntFromDialog(dialog, R.id.editBLevel, v));
                    } else {
                        specifiedLevel.add(UiUtil.getIntFromDialog(dialog, R.id.editColumnLevel, v));
                    }
                    fixture.setChLevels(specifiedLevel);

                } catch (NumberFormatException n) {
                    Toast.makeText(context, R.string.errNumConv, Toast.LENGTH_SHORT).show();
                } catch (Throwable t) {
                    Toast.makeText(context, R.string.Error, Toast.LENGTH_SHORT).show();
                }
                Switch rgbSwitch = ((AlertDialog) dialog).findViewById(R.id.fixture_rgb);
                Switch parkSwitch = ((AlertDialog) dialog).findViewById(R.id.fixture_park);
                fixture.setParked(parkSwitch.isChecked());

                if (rgbSwitch.isChecked() && !fixture.isRGB()) {
                    FixtureUtility.switchToRGB(fixture, context);
                } else if (!rgbSwitch.isChecked() && fixture.isRGB()) {
                    FixtureUtility.switchToStandard(fixture, context);
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
        ((Switch) promptsView.findViewById(R.id.fixture_rgb)).setChecked(fixture.isRGB());
        ((Switch) promptsView.findViewById(R.id.fixture_park)).setChecked(fixture.isParked());
        ((EditText) promptsView.findViewById(R.id.editColumnName)).setText(chText);
        ((EditText) promptsView.findViewById(R.id.editValuePresets)).setText(chValuePresets);

        builder.setView(promptsView);
        AlertDialog alert = builder.create();

        alert.show();
    }
}
