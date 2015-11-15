package com.AuroraByteSoftware.AuroraDMX.fixture;

import android.content.Context;
import android.widget.LinearLayout;

import java.util.List;

/**
 * Created by furtchet on 11/14/15.
 */
public abstract class Fixture {

    public abstract void init();

    public abstract LinearLayout getViewGroup();

    public abstract void setChLevel(double a_chLevel);

    public abstract List<Integer> getChLevels();

    public abstract void setupIncrementLevelFade(int endVal);

    public abstract void incrementLevelUp();

    public abstract void incrementLevelDown();

    public abstract void setScrollColor(int scrollColor);

    public abstract void setColumnText(String text, Context context);

    public abstract String getChText();

    public abstract boolean isRGB();

    public abstract void removeSelector();

    public abstract void setFixtureNumber(int currentFixtureNum);
}
