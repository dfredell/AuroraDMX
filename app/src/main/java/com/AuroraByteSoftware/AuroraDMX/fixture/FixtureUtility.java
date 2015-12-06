package com.AuroraByteSoftware.AuroraDMX.fixture;

import android.widget.LinearLayout;

import com.AuroraByteSoftware.AuroraDMX.ChPatch;
import com.AuroraByteSoftware.AuroraDMX.CueObj;
import com.AuroraByteSoftware.AuroraDMX.MainActivity;

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
}
