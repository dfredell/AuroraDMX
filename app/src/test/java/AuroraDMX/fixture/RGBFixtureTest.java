package AuroraDMX.fixture;

import com.AuroraByteSoftware.AuroraDMX.fixture.RGBFixture;

import junit.framework.TestCase;

import java.util.List;

import static org.junit.Assert.assertNotEquals;

/**
 * Test static methods in RGBFixture
 * Created by Dan on 7/23/16.
 */
public class RGBFixtureTest extends TestCase {

    /**
     * Validate the user input RGB parsing utility
     *
     * @throws Exception
     */
    public void testSplitLevels() throws Exception {

        List<Integer> integers = RGBFixture.splitLevels("12,34,56");
        assertNotNull("Parse success", integers);
        assertEquals("Parse success", 3, integers.size());
        assertEquals("Red", Integer.valueOf(12), integers.get(0));
        assertEquals("Green", Integer.valueOf(34), integers.get(1));
        assertEquals("Blue", Integer.valueOf(56), integers.get(2));
        assertNotEquals("Blue Bad", Integer.valueOf(57), integers.get(2));


        integers = RGBFixture.splitLevels("ffABa2");
        assertNotNull("Parse success", integers);
        assertEquals("Parse success", 3, integers.size());
        assertEquals("Red", Integer.valueOf(255), integers.get(0));
        assertEquals("Green", Integer.valueOf(171), integers.get(1));
        assertEquals("Blue", Integer.valueOf(162), integers.get(2));
        assertNotEquals("Blue Bad", Integer.valueOf(57), integers.get(2));

        //Verify trim works
        integers = RGBFixture.splitLevels("  ff00a2 ");
        assertNotNull("Parse success", integers);
        assertEquals("Parse success", 3, integers.size());
        assertEquals("Red", Integer.valueOf(255), integers.get(0));
        assertEquals("Green", Integer.valueOf(0), integers.get(1));
        assertEquals("Blue", Integer.valueOf(162), integers.get(2));
        assertNotEquals("Blue Bad", Integer.valueOf(57), integers.get(2));

        //Verify invalid inputs
        integers = RGBFixture.splitLevels("  ff00a ");
        assertNull("Parse unsuccessful", integers);

        integers = RGBFixture.splitLevels("999,23,23");
        assertNull("Parse unsuccessful", integers);

        integers = RGBFixture.splitLevels("gfffff");
        assertNull("Parse unsuccessful", integers);
    }

}