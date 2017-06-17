package com.AuroraByteSoftware.AuroraDMX.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.AuroraByteSoftware.AuroraDMX.CueClickListener;
import com.AuroraByteSoftware.AuroraDMX.CueObj;
import com.AuroraByteSoftware.AuroraDMX.MainActivity;
import com.AuroraByteSoftware.AuroraDMX.R;
import com.jmedeisis.draglinearlayout.DragLinearLayout;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.Collections;

public class EditCueMenu extends MainActivity {
    private static int currentCue = -1;// used to and from cue edit

    @SuppressLint("SetTextI18n")
    public static void createEditCueMenu(final ArrayList<CueObj> alCues, final Button button, final boolean inCueSheet) {
        // Find what Cue we are in
        for (int x = 0; x < alCues.size(); x++) {
            if (alCues.get(x).getButton() == button) {
                currentCue = x;
                break;
            }
        }

        final Context context = button.getContext();

        // set prompts.xml to alert dialog builder
        LayoutInflater li = LayoutInflater.from(context);
        final View view = li.inflate(R.layout.dialog_cue, (ViewGroup) button.getParent(), false);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        final AlertDialog alert = builder.create();

        builder.setCancelable(true);
        builder.setIcon(
                new IconDrawable(context.getApplicationContext(), FontAwesomeIcons.fa_info_circle)
                        .colorRes(R.color.white)
                        .alpha(204)
                        .actionBarSize());

        builder.setTitle(String.format(context.getString(R.string.cue), alCues.get(currentCue).getCueName()));


        ///////////////// SAVE /////////////////
        view.findViewById(R.id.cue_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {


                EditText editCueName = (EditText) view.findViewById(R.id.editCueName);
                EditText editTextFadeUp = (EditText) view.findViewById(R.id.editTextFadeUp);
                EditText editTextFadeDown = (EditText) view.findViewById(R.id.editTextFadeDown);

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
                alert.dismiss();
            }
        });

        ///////////////// INSERT /////////////////
        view.findViewById(R.id.cue_insert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                String thisCueNum = alCues.get(currentCue).getCueName();

                // setup button
                button.setText(thisCueNum);
                button.setLongClickable(true);
                button.setOnLongClickListener(new CueClickListener());
                if (!inCueSheet) {
                    layout.addView(button, currentCue);// add new button after
                } else {
                    //Refresh the cue sheet screen
                    ((GridView) layout).invalidateViews();
                }
                // currentCue
                CueObj newCue = new CueObj(thisCueNum, fadeUpTime, fadeDownTime, getCurrentChannelArray(), button);
                alCues.add(currentCue, newCue);
                alert.dismiss();
            }
        });

        ///////////////// DELETE /////////////////
        view.findViewById(R.id.cue_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check if any cues are fading
                boolean fadeInProgress = false;
                for (CueObj cue : alCues) {
                    if (cue.isFadeInProgress()) {
                        fadeInProgress = true;
                    }
                }
                // Don't delete if fading
                if (!fadeInProgress) {
                    Toast.makeText(
                            context,
                            context.getString(R.string.deletedCue) + " "
                                    + alCues.get(currentCue).getCueName(), Toast.LENGTH_SHORT)
                            .show();
                    Button cueButton = alCues.get(currentCue).getButton();
                    ViewGroup layout = (ViewGroup) cueButton.getParent();
                    alCues.remove(currentCue);
                    if (null != layout && !inCueSheet) {
                        // LinearLayout can remove view, used on home screen, not cue grid screen
                        layout.removeView(cueButton);
                    } else if (null != layout && inCueSheet) {
                        //Refresh the cue sheet screen
                        ((GridView) layout).invalidateViews();
                    }
                } else {
                    Toast.makeText(context, R.string.canNotDeleteWhileFading, Toast.LENGTH_SHORT).show();
                }
                alert.dismiss();
            }
        });

        ///////////////// UPDATE /////////////////
        view.findViewById(R.id.cue_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CueObj cue = alCues.get(currentCue);
                cue.setLevelsList(MainActivity.getCurrentChannelArray());
                alert.dismiss();
                Toast.makeText(context, "Updated channel levels", Toast.LENGTH_SHORT).show();
            }
        });

        ///////////////// REORDER /////////////////
        view.findViewById(R.id.cue_reorder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View clickView) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater li = LayoutInflater.from(context);
                final View reorderView = li.inflate(R.layout.dialog_cue_reorder, null, false);
                builder.setView(reorderView);
                final AlertDialog reorderAlert = builder.create();

                DragLinearLayout dragLinearLayout = (DragLinearLayout) reorderView.findViewById(R.id.cue_reorder_view);
                for (CueObj alCue : alCues) {
                    TextView textView = new TextView(context);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    textView.setText(alCue.getCueName());
                    dragLinearLayout.addView(textView);
                }
                for (int i = 0; i < dragLinearLayout.getChildCount(); i++) {
                    View child = dragLinearLayout.getChildAt(i);
                    dragLinearLayout.setViewDraggable(child, child);
                }
                //When items drag reorder them in their array
                dragLinearLayout.setOnViewSwapListener(new DragLinearLayout.OnViewSwapListener() {
                    @Override
                    public void onSwap(View firstView, int firstPosition,
                                       View secondView, int secondPosition) {
                        Collections.swap(alCues, firstPosition, secondPosition);
                        ViewGroup parent = (ViewGroup) button.getParent();
                        if (!inCueSheet) {
                            View childAt = parent.getChildAt(firstPosition);
                            parent.removeView(childAt);
                            parent.addView(childAt, secondPosition);
                        } else {
                            //Refresh the cue sheet screen
                            ((GridView) alCues.get(0).getButton().getParent()).invalidateViews();
                        }
                    }
                });
                alert.dismiss();
                reorderAlert.show();
            }
        });

        EditText editCueName = (EditText) view.findViewById(R.id.editCueName);
        editCueName.setText(alCues.get(currentCue).getCueName());

        EditText editTextFadeUp = (EditText) view.findViewById(R.id.editTextFadeUp);
        editTextFadeUp.setText(String.format("%1$s", alCues.get(currentCue).getFadeUpTime()));

        EditText editTextFadeDown = (EditText) view.findViewById(R.id.editTextFadeDown);
        editTextFadeDown.setText(String.format("%1$s", alCues.get(currentCue).getFadeDownTime()));

        alert.show();
    }
}
