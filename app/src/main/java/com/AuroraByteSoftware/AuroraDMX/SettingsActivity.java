package com.AuroraByteSoftware.AuroraDMX;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.AuroraByteSoftware.AuroraDMX.billing.Billing;
import com.AuroraByteSoftware.AuroraDMX.billing.ClientStateListener;
import com.AuroraByteSoftware.AuroraDMX.network.SendArtnetPoll;
import com.AuroraByteSoftware.AuroraDMX.ui.ManualServerIP;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.List;

import fr.azelart.artnetstack.domain.artpollreply.ArtPollReply;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    public static final String channels = "channels";
    public static final String manualserver = "manualserver";
    public static final String serveraddress = "serveraddress";
    public static final String restoredefaults = "restoredefaults";
    private static Thread t;
    private static SettingsActivity settings;
    private static Billing billing = new Billing();

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBuildHeaders(List<Header> target) {
        String protocol = MainActivity.getSharedPref().getString("select_protocol", "");
        if ("SACNUNI".equals(protocol)) {
            loadHeadersFromResource(R.xml.pref_headers_sacn_unicast, target);
        } else if ("SACN".equals(protocol)) {
            loadHeadersFromResource(R.xml.pref_headers_sacn, target);
        } else {
            loadHeadersFromResource(R.xml.pref_headers_artnet, target);
        }
        settings = this;
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static final Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(
                preference,
                PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(
                        preference.getKey(), ""));
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            billing.setup(getActivity());
            addPreferencesFromResource(R.xml.pref_general);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("fade_up_time"));
