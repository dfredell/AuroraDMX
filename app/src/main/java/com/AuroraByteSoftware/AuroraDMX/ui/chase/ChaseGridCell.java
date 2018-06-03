package com.AuroraByteSoftware.AuroraDMX.ui.chase;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.AuroraByteSoftware.AuroraDMX.MainActivity;
import com.AuroraByteSoftware.AuroraDMX.chase.ChaseClickListener;
import com.AuroraByteSoftware.AuroraDMX.chase.ChaseObj;

/**
 * One cue item/ button on the cue sheet
 */
class ChaseGridCell extends BaseAdapter {
    private final ChaseActivity mContext;

    ChaseGridCell(ChaseActivity c) {
        mContext = c;
    }

    @Override
    public int getCount() {
        return MainActivity.alChases.size();
    }

    @Override
    public Object getItem(int position) {
        return this;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    //Called when scrolling
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChaseObj chase = MainActivity.alChases.get(position);

        if (convertView != null
                && chase != null
                && chase.getButton() != null
                && chase.getButton().equals(convertView)
                && chase.getButton().getButton().getText().equals(chase.getName())) {
            return convertView;
        }

        //reuse the button to save memory
        if (chase != null && chase.getButton() != null && chase.getButton().getParent() instanceof GridView) {
            return chase.getButton();
        }
//        Log.v(getClass().getSimpleName(), "Adding cue button " + chase.getName() + " was " + (convertView != null ? ((Button) convertView).getText() : "null"));

        ChaseButton button = ChaseClickListener.makeButton(chase, mContext);
        chase.setButton(button);
        return button;
    }



}