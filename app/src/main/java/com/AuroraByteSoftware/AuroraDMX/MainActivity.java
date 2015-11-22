package com.AuroraByteSoftware.AuroraDMX;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.AuroraByteSoftware.AuroraDMX.fixture.Fixture;
import com.AuroraByteSoftware.AuroraDMX.fixture.RGBFixture;
import com.AuroraByteSoftware.AuroraDMX.fixture.StandardFixture;
import com.AuroraByteSoftware.AuroraDMX.network.SendArtnetUpdate;
import com.AuroraByteSoftware.AuroraDMX.network.SendSacnUpdate;
import com.AuroraByteSoftware.AuroraDMX.ui.EditCueMenu;
import com.AuroraByteSoftware.AuroraDMX.ui.PatchActivity;
import com.AuroraByteSoftware.Billing.util.IabException;
import com.AuroraByteSoftware.Billing.util.IabHelper;
import com.AuroraByteSoftware.Billing.util.IabResult;
import com.AuroraByteSoftware.Billing.util.Inventory;

import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class MainActivity extends Activity implements OnClickListener, OnLongClickListener, OnSharedPreferenceChangeListener {

    public static List<Fixture> alColumns = null;
    static ArrayList<CueObj> alCues = null;
    private static SharedPreferences sharedPref;
    static Double cueCount = 1.0;// cueCount++ = new cue num
    public static DatagramSocket clientSocket = null;
    private int orgColor = 0;
    private Timer ArtNet;
    private Timer SACN;
    private Timer SACNUnicast;
    public static final ArrayList<String> foundServers = new ArrayList<>();
    public static ProgressDialog progressDialog = null;
    public static final int ALLOWED_PATCHED_DIMMERS = 50;
    public static final int MAX_DIMMERS = 512;
    public static final int MAX_CHANNEL = 200;
    public static int[][] patch;
    private static final String TAG = "AuroraDMX";
    private static IabHelper mHelper;
    public static final String ITEM_SKU = "unlock_channels";
    private final List<String> listOfSkus = new ArrayList<>();
    private ProjectManagement pm = null;
    private final static String BASE_64_ENCODED_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAj9upoasavmU51/j6g7vWchEf/g2SGuntcXPlzVu8vp3avDGMQp8E20iI+IO5vqB4wVKf9QRiAv0DFLw+XAGCpx7t6GDt4Sd/qMOkj49Eas1R1Uvghp4yy9Cc/8pL7QOvSW99pq9Pg2iqqbPXlAlLmByQy2p9qhDhl788dMZsUd2VxL5NHY2zQl7a1emWH/MUpvVHNSJkTSdQrLJ4cruTvEDldtD0jSNadK1NSruwa/BH6ieLVswek1cyE7hm0Od5pWw0XCpkR6L7ZkEkeTovSihA3h+rSy6kxZCqrDzMR++EOCxwS/kB3Ly6M5E6EwjZVbK18UQM8/Ecr7/buYxalQIDAQAB";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pm = new ProjectManagement(this);
        // System.out.println("onCreate");
        startupIAB();
        startup();
        pm.open(null);
        setUpNetwork();
    }

    private void startupIAB() {
        mHelper = new IabHelper(this, BASE_64_ENCODED_PUBLIC_KEY);
        mHelper.enableDebugLogging(true, "IabHelper");
        try {
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {
                    Log.d(TAG, "Setup finished.");
                    if (!result.isSuccess()) {
                        // Oh noes, there was a problem.
                        // complain("Problem setting up in-app billing: " + result);
                        return;
                    }
                    // Have we been disposed of in the meantime? If so, quit.
                    if (mHelper == null)
                        return;
                    // IAB is fully set up. Now, let's get an inventory of stuff
                    // we own.
                    Log.d(TAG, "Setup successful. Querying inventory.");
                    listOfSkus.add(ITEM_SKU);
                    mHelper.queryInventoryAsync(true, listOfSkus, mQueryFinishedListener);
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
            mHelper = null;
            Toast.makeText(MainActivity.this, R.string.errorProcessPurchases, Toast.LENGTH_SHORT).show();
        }
    }

    private void startup() {
        // load the layout
        setContentView(R.layout.activity_main);
        // Get the MainLayout from the XML
        Button button = (Button) findViewById(R.id.AddCueButton);
        button.setOnClickListener(this);
        button.setOnLongClickListener(this);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(this);
        alCues = new ArrayList<>();
        alColumns = new ArrayList<>();
        int number_channels = Integer.parseInt(sharedPref.getString(SettingsActivity.channels, "5"));
        setNumberOfChannels(number_channels, null, null);
    }

    public static SharedPreferences getSharedPref() {
        return sharedPref;
    }

    void setUpNetwork() {
        // System.out.println("SetupNetwork ServerAddress: "+sharedPref.getString(SettingsActivity.serveraddress,
        // ""));
        String protocol = getSharedPref().getString("select_protocol", "");

        if (clientSocket != null)
            clientSocket.close();
        if (SACNUnicast != null)
            SACNUnicast.cancel();
        if (SACN != null)
            SACN.cancel();
        if (ArtNet != null)
            ArtNet.cancel();

        if ("SACNUNI".equals(protocol)) {
            SACNUnicast = new Timer();
            SACNUnicast.scheduleAtFixedRate(new SendSacnUpdate(this), 200, 100);
        } else if ("SACN".equals(protocol)) {
            SACN = new Timer();
            SACN.scheduleAtFixedRate(new SendSacnUpdate(this), 200, 100);
        } else {
            ArtNet = new Timer();
            ArtNet.scheduleAtFixedRate(new SendArtnetUpdate(this), 200, 100);
        }

    }

    void setNumberOfChannels(int number_channels, String[] channelNames, boolean[] isRGB) {
        // check for app purchace
        boolean paid = true;
        try {
            if (null != mHelper) {
                Inventory inv = mHelper.queryInventory(false, listOfSkus);
                paid = inv.hasPurchase(ITEM_SKU);
            } else {
                paid = false;
            }
        } catch (IabException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException | NullPointerException e) {
            // Do nothing we must not be connected yet
        }

        // Input cleansing
        if (number_channels > MAX_CHANNEL) {
            Toast.makeText(MainActivity.this, R.string.dmxRangeError, Toast.LENGTH_SHORT).show();
            number_channels = MAX_CHANNEL;
        } else if (number_channels < 1) {
            Toast.makeText(MainActivity.this, R.string.dmxRangeError, Toast.LENGTH_SHORT).show();
            number_channels = 1;
        } else if (number_channels > 5 && !paid) {
            Toast.makeText(MainActivity.this, R.string.dmxRangePurchaseLimit, Toast.LENGTH_SHORT).show();
            number_channels = 5;
        }

        int change = number_channels - alColumns.size();
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.ChanelLayout);

        orgColor = Color.parseColor(getSharedPref().getString("channel_color", "#ffcc00"));
        if (change > 0) {// Adding channels
            for (int x = (number_channels - change); x < number_channels && x < 512; x++) {
                if (isRGB != null && isRGB[x])
                    alColumns.add(new RGBFixture(this, channelNames == null ? null : channelNames[x]));
                else
                    alColumns.add(new StandardFixture(this, channelNames == null ? null : channelNames[x]));
                mainLayout.addView(alColumns.get(x).getViewGroup());
            }
            for (CueObj cue : alCues) {// Pad ch's in cues
                cue.padChannels(number_channels);
            }

            // Update Patch
            incrementPatch(number_channels);

        } else if (change < 0) {// Removing channels
            for (int x = (number_channels - change); x > number_channels && x < 512; x--) {
                mainLayout.removeView(alColumns.get(x - 1).getViewGroup());
                alColumns.remove(x - 1);
            }
            for (CueObj cue : alCues) {
                cue.padChannels(number_channels);
            }
            patch = new int[MAX_DIMMERS + 1][ALLOWED_PATCHED_DIMMERS];

        }

        //Reset all the levels to display the percentage or step value
        for (Fixture alColumn : alColumns) {
            alColumn.setChLevels(alColumn.getChLevels());
        }

        recalculateFixtureNumbers();

        getSharedPref().edit().putString(SettingsActivity.channels, String.format("%1$s", number_channels)).apply();
    }

    private void incrementPatch(int number_channels) {
        boolean forceOneToOne = false;
        if (patch == null) {
            patch = new int[MAX_DIMMERS + 1][ALLOWED_PATCHED_DIMMERS];
            forceOneToOne = true;
        } else {
            patch = new int[MAX_DIMMERS + 1][ALLOWED_PATCHED_DIMMERS];
        }

        for (int i = 0; i < patch.length; i++) {
            if (forceOneToOne || patch[i] == null) {
                patch[i] = new int[ALLOWED_PATCHED_DIMMERS];
                patch[i][0] = i;
            }
        }
    }

    // Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return (super.onCreateOptionsMenu(menu));
    }

    /**
     * Event Handling for Individual menu item selected Identify single menu
     * item by it's id
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean fadingInProgress = false;
        for (CueObj cue : alCues) {
            if (cue.isFadeInProgress())
                fadingInProgress = true;
        }
        if (!fadingInProgress) {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.menu_patch:
                    //Toast.makeText(MainActivity.this, "Patch", Toast.LENGTH_SHORT).show();
                    intent = new Intent(this, PatchActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.menu_settings:
                    intent = new Intent(this, SettingsActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.menu_save:
                    pm.onSaveClick();
                    return true;
                case R.id.menu_load:
                    pm.onLoadClick();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        } else {
            Toast.makeText(MainActivity.this, R.string.waitingOnFade, Toast.LENGTH_SHORT).show();
            return super.onOptionsItemSelected(item);
        }

    }

    protected static List<Integer> getCurrentChannelArray() {
        List<Integer> out = new ArrayList<>();
        for (Fixture fixture : alColumns) {
            out.addAll(fixture.getChLevels());
        }
        return out;
    }

    public static int[] getCurrentDimmerLevels() {
        int out[] = new int[MAX_DIMMERS];

        //flatten the channel values from the UI
        List<Integer> chValues = new ArrayList<>();
        for (Fixture fixture : alColumns) {
            chValues.addAll(fixture.getChLevels());
        }
//patch[channel][goes to dimmers]
        for (int ch = 0; ch < chValues.size(); ch++) {
            for (int i = 0; i < patch[ch].length; i++) {
                int x = patch[ch + 1][i] - 1;
                if (x == -1)
                    continue;
                int oldLvl = out[x];
                int newVal = chValues.get(ch);
                if (oldLvl < newVal)
                    out[x] = newVal;
            }
        }
        return out;
    }

    /**
     * Cue Click handler
     */
    @Override
    public void onClick(View arg0) {
        Button button = null;
        if (arg0 instanceof Button) {
            button = (Button) arg0;
        }
        int curCue = -1;// Current cue number on alCues scale
        boolean otherCueFading = false;
        for (int x = 0; x < alCues.size(); x++) {
            if (button == alCues.get(x).getButton()) {
                curCue = x;
                break;
            }
        }

        if (curCue == -1) {// Adding a new cue
            createCue(button, getCurrentChannelArray(), -1);
        } else {
            // check if anyone else is fading

            // ======= Loading a cue ========
            List<Integer> newChLevels = alCues.get(curCue).getLevels();
            // Find previously active cue
            int prevCueNum = -1;
            for (int x = 0; x < alCues.size(); x++) {
                if (alCues.get(x).getHighlight() > 1 && x != curCue)
                    prevCueNum = x;
                if (alCues.get(x).isFadeInProgress())
                    otherCueFading = true;
            }
            if (!otherCueFading) {
                // Set the channels to the cue
                for (int x = 0; x < alColumns.size() && x < newChLevels.size(); x++) {
                    // If a channel changed value
                    alColumns.get(x).setupIncrementLevelFade(newChLevels.get(x));
                }
                alCues.get(curCue).startCueFade(curCue, prevCueNum);
            } else {
                Toast.makeText(MainActivity.this, R.string.waitingOnFade, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Cue Long Click handler
     */
    @Override
    public boolean onLongClick(View v) {
        boolean buttonIsAddCue = true;
        for (CueObj cue : alCues) {
            if (cue.getButton() == v) {
                buttonIsAddCue = false;
            }
        }
        if (!buttonIsAddCue)
            EditCueMenu.createEditCueMenu(alCues, v, MainActivity.this);
        return true;
    }

    /**
     * Adds a new cue with the current ch Levels
     *
     * @param button   of "Add Cue"
     * @param chLevels set levels
     * @param cueNum   number for the cue
     */
    private void createCue(Button button, List<Integer> chLevels, double cueNum) {
        createCue(button, chLevels, cueNum, String.format(this.getString(R.string.cue), cueNum), -1, -1);
    }

    /**
     * Adds a new cue with the current ch Levels
     *
     * @param button   of "Add Cue"
     * @param chLevels level of channels
     * @param cueNum   cue number
     * @param cueName  the cue name
     */
    private void createCue(Button button, List<Integer> chLevels, double cueNum, String cueName, int fadeUpTime, int fadeDownTime) {
        // Rename the old button to Cue #

        try {
            fadeUpTime = (fadeUpTime == -1) ? Integer.parseInt(getSharedPref().getString("fade_up_time", "5")) : fadeUpTime;
            fadeDownTime = (fadeDownTime == -1) ? Integer.parseInt(getSharedPref().getString("fade_down_time", "5")) : fadeDownTime;
        } catch (Throwable t) {
            t.printStackTrace();
            Toast.makeText(MainActivity.this, R.string.errNumConv, Toast.LENGTH_SHORT).show();
        }
        if (cueNum == -1) {
            String name = String.format(getResources().getString(R.string.cue), cueCount);
            button.setText(name);
            alCues.add(new CueObj(cueCount, name, fadeUpTime, fadeDownTime, chLevels, button));
        } else {
            button.setText(String.format(getResources().getString(R.string.cue), cueNum));
            // Add cue name
            if (cueName.equals("")) {
                alCues.add(new CueObj(cueNum, String.format(getResources().getString(R.string.cue), cueNum), fadeUpTime, fadeDownTime, chLevels, button));
            } else {
                alCues.add(new CueObj(cueNum, cueName, fadeUpTime, fadeDownTime, chLevels, button));
            }
        }
        cueCount++;

        // create a new "Add Cue" button
        ((LinearLayout) findViewById(R.id.CueLine)).addView(makeButton(getString(R.string.AddCue)));
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // System.out.println("Pref Change");
        if (key.equals(SettingsActivity.channels)) {
            setNumberOfChannels(Integer.parseInt(sharedPreferences.getString(SettingsActivity.channels, "5")), null, null);
        }
    }

    protected void onResume() {
        super.onResume();
        // System.out.println("onResume");
        getSharedPref().registerOnSharedPreferenceChangeListener(this);

        if (getSharedPref().getBoolean(SettingsActivity.restoredefaults, false)) {
            restoreDefaults();
            getSharedPref().edit().putBoolean(SettingsActivity.restoredefaults, false).apply();
        }
        // Change the color
        int color = Color.parseColor(getSharedPref().getString("channel_color", "#ffcc00"));
        if (color != orgColor) {// update the channel color
            for (Fixture col : alColumns) {
                col.setScrollColor(color);
            }
        }
        // Change the num of ch.
        int number_channels = 5;
        try {
            number_channels = Integer.parseInt(getSharedPref().getString(SettingsActivity.channels, "5"));
        } catch (Throwable t) {
            Log.w("ExternalStorage", "Error reading channel number", t);
        }
        setNumberOfChannels(number_channels, null, null);
        setUpNetwork();
    }

    @Override
    protected void onPause() {
        if (ArtNet != null) {
            ArtNet.cancel();
            ArtNet.purge();
        }
        if (SACN != null) {
            SACN.cancel();
            SACN.purge();
        }
        if (SACNUnicast != null) {
            SACNUnicast.cancel();
            SACNUnicast.purge();
        }
        if (clientSocket != null)
            clientSocket.close();
        // System.out.println("onPause");
        pm.save(null);
        if (getSharedPref() != null)
            getSharedPref().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    // We're being destroyed. It's important to dispose of the helper here!
    @Override
    public void onDestroy() {
        super.onDestroy();

        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }


    Button makeButton(String name) {
        Button button = new Button(this);
        button.setText(name);
        button.setOnClickListener(this);
        button.setLongClickable(true);
        button.setOnLongClickListener(this);
        return button;
    }

    private void restoreDefaults() {
        // System.out.println("Restoring Defaults");
        // Remove all the preferences
        getSharedPref().edit().clear().apply();
        // Stop ArtNet
        clientSocket.close();
        alCues.clear();
        cueCount = 1.0;
        for (Fixture columns : alColumns) {
            columns.getViewGroup().removeAllViews();
        }
        alColumns.clear();
        ((LinearLayout) findViewById(R.id.ChanelLayout)).removeAllViews();
        // Refresh views
        LinearLayout cueLine = ((LinearLayout) findViewById(R.id.CueLine));
        cueLine.removeAllViews();
        for (CueObj cue : alCues) {
            // create a new "Add Cue" button
            cue.setButton(makeButton(cue.getCueName()));
            cueLine.addView(cue.getButton());
            cue.setHighlight(0, 0, 0);
        }
        PatchActivity.patchOneToOne();

        // create a new "Add Cue" button
        ((LinearLayout) findViewById(R.id.CueLine)).addView(makeButton(getString(R.string.AddCue)));
        setUpNetwork();
    }

    /**
     * @return the mHelper
     */
    public static IabHelper getmHelper() {
        return mHelper;
    }

    // Listener that's called when we finish querying the items and
    // subscriptions we own
    private static final IabHelper.QueryInventoryFinishedListener mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            if (result.isFailure()) {
                // handle error
                System.err.print(result.getMessage());
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if (mHelper != null && !mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.i(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    public void recalculateFixtureNumbers() {
        int currentFixtureNum = 1;
        for (Fixture fixture : alColumns) {
            fixture.setFixtureNumber(currentFixtureNum);
            currentFixtureNum += fixture.getChLevels().size();
        }
    }

    public static List<Fixture> getAlColumns() {
        return alColumns;
    }
}
