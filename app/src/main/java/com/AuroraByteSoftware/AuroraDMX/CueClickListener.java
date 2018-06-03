package com.AuroraByteSoftware.AuroraDMX;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.AuroraByteSoftware.AuroraDMX.ui.EditCueMenu;

import java.util.List;

/**
 * Listen to click events on the cue list
 * Created by furtchet on 12/6/15.
 */
public class CueClickListener implements View.OnClickListener, View.OnLongClickListener {
    private Context context;
    private Button button;

    /**
     * Cue Click handler
     */
    @Override
    public void onClick(View arg0) {
        if (arg0 instanceof Button) {
            button = (Button) arg0;
        }
        if (button == null || button.getContext() == null) {
            Log.e(getClass().getSimpleName(), "Cue button onclick had a null context");
            return;
        }
        context = button.getContext();
        int curCue = -1;// Current cue number on alCues scale
        for (int x = 0; x < MainActivity.alCues.size(); x++) {
            if (button == MainActivity.alCues.get(x).getButton()) {
                curCue = x;
                break;
            }
        }

        if (curCue == -1) {// Adding a new cue
            createCue(button, MainActivity.getCurrentChannelArray(), -1);
        } else {
            // ======= Loading a cue ========
            Log.d(getClass().getSimpleName(), "oldChLevels " + MainActivity.getCurrentChannelArray());
            MainActivity.getCueFade().startCueFade(MainActivity.alCues.get(curCue));
        }
    }


    /**
     * Cue Long Click handler
     */
    @Override
    public boolean onLongClick(View buttonView) {
        boolean buttonIsAddCue = true;
        for (CueObj cue : MainActivity.alCues) {
            if (cue.getButton() == buttonView) {
                buttonIsAddCue = false;
            }
        }
        if (!buttonIsAddCue) {
            EditCueMenu.createEditCueMenu(MainActivity.alCues, (Button) buttonView, false);
        }
        return true;
    }


    /**
     * Adds a new cue with the current ch Levels
     *  @param button   of "Add Cue"
     * @param chLevels level of channels
     */
    private void createCue(Button button, List<Integer> chLevels, int fadeTime) {
        // Rename the old button to Cue #

        try {
            fadeTime = (fadeTime == -1) ? Integer.parseInt(MainActivity.getSharedPref().getString("fade_up_time", "5")) : fadeTime;
        } catch (Throwable t) {
            t.printStackTrace();
            Toast.makeText(context, R.string.errNumConv, Toast.LENGTH_SHORT).show();
        }
        String name = String.format(context.getString(R.string.cue), Integer.toString(MainActivity.alCues.size() + 1));

        button.setText(name);
        // Add cue name
        MainActivity.alCues.add(new CueObj(name, fadeTime, chLevels, button));
        MainActivity.cueCount++;

        // create a new "Add Cue" button
        ((LinearLayout) button.getParent()).addView(makeButton(context.getString(R.string.AddCue), context));
    }


    public static Button makeButton(String name, Context context) {
        Button button = new Button(context);
        button.setText(name);
        button.setOnClickListener(new CueClickListener());
        button.setLongClickable(true);
        button.setOnLongClickListener(new CueClickListener());
        return button;
    }
}
