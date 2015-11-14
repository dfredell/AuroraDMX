package yuku.ambilwarna;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.AuroraByteSoftware.AuroraDMX.ColumnObj;
import com.AuroraByteSoftware.AuroraDMX.R;

public class AmbilWarnaDialog {
    public interface OnAmbilWarnaListener {
        void onCancel(AmbilWarnaDialog dialog);

        void onOk(AmbilWarnaDialog dialog, int color);
    }

    final View view;
    final View viewHue;
    final AmbilWarnaSquare viewSatVal;
    final ImageView viewCursor;
    final ImageView viewTarget;
    final ViewGroup viewContainer;
    final float[] currentColorHsv = new float[3];
    final ColumnObj columnObj;


    /**
     * Create an AmbilWarnaDialog.
     *  @param context       activity context
     * @param color         current color
     * @param columnObj
     */
    public  AmbilWarnaDialog(final Context context, int color, ColumnObj columnObj) {

        this.columnObj = columnObj;

        // remove alpha if not supported
            color = color | 0xff000000;

        Color.colorToHSV(color, currentColorHsv);

        view = LayoutInflater.from(context).inflate(R.layout.ambilwarna_dialog, null);
        viewHue = view.findViewById(R.id.ambilwarna_viewHue);
        viewSatVal = (AmbilWarnaSquare) view.findViewById(R.id.ambilwarna_viewSatBri);
        viewCursor = (ImageView) view.findViewById(R.id.ambilwarna_cursor);
        viewTarget = (ImageView) view.findViewById(R.id.ambilwarna_target);
        viewContainer = (ViewGroup) view.findViewById(R.id.ambilwarna_viewContainer);

        viewSatVal.setHue(getHue());

        viewHue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE
                        || event.getAction() == MotionEvent.ACTION_DOWN
                        || event.getAction() == MotionEvent.ACTION_UP) {

                    float y = event.getY();
                    if (y < 0.f) y = 0.f;
                    if (y > viewHue.getMeasuredHeight()) {
                        y = viewHue.getMeasuredHeight() - 0.001f; // to avoid jumping the cursor from bottom to top.
                    }
                    float hue = 360.f - 360.f / viewHue.getMeasuredHeight() * y;
                    if (hue == 360.f) hue = 0.f;
                    setHue(hue);

                    // update view
                    viewSatVal.setHue(getHue());
                    moveCursor();

                    updateChannelLevel();

                    return true;
                }
                return false;
            }
        });

        viewSatVal.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE
                        || event.getAction() == MotionEvent.ACTION_DOWN
                        || event.getAction() == MotionEvent.ACTION_UP) {

                    float x = event.getX(); // touch event are in dp units.
                    float y = event.getY();

                    if (x < 0.f) x = 0.f;
                    if (x > viewSatVal.getMeasuredWidth()) x = viewSatVal.getMeasuredWidth();
                    if (y < 0.f) y = 0.f;
                    if (y > viewSatVal.getMeasuredHeight()) y = viewSatVal.getMeasuredHeight();

                    setSat(1.f / viewSatVal.getMeasuredWidth() * x);
                    setVal(1.f - (1.f / viewSatVal.getMeasuredHeight() * y));

                    // update view
                    moveTarget();

                    updateChannelLevel();

                    return true;
                }
                return false;
            }
        });


        // move cursor & target on first draw
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                moveCursor();
                moveTarget();
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    protected void updateChannelLevel(){
        int r = (getColor() >> 16) & 0xFF;
        int g = (getColor() >> 8) & 0xFF;
        int b = (getColor() >> 0) & 0xFF;
        columnObj.setChLevel(r);
        columnObj.setChText("R:"+r+" G:"+g+" B:"+b);
        columnObj.setRgbLevel(new int[]{r,g,b});
    }

    protected void moveCursor() {
        float y = viewHue.getMeasuredHeight() - (getHue() * viewHue.getMeasuredHeight() / 360.f);
        if (y == viewHue.getMeasuredHeight()) y = 0.f;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewCursor.getLayoutParams();
        layoutParams.leftMargin = (int) (viewHue.getLeft() - Math.floor(viewCursor.getMeasuredWidth() / 2) - viewContainer.getPaddingLeft());
        layoutParams.topMargin = (int) (viewHue.getTop() + y - Math.floor(viewCursor.getMeasuredHeight() / 2) - viewContainer.getPaddingTop());
        viewCursor.setLayoutParams(layoutParams);
    }

    protected void moveTarget() {
        float x = getSat() * viewSatVal.getMeasuredWidth();
        float y = (1.f - getVal()) * viewSatVal.getMeasuredHeight();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewTarget.getLayoutParams();
        layoutParams.leftMargin = (int) (viewSatVal.getLeft() + x - Math.floor(viewTarget.getMeasuredWidth() / 2) - viewContainer.getPaddingLeft());
        layoutParams.topMargin = (int) (viewSatVal.getTop() + y - Math.floor(viewTarget.getMeasuredHeight() / 2) - viewContainer.getPaddingTop());
        viewTarget.setLayoutParams(layoutParams);
    }


    private int getColor() {
        final int argb = Color.HSVToColor(currentColorHsv);
        return (argb & 0x00ffffff);
    }

    public View getView() {
        return view;
    }

    private float getHue() {
        return currentColorHsv[0];
    }

    private float getSat() {
        return currentColorHsv[1];
    }

    private float getVal() {
        return currentColorHsv[2];
    }

    private void setHue(float hue) {
        currentColorHsv[0] = hue;
    }

    private void setSat(float sat) {
        currentColorHsv[1] = sat;
    }

    private void setVal(float val) {
        currentColorHsv[2] = val;
    }
}