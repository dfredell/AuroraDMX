package com.AuroraByteSoftware.AuroraDMX.fixture;

import android.widget.LinearLayout;

import com.AuroraByteSoftware.AuroraDMX.MainActivity;

import java.util.List;

/**
 * Created by furtchet on 11/14/15.
 */
public class FixtureUtility {

    public static void switchToRGB(Fixture fixture, MainActivity context) {
        List<Fixture> alColumns = MainActivity.getAlColumns();
        int indexOf = alColumns.indexOf(fixture);

        LinearLayout viewGroup = fixture.getViewGroup();
        fixture.removeSelector();
        Fixture rgbFixture = new RGBFixture(context,"",viewGroup);

        alColumns.set(indexOf,rgbFixture);

        context.recalculateFixtureNumbers();
    }

    public static void switchToStandard(Fixture fixture, MainActivity context) {
        List<Fixture> alColumns = MainActivity.getAlColumns();
        int indexOf = alColumns.indexOf(fixture);

        LinearLayout viewGroup = fixture.getViewGroup();
        fixture.removeSelector();
        Fixture standardFixture = new StandardFixture(context,"",viewGroup);

        alColumns.set(indexOf,standardFixture);

        context.recalculateFixtureNumbers();
    }
}
