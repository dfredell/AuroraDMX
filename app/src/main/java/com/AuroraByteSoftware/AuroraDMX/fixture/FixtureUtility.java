package com.AuroraByteSoftware.AuroraDMX.fixture;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.AuroraByteSoftware.AuroraDMX.ChPatch;
import com.AuroraByteSoftware.AuroraDMX.CueObj;
import com.AuroraByteSoftware.AuroraDMX.MainActivity;
import com.AuroraByteSoftware.AuroraDMX.R;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Modify fixtures.
 * Created by furtchet on 11/14/15.
 */
public class FixtureUtility {

    /**
     * Migrate fixture from Standard to RGB
     *
     * @param fixture Standard
     * @param context mainactivity
     */
    public static void switchToRGB(Fixture fixture, MainActivity context) {
        List<Fixture> alColumns = MainActivity.getAlColumns();
        int indexOf = alColumns.indexOf(fixture);

        //Make sure we aren't putting channels past 512
        int currentDimCount = 0;
        for (ChPatch patch : MainActivity.patchList) {
            currentDimCount += patch.getDimmers().size();
        }
        if(currentDimCount>MainActivity.MAX_DIMMERS){
            Toast.makeText(context, "Can not exceed 512 channels", Toast.LENGTH_SHORT).show();
            return;
        }

        MainActivity.setUpdatingFixtures(true);

        final List<ChPatch> patchList = MainActivity.patchList;
        patchList.add(new ChPatch(patchList.size()));
        patchList.add(new ChPatch(patchList.size()));

        RelativeLayout viewGroup = fixture.getViewGroup();
        Fixture rgbFixture = new RGBFixture(context, fixture.getChText());

        //Remove and add the new view
        LinearLayout fixtureView = (LinearLayout) context.findViewById(R.id.ChanelLayout);
        fixtureView.addView(rgbFixture.getViewGroup(),fixtureView.indexOfChild(viewGroup));
        fixtureView.removeView(viewGroup);

        alColumns.set(indexOf, rgbFixture);

        context.recalculateFixtureNumbers();

        //Add Red and Green levels to cues at 0
        for (CueObj cue : MainActivity.alCues) {
            cue.getLevels().add(indexOf, 0);
            cue.getLevels().add(indexOf, 0);
        }

        MainActivity.setUpdatingFixtures(false);
    }

    /**
     * Migrate fixture from RGB to Standard
     *
     * @param fixture RGB
     * @param context mainactivity
     */
    public static void switchToStandard(Fixture fixture, MainActivity context) {
        List<Fixture> alColumns = MainActivity.getAlColumns();
        int indexOf = alColumns.indexOf(fixture);
        MainActivity.setUpdatingFixtures(true);

        RelativeLayout viewGroup = fixture.getViewGroup();
        Fixture standardFixture = new StandardFixture(context, fixture.getChText(), "");

        //Remove and add the new view
        LinearLayout fixtureView = (LinearLayout) context.findViewById(R.id.ChanelLayout);
        fixtureView.addView(standardFixture.getViewGroup(),fixtureView.indexOfChild(viewGroup));
        fixtureView.removeView(viewGroup);

        alColumns.set(indexOf, standardFixture);

        context.recalculateFixtureNumbers();
        //Remove Red and Green levels from cues
        for (CueObj cue : MainActivity.alCues) {
            cue.getLevels().remove(indexOf);
            cue.getLevels().remove(indexOf);
        }

        final List<ChPatch> patchList = MainActivity.patchList;
        patchList.remove(patchList.size() - 1);
        patchList.remove(patchList.size() - 1);

        MainActivity.setUpdatingFixtures(false);
    }


    /**
     * Get parsed list of preset values
     * <p>
     * Input String format:
     * <p>
     * Name 1:10;Name 2:20;200
     * <p>
     * Returns null if there are no (valid) presets
     *
     * @return null|List
     * @param presetValue string input to parse
     * @param regex validation used for the value
     */
    static List<Pair<String, String>> getParsedValuePresets(String presetValue, String regex) {
        if (StringUtils.isEmpty(presetValue)) {
            return null;
        }
        ArrayList<Pair<String, String>> presets = new ArrayList<>();
        String[] pairsList = presetValue.split(";");
        for (String aSplit : pairsList) {
            String[] row = aSplit.split(":");
            if (row.length == 0) {
                continue; // error: entry does not contain anything
            }
            String name;
            String value;
            if (row.length == 1) {
                name = row[0].trim();
                value = row[0].trim();
            } else {
                name = row[0].trim();
                value = row[1].trim();
            }

            if (!value.matches(regex)) {
                continue; // error: invalid value
            }

            presets.add(new ImmutablePair<>(name, value));
        }

        if (presets.isEmpty()) {
            return null;
        }

        return presets;
    }

}
