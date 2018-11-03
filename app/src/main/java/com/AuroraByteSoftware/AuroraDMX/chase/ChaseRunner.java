package com.AuroraByteSoftware.AuroraDMX.chase;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;

import com.AuroraByteSoftware.AuroraDMX.CueFade;
import com.AuroraByteSoftware.AuroraDMX.MainActivity;

import java.util.TimerTask;

public class ChaseRunner extends TimerTask {

    private ChaseObj chase;
    private int currentCue = 0;
    //    private static Timer chaseRunnerTimer;
    private boolean isRunning = false;
    //    private static Timer progressWaitTimer;
    private final static int PROGRESS_FRAMES_PER_SEC = 50;
    public final static String BUNDLE_FADE_PROGRESS = "BUNDLE_FADE_PROGRESS";
    private CueFade.FadeObj fadeObj;
    private ProgressWait progressWait;

    private final Handler chaseToChaseHandler = new Handler(Looper.getMainLooper());
    private final ProgressHandler waitFadeHandler = new ProgressHandler(Looper.getMainLooper());
    private long delay = 1000;

    /**
     * Hit once at the start of a new cue fade.
     */
    @Override
    public void run() {
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
        Log.d(getClass().getSimpleName(), "Chase Runner '" + chase.getName() + "'");

        int nextCue = currentCue;
        if (chase.getCues().size() <= currentCue + 1) {
            nextCue = 0;
        } else {
            nextCue++;
        }

        waitFadeHandler.removeCallbacksAndMessages(null);
        waitFadeHandler.removeCallbacks(progressWait);

        final ProgressBar progressBar = chase.getButton().getProgressBar();
        progressBar.setProgress(0);
        progressBar.getProgressDrawable().setColorFilter(Color.parseColor("#ffd700"), PorterDuff.Mode.MULTIPLY);
        if (fadeObj != null) {
            fadeObj.stop();
        }

        // Move the chase progress bar
        CueFade.FadeFinish fadeFinish = new CueFade.FadeFinish() {
            @Override
            public void finished() {
                //Skip wait?
                if (chase.getWaitTime() > 0) {
                    //Display the wait time
                    progressWait = new ProgressWait(chase.getWaitTime());
                    waitFadeHandler.setProgressBar(progressBar);
                    waitFadeHandler.postDelayed(progressWait, 0);
                }
            }
        };
        CueFade.FadeProgress fadeUpProgress = new CueFade.FadeProgress() {
            @Override
            public void update(int percent) {
                progressBar.setRotation(0);
                progressBar.setProgress(percent);
            }
        };
        fadeObj = MainActivity.getCueFade().startCueFade(chase.getCues().get(nextCue), chase.getFadeTime(), fadeUpProgress, fadeFinish);
        currentCue = nextCue;



        if (isRunning) {
            // Add the next cue fade to the stack. This causes the next cue fade to start after this one finishes.
            Log.d(getClass().getSimpleName(), "chaseToChaseHandler postDelayed '" + delay + "'");
            chaseToChaseHandler.postDelayed(this, delay);
        }
    }

    public void start(ChaseObj chase, Activity activity) {
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }

        int fadeTime = chase.getFadeTime();
        int waitTime = chase.getWaitTime();
        Log.d("ChaseRunner", "Starting Chase '" + chase.getName() + "' with times " + fadeTime + ":" + waitTime);

        //Set progress bar
        ProgressBar progressBar = chase.getButton().getProgressBar();
        progressBar.setAlpha(1);
        progressBar.setProgress(0);
        progressBar.setRotation(0);

        //stop the previous
//        chaseToChaseHandler.removeCallbacks(chaseRunner);
        waitFadeHandler.removeCallbacks(progressWait);
        this.chase = chase;
        this.delay = (fadeTime + waitTime) * 1000L;
        this.isRunning = true;
        chaseToChaseHandler.postDelayed(this, 0);
    }


    /**
     * Move the progress bar across the chase button while waiting
     */
    private class ProgressWait extends TimerTask {

        private int waitTime;
        private int count = 0;

        ProgressWait(int waitTime) {
            this.waitTime = waitTime;
        }

        @Override
        public void run() {
            int progress = count++ * 100 / (waitTime * PROGRESS_FRAMES_PER_SEC);
            if (progress < 100) {
                waitFadeHandler.postDelayed(this, 1000 / PROGRESS_FRAMES_PER_SEC);
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putInt(BUNDLE_FADE_PROGRESS, progress);
                msg.setData(data);
                waitFadeHandler.sendMessage(msg);
            }
        }
    }

    /**
     * When stopping the Chase, cleanup the timers and clear UI
     *
     * @param chase
     */
    public void stop(ChaseObj chase) {
        isRunning = false;
        chase.getButton().getProgressBar().setAlpha(0);
        waitFadeHandler.removeCallbacks(progressWait);
        waitFadeHandler.removeCallbacksAndMessages(null);
        chaseToChaseHandler.removeCallbacksAndMessages(null);
        if (fadeObj != null) {
            fadeObj.stop();
        }
        this.cancel();
        Log.d(getClass().getSimpleName(), "Chase Runner '" + chase.getName() + "' Stopped");
    }

    public boolean isRunning() {
        return isRunning;
    }

    /**
     * When stopping the Chases, cleanup the timers and clear UI
     */
    public void stopAll() {
        isRunning = false;
        for (ChaseObj alChase : MainActivity.getAlChases()) {
            alChase.getButton().getProgressBar().setAlpha(0);
        }
        waitFadeHandler.removeCallbacksAndMessages(null);
        chaseToChaseHandler.removeCallbacksAndMessages(null);
        if (fadeObj != null) {
            fadeObj.stop();
        }
        Log.d(getClass().getSimpleName(), "Chase Runner '" + chase + "' Stop All");
    }

    public boolean isActive(ChaseObj chase) {
        return chase != null && chase.equals(this.chase) && isRunning();
    }
}
