package com.AuroraByteSoftware.AuroraDMX;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.AuroraByteSoftware.AuroraDMX.ui.VerticalSeekBar;

class ColumnObj implements OnSeekBarChangeListener, OnClickListener {

	/**
	 * 
	 */
	private LinearLayout viewGroup = null;
	private TextView tvVal = null;
	private int ChNum = 0;
	private VerticalSeekBar seekBar = null;

	private ToggleButton toggleButton = null;
	private int preFullLevel = 0;
	private double chLevel = 0;
	private final int MAX_LEVEL = 255;
    private double steep = 0;

	public ColumnObj(int a_ChNum, Context context, int scrollColor) {
		ChNum = a_ChNum;
		viewGroup = new LinearLayout(context);
		viewGroup.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.MATCH_PARENT));
		viewGroup.setOrientation(LinearLayout.VERTICAL);

		TextView tvChNum = new TextView(context);
		tvChNum.setText(ChNum + "");
		tvChNum.setTextSize((int) context.getResources().getDimension(R.dimen.font_size));
		tvChNum.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		//TODO: Allow naming the channel 
		viewGroup.addView(tvChNum);
		tvVal = new TextView(context);
		tvVal.setText("0%");
		tvVal.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		tvVal.setTextSize((int) context.getResources().getDimension(R.dimen.font_size_sm));
		viewGroup.addView(tvVal);

		seekBar = new VerticalSeekBar(context); // make

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);
		// Sets weight to 1
		layoutParams.setMargins(
				(int) context.getResources().getDimension(R.dimen.column_padding_X), 
				(int) context.getResources().getDimension(R.dimen.column_padding_Y), 
				(int) context.getResources().getDimension(R.dimen.column_padding_X), 
				(int) context.getResources().getDimension(R.dimen.column_padding_Y));
		seekBar.setLayoutParams(layoutParams);

		seekBar.setThumb(new ShapeDrawable());// No thumb

		seekBar.setMax(MAX_LEVEL);
		seekBar.setOnSeekBarChangeListener(this);

		LayerDrawable mylayer = generateLayerDrawable(context,scrollColor);
		
		seekBar.setProgressDrawable(mylayer);

		viewGroup.addView(seekBar);

		toggleButton = new ToggleButton(context);
		toggleButton.setOnClickListener(this);
		viewGroup.addView(toggleButton);
	}
	
	private LayerDrawable generateLayerDrawable(Context context, int scrollColor){
		GradientDrawable shape2 = new GradientDrawable(Orientation.TOP_BOTTOM, new int[] {
				Color.rgb(0, 0, 0), scrollColor });
		shape2.setCornerRadius((int) context.getResources().getDimension(R.dimen.column_round_corners));
		ClipDrawable clip = new ClipDrawable(shape2, Gravity.START, ClipDrawable.HORIZONTAL);
		GradientDrawable shape1 = new GradientDrawable(Orientation.TOP_BOTTOM, new int[] {
				Color.rgb(10, 10, 10), Color.rgb(110, 110, 110) });
		shape1.setCornerRadius((int) context.getResources().getDimension(R.dimen.column_round_corners));// change the corners of the rectangle
		InsetDrawable d1 = new InsetDrawable(shape1, 5, 5, 5, 5);// the padding u want to use

		return new LayerDrawable(new Drawable[] { d1, clip });
	}

	/**
	 * Get the LinearLayout that contains the text, slider, and button for one
	 * channel
	 * 
	 * @return the viewGroup
	 */
	public LinearLayout getViewGroup() {
		return viewGroup;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		setChLevel(progress);
		chLevel = progress;
		// System.out.println("Ch " + ChNum + "\t" + progress + "%");

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		// System.out.println("onStartTrackingTouch");
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		// System.out.println("onStopTrackingTouch");
	}

	/**
	 * Sets the level of the channel
	 * 
	 * @param a_chLevel set the level
	 */
	public void setChLevel(double a_chLevel) {
        if(MainActivity.sharedPref.getBoolean("channel_display_value", false)) {
            tvVal.setText(Integer.toString((int) a_chLevel));
        }else {
            if (a_chLevel >= MAX_LEVEL) {
                tvVal.setText("Full");
                toggleButton.setChecked(true);
            } else {
                tvVal.setText((int) (a_chLevel * 100 / MAX_LEVEL) + "%");
                if (preFullLevel != a_chLevel)
                    toggleButton.setChecked(false);
            }
        }
		seekBar.setProgress((int) a_chLevel);
		chLevel = a_chLevel;
	}

	/**
	 * get the level of the channel
	 * 
	 */
	public int getChLevel() {
		return (int) chLevel;
	}

	/**
	 * Toggle button
	 */
	@Override
	public void onClick(View v) {

		// System.out.println("Toggle " + toggleButton.isChecked());
		if (toggleButton.isChecked()) {
			preFullLevel = getChLevel();
			setChLevel(MAX_LEVEL);
		} else {
			setChLevel(preFullLevel);
		}
	}


    public String toString() {
		return ("Ch: " + ChNum + "\tLvl: " + chLevel);
	}

	/**
	 * Creates 255 steeps between current and endVal
	 */
	public void setupIncrementLevelFade(int endVal) {
		steep = (endVal - chLevel) / 256.0;
	}

	/**
	 * Adds one step Up to the current level
	 */
	public void incrementLevelUp() {
		if(steep>0)
			setChLevel(chLevel + steep);
	}

	/**
	 * Adds one step Down to the current level
	 */
	public void incrementLevelDown() {
		if(steep<0)
			setChLevel(chLevel + steep);
	}
	

	public void setScrollColor(int scrollColor){
		LayerDrawable mylayer = generateLayerDrawable(viewGroup.getContext(),scrollColor);
		seekBar.setProgressDrawable(mylayer);
		seekBar.updateThumb();
		//TODO: Allow previous lvl to be displayed
		chLevel = 0;
		tvVal.setText("0%");
	}
}
