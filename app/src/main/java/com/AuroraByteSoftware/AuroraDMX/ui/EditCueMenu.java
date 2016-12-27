package com.AuroraByteSoftware.AuroraDMX.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.AuroraByteSoftware.AuroraDMX.CueClickListener;
import com.AuroraByteSoftware.AuroraDMX.CueObj;
import com.AuroraByteSoftware.AuroraDMX.CueSorter;
import com.AuroraByteSoftware.AuroraDMX.MainActivity;
import com.AuroraByteSoftware.AuroraDMX.R;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.Collections;

public class EditCueMenu extends MainActivity {
    private static int currentCue = -1;// used to and from cue edit
    private static final String TAG = "AuroraDMX";

    @SuppressLint("SetTextI18n")
    public static void createEditCueMenu(final ArrayList<CueObj> alCues, Button button) {
        // Find what Cue we are in
        for (int x = 0; x < alCues.size(); x++) {
            if (alCues.get(x).getButton() == button) {
                currentCue = x;
            }
        }
        final Context context = button.getContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setIcon(
                new IconDrawable(button.getContext().getApplicationContext(), FontAwesomeIcons.fa_info_circle)
                        .colorRes(R.color.white)
                        .alpha(204)
                        .actionBarSize());

        builder.setTitle(String.format(context.getString(R.string.cue), Double.toString(alCues.get(currentCue).getCueNum())));

        // Create the Save button for the Edit Cue menu
        builder.setPositiveButton(R.string.Save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                EditText editCueName = (EditText) ((AlertDialog) dialog).findViewById(R.id.editCueName);
                EditText editTextFadeUp = (EditText) ((AlertDialog) dialog).findViewById(R.id.editTextFadeUp);
                EditText editTextFadeDown = (EditText) ((AlertDialog) dialog).findViewById(R.id.editTextFadeDown);

                try {
                    String cueName = editCueName.getText().toString();
                    int FadeUp = Integer.parseInt(editTextFadeUp.getText().toString());
                    int FadeDown = Integer.parseInt(editTextFadeDown.getText().toString());
                    alCues.get(currentCue).setCueName(cueName);
                    alCues.get(currentCue).setFadeUpTime(FadeUp);
                    alCues.get(currentCue).setFadeDownTime(FadeDown);

                    // Set new button name
                    alCues.get(currentCue).getButton().setText(cueName);
                } catch (NumberFormatException n) {
                    Toast.makeText(context, R.string.errNumConv, Toast.LENGTH_SHORT).show();
                } catch (Throwable t) {
                    Toast.makeText(context, R.string.Error, Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
            }
        });
        // Create the Insert button for the Edit Cue menu
        builder.setNegativeButton(R.string.insert, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ViewGroup layout = (ViewGroup) alCues.get(currentCue).getButton().getParent();

                // Read global settings
                int fadeUpTime = 5;
                int fadeDownTime = 5;
                try {
                    fadeUpTime = Integer.parseInt(getSharedPref().getString("fade_up_time", "5"));
                    fadeDownTime = Integer.parseInt(getSharedPref().getString("fade_down_time", "5"));
                } catch (Throwable t) {
                    Toast.makeText(context, R.string.errNumConv, Toast.LENGTH_SHORT).show();
                }
                // create a new "Add Cue" button
                Button button = new Button(context);
                button.setOnClickListener(new CueClickListener());

                // Create a sub cue number
                double thisCueNum;
                if (currentCue == 0)
                    thisCueNum = alCues.get(currentCue).getCueNum() - 0.1;
                else {
                    thisCueNum = alCues.get(currentCue - 1).getCueNum();
                    // mods check what level should be incremented
                    double nextCueNum = alCues.get(currentCue).getCueNum();
                    int diff = (int) ((nextCueNum * 100) - (thisCueNum * 100));
                    if (diff <= 1) {
                        Toast.makeText(context, R.string.onlyTwoDec, Toast.LENGTH_SHORT).show();
                        thisCueNum = -1;
                    } else if (diff <= 10)
                        thisCueNum += 0.01;
                    else if (diff <= 100) {
                        thisCueNum += 0.1;
                    } else {
                        Toast.makeText(context, R.string.onlyTwoDec, Toast.LENGTH_SHORT).show();
                        thisCueNum = -1;
                    }
                    Log.v(TAG, "this: " + thisCueNum + " diff " + diff);
                }
                thisCueNum = Math.round(thisCueNum * 100.0) / 100.0;

                if (thisCueNum > 0) {

                    // setup button
                    button.setText(String.format(context.getString(R.string.cue), Double.toString(thisCueNum)));
                    button.setLongClickable(true);
                    button.setOnLongClickListener(new CueClickListener());
                    layout.addView(button, currentCue);// add new button after
                    // currentCue
                    String cueName = String.format(context.getString(R.string.cue), Double.toString(thisCueNum));
                    alCues.add(new CueObj(thisCueNum, cueName, fadeUpTime, fadeDownTime, getCurrentChannelArray(), button));
                    Collections.sort(alCues, new CueSorter());

                    // Toast.makeText(context, "Inserted " + thisCueNum,
                    // Toast.LENGTH_SHORT).show();
                    // dialog.dismiss();

                } else {// Cue can not be below 0
                    Toast.makeText(context, R.string.cueMustBePos, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
        // Create the Delete button for the Edit Cue menu
        builder.setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Check if any cues are fading
                boolean fadeInProgress = false;
                for (CueObj cue : alCues) {
                    if (cue.isFadeInProgress())
                        fadeInProgress = true;
                }
                // Don't delete if fading
                if (!fadeInProgress) {
                    Toast.makeText(
                            context,
                            context.getString(R.string.deletedCue)
                                    + alCues.get(currentCue).getCueNum(), Toast.LENGTH_SHORT)
                            .show();
                    ViewGroup layout = (ViewGroup) alCues.get(currentCue).getButton().getParent();
                    if (null != layout) // for safety only as you are doing
                        // onClick
                        layout.removeView(alCues.get(currentCue).getButton());
                    alCues.remove(currentCue);
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, R.string.canNotDeleteWhileFading, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // set prompts.xml to alert dialog builder
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_cue, (ViewGroup) button.getParent(), false);

        EditText editCueName = (EditText) promptsView.findViewById(R.id.editCueName);
        editCueName.setText(alCues.get(currentCue).getCueName());
        editCueName.selectAll();

        EditText editTextFadeUp = (EditText) promptsView.findViewById(R.id.editTextFadeUp);
        editTextFadeUp.setText(String.format("%1$s", alCues.get(currentCue).getFadeUpTime()));

        EditText editTextFadeDown = (EditText) promptsView.findViewById(R.id.editTextFadeDown);
        editTextFadeDown.setText(String.format("%1$s", alCues.get(currentCue).getFadeDownTime()));

        builder.setView(promptsView);

        AlertDialog alert = builder.create();

        alert.show();
    }
}
