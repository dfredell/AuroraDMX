package com.AuroraByteSoftware.AuroraDMX;

import android.util.Log;

import com.AuroraByteSoftware.AuroraDMX.fixture.Fixture;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CueFade extends MainActivity implements Serializable {

    private static final String TAG = "AuroraDMX";

    /**
     * Fades the cue light from the previous cue to the next cue
     *
     * @param nextCue the destination cue to set fixture levels
     */
    public void startCueFade(final CueObj nextCue) {

        int prevCueNum = MainActivity.downwardCue;
        final CueObj prevCue = prevCueNum < 0 ? null : MainActivity.alCues.get(prevCueNum);

        Log.d(TAG, "startCueFade next " + nextCue + "\tprev " + prevCue);
        List<Integer> newChLevels = nextCue.getLevels();

        Log.d(TAG, String.format("newChLevels %1$s", newChLevels));
        MainActivity.upwardCue = alCues.indexOf(nextCue);
        MainActivity.downwardCue = alCues.indexOf(prevCue);
        MainActivity.downwardCue = MainActivity.upwardCue;

        //Cancel any previous fades
        for (CueObj cue : MainActivity.alCues) {
            cue.setFadeInProgress(false);
            if (cue.getFadeTimer() != null) {
                cue.getFadeTimer().cancel();
            }
            cue.setHighlight(0, 0, 0);
        }
        
        //Set up steps
        // Min of 1 max of 256, larger of fadeUp and fadeDown
        final int steps = Math.max(1, Math.min(256, Math.max(nextCue.getFadeUpTime(), nextCue.getFadeDownTime()) * 10));
        Log.d(TAG, String.format("Fading with %1$s steps", steps));

        // Set the channels to the cue
        int chIndex = 0;
        for (int x = 0; x < alColumns.size() && x < newChLevels.size(); x++) {
            // If a channel changed value
            int fixtureUses = alColumns.get(x).getChLevels().size();
            ArrayList<Integer> stepValues = new ArrayList<>(newChLevels.subList(chIndex, chIndex + fixtureUses));
            alColumns.get(x).setupIncrementLevelFade(stepValues, steps);
            chIndex += fixtureUses;
        }

        // Set fades inProgress
        nextCue.setFadeInProgress(true);
        // Fade up timer
        final Timer T = new Timer();
        int stepsToEndVal = (int) Math.ceil((nextCue.getFadeUpTime() * 1000.0) / steps);

        // if the fade time is none, just pop to final
        if (stepsToEndVal < 1) {
            nextCue.setHighlight(0, 256, 0);
            for (Fixture col : alColumns) {
                for (int i = 0; i < steps; i++) {
                    col.incrementLevelUp();
                    if (prevCue == null) {
                        col.incrementLevelDown();
                    }
                }
            }
            nextCue.setFadeInProgress(false);
        } else {
            nextCue.setFadeTimer(T);
            T.scheduleAtFixedRate(new TimerTask() {
                private int progress = 0;

                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (progress < steps) {
                                nextCue.setHighlight(0, nextCue.getHighlight() + (265 / steps), 0);
                                for (Fixture col : alColumns) {
                                    col.incrementLevelUp();
                                    if (prevCue == null) {
                                        col.incrementLevelDown();
                                    }
                                }
                                progress++;
                            } else {
                                nextCue.setHighlight(0, 265, 0);
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
            final long stepsToEndValDown = (long) ((nextCue.getFadeDownTime() + 0.0) / steps * 1000);
            // If there is an instant fade
            if (stepsToEndValDown < 1) {
                prevCue.setHighlight(0, 0, 0);
                prevCue.setFadeInProgress(false);
                for (int i = 0; i < steps; i++) {
                    for (Fixture col : alColumns) {
                        col.incrementLevelDown();
                    }
                }
            } else {
                prevCue.setFadeTimer(T1);
                T1.scheduleAtFixedRate(new TimerTask() {
                    private int progress = 0;

                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (progress < steps) {
                                    int nextBrightness = prevCue.getHighlight() - (256 / steps);
                                    prevCue.setHighlight(nextBrightness, 0, 0);
                                    for (Fixture col : alColumns) {
                                        col.incrementLevelDown();
                                    }
                                    progress++;
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
//        }// end if fade in progress
    }

}
