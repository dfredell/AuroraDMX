package com.AuroraByteSoftware.AuroraDMX.ui.chase;

import android.app.Activity;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.AuroraByteSoftware.AuroraDMX.R;
import com.AuroraByteSoftware.AuroraDMX.chase.ChaseObj;

public class ChaseButton extends RelativeLayout {
    private ChaseObj chase;
    private Activity activity;
    private Button button;
    private ProgressBar progressBar;

    private void init() {
        inflate(getContext(), R.layout.chase_button, this);
        button = this.findViewById(R.id.chase_button);
        progressBar = this.findViewById(R.id.chase_button_progress);
        progressBar.setAlpha(0);
    }

    public ChaseButton(Activity activity, ChaseObj chase) {
        super(activity);
        this.activity = activity;
        this.chase = chase;
        init();
    }

    public ChaseObj getChase() {
        return chase;
    }

    public Activity getActivity() {
        return activity;
    }

    public Button getButton() {
        return button;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}
