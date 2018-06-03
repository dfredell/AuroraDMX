package com.AuroraByteSoftware.AuroraDMX.ui.chase;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.AuroraByteSoftware.AuroraDMX.R;
import com.AuroraByteSoftware.AuroraDMX.ui.UiUtil;

/**
 * Popup for changing chase attributes, ex name and timings
 * Created by furtchet on 3/17/18.
 */

public class EditChaseDetailsDialog extends DialogFragment implements View.OnClickListener {



    EditChaseDialogListener mListener;

    public interface EditChaseDialogListener {
        void onEditChaseDetails(String name, int fadeTime, int waitTime);

        EditChaseDetailsPOJO loadEditChaseDetails();
    }

    static class EditChaseDetailsPOJO {
        String name;
        int fade;
        int wait;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        View inflate = inflater.inflate(R.layout.dialog_edit_chase_details, container, false);
        EditChaseDetailsPOJO detailsPojo = mListener.loadEditChaseDetails();
        ((EditText) inflate.findViewById(R.id.edit_chase_name)).setText(detailsPojo.name);
        ((EditText) inflate.findViewById(R.id.edit_chase_fade_time)).setText(Integer.toString(detailsPojo.fade));
        ((EditText) inflate.findViewById(R.id.edit_chase_wait_time)).setText(Integer.toString(detailsPojo.wait));
        View saveButton = inflate.findViewById(R.id.edit_chase_save);
        saveButton.setOnClickListener(this);
        return inflate;
    }

    @Override
    public void onClick(View v) {
        String name = UiUtil.getTextFromDialog(getDialog(), R.id.edit_chase_name);
        int fade = UiUtil.getIntFromDialog(getDialog(), R.id.edit_chase_fade_time);
        int wait = UiUtil.getIntFromDialog(getDialog(), R.id.edit_chase_wait_time);
        if (wait + fade <= 0) {
            Toast.makeText(v.getContext(), R.string.errorZeroFade, Toast.LENGTH_SHORT).show();
        } else {
            mListener.onEditChaseDetails(name, fade, wait);
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
