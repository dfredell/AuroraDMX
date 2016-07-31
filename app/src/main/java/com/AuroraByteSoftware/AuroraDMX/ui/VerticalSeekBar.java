package com.AuroraByteSoftware.AuroraDMX.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.widget.SeekBar;

import com.AuroraByteSoftware.AuroraDMX.fixture.Fixture;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class VerticalSeekBar extends SeekBar {

    private List<Double> presetTicks = new ArrayList<>();

    public VerticalSeekBar(Context context) {
        super(context);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    protected void onDraw(Canvas c) {
        c.rotate(-90);
        c.translate(-getHeight(), 0);

        super.onDraw(c);
        drawTicks(c);
    }

    private void drawTicks(Canvas canvas) {

        // Loop through and draw each tick (except final tick).
        Paint mPaint;
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor(Fixture.PRESET_TEXT_COLOR));
        mPaint.setStrokeWidth(5);
        mPaint.setAntiAlias(true);

        int left = getPaddingRight();
        int right = getWidth() - getPaddingRight();
        final int scrollBarHeight = getHeight() - getPaddingRight() - getPaddingLeft();

        for (Double presetTick : presetTicks) {
            double percent = presetTick / Fixture.MAX_LEVEL;
            int height = ((int) (percent * scrollBarHeight)) + getPaddingLeft();
            canvas.drawLine(height + 2, left, height - 2, right, mPaint);
        }
    }

    public void setPresetTicks(List<Pair<String, String>> presets) {
        presetTicks.clear();
        if (presets == null)
            return;
        for (Pair<String, String> preset : presets) {
            presetTicks.add(Double.parseDouble(preset.getRight()));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                setProgress(getMax() - (int) (getMax() * event.getY() / getHeight()));
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                break;

            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    public void updateThumb() {
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }
}