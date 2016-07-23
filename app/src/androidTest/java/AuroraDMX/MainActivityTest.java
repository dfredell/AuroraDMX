package AuroraDMX;

import com.AuroraByteSoftware.AuroraDMX.ChPatch;
import com.AuroraByteSoftware.AuroraDMX.MainActivity;
import com.AuroraByteSoftware.AuroraDMX.fixture.Fixture;
import com.AuroraByteSoftware.AuroraDMX.test.DummyFixture;

import junit.framework.TestCase;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Test methods on the MainActivity
 * Created by Dan on 12/6/15.
 */
public class MainActivityTest extends TestCase {

    private MainActivity mainActivity;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mainActivity = new MainActivity();
        MainActivity.alColumns = new ArrayList<>();
        MainActivity.patchList = new ArrayList<>();
    }

    public void testGetCurrentDimmerLevels() throws Exception {
        setUpOneToOnePatch();
        MainActivity.alColumns.clear();
        addDummyRGBFixture();

        List<Integer> expectedOutput = new ArrayList<>(Collections.nCopies(512, 0));
        expectedOutput.set(0, 10);
        expectedOutput.set(1, 20);
        expectedOutput.set(2, 30);

        assertEquals(arrayToString(expectedOutput), Arrays.toString(MainActivity.getCurrentDimmerLevels()));

        MainActivity.patchList.get(3).addDimmer(4);
        expectedOutput.set(3, 30);
        assertEquals(arrayToString(expectedOutput), Arrays.toString(MainActivity.getCurrentDimmerLevels()));

    }

    private String arrayToString(List<Integer> expectedOutput) {
        return Arrays.toString(ArrayUtils.toPrimitive(expectedOutput.toArray(new Integer[expectedOutput.size()])));
    }

    public void testOneToOnePatch() {
        setUpOneToOnePatch();
        for (int i = 0; i < MainActivity.patchList.size(); i++) {
            assertEquals(1, MainActivity.patchList.get(i).getDimmers().size());
            assertEquals((Integer) i, MainActivity.patchList.get(i).getDimmers().get(0));
        }
    }

    private void addDummyRGBFixture() {
        Fixture rgbFixture = new DummyFixture(mainActivity, "Test RGB 1");
        MainActivity.alColumns.add(rgbFixture);

        List<Integer> levels = new ArrayList<>(3);
        levels.add(10);
        levels.add(20);
        levels.add(30);

        rgbFixture.setChLevels(levels);

    }

    private void setUpOneToOnePatch() {
        MainActivity.patchList.clear();
        for (int i = 0; i < 10; i++) {
            MainActivity.patchList.add(new ChPatch(i));
        }
    }
}