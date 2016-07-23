package com.AuroraByteSoftware.AuroraDMX.fixture;

import android.widget.LinearLayout;
import android.widget.Toast;

import com.AuroraByteSoftware.AuroraDMX.ChPatch;
import com.AuroraByteSoftware.AuroraDMX.CueObj;
import com.AuroraByteSoftware.AuroraDMX.MainActivity;

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

        LinearLayout viewGroup = fixture.getViewGroup();
        fixture.removeSelector();
        Fixture rgbFixture = new RGBFixture(context, fixture.getChText(), viewGroup);

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

        LinearLayout viewGroup = fixture.getViewGroup();
        fixture.removeSelector();
        Fixture standardFixture = new StandardFixture(context, fixture.getChText(), viewGroup);

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
     */
    public static List<Pair<String, Integer>> getParsedValuePresets(String presetValue) {
        if (StringUtils.isEmpty(presetValue)) {
            return null;
        }
        ArrayList<Pair<String, Integer>> presets = new ArrayList<>();
        String[] split = presetValue.split(";");
        for (int i = 0; i < split.length; i++) {
            String[] row = split[i].split(":");
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

            presets.add(new ImmutablePair<>(name, value));
        }

        if (presets.isEmpty()) {
            return null;
        }

        return presets;
    }

}
