package com.AuroraByteSoftware.AuroraDMX.fixture;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.AuroraByteSoftware.AuroraDMX.MainActivity;
import com.AuroraByteSoftware.AuroraDMX.R;
import com.AuroraByteSoftware.AuroraDMX.TextDrawable;
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

    private double chLevel = 0;
	private final int MAX_LEVEL = 255;
    private double steep = 0;
    private MainActivity context;
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

        ambilWarnaDialog = new AmbilWarnaDialog(context,0,this);
        rgbSelectView = ambilWarnaDialog.getView();
        viewGroup.addView(rgbSelectView, 2);

        viewGroup.getChildAt(3).setOnClickListener(this);

        tvChNum = ((TextView)viewGroup.getChildAt(0));
        tvVal = ((TextView)viewGroup.getChildAt(1));
        tvVal.setText("R:0 G:0 B:0");
    }

	@Override
    public void init() {
		this.viewGroup.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
		this.viewGroup.setOrientation(LinearLayout.VERTICAL);

        tvChNum = new TextView(context);
		tvChNum.setText(ChNum + "");
		tvChNum.setTextSize((int) context.getResources().getDimension(R.dimen.font_size));
		tvChNum.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		this.viewGroup.addView(tvChNum);
		tvVal = new TextView(context);
		tvVal.setText("R:0 G:0 B:0");
		tvVal.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		tvVal.setTextSize((int) context.getResources().getDimension(R.dimen.font_size_sm));
		this.viewGroup.addView(tvVal);

		ambilWarnaDialog = new AmbilWarnaDialog(context,0,this);
        rgbSelectView = ambilWarnaDialog.getView();
		this.viewGroup.addView(rgbSelectView);

		Button editButton = new Button(context);
        editButton.setOnClickListener(this);
        editButton.setText(R.string.edit);
		this.viewGroup.addView(editButton);
	}
	
	private LayerDrawable generateLayerDrawable(Context context, int scrollColor, int height){
		GradientDrawable shape2 = new GradientDrawable(Orientation.TOP_BOTTOM, new int[] {
				Color.rgb(0, 0, 0), scrollColor });
		shape2.setCornerRadius((int) context.getResources().getDimension(R.dimen.column_round_corners));
		ClipDrawable foreground = new ClipDrawable(shape2, Gravity.START, ClipDrawable.HORIZONTAL);

        GradientDrawable shape1 = new GradientDrawable(Orientation.TOP_BOTTOM, new int[] {
				Color.rgb(10, 10, 10), Color.rgb(110, 110, 110) });
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


	/**
	 * Sets the level of the channel
	 *
	 * @param a_chLevel set the level
	 */
    @Override
    public void setChLevels(List<Integer> a_chLevel) {
        rgbLevel = a_chLevel;
        ambilWarnaDialog.setRGBLevel(rgbLevel);
	}

	/**
	 * get the level of the channel
	 * 
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
        EditColumnMenu.createEditColumnMenu(viewGroup,context,this,chText,(int)chLevel);
	}


    @Override
    public String toString() {
		return ("Ch: " + ChNum + "\tLvl: " + chLevel);
	}

	/**
	 * Creates 255 steeps between current and endVal
	 */
	@Override
    public void setupIncrementLevelFade(int endVal) {
		steep = (endVal - chLevel) / 256.0;
	}

	/**
	 * Adds one step Up to the current level
	 */
	@Override
    public void incrementLevelUp() {
//		TODO if(steep>0)
//			setChLevels(chLevel + steep);
	}

	/**
	 * Adds one step Down to the current level
	 */
	@Override
    public void incrementLevelDown() {
//		TODO if(steep<0)
//			setChLevels(chLevel + steep);
	}
	

	@Override
    public void setScrollColor(int scrollColor){

    }

    public void setColumnText(String text, Context context){

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
        tvChNum.setText(Integer.toString(currentFixtureNum));
    }

    public void setChLevelArray(List<Integer> chLevels) {
        rgbLevel = chLevels;
    }
}
