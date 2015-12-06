package com.AuroraByteSoftware.AuroraDMX;

import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.AuroraByteSoftware.AuroraDMX.fixture.Fixture;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CueObj extends MainActivity implements Serializable {
    /**
     * s
     */
    private static final long serialVersionUID = -6835651116806619514L;
    private int fadeUpTime = 5;
    private int fadeDownTime = 5;
    private double cueNum = 0;
    private ArrayList<Integer> levelsList;
    private int[] levels = new int[0];
    private transient Button button;
    private int highlight = 0;
    private boolean fadeInProgress = false;
    private String name = "";
    private static final String TAG = "AuroraDMX";

    public CueObj() {
    }

    /**
     * @return the button
     */
    public Button getButton() {
        return button;
    }

    /**
     * @param button the button to set
     */
    public void setButton(Button button) {
        this.button = button;
    }

    public CueObj(double a_cueNum, String cueName, int a_fadeUpTime, int a_fadeDownTime,
                  List<Integer> a_levels, Button a_button) {
        if (a_cueNum > 0) {
            fadeUpTime = a_fadeUpTime;
            fadeDownTime = a_fadeDownTime;
            cueNum = a_cueNum;
            levelsList = new ArrayList<>(a_levels);
            button = a_button;
            name = cueName;
        } else {// Cue can not be below 0
            Toast.makeText(button.getContext(), getString(R.string.cueMustBePos), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * @return the fadeUpTime
     */
    public int getFadeUpTime() {
        return fadeUpTime;
    }

    /**
     * @param fadeUpTime2 the fadeUpTime to set
     */
    public void setFadeUpTime(int fadeUpTime2) {
        this.fadeUpTime = fadeUpTime2;
    }

    /**
     * @return the fadeDownTime
     */
    public int getFadeDownTime() {
        return fadeDownTime;
    }

    /**
     * @param fadeDownTime2 the fadeDownTime to set
     */
    public void setFadeDownTime(int fadeDownTime2) {
        this.fadeDownTime = fadeDownTime2;
    }

    /**
     * @return the cueNum
     */
    public double getCueNum() {
        return cueNum;
    }

    /**
     * @return the levels
     */
    public ArrayList<Integer> getLevels() {
        return levelsList;
    }

    /**
     * Sets the cue button to be highlighted with its progress
     */
    public void setHighlight(int r, int g, int b) {
        highlight = r + g + b;
        if (highlight != 0) {
            button.getBackground().setColorFilter(Color.argb(255, r, g, b), Mode.DARKEN);
            button.postInvalidate();
        } else if (highlight == 255) {
            button.forceLayout();
        } else {
            button.getBackground().setColorFilter(null);
        }
    }

    public int getHighlight() {
        return highlight;
    }

    /**
     * @return the fadeInProgress
     */
    public boolean isFadeInProgress() {
        return fadeInProgress;
    }

    /**
     * @param fadeInProgress the fadeInProgress to set
     */
    public void setFadeInProgress(boolean fadeInProgress) {
        this.fadeInProgress = fadeInProgress;
    }

    /**
     * Fades the cue light from the previous cue to the next cue
     *
     * @param nextCueNum not in array list form
     * @param prevCueNum not in array list form
     */
    void startCueFade(final int nextCueNum, final int prevCueNum) {

        Log.d(TAG, "startCueFade next " + nextCueNum + "\tprev " + prevCueNum);

        // Check if any cues are currently fading
        boolean prevCueReady = true;
        if (prevCueNum != -1)
            prevCueReady = !alCues.get(prevCueNum).isFadeInProgress();

        if (!alCues.get(nextCueNum).isFadeInProgress() && prevCueReady) {
            // Set fades inProgress
            alCues.get(nextCueNum).setFadeInProgress(true);
            // Fade up timer
            final Timer T = new Timer();
            int temp = (int) Math.ceil((fadeUpTime * 1000.0) / 255);
            if (temp < 1)
                temp = 1;
            final int stepsToEndVal = temp;
            T.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (alCues.get(nextCueNum).getHighlight() < 256) {
                                alCues.get(nextCueNum).setHighlight(0,
                                        alCues.get(nextCueNum).getHighlight() + 1, 0);
                                for (Fixture col : alColumns) {
                                    col.incrementLevelUp();
                                    if (prevCueNum == -1)
                                        col.incrementLevelDown();
                                }
                            } else {
                                alCues.get(nextCueNum).setFadeInProgress(false);
                                T.cancel();
                                T.purge();
                            }
                        }
                    });
                }
            }, 0, stepsToEndVal);
            // }

            // Fade down timer
            if (prevCueNum != -1) {
                final Timer T1 = new Timer();
                alCues.get(prevCueNum).setFadeInProgress(true);
                final long stepsToEndValDown = (long) ((fadeDownTime + 0.0) / 256 * 1000);
                if (stepsToEndValDown == 0) {
                    alCues.get(prevCueNum).setHighlight(0, 0, 0);
                    alCues.get(prevCueNum).setFadeInProgress(false);
                } else {
                    T1.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (alCues.get(prevCueNum).getHighlight() != 0) {
                                        alCues.get(prevCueNum).setHighlight(
                                                alCues.get(prevCueNum).getHighlight() - 1, 0, 0);
                                        for (Fixture col : alColumns) {
                                            col.incrementLevelDown();
                                        }
                                    } else {
                                        alCues.get(prevCueNum).setHighlight(0, 0, 0);
                                        alCues.get(prevCueNum).setFadeInProgress(false);
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

    public void padChannels(int number_channels) {
        if (number_channels > levelsList.size()) {
            for (int i = levelsList.size(); i < number_channels; i++) {
                levelsList.add(0);
            }
        } else {
            levelsList = new ArrayList<>(levelsList.subList(0, number_channels));
        }
    }

    public String getCueName() {
        if (name.equals("")) {
            return Double.toString(cueNum);
        }
        return name;
    }

    public void setCueName(String name) {
        this.name = name;
    }

    public String getString() {
        return getCueName();
    }

    public int[] getOriginalLevels() {
        return levels;
    }

    public void setOriginalLevels(int[] array) {
        levels = array;
    }

    public void setLevelsList(List<Integer> levelsList) {
        this.levelsList = new ArrayList<>(levelsList);
    }
}
