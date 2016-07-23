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
import android.graphics.drawable.ShapeDrawable;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.AuroraByteSoftware.AuroraDMX.MainActivity;
import com.AuroraByteSoftware.AuroraDMX.R;
import com.AuroraByteSoftware.AuroraDMX.TextDrawable;
import com.AuroraByteSoftware.AuroraDMX.ui.EditColumnMenu;
import com.AuroraByteSoftware.AuroraDMX.ui.VerticalSeekBar;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StandardFixture extends Fixture implements OnSeekBarChangeListener, OnClickListener {


    private LinearLayout viewGroup = null;
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
    private static final String TAG = "AuroraDMX";

    public StandardFixture(final MainActivity context, String channelName, String valuePresets) {
        this.context = context;
        this.chText = channelName == null ? this.chText : channelName;
        this.setValuePresets(valuePresets);
        init();
    }

    public StandardFixture(MainActivity context, String channelName, LinearLayout viewGroup) {
        this.context = context;
        this.chText = channelName == null ? this.chText : channelName;
        this.viewGroup = viewGroup;

        seekBar = createSeekBar();
        viewGroup.addView(seekBar, 2);

        viewGroup.getChildAt(3).setOnClickListener(this);

        tvChNum = ((TextView) viewGroup.getChildAt(0));
        tvVal = ((TextView) viewGroup.getChildAt(1));
        setChLevel(0);
    }

    @Override
    public void init() {
        viewGroup = new LinearLayout(context);
        viewGroup.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        viewGroup.setOrientation(LinearLayout.VERTICAL);

        tvChNum = new TextView(context);
        tvChNum.setText(String.format("%1$s", ChNum));
        tvChNum.setTextSize((int) context.getResources().getDimension(R.dimen.font_size));
        tvChNum.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        viewGroup.addView(tvChNum);

        tvVal = new TextView(context);
        tvVal.setText(String.format(context.getString(R.string.ChPercent), 0));
        tvVal.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        tvVal.setTextSize((int) context.getResources().getDimension(R.dimen.font_size_sm));
        viewGroup.addView(tvVal);

        seekBar = createSeekBar();
        viewGroup.addView(seekBar);

        Button editButton = new Button(context);
        editButton.setOnClickListener(this);
        editButton.setText(R.string.edit);

        refreshValuePresetsHook();
        viewGroup.addView(editButton);
    }

    /**
     * Set color to indicate that there are presets and bind listener
     */
    public void refreshValuePresetsHook() {
        if (tvVal == null) {
            return;
        }
        if (getParsedValuePresets() != null) {
            tvVal.setTextColor(Color.parseColor(MainActivity.getSharedPref().getString("has_presets_color", "#99ccff")));
            tvVal.setOnClickListener(this);
        } else {
            tvVal.setTextColor(tvVal.getTextColors().getDefaultColor());
            tvVal.setOnClickListener(null);
        }
    }

    private VerticalSeekBar createSeekBar() {
        VerticalSeekBar verticalSeekBar = new VerticalSeekBar(context);// make

        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);
        // Sets weight to 1
        layoutParams2.setMargins(
                (int) context.getResources().getDimension(R.dimen.column_padding_X),
                (int) context.getResources().getDimension(R.dimen.column_padding_Y),
                (int) context.getResources().getDimension(R.dimen.column_padding_X),
                (int) context.getResources().getDimension(R.dimen.column_padding_Y));
        verticalSeekBar.setLayoutParams(layoutParams2);

        verticalSeekBar.setThumb(new ShapeDrawable());// No thumb

        verticalSeekBar.setMax(MAX_LEVEL);
        verticalSeekBar.setOnSeekBarChangeListener(this);

        //Populate the text after the seekBar has rendered on the screen
        verticalSeekBar.post(new Runnable() {
            @Override
            public void run() {
                setColumnText(chText);
            }
        });
        return verticalSeekBar;
    }

    private LayerDrawable generateLayerDrawable(Context context, int scrollColor, int height) {
        GradientDrawable shape2 = new GradientDrawable(Orientation.TOP_BOTTOM, new int[]{
                Color.rgb(0, 0, 0), scrollColor});
        shape2.setCornerRadius((int) context.getResources().getDimension(R.dimen.column_round_corners));
        ClipDrawable foreground = new ClipDrawable(shape2, Gravity.START, ClipDrawable.HORIZONTAL);

        GradientDrawable shape1 = new GradientDrawable(Orientation.TOP_BOTTOM, new int[]{
                Color.rgb(10, 10, 10), Color.rgb(110, 110, 110)});
        shape1.setCornerRadius((int) context.getResources().getDimension(R.dimen.column_round_corners));// change the corners of the rectangle
        InsetDrawable background = new InsetDrawable(shape1, 5, 5, 5, 5);// the padding u want to use

        TextDrawable d = new TextDrawable(context);
        d.setText(chText);
        d.setTextAlign(Layout.Alignment.ALIGN_CENTER);

        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{background, foreground, d});
        layerDrawable.setLayerInset(2, 10, (height / 2) - 15, 0, 0);//set offset of the text layer
        return layerDrawable;
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
            if (chLevel >= MAX_LEVEL)
                tvVal.setText(context.getString(R.string.ChFull));
            else
                tvVal.setText(String.format(context.getString(R.string.ChPercent), ((int) chLevel * 100 / MAX_LEVEL)));
        }
    }


    /**
     * get the level of the channel
     */
    @Override
    public List<Integer> getChLevels() {
        return Collections.singletonList((int) chLevel);
    }


    public void openSelectPresetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Add the buttons
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        final List<Pair<String, Integer>> presets = getParsedValuePresets();
        String[] presetArray = new String[presets.size()];
        int i = 0;
        for (Pair<String, Integer> v : presets) {
            presetArray[i] = v.getLeft() + " (" + v.getRight() + ")";
            i++;
        }

        builder.setTitle(R.string.pick_preset)
                .setItems(presetArray, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setChLevel(presets.get(which).getRight());
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

    /**
     * Get parsed list of preset values
     * <p>
     * Input String format:
     * <p>
     * Name1=10;Name2=20;Name3=200
     * <p>
     * Returns null if there are no (valid) presets
     *
     * @return null|List
     */
    public List<Pair<String, Integer>> getParsedValuePresets() {
        if (StringUtils.isEmpty(getValuePresets())) {
            return null;
        }
        ArrayList<Pair<String, Integer>> presets = new ArrayList<Pair<String, Integer>>();
        String[] split = getValuePresets().split(";");
        for (int i = 0; i < split.length; i++) {
            String[] row = split[i].split("=");
            if (row.length == 0) {
                continue; // error: entry does not contain anything
            }
            String name;
            Integer value;
            if (row.length == 1) {
                name = row[0];
                try {
                    value = Integer.parseInt(row[0]);
                } catch (NumberFormatException e) {
                    continue;
                }
            } else {
                name = row[0];
                try {
                    value = Integer.parseInt(row[1]);
                } catch (NumberFormatException e) {
                    continue;
                }
            }

            if (value < 0 || value > 255) {
                continue; // error: invalid value
            }

            presets.add(new ImmutablePair<String, Integer>(name, value));
        }

        if (presets.isEmpty()) {
            return null;
        }

        return presets;
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
    public void removeSelector() {
        viewGroup.removeView(seekBar);
    }

    @Override
    public void setFixtureNumber(int currentFixtureNum) {
        tvChNum.setText(String.format("%1$s", currentFixtureNum));
    }
}
