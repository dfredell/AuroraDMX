package com.AuroraByteSoftware.AuroraDMX.fixture;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.AuroraByteSoftware.AuroraDMX.MainActivity;
import com.AuroraByteSoftware.AuroraDMX.R;
import com.AuroraByteSoftware.AuroraDMX.ui.EditColumnMenu;
import com.AuroraByteSoftware.AuroraDMX.ui.VerticalSeekBar;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;

public class StandardFixture extends Fixture implements OnSeekBarChangeListener, OnClickListener {


    private RelativeLayout viewGroup = null;
    private TextView tvVal = null;
    private int ChNum = 0;
    private VerticalSeekBar seekBar = null;

    private double chLevel = 0;
    private double step = 0;
    private double stepIteram = 0;
    private final MainActivity context;
    private String chText = "";
    private String chValuePresets = "";
    private TextView tvChNum;
    private int defaultLvlTextColor = 0;
    private final static String PRESET_VALUE_REGEX = "^" + REGEX_255 + "$";


    public StandardFixture(final MainActivity context, String channelName, String valuePresets) {
        this.context = context;
        this.chText = channelName == null ? this.chText : channelName;
        this.setValuePresets(valuePresets);
        init();
    }

    @Override
    public void init() {
        LayoutInflater inflater = (LayoutInflater)   context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewGroup = (RelativeLayout) inflater.inflate(R.layout.fixture_standard, null);

        tvChNum = (TextView) viewGroup.findViewById(R.id.channel_number);

        tvVal = (TextView) viewGroup.findViewById(R.id.channel_level);
        defaultLvlTextColor = tvVal.getTextColors().getDefaultColor();

        seekBar = (VerticalSeekBar) viewGroup.findViewById(R.id.channel_seek);
        seekBar.setOnSeekBarChangeListener(this);

        TextView editButton = (TextView) viewGroup.findViewById(R.id.channel_edit);
        editButton.setOnClickListener(this);

        refreshValuePresetsHook();

        setColumnText(chText);
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
        seekBar.setPresetTicks(presets);
    }

    private LayerDrawable generateLayerDrawable(Context context, int scrollColor, int height) {

        //Foreground column color
        GradientDrawable shape2 = new GradientDrawable(Orientation.TOP_BOTTOM, new int[]{
                Color.rgb(0, 0, 0), scrollColor});
        shape2.setCornerRadius((int) context.getResources().getDimension(R.dimen.column_round_corners));
        ClipDrawable foreground = new ClipDrawable(shape2, Gravity.START, ClipDrawable.HORIZONTAL);

        //Background column color
        GradientDrawable shape1 = new GradientDrawable(Orientation.TOP_BOTTOM, new int[]{
                Color.rgb(10, 10, 10), Color.rgb(110, 110, 110)});
        shape1.setCornerRadius((int) context.getResources().getDimension(R.dimen.column_round_corners));// change the corners of the rectangle
        InsetDrawable background = new InsetDrawable(shape1, 5, 5, 5, 5);// the padding u want to use

        //Update the text view
        TextView channelName = (TextView) viewGroup.findViewById(R.id.channel_name);
        channelName.setText(chText);

        return new LayerDrawable(new Drawable[]{background, foreground});
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        setChLevels(Collections.singletonList(progress));
        chLevel = progress;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Unused, but required from implements
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Unused, but required from implements
    }

    /**
     * Sets the level of the channel
     *
     * @param a_chLevel set the level
     */
    @Override
    public void setChLevels(List<Integer> a_chLevel) {
        int newLvl = Math.min(MAX_LEVEL, a_chLevel.get(0));
        setChLevel(newLvl);
    }

    private void setChLevel(int a_chLevel) {
        seekBar.setProgress(a_chLevel);
        chLevel = a_chLevel;
        updateFixtureLevelText();
    }


