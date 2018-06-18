package com.AuroraByteSoftware.AuroraDMX.ui.chase;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

/**
 * Show a popup of cues to add to the current chase.
 * <p>
 * Created by furtchet on 3/17/18.
 */

public class AddCueToChaseDialog extends DialogFragment {

    AddCueDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Cue")
                .setItems(mListener.getAddCueItems(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        Log.v(getClass().getSimpleName(), "Cue added to chase " + which);
                        mListener.onClickAddCue(which);
                    }
                });
        return builder.create();
    }

    // Override the Fragment.onAttach() method to instantiate the EditChaseDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the EditChaseDialogListener so we can send events to the host
            mListener = (AddCueDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement EditChaseDialogListener");
        }
    }

    public interface AddCueDialogListener {
        void onClickAddCue(int which);

        String[] getAddCueItems();
    }
}
