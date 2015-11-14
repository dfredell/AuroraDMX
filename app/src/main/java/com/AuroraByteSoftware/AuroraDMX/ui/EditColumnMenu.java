package com.AuroraByteSoftware.AuroraDMX.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.AuroraByteSoftware.AuroraDMX.ColumnObj;
import com.AuroraByteSoftware.AuroraDMX.MainActivity;
import com.AuroraByteSoftware.AuroraDMX.R;

public class EditColumnMenu extends MainActivity {

	public static void createEditColumnMenu(View v, final Context context, final ColumnObj columnObj, String chText, int chLevel) {


		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(true);
		builder.setIcon(R.drawable.action_about);

		builder.setInverseBackgroundForced(true);
		// Create the Save button
		builder.setPositiveButton(R.string.Save, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				EditText editColumnName = (EditText) ((AlertDialog) dialog).findViewById(R.id.editColumnName);
				EditText editColumnLevel = (EditText) ((AlertDialog) dialog).findViewById(R.id.editColumnLevel);
				Switch rgbSwitch = (Switch) ((AlertDialog) dialog).findViewById(R.id.chanel_rgb);

				columnObj.setIsRGB(rgbSwitch.isChecked());

				try {
					String columnName = editColumnName.getText().toString();
                    System.out.println("Col name: "+columnName);
                    columnObj.setColumnText(columnName,context);

                    int columnLevel = Integer.parseInt(editColumnLevel.getText().toString());
                    columnObj.setChLevel(columnLevel);

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
		View promptsView = li.inflate(R.layout.dialog_column, (ViewGroup) v.getParent(), false);

		builder.setView(promptsView);

        //Set previous text
        ((EditText) promptsView.findViewById(R.id.editColumnName)).setText(chText);
        ((EditText) promptsView.findViewById(R.id.editColumnLevel)).setText(Integer.toString(chLevel));
		((Switch) promptsView.findViewById(R.id.chanel_rgb)).setChecked(columnObj.isRGB());

		AlertDialog alert = builder.create();
		alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

		alert.show();
	}
}
