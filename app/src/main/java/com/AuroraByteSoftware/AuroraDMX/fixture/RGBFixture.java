package com.AuroraByteSoftware.AuroraDMX.fixture;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.AuroraByteSoftware.AuroraDMX.MainActivity;
import com.AuroraByteSoftware.AuroraDMX.R;
import com.AuroraByteSoftware.AuroraDMX.ui.EditColumnMenu;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import yuku.ambilwarna.AmbilWarnaDialog;

public class RGBFixture extends Fixture implements OnClickListener {


    private RelativeLayout viewGroup = null;
    private TextView tvVal = null;

    private final double[] step = new double[3];
    private final double[] stepIteram = new double[3];
    private final MainActivity context;
    private String chText = "";
    private String chValuePresets = "";
    private TextView tvChNum;
    private List<Integer> rgbLevel = new ArrayList<>(Collections.nCopies(3, 0));
    private AmbilWarnaDialog ambilWarnaDialog = null;
    private int defaultLvlTextColor = 0;
    private final static String RGB_REGEX = REGEX_255 + "," + REGEX_255 + "," + REGEX_255;
    private final static String RGB_HEX_REGEX = "[a-fA-F0-9]{6}";
    private final static String PRESET_VALUE_REGEX = "^(" + RGB_REGEX + "|" + RGB_HEX_REGEX + ")$";

    public RGBFixture(final MainActivity context, String channelName, String presets) {
        this.context = context;
        this.chText = channelName == null ? this.chText : channelName;
        this.chValuePresets = presets;
        init();
    }

    RGBFixture(final MainActivity context, String channelName) {
        this.context = context;
        this.chText = channelName == null ? this.chText : channelName;

        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewGroup = (RelativeLayout) inflater.inflate(R.layout.fixture_rgb, null);

        tvChNum = (TextView) viewGroup.findViewById(R.id.channel_rgb_number);

        tvVal = (TextView) viewGroup.findViewById(R.id.channel_rgb_level);
        defaultLvlTextColor = tvVal.getTextColors().getDefaultColor();

        View viewById = viewGroup.findViewById(R.id.ambilwarna_dialogView);
        ambilWarnaDialog = new AmbilWarnaDialog(context, 0, this, viewById);

        TextView editButton = (TextView) viewGroup.findViewById(R.id.channel_rgb_edit);
        editButton.setOnClickListener(this);

        refreshValuePresetsHook();
    }


    /**
     * Set color to indicate that there are presets and bind listener
     */
    private void refreshValuePresetsHook() {
        if (tvVal == null) {
            return;
        }
        final List<Pair<String, String>> presets = FixtureUtility.getParsedValuePresets(getValuePresets(), PRESET_VALUE_REGEX);
        if (presets != null) {
            tvVal.setTextColor(Color.parseColor(PRESET_TEXT_COLOR));
            tvVal.setOnClickListener(this);
        } else {
            tvVal.setTextColor(defaultLvlTextColor);
            tvVal.setOnClickListener(null);
        }
    }

    /**
     * Ask the user what preset they want to jump to
     * then jump there
     */
    private void openSelectPresetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Add the buttons
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        final List<Pair<String, String>> presets = FixtureUtility.getParsedValuePresets(getValuePresets(), PRESET_VALUE_REGEX);
        if (presets == null) {
            return;
        }
        String[] presetArray = new String[presets.size()];
        int i = 0;
        for (Pair<String, String> v : presets) {
            presetArray[i] = v.getLeft() + " (" + v.getRight() + ")";
            i++;
        }

        builder.setTitle(R.string.pick_preset)
                .setItems(presetArray, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final List<Integer> integers = splitLevels(presets.get(which).getRight());
                        if (integers != null) {
                            setChLevels(integers);
                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Split the user input preset into RGB ints
     *
     * @param right ff0011 or 12,34,56
     * @return list of DMX rgb values
     */
    public static List<Integer> splitLevels(String right) {
        String value = right.trim();
        final List<Integer> integers = new ArrayList<>(3);
        if (value.matches(RGB_REGEX)) {
            final String[] split = value.split(",");
            integers.add(Integer.parseInt(split[0]));
            integers.add(Integer.parseInt(split[1]));
            integers.add(Integer.parseInt(split[2]));
            return integers;
        } else if (value.matches(RGB_HEX_REGEX)) {
            integers.add(Integer.valueOf(value.substring(0, 2), 16));
            integers.add(Integer.valueOf(value.substring(2, 4), 16));
            integers.add(Integer.valueOf(value.substring(4, 6), 16));
            return integers;
        } else {
            return null;
        }
    }

    /**
     * Get the LinearLayout that contains the text, slider, and button for one
     * channel
     *
     * @return the viewGroup
     */
    @Override
    public RelativeLayout getViewGroup() {
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

    private void updateFixtureLevelText() {
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
        if (v == tvVal) {
            openSelectPresetDialog();
        } else {
            EditColumnMenu.createEditColumnMenu(viewGroup, context, this, chText, 0, chValuePresets);
        }
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
        // TODO: I don't know how to determine if a color is going up or down, so I'll just always use stepsToEndValUp
        // It would be weird if Red went to its final color before other just becasue he decreased
        step[0] = (endVal.get(0) - rgbLevel.get(0)) / steps;
        step[1] = (endVal.get(1) - rgbLevel.get(1)) / steps;
        step[2] = (endVal.get(2) - rgbLevel.get(2)) / steps;
        stepIteram[0] = rgbLevel.get(0);
        stepIteram[1] = rgbLevel.get(1);
        stepIteram[2] = rgbLevel.get(2);
    }

    /**
     * Adds one step Up to the current level
     */
    @Override
    public void incrementLevel() {
        stepIteram[0] += step[0];
        stepIteram[1] += step[1];
        stepIteram[2] += step[2];
        updateIncrementedLevel();
    }

    private void updateIncrementedLevel() {
        rgbLevel.set(0, (int) Math.round(stepIteram[0]));
        rgbLevel.set(1, (int) Math.round(stepIteram[1]));
        rgbLevel.set(2, (int) Math.round(stepIteram[2]));
        ambilWarnaDialog.setRGBLevel(rgbLevel);
    }

    public void updateUi() {
//        ambilWarnaDialog.getViewSatVal().invalidate();
    }

    @Override
    public void setScrollColor(int scrollColor) {
        //RGB has its own color
    }

    public void setColumnText(String text) {
        this.chText = text;
        ambilWarnaDialog.setChannelName(text);
    }

    public void setValuePresets(String text) {
        this.chValuePresets = text;
        refreshValuePresetsHook();
    }

    @Override
    public String getChText() {
        return chText;
    }

    public void setChText(String chText) {
        tvVal.setText(chText);
    }

    public String getValuePresets() {
        return chValuePresets;
    }

    @Override
    public boolean isRGB() {
        return true;
    }

    @Override
    public void setFixtureNumber(int currentFixtureNum) {
        tvChNum.setText(String.format("%1$s", currentFixtureNum));
    }

    public void setChLevelArray(List<Integer> chLevels) {
        rgbLevel = chLevels;
    }
}
