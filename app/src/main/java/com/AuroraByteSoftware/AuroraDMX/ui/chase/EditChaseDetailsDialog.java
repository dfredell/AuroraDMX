package com.AuroraByteSoftware.AuroraDMX.ui.chase;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.AuroraByteSoftware.AuroraDMX.R;
import com.AuroraByteSoftware.AuroraDMX.ui.UiUtil;
import com.joanzapata.iconify.widget.IconButton;

import yuku.ambilwarna.AmbilWarnaDialog;

/**
 * Popup for changing chase attributes, ex name and timings
 * Created by furtchet on 3/17/18.
 */

public class EditChaseDetailsDialog extends DialogFragment implements View.OnClickListener {


    EditChaseDialogListener mListener;
    private int buttonColor=0;

    // create a blank on touch when the user is picking a color since we don't update the ui
    final AmbilWarnaDialog.AmbilWarnaDialogTouch onTouch = new AmbilWarnaDialog.AmbilWarnaDialogTouch() {
        @Override
        public void onTouch(int color) {
        }
    };

    public interface EditChaseDialogListener {
        void onEditChaseDetails(String name, int fadeTime, int waitTime, int buttonColor);

        EditChaseDetailsPOJO loadEditChaseDetails();
    }

    static class EditChaseDetailsPOJO {
        String name;
        int fade;
        int wait;
        int buttonColor;
    }

    interface DialogDismissed {
        void onDismiss();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Edit Chase Details");
        return dialog;
    }

    /**
     * Create a popup with Chase settings
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        View inflate = inflater.inflate(R.layout.dialog_edit_chase_details, container, false);
        EditChaseDetailsPOJO detailsPojo = mListener.loadEditChaseDetails();
        ((EditText) inflate.findViewById(R.id.edit_chase_name)).setText(detailsPojo.name);
        ((EditText) inflate.findViewById(R.id.edit_chase_fade_time)).setText(Integer.toString(detailsPojo.fade));
        ((EditText) inflate.findViewById(R.id.edit_chase_wait_time)).setText(Integer.toString(detailsPojo.wait));
        inflate.findViewById(R.id.edit_chase_save).setOnClickListener(this);
        final View chaseButtonColor = inflate.findViewById(R.id.edit_chase_button_color);
        chaseButtonColor.setOnClickListener(this);
        if (detailsPojo.buttonColor != 0)
            chaseButtonColor.getBackground().setColorFilter(detailsPojo.buttonColor, PorterDuff.Mode.MULTIPLY);
        buttonColor = detailsPojo.buttonColor;
        return inflate;
    }

    /**
     * Clicking save or color button on the Chase Edit on the popup
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (R.id.edit_chase_save == v.getId())
            save(v);
        else if (R.id.edit_chase_button_color == v.getId())
            selectButtonColor(v);
    }

    /**
     * The user hit the Edit Chase Button Color button on the edit chase popup
     * @param v
     */
    private void selectButtonColor(final View v) {
        EditChaseDetailsPOJO detailsPojo = mListener.loadEditChaseDetails();

        final Context context = v.getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater li = LayoutInflater.from(context);
        final View reorderView = li.inflate(R.layout.dialog_edit_chase_color, null, false);
        builder.setView(reorderView);
        final AlertDialog reorderAlert = builder.create();

        View viewById = reorderView.findViewById(R.id.edit_chase_ambilwarna_viewContainer);
        TextView title = reorderView.findViewById(R.id.chase_edit_color_title);
        title.setText(detailsPojo.name);

        final AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(detailsPojo.buttonColor, viewById, onTouch);

        // on save color
        IconButton saveButton = reorderView.findViewById(R.id.edit_chase_color_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View colorV) {
                final int color = Color.HSVToColor(255, ambilWarnaDialog.getCurrentColorHsv());
                v.findViewById(R.id.edit_chase_button_color).getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                buttonColor = color;
                reorderAlert.dismiss();
            }
        });

        // on reset color
        IconButton resetButton = reorderView.findViewById(R.id.edit_chase_color_reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View colorV) {
                v.findViewById(R.id.edit_chase_button_color).getBackground().clearColorFilter();
                buttonColor = 0;
                reorderAlert.dismiss();
            }
        });

        reorderAlert.show();
    }

    /**
     * Save the chase properties
     *
     * @param v
     */
    public void save(View v) {
        String name = UiUtil.getTextFromDialog(getDialog(), R.id.edit_chase_name);
        int fade = UiUtil.getIntFromDialog(getDialog(), R.id.edit_chase_fade_time);
        int wait = UiUtil.getIntFromDialog(getDialog(), R.id.edit_chase_wait_time);

        if (wait + fade <= 0) {
            Toast.makeText(v.getContext(), R.string.errorZeroFade, Toast.LENGTH_SHORT).show();
        } else {
            mListener.onEditChaseDetails(name, fade, wait, buttonColor);
        }
        getDialog().dismiss();
    }

    // Override the Fragment.onAttach() method to instantiate the EditChaseDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the EditChaseDialogListener so we can send events to the host
            mListener = (EditChaseDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement EditChaseDialogListener");
        }
    }

    @Override
    public void onDetach() {
        ((EditChaseActivity) getActivity()).onDismiss();
        super.onDetach();
    }
}