//            bindPreferenceSummaryToValue(findPreference("fade_down_time"));
            bindPreferenceSummaryToValue(findPreference(channels));
            bindPreferenceSummaryToValue(findPreference("chase_fade_time"));
            bindPreferenceSummaryToValue(findPreference("chase_wait_time"));


            // The content view embeds two fragments; now retrieve them and attach
            // their "hide" button.


            findPreference("unlock_channels").setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    //open browser or intent here
                    Log.i(getClass().getSimpleName(), "unlock_channels");
                    if (billing != null &&
                            billing.getBillingClient() != null) {
                        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                .setSku(ClientStateListener.ITEM_SKU)
                                .setType(BillingClient.SkuType.INAPP) // SkuType.SUB for subscription
                                .build();
                        int responseCode = billing.getBillingClient()
                                .launchBillingFlow(getActivity(), flowParams);
                    }
                    return true;
                }
            });

            findPreference("select_protocol").setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    settings.invalidateHeaders();//loads the header with only one protocol
                    return true;
                }
            });
        }
    }


    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    public static class ArtnetPreferenceFragment extends PreferenceFragment {
        CheckBoxPreference checkboxPrefManual;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_artnet);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            // bindPreferenceSummaryToValue(findPreference("ip_address"));
            setHasOptionsMenu(true);

            checkboxPrefManual = (CheckBoxPreference) findPreference("checkboxPrefManual");
            Context context = getActivity();
            if (context == null) {
                return;
            }
            final String server = PreferenceManager.getDefaultSharedPreferences(context).getString(SettingsActivity.manualserver, "192.168.0.0:0");
            checkboxPrefManual.setSummary(server);
            // Set listener
            checkboxPrefManual.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    checkboxPrefManual.setChecked(true);
                    PreferenceCategory targetCategory = (PreferenceCategory) findPreference("targetCategory");
                    ArrayList<CheckBoxPreference> list = getPreferenceList(targetCategory,
                            new ArrayList<CheckBoxPreference>());
                    for (CheckBoxPreference p : list) {
                        p.setChecked(false);// Uncheck the other boxes
                    }
                    if (newValue.equals(true))// Only ask for text when checking
                    // not unchecking box
                    {
                        ManualServerIP.askForString(getActivity(), checkboxPrefManual);
                    }
                    return true;
                }
            });
            refreshServers();
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.server, menu);
            menu.findItem(R.id.menu_server_refresh).setIcon(
                    new IconDrawable(this.getActivity(), FontAwesomeIcons.fa_refresh)
                            .colorRes(R.color.white)
                            .alpha(204)
                            .actionBarSize());
        }

        /**
         * Event Handling for Individual menu item selected Identify single menu
         * item by it's id
         */
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {

            switch (item.getItemId()) {
                case R.id.menu_server_refresh:
                    refreshServers();
            }
            return true;
        }

        public void refreshServers() {
            Log.d(getClass().getSimpleName(), "Refresh");
            PreferenceCategory targetCategory = (PreferenceCategory) findPreference("targetCategory");
            if (targetCategory != null) {
                targetCategory.removeAll();
            }
            MainActivity.progressDialog = ProgressDialog.show(getActivity(), "",
                    "Searching for ArtNet devices...");
            SendArtnetPoll poll = new SendArtnetPoll();
            poll.setContext(getActivity().getApplicationContext());
            t = new Thread(poll);
            t.start();// Start the scan thread

            // Start another thread to wait for t to finish
            (new Thread() {
                public void run() {
                    while (t.isAlive()) {
                        try {
                            t.join(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    finishedSearch();
                }
            }).start();
        }

        /**
         * Called when we are done waiting for an ArtnetPoll to come back. This
         * updates the settings UI with the server list.
         */
        public void finishedSearch() {
            // fetch the item where you wish to insert the CheckBoxPreference,
            // in this case a PreferenceCategory with key "targetCategory"
            PreferenceCategory targetCategory = (PreferenceCategory) findPreference("targetCategory");

            ArrayList<ArtPollReply> foundServers = MainActivity.foundServers;
            for (int i1 = 0; i1 < foundServers.size(); i1++) {
                ArtPollReply artPollReply = foundServers.get(i1);
                for (int i = 0; i < artPollReply.getOutputStatus().length; i++) {
                    if (artPollReply.getOutputStatus()[i].dataTransmitted) {
                        // create one check box for each setting you need
                        String ipPort = artPollReply.getIp() + ":" + artPollReply.getOutputSubswitch()[i];
                        Context context = getActivity();
                        if (context == null) {
                            return;
                        }
                        CheckBoxPreference checkBoxPreference = new CheckBoxPreference(context);
                        checkBoxPreference.setTitle(ipPort);
                        checkBoxPreference.setSummary(artPollReply.getShortName());
                        checkBoxPreference.setKey("keyName" + ipPort);// make sure each key
                        // is unique
                        checkBoxPreference.setChecked(false);

                        targetCategory.addPreference(checkBoxPreference);
                    }
                }

            }

            ArrayList<CheckBoxPreference> list = getPreferenceList(targetCategory,
                    new ArrayList<CheckBoxPreference>());
            for (final CheckBoxPreference p : list) {
                // Set listener
                p.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        p.setChecked(true);
                        PreferenceCategory targetCategory = (PreferenceCategory) findPreference("targetCategory");
                        ArrayList<CheckBoxPreference> list = getPreferenceList(targetCategory,
                                new ArrayList<CheckBoxPreference>());
                        for (CheckBoxPreference others : list) {
                            if (!others.getKey().equalsIgnoreCase(p.getKey())) {
                                others.setChecked(false);// Uncheck the other boxes
                            }
                        }
                        if (preference instanceof CheckBoxPreference)//Set server IP
                        {
                            MainActivity.getSharedPref().edit().putString(SettingsActivity.serveraddress, (String) preference.getTitle()).apply();
                        }


                        checkboxPrefManual = (CheckBoxPreference) findPreference("checkboxPrefManual");
                        checkboxPrefManual.setChecked(false);
                        return true;
                    }

                });

            }
        }

        private ArrayList<CheckBoxPreference> getPreferenceList(Preference p,
                                                                ArrayList<CheckBoxPreference> list) {
            if (p instanceof PreferenceCategory || p instanceof PreferenceScreen) {
                PreferenceGroup pGroup = (PreferenceGroup) p;
                int pCount = pGroup.getPreferenceCount();
                for (int i = 0; i < pCount; i++) {
                    getPreferenceList(pGroup.getPreference(i), list); // recursive
                    // call
                }
            } else {
                list.add((CheckBoxPreference) p);
            }
            return list;
        }
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    public static class SacnPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_sacn);
            bindPreferenceSummaryToValue(findPreference("protocol_sacn_universe"));

        }
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    public static class SacnUnicastPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_sacn_unicast);
            bindPreferenceSummaryToValue(findPreference("protocol_sacn_universe"));
            bindPreferenceSummaryToValue(findPreference("protocol_sacn_unicast_ip"));
        }
    }

    /**
     * Required to be Android API19 compliant
     * http://securityintelligence.com/new-vulnerability-android-framework-fragment-injection/
     *
     * @param fragmentName class name
     * @return true if valid
     */
    protected boolean isValidFragment(String fragmentName) {
        if (GeneralPreferenceFragment.class.getName().equals(fragmentName)) {
            return true;
        }
        if (SacnPreferenceFragment.class.getName().equals(fragmentName)) {
            return true;
        }
        if (ArtnetPreferenceFragment.class.getName().equals(fragmentName)) {
            return true;
        }
        return SacnUnicastPreferenceFragment.class.getName().equals(fragmentName);
    }
}
