package com.AuroraByteSoftware.AuroraDMX.ui;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.AuroraByteSoftware.AuroraDMX.CueClickListener;
import com.AuroraByteSoftware.AuroraDMX.CueObj;
import com.AuroraByteSoftware.AuroraDMX.CueSheetClickListener;
import com.AuroraByteSoftware.AuroraDMX.MainActivity;

import java.util.Random;

/**
 * One cue item/ button on the cue sheet
 */
class CueGridCell extends BaseAdapter {
    private final CueActivity mContext;
    private int count = 0;
    private static final String TAG = "AuroraDMX";

    public CueGridCell(CueActivity c, int a_count) {
        mContext = c;
        count = a_count;
    }

    @Override
    public int getCount() {
        return count;
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
        CueObj cue = MainActivity.alCues.get(position);

        if(convertView!=null && cue!=null && cue.getButton() != null && cue.getButton().equals(convertView)){
            return convertView;
        }

        //reuse the button to save memory
        if (cue.getButton() !=null && cue.getButton().getParent() instanceof GridView){
            return cue.getButton();
        }
        Log.v(TAG,"Adding cue button " + cue.getCueName() + " was " + (convertView!=null?((Button)convertView).getText():"null"));

        Button button = CueClickListener.makeButton(cue.getCueName(), mContext);
        button.setOnClickListener(new CueSheetClickListener());
        button.setId(new Random().nextInt());
        cue.setButton(button);
        cue.refreshHighlight();
        return button;
    }



}