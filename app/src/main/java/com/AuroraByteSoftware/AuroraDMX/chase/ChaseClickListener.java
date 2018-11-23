package com.AuroraByteSoftware.AuroraDMX.chase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.AuroraByteSoftware.AuroraDMX.MainActivity;
import com.AuroraByteSoftware.AuroraDMX.R;
import com.AuroraByteSoftware.AuroraDMX.ui.chase.ChaseActivity;
import com.AuroraByteSoftware.AuroraDMX.ui.chase.ChaseButton;
import com.AuroraByteSoftware.AuroraDMX.ui.chase.EditChaseActivity;

/**
 * Listen to click events on the chase list
 * Created by furtchet on 12/6/15.
 */
public class ChaseClickListener implements View.OnClickListener, View.OnLongClickListener {
    public static String CHASE_EXTRA = "CHASE_EXTRA";
    private Button button;
    private ChaseObj chase;

    private ChaseClickListener(ChaseObj chase) {
        this.chase = chase;
    }

    /**
     * Cue Click handler
     */
    @Override
    public void onClick(View arg0) {
        if (arg0 instanceof Button) {
            button = (Button) arg0;
        }
        if (button == null || button.getContext() == null) {
            Log.e(getClass().getSimpleName(), "Chase button onclick had a null context");
            return;
        }
        Context context = button.getContext();
        if (!(button instanceof Button)) {
            Log.e(getClass().getSimpleName(), "Chase button wasn't a ChaseButton");
            return;
        }

        // no cues added to chase yet
        if (chase.getCues().isEmpty()) {
            Toast.makeText(context, R.string.noCuesInChase, Toast.LENGTH_LONG).show();
            return;
        }

        ChaseRunner chaseRunner = ChaseActivity.getChaseRunner();
        if (chaseRunner.isActive(chase)) {
            // Stop the active one
            chaseRunner.stop(chase);
        } else if (chaseRunner.isRunning()) {
            // Stop other chases
            chaseRunner.stopAll();
            // Start this one
            chaseRunner.start(chase, chase.getButton().getActivity());
        } else {
            // Start chases
            chaseRunner.start(chase, chase.getButton().getActivity());
        }

    }

    /**
     * Chase Long Click handler
     */
    @Override
    public boolean onLongClick(View buttonView) {
        Intent intent = new Intent(buttonView.getContext(), EditChaseActivity.class);
        intent.putExtra(CHASE_EXTRA, MainActivity.getAlChases().indexOf(chase));
        buttonView.getContext().startActivity(intent);

        return true;
    }

    public static ChaseButton makeButton(ChaseObj chase, Activity activity) {
        ChaseButton button = new ChaseButton(activity, chase);
        button.getButton().setText(chase.getName());
        button.getButton().setOnClickListener(new ChaseClickListener(chase));
        button.getButton().setLongClickable(true);
        button.getButton().setOnLongClickListener(new ChaseClickListener(chase));
        if (chase.getButtonColor() != 0)
            button.getButton().getBackground().setColorFilter(chase.getButtonColor(), PorterDuff.Mode.MULTIPLY);
        return button;
    }
}
