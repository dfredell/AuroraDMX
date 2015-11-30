package com.AuroraByteSoftware.AuroraDMX.fixture;

import android.widget.LinearLayout;

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
     * @param fixture Standard
     * @param context mainactivity
     */
    public static void switchToRGB(Fixture fixture, MainActivity context) {
        List<Fixture> alColumns = MainActivity.getAlColumns();
        int indexOf = alColumns.indexOf(fixture);

        LinearLayout viewGroup = fixture.getViewGroup();
        fixture.removeSelector();
        Fixture rgbFixture = new RGBFixture(context, fixture.getChText(), viewGroup);

        alColumns.set(indexOf, rgbFixture);

        context.recalculateFixtureNumbers();
        rgbFixture.updateFixtureLevelText();
        //Add Red and Green levels at 0
        for (CueObj cue : MainActivity.alCues) {
            cue.getLevels().add(indexOf, 0);
            cue.getLevels().add(indexOf, 0);
        }
    }

    /**
     * Migrate fixture from RGB to Standard
     * @param fixture RGB
     * @param context mainactivity
     */
    public static void switchToStandard(Fixture fixture, MainActivity context) {
        List<Fixture> alColumns = MainActivity.getAlColumns();
        int indexOf = alColumns.indexOf(fixture);

        LinearLayout viewGroup = fixture.getViewGroup();
        fixture.removeSelector();
        Fixture standardFixture = new StandardFixture(context, fixture.getChText(), viewGroup);

        alColumns.set(indexOf, standardFixture);

        context.recalculateFixtureNumbers();
        standardFixture.updateFixtureLevelText();
        //Remove Red and Green levels
        for (CueObj cue : MainActivity.alCues) {
            cue.getLevels().remove(indexOf);
            cue.getLevels().remove(indexOf);
        }
    }
}
