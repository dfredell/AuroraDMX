package com.AuroraByteSoftware.AuroraDMX.chase;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ProgressBar;

public class ProgressHandler extends Handler {
    private ProgressBar progressBar;

    ProgressHandler(Looper mainLooper) {
        super(mainLooper);
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    public void handleMessage(Message msg) {
        progressBar.setRotation(180);
        progressBar.getProgressDrawable().setColorFilter(Color.parseColor("#00CED1"), PorterDuff.Mode.LIGHTEN);
        progressBar.setProgress(msg.getData().getInt(ChaseRunner.BUNDLE_FADE_PROGRESS));
//        super.handleMessage(msg);
    }
}
