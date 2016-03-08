package com.AuroraByteSoftware.AuroraDMX;

import android.util.Log;

import com.AuroraByteSoftware.AuroraDMX.fixture.Fixture;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

public class CueFade extends MainActivity implements Serializable {

    private static final String TAG = "AuroraDMX";

    /**
     * Fades the cue light from the previous cue to the next cue
     *
     * @param nextCue
     * @param prevCue
     */
    public void startCueFade(final CueObj nextCue, final CueObj prevCue) {

        Log.d(TAG, "startCueFade next " + nextCue + "\tprev " + prevCue);

        // Check if any cues are currently fading
        boolean prevCueReady = true;
        if (prevCue != null)
            prevCueReady = !prevCue.isFadeInProgress();

        if (!nextCue.isFadeInProgress() && prevCueReady) {
            // Set fades inProgress
            nextCue.setFadeInProgress(true);
            // Fade up timer
            final Timer T = new Timer();
            int temp = (int) Math.ceil((nextCue.getFadeUpTime() * 1000.0) / 255);
            if (temp < 1)
                temp = 1;
            final int stepsToEndVal = temp;

            // if the fade time is none, just pop to final
            if (stepsToEndVal == 1) {
                nextCue.setHighlight(0, 256, 0);
                for (Fixture col : alColumns) {
                    for (int i = 0; i < 256; i++) {
                        col.incrementLevelUp();
                        if (prevCue == null)
                            col.incrementLevelDown();
                    }
                }
                nextCue.setFadeInProgress(false);
            } else {
                T.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (nextCue.getHighlight() < 256) {
                                    nextCue.setHighlight(0,
                                            nextCue.getHighlight() + 1, 0);
                                    for (Fixture col : alColumns) {
                                        col.incrementLevelUp();
                                        if (prevCue == null)
                                            col.incrementLevelDown();
                                    }
                                } else {
                                    nextCue.setFadeInProgress(false);
                                    T.cancel();
                                    T.purge();
                                }
                            }
                        });
                    }
                }, 0, stepsToEndVal);
            }

            // Fade down timer
            if (prevCue != null) {
                final Timer T1 = new Timer();
                prevCue.setFadeInProgress(true);
                final long stepsToEndValDown = (long) ((nextCue.getFadeDownTime() + 0.0) / 256 * 1000);
                // If there is an instant fade
                if (stepsToEndValDown == 0) {
                    prevCue.setHighlight(0, 0, 0);
                    prevCue.setFadeInProgress(false);
                    prevCue.setHighlight(0, 0, 0);
                    for (int i = 0; i < 256; i++) {
                        for (Fixture col : alColumns) {
                            col.incrementLevelDown();
                        }
                    }
                } else {
                    T1.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (prevCue.getHighlight() != 0) {
                                        prevCue.setHighlight(
                                                prevCue.getHighlight() - 1, 0, 0);
                                        for (Fixture col : alColumns) {
                                            col.incrementLevelDown();
                                        }
                                    } else {
                                        prevCue.setHighlight(0, 0, 0);
                                        prevCue.setFadeInProgress(false);
                                        T1.cancel();
                                        T1.purge();
                                    }
                                }// end run()
                            });
                        }// end run()
                    }, 0, stepsToEndValDown);// end scheduleAtFixedRate
                }// end if 0 fade
            }// end if prevCueNum
        }// end if fade in progress
    }

}
