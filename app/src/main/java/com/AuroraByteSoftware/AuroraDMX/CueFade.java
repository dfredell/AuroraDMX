package com.AuroraByteSoftware.AuroraDMX;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.AuroraByteSoftware.AuroraDMX.fixture.Fixture;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CueFade extends MainActivity implements Serializable {

    public static int upwardCue = -1;
    public static int downwardCue = -1;
    private final static FadeObj fadeObj = new FadeObj();
    private final static int MS_BETWEEN_UPDATES = 100; //MILLISECONDS
    private final static Handler fadeHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            for (Fixture col : alColumns) {
                col.updateUi();
            }
            super.handleMessage(msg);
        }
    };

    public interface FadeProgress {
        void update(int percent);
    }
    public interface FadeFinish {
        void finished();
    }


    /**
     * Fades the cue light from the previous cue to the next cue
     *
     * @param nextCue the destination cue to set fixture levels
     */
    public void startCueFade(final CueObj nextCue) {
        startCueFade(nextCue, nextCue.getFadeTime(), null, null);
    }

    /**
     * Fades the cue light from the previous cue to the next cue
     *
     * @param nextCue  the destination cue to set fixture levels
     * @param fadeTime Override the fade time
     */
    public FadeObj startCueFade(final CueObj nextCue, double fadeTime, final FadeProgress fadeProgress, final FadeFinish finish) {

        int prevCueNum = downwardCue;
        final CueObj prevCue;
        if (getAlCues().size() <= prevCueNum || prevCueNum < 0) {
            prevCue = null;
        } else {
            prevCue = getAlCues().get(prevCueNum);
        }

        Log.d(getClass().getSimpleName(), "startCueFade next " + nextCue + "\tprev " + prevCue);
        List<Integer> newChLevels = nextCue.getLevels();

        Log.d(getClass().getSimpleName(), String.format("newChLevels %1$s", newChLevels));
        upwardCue = getAlCues().indexOf(nextCue);
        downwardCue = upwardCue;

        //Cancel any previous fades
        fadeObj.stop();
        for (CueObj cue : MainActivity.getAlCues()) {
            cue.setFadeInProgress(false);
            cue.setHighlight(0, 0, 0);
        }

        // Tapped the current fade so cancel the fade
        if (nextCue.equals(prevCue)) {
            Log.d(getClass().getSimpleName(), "Double tapped cue, canceling fades");
            downwardCue = -1;
            return null;
        }

        //Set up steps
        // Number of runs it takes to get to destination level with MS_BETWEEN_UPDATES
        final double steps = Math.max(0, fadeTime * (1000.0 / MS_BETWEEN_UPDATES));
        Log.d(getClass().getSimpleName(), String.format("Fading with %1$s steps", steps));

        // Set the channels to the cue
        int chIndex = 0;
        for (int x = 0; x < alColumns.size() && x < newChLevels.size(); x++) {
            // If a channel changed value
            int fixtureUses = alColumns.get(x).getChLevels().size();
            if (newChLevels.size() >= chIndex + fixtureUses){
                ArrayList<Integer> stepValues = new ArrayList<>(newChLevels.subList(chIndex, chIndex + fixtureUses));
                alColumns.get(x).setupIncrementLevelFade(stepValues, steps == 0 ? 1 : steps);
                chIndex += fixtureUses;
            }
        }

        // Set fades inProgress
        nextCue.setFadeInProgress(true);


        ////// Fade timer /////
        // if the fade time is none, just pop to final
        if (steps == 0) {
            nextCue.setHighlight(0, 171, 102);
            for (Fixture col : alColumns) {
                col.incrementLevel();
            }
            nextCue.setFadeInProgress(false);
        } else {
            Runnable fadeRunnable = new Runnable() {
                private int progress = 0;

                @Override
                public void run() {
                    if (progress < steps) {
                        nextCue.setHighlight(0, (int) (nextCue.getHighlight() + (256.0 / steps)), 0);
                        if (fadeProgress != null) {
                            fadeProgress.update((int) (progress * 100.0 / steps));
                        }
                        for (Fixture col : alColumns) {
                            col.incrementLevel();
                        }
                        progress++;
                        fadeHandler.postDelayed(this, MS_BETWEEN_UPDATES);
                        fadeHandler.sendMessage(new Message());
                    } else {
                        nextCue.setHighlight(0, 171, 102);
                        nextCue.setFadeInProgress(false);
                        if(finish!=null)
                            finish.finished();
                    }
                }
            };
            fadeObj.setRunnable(fadeRunnable);
            fadeHandler.postDelayed(fadeRunnable, 0);
        }

        return fadeObj;
    }

    public static class FadeObj {

        private Runnable upRunnable;

        void setRunnable(Runnable upRunnable) {
            this.upRunnable = upRunnable;
        }

        public void stop() {
            if (upRunnable != null) {
                fadeHandler.removeCallbacks(upRunnable);
                upRunnable = null;
            }
            for (CueObj cue : MainActivity.getAlCues()) {
                cue.setFadeInProgress(false);
            }
        }
    }


    public int getUpwardCue() {
        return upwardCue;
    }
}
