package com.AuroraByteSoftware.AuroraDMX.test;


import android.widget.RelativeLayout;

import com.AuroraByteSoftware.AuroraDMX.MainActivity;
import com.AuroraByteSoftware.AuroraDMX.fixture.Fixture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DummyFixture extends Fixture {


    private final double[] step = new double[3];
    private final double[] stepIteram = new double[3];
    private String chText = "";
    private String chValuePresets = "";
    private List<Integer> rgbLevel = new ArrayList<>(Collections.nCopies(3, 0));

    public DummyFixture(final MainActivity context, String channelName) {
        this.chText = channelName == null ? this.chText : channelName;
    }


    /**
     * Get the LinearLayout that contains the text, slider, and button for one
     * channel
     *
     * @return the viewGroup
     */
    @Override
    public RelativeLayout getViewGroup() {
        return null;
    }


    /**
     * Sets the level of the channel
     *
     * @param a_chLevel set the level
     */
    @Override
    public void setChLevels(List<Integer> a_chLevel) {
        for (int i = 0; i < a_chLevel.size(); i++) {
            rgbLevel.set(i, Math.min(MAX_LEVEL, a_chLevel.get(i)));
        }
    }

    /**
     * get the level of the channel
     */
    @Override
    public List<Integer> getChLevels() {
        return rgbLevel;
    }

    @Override
    public String toString() {
        return ("Lvl: " + rgbLevel);
    }

    /**
     * Creates 255 steeps between current and endVal
     *
     * @param endVal value after fade
     * @param steps  number of steps to take to get to the final falue
     */
    @Override
    public void setupIncrementLevelFade(List<Integer> endVal, double steps) {
        step[0] = (endVal.get(0) - rgbLevel.get(0)) / 256.0;
        step[1] = (endVal.get(1) - rgbLevel.get(1)) / 256.0;
        step[2] = (endVal.get(2) - rgbLevel.get(2)) / 256.0;
        stepIteram[0] = rgbLevel.get(0);
        stepIteram[1] = rgbLevel.get(1);
        stepIteram[2] = rgbLevel.get(2);
    }

    /**
     * Adds one step Up to the current level
     */
    @Override
    public void incrementLevel() {
        if (step[0] > 0) {
            stepIteram[0] += step[0];
        }
        if (step[1] > 0) {
            stepIteram[1] += step[1];
        }
        if (step[2] > 0) {
            stepIteram[2] += step[2];
        }
        updateIncrementedLevel();
    }

    private void updateIncrementedLevel() {
        rgbLevel.set(0, (int) Math.round(stepIteram[0]));
        rgbLevel.set(1, (int) Math.round(stepIteram[1]));
        rgbLevel.set(2, (int) Math.round(stepIteram[2]));
    }

    @Override
    public void setScrollColor(int scrollColor) {
        //RGB has its own color
    }

    public void setColumnText(String text) {
        this.chText = text;
    }

    public void setValuePresets(String text) {
        this.chValuePresets = text;
    }

    public String getValuePresets() {
        return chValuePresets;
    }

    @Override
    public String getChText() {
        return chText;
    }

    @Override
    public boolean isRGB() {
        return true;
    }

    @Override
    public void updateUi() {

    }

    @Override
    public void setFixtureNumber(int currentFixtureNum) {
    }
}
