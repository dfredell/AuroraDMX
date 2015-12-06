package com.AuroraByteSoftware.AuroraDMX.ui;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.AuroraByteSoftware.AuroraDMX.R;

class GridCell extends BaseAdapter implements OnClickListener {
    private final Context mContext;
    private int count = 0;
    private final boolean isCh;
    final int SELECTED_COLOR = 0xe03399ff;

    public GridCell(Context c, int a_count, boolean a_isChs) {
        mContext = c;
        count = a_count;
        isCh = a_isChs;
    }

    public int getCount() {
        return count;
    }

    public Object getItem(int position) {
        return this;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    //Called when scrolling
    public View getView(int position, View convertView, ViewGroup parent) {
        Button button;
        button = new Button(mContext);
        button.setPadding(8, 8, 8, 8);
        button.setText(String.format("%1$s", position + 1));
        button.setGravity(Gravity.CENTER);
        button.setTextSize((int) mContext.getResources().getDimension(R.dimen.font_size_sm));
        button.setMaxLines(1);
        button.setOnClickListener(this);
        if (!isCh && PatchActivity.chContainsDimmer(position)) {
            button.getBackground().setColorFilter(SELECTED_COLOR, PorterDuff.Mode.SRC_ATOP);
        } else if (isCh && PatchActivity.currentCh - 1 == position) {
            button.getBackground().setColorFilter(SELECTED_COLOR, PorterDuff.Mode.SRC_ATOP);
        }
        return button;
    }

    @Override
    public void onClick(View arg0) {
        if (arg0 instanceof Button) {
            Button button = (Button) arg0;

            if (isCh) {
                // set current ch
                PatchActivity.currentCh = Integer.parseInt(button.getText().toString());

                final int numVisibleChildren = PatchActivity.chGridView.getChildCount();
                int numVisibleChildrenOffset = PatchActivity.chGridView.getFirstVisiblePosition();

                // Set dimmers
                for (int i = 0; i < numVisibleChildren; i++) {
                    View view = PatchActivity.chGridView.getChildAt(i);
                    if (PatchActivity.currentCh == (i + numVisibleChildrenOffset + 1)) {
                        view.getBackground().setColorFilter(SELECTED_COLOR, PorterDuff.Mode.SRC_ATOP);
                    } else {
                        view.getBackground().clearColorFilter();
                    }
                }
            } else {// add or remove dim to ch
                PatchActivity.toggleDimToCh(PatchActivity.currentCh, Integer.parseInt(button.getText().toString()));
            }
            button.getBackground().setColorFilter(SELECTED_COLOR, PorterDuff.Mode.SRC_ATOP);

            final int numVisibleChildren = PatchActivity.dimGridView.getChildCount();
            int numVisibleChildrenOffset = PatchActivity.dimGridView.getFirstVisiblePosition();

            // Set dimmers
            for (int i = 0; i < numVisibleChildren; i++) {
                View view = PatchActivity.dimGridView.getChildAt(i);
                if (PatchActivity.chContainsDimmer(i + numVisibleChildrenOffset)) {
                    view.getBackground().setColorFilter(SELECTED_COLOR, PorterDuff.Mode.SRC_ATOP);
                } else {
                    view.getBackground().clearColorFilter();
                }
            }
        }
    }

}