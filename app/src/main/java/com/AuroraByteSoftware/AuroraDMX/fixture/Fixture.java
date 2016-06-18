package com.AuroraByteSoftware.AuroraDMX.fixture;

import android.widget.LinearLayout;

import java.util.List;

/**
 * Abstract method used to represent a lighting fixture in the theatre
 * Created by furtchet on 11/14/15.
 */
public abstract class Fixture {
    protected final int MAX_LEVEL = 255;

    public abstract void init();

    public abstract LinearLayout getViewGroup();

    public abstract void setChLevels(List<Integer> a_chLevel);

    public abstract List<Integer> getChLevels();

    public abstract void setupIncrementLevelFade(List<Integer> endVal, double steps);

    public abstract void incrementLevelUp();

    public abstract void incrementLevelDown();

    public abstract void setScrollColor(int scrollColor);

    public abstract void setColumnText(String text);

    public abstract String getChText();

    public abstract void setValuePresets(String text);

    public abstract String getValuePresets();

    public abstract boolean isRGB();

    public abstract void removeSelector();

    public abstract void setFixtureNumber(int currentFixtureNum);

    protected abstract void updateFixtureLevelText();
}
