package com.AuroraByteSoftware.AuroraDMX.fixture;


import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.AuroraByteSoftware.AuroraDMX.MainActivity;
import com.AuroraByteSoftware.AuroraDMX.R;
import com.AuroraByteSoftware.AuroraDMX.ui.EditColumnMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import yuku.ambilwarna.AmbilWarnaDialog;

public class RGBFixture extends Fixture implements OnClickListener {


    private LinearLayout viewGroup = null;
    private TextView tvVal = null;
    private int ChNum = 0;
    private View rgbSelectView;

    private double step[] = new double[3];
    private double stepIteram[] = new double[3];
    private final MainActivity context;
    private String chText = "";
    private TextView tvChNum;
    private List<Integer> rgbLevel = new ArrayList<>(Collections.nCopies(3, 0));
    private AmbilWarnaDialog ambilWarnaDialog = null;

    public RGBFixture(final MainActivity context, String channelName) {
        this.context = context;
        this.chText = channelName == null ? this.chText : channelName;
        this.viewGroup = new LinearLayout(context);
        init();
    }

    public RGBFixture(final MainActivity context, String channelName, LinearLayout viewGroup) {
        this.context = context;
        this.chText = channelName == null ? this.chText : channelName;
        this.viewGroup = viewGroup;

        ambilWarnaDialog = new AmbilWarnaDialog(context, 0, this);
        ambilWarnaDialog.setChannelName(chText);
        rgbSelectView = ambilWarnaDialog.getView();
        viewGroup.addView(rgbSelectView, 2);

        viewGroup.getChildAt(3).setOnClickListener(this);

        tvChNum = ((TextView) viewGroup.getChildAt(0));
        tvVal = ((TextView) viewGroup.getChildAt(1));
        tvVal.setText("R:0 G:0 B:0");
    }

    @Override
    public void init() {
        viewGroup.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        viewGroup.setOrientation(LinearLayout.VERTICAL);

        tvChNum = new TextView(context);
        tvChNum.setText(String.format("%1$s", ChNum));
        tvChNum.setTextSize((int) context.getResources().getDimension(R.dimen.font_size));
        tvChNum.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        viewGroup.addView(tvChNum);

        tvVal = new TextView(context);
        tvVal.setText("R:0 G:0 B:0");
        tvVal.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        tvVal.setTextSize((int) context.getResources().getDimension(R.dimen.font_size_sm));
        viewGroup.addView(tvVal);

        ambilWarnaDialog = new AmbilWarnaDialog(context, 0, this);
        rgbSelectView = ambilWarnaDialog.getView();
        viewGroup.addView(rgbSelectView);

        Button editButton = new Button(context);
        editButton.setOnClickListener(this);
        editButton.setText(R.string.edit);
        viewGroup.addView(editButton);
    }

    /**
     * Get the LinearLayout that contains the text, slider, and button for one
     * channel
     *
     * @return the viewGroup
     */
    @Override
    public LinearLayout getViewGroup() {
        return viewGroup;
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
        updateFixtureLevelText();
    }

    @Override
    protected void updateFixtureLevelText() {
        ambilWarnaDialog.setRGBLevel(rgbLevel);
    }

    /**
     * get the level of the channel
     */
    @Override
    public List<Integer> getChLevels() {
        return rgbLevel;
    }

    /**
     * Toggle button
     */
    @Override
    public void onClick(View v) {
        EditColumnMenu.createEditColumnMenu(viewGroup, context, this, chText, 0);
    }


    @Override
    public String toString() {
        return ("Ch: " + ChNum + "\tLvl: " + rgbLevel);
    }

    /**
     * Creates 255 steeps between current and endVal
     *
     * @param endVal
     */
    @Override
    public void setupIncrementLevelFade(List<Integer> endVal) {
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
    public void incrementLevelUp() {
        if (step[0] > 0)
            stepIteram[0] += step[0];
        if (step[1] > 0)
            stepIteram[1] += step[1];
        if (step[2] > 0)
            stepIteram[2] += step[2];
        updateIncrementedLevel();
    }

    /**
     * Adds one step Down to the current level
     */
    @Override
    public void incrementLevelDown() {
        if (step[0] < 0)
            stepIteram[0] += step[0];
        if (step[1] < 0)
            stepIteram[1] += step[1];
        if (step[2] < 0)
            stepIteram[2] += step[2];
        updateIncrementedLevel();
    }

    private void updateIncrementedLevel() {
        rgbLevel.set(0, (int) stepIteram[0]);
        rgbLevel.set(1, (int) stepIteram[1]);
        rgbLevel.set(2, (int) stepIteram[2]);
        ambilWarnaDialog.setRGBLevel(rgbLevel);
    }

    @Override
    public void setScrollColor(int scrollColor) {
        //RGB has its own color
    }

    public void setColumnText(String text) {
        this.chText = text;
        ambilWarnaDialog.setChannelName(text);
    }

    @Override
    public String getChText() {
        return chText;
    }

    public void setChText(String chText) {
        tvVal.setText(chText);
    }

    @Override
    public boolean isRGB() {
        return true;
    }

    @Override
    public void removeSelector() {
        viewGroup.removeView(rgbSelectView);
    }

    @Override
    public void setFixtureNumber(int currentFixtureNum) {
        tvChNum.setText(String.format("%1$s", currentFixtureNum));
    }

    public void refreshLayout() {
        ambilWarnaDialog.getView().post(new Runnable() {
            @Override
            public void run() {
                ambilWarnaDialog.getViewSatVal().invalidate();
            }
        });
    }

    public void setChLevelArray(List<Integer> chLevels) {
        rgbLevel = chLevels;
    }
}