    @Override
    protected void updateFixtureLevelText() {
        if (MainActivity.getSharedPref().getBoolean("channel_display_value", false)) {
            tvVal.setText(String.format("%1$s", (int) chLevel));
        } else {
            if (chLevel >= MAX_LEVEL) {
                tvVal.setText(context.getString(R.string.ChFull));
            } else {
                final String percent = Integer.toString((int) chLevel * 100 / MAX_LEVEL);
                tvVal.setText(String.format(context.getString(R.string.ChPercent), percent));
            }
        }
    }


    /**
     * get the level of the channel
     */
    @Override
    public List<Integer> getChLevels() {
        return Collections.singletonList((int) chLevel);
    }


    private void openSelectPresetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Add the buttons
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        final List<Pair<String, String>> presets = FixtureUtility.getParsedValuePresets(getValuePresets(), PRESET_VALUE_REGEX);
        if (presets == null)
            return;
        String[] presetArray = new String[presets.size()];
        int i = 0;
        for (Pair<String, String> v : presets) {
            presetArray[i] = v.getLeft() + " (" + v.getRight() + ")";
            i++;
        }

        builder.setTitle(R.string.pick_preset)
                .setItems(presetArray, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setChLevel(Integer.parseInt(presets.get(which).getRight()));
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Toggle button
     */
    @Override
    public void onClick(View v) {
        if (v == tvVal) {
            openSelectPresetDialog();
        } else {
            EditColumnMenu.createEditColumnMenu(viewGroup, context, this, chText, (int) chLevel, chValuePresets);
        }
    }


    @Override
    public String toString() {
        return ("Ch: " + ChNum + "\tLvl: " + chLevel);
    }

    /**
     * Creates 255 steeps between current and endVal
     *
     * @param endVal value when the fade is finished
     * @param steps  number of steps to take to get to the final falue
     */
    @Override
    public void setupIncrementLevelFade(List<Integer> endVal, double steps) {
        step = (endVal.get(0) - chLevel) / steps;
        stepIteram = chLevel;
        Log.v(TAG, String.format("Setting up fade %1$s %2$s", endVal.toString(), step));
    }

    /**
     * Adds one step Up to the current level
     */
    @Override
    public void incrementLevelUp() {
        if (step > 0) {
            stepIteram += step;
            setChLevel((int) Math.round(stepIteram));
        }
    }

    /**
     * Adds one step Down to the current level
     */
    @Override
    public void incrementLevelDown() {
        if (step < 0) {
            stepIteram += step;
            setChLevel((int) Math.round(stepIteram));
        }
    }


    @Override
    public void setScrollColor(int scrollColor) {
        seekBar.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int height = seekBar.getMeasuredHeight();
        LayerDrawable mylayer = generateLayerDrawable(viewGroup.getContext(), scrollColor, height);
        seekBar.setProgressDrawable(mylayer);
        seekBar.updateThumb();
        mylayer.setLevel((int) (chLevel / MAX_LEVEL * 10000));
    }

    @Override
    public void setColumnText(String text) {
        chText = text;

        LayerDrawable mylayer = generateLayerDrawable(this.context, Color.parseColor(MainActivity.getSharedPref().getString("channel_color", "#ffcc00")), seekBar.getMeasuredWidth());

        seekBar.setProgressDrawable(mylayer);
        seekBar.updateThumb();
        seekBar.setProgress((int) chLevel);

        mylayer.setLevel((int) (chLevel / MAX_LEVEL * 10000));
    }

    public String getValuePresets() {
        return chValuePresets;
    }

    public void setValuePresets(String text) {
        this.chValuePresets = text;
        refreshValuePresetsHook();
    }

    @Override
    public String getChText() {
        return chText;
    }

    @Override
    public boolean isRGB() {
        return false;
    }

    @Override
    public void setFixtureNumber(int currentFixtureNum) {
        tvChNum.setText(String.format("%1$s", currentFixtureNum));
    }
}
