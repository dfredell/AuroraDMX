package com.AuroraByteSoftware.AuroraDMX.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.AuroraByteSoftware.AuroraDMX.CueObj;
import com.AuroraByteSoftware.AuroraDMX.CueSorter;
import com.AuroraByteSoftware.AuroraDMX.MainActivity;
import com.AuroraByteSoftware.AuroraDMX.R;

import java.util.ArrayList;
import java.util.Collections;

public class EditCueMenu extends MainActivity {
	private static int currentCue = -1;// used to and from cue edit

	public static void createEditCueMenu(final ArrayList<CueObj> alCues, View v,
			final MainActivity mainActivity) {
		// Find what Cue we are in
		for (int x = 0; x < alCues.size(); x++) {
			if (alCues.get(x).getButton() == v) {
				currentCue = x;
			}
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
		builder.setCancelable(true);
		builder.setIcon(R.drawable.action_about);
		builder.setTitle(mainActivity.getString(R.string.cue) + " " + alCues.get(currentCue).getCueNum());

		builder.setInverseBackgroundForced(true);
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
					Toast.makeText(mainActivity, R.string.errNumConv, Toast.LENGTH_SHORT).show();
				} catch (Throwable t) {
					Toast.makeText(mainActivity, R.string.Error, Toast.LENGTH_SHORT).show();
				}

				dialog.dismiss();
			}
		});
		// Create the Insert button for the Edit Cue menu
		builder.setNegativeButton("Insert", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ViewGroup layout = (ViewGroup) alCues.get(currentCue).getButton().getParent();

				// Read global settings
				int fadeUpTime = 5;
				int fadeDownTime = 5;
				try {
					fadeUpTime = Integer.parseInt(sharedPref.getString("fade_up_time", "5"));
					fadeDownTime = Integer.parseInt(sharedPref.getString("fade_down_time", "5"));
				} catch (Throwable t) {
					Toast.makeText(mainActivity, R.string.errNumConv, Toast.LENGTH_SHORT).show();
				}
				// create a new "Add Cue" button
				Button button = new Button(mainActivity);
				button.setOnClickListener(mainActivity);

				// Create a sub cue number
				double thisCueNum;
				if (currentCue == 0)
					thisCueNum = alCues.get(currentCue).getCueNum() - 0.1;
				else {
					thisCueNum = alCues.get(currentCue - 1).getCueNum();
					// mods check what level should be incremented
					double nextCueNum = alCues.get(currentCue).getCueNum();
					System.out.print("a "+thisCueNum);
					int diff = (int) ((nextCueNum * 100) - (thisCueNum * 100));
					System.out.print("b "+thisCueNum);
					if(diff <=1){
						Toast.makeText(mainActivity, R.string.onlyTwoDec, Toast.LENGTH_SHORT).show();
						thisCueNum = -1;	
					}else if (diff <= 10)
						thisCueNum += 0.01;
					else if (diff <= 100) {
						thisCueNum += 0.1;
					} else{
						Toast.makeText(mainActivity, R.string.onlyTwoDec, Toast.LENGTH_SHORT).show();
						thisCueNum = -1;
					}
					System.out.println("this: "+thisCueNum +" diff "+ diff);
				}
				thisCueNum = Math.round(thisCueNum * 100.0)/100.0;

				if (thisCueNum > 0) {

					// setup button
					button.setText(mainActivity.getString(R.string.cue) + " " + thisCueNum);
					button.setLongClickable(true);
					button.setOnLongClickListener(mainActivity);
					layout.addView(button, currentCue);// add new button after
														// currentCue
					String cueName=mainActivity.getString(R.string.cue)+" "+thisCueNum;
					alCues.add(new CueObj(thisCueNum, cueName, fadeUpTime, fadeDownTime, getCurrentChannelArray(), button));
					Collections.sort(alCues, new CueSorter());

					// Toast.makeText(mainActivity, "Inserted " + thisCueNum,
					// Toast.LENGTH_SHORT).show();
					// dialog.dismiss();

				} else {// Cue can not be below 0
					Toast.makeText(mainActivity, R.string.cueMustBePos, Toast.LENGTH_SHORT).show();
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
							mainActivity,
							mainActivity.getString(R.string.deletedCue)
									+ alCues.get(currentCue).getCueNum(), Toast.LENGTH_SHORT)
							.show();
					ViewGroup layout = (ViewGroup) alCues.get(currentCue).getButton().getParent();
					if (null != layout) // for safety only as you are doing
										// onClick
						layout.removeView(alCues.get(currentCue).getButton());
					alCues.remove(currentCue);
					dialog.dismiss();
				} else {
					Toast.makeText(mainActivity, R.string.canNotDeleteWhileFading, Toast.LENGTH_SHORT).show();
				}
			}
		});

		// set prompts.xml to alertdialog builder
		LayoutInflater li = LayoutInflater.from(mainActivity);
		View promptsView = li.inflate(R.layout.dialog_cue, (ViewGroup) v.getParent(), false);

		EditText editCueName = (EditText) promptsView.findViewById(R.id.editCueName);
		editCueName.setText("" + alCues.get(currentCue).getCueName());
		editCueName.selectAll();

		EditText editTextFadeUp = (EditText) promptsView.findViewById(R.id.editTextFadeUp);
		editTextFadeUp.setText("" + alCues.get(currentCue).getFadeUpTime());

		EditText editTextFadeDown = (EditText) promptsView.findViewById(R.id.editTextFadeDown);
		editTextFadeDown.setText("" + alCues.get(currentCue).getFadeDownTime());

		builder.setView(promptsView);

		AlertDialog alert = builder.create();
		alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

		alert.show();
	}
}
