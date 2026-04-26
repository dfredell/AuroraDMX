package com.AuroraByteSoftware.AuroraDMX;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import com.AuroraByteSoftware.AuroraDMX.billing.Billing;
import com.AuroraByteSoftware.AuroraDMX.network.SendArtnetPoll;
import com.AuroraByteSoftware.AuroraDMX.ui.ManualServerIP;
import com.AuroraByteSoftware.AuroraDMX.ui.fontawesome.FontAwesomeIcons;
import com.AuroraByteSoftware.AuroraDMX.ui.fontawesome.FontAwesomeManager;

import java.util.ArrayList;
import java.util.List;

import fr.azelart.artnetstack.domain.artpollreply.ArtPollReply;

public class SettingsActivity extends AppCompatActivity implements
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    public static final String channels = "channels";
    public static final String manualserver = "manualserver";
    public static final String serveraddress = "serveraddress";
    public static final String restoredefaults = "restoredefaults";
    private static Thread t;
    private static SettingsActivity settings;
    private static final Billing billing = new Billing();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new MainSettingsFragment())
                    .commit();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (getSupportFragmentManager().popBackStackImmediate()) {
            return true;
        }
        finish();
        return true;
    }

    @Override
    public boolean onPreferenceStartFragment(@NonNull PreferenceFragmentCompat caller, @NonNull Preference pref) {
        final Bundle args = pref.getExtras();
        final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
                getClassLoader(),
                pref.getFragment());
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings, fragment)
                .addToBackStack(null)
                .commit();
        setTitle(pref.getTitle());
        return true;
    }

    private static final Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = (preference, value) -> {
        String stringValue = value.toString();
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);
            preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
        } else {
            preference.setSummary(stringValue);
        }
        return true;
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        if (preference == null) return;
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, pref.getString(preference.getKey(), ""));
    }

    public static class MainSettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.pref_general);
            setupGeneralLogic();
            updateServerPreference();
        }

        private void setupGeneralLogic() {
            billing.setup(getActivity());
            bindPreferenceSummaryToValue(findPreference("fade_up_time"));
            bindPreferenceSummaryToValue(findPreference(channels));
            bindPreferenceSummaryToValue(findPreference("chase_fade_time"));
            bindPreferenceSummaryToValue(findPreference("chase_wait_time"));
            bindPreferenceSummaryToValue(findPreference("channel_color"));

            Preference selectProtocol = findPreference("select_protocol");
            bindPreferenceSummaryToValue(selectProtocol);
            if (selectProtocol != null) {
                selectProtocol.setOnPreferenceChangeListener((preference, newValue) -> {
                    sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, newValue);
                    PreferenceManager.getDefaultSharedPreferences(getContext())
                            .edit().putString(preference.getKey(), newValue.toString()).commit();
                    updateServerPreference();
                    return true;
                });
            }

            Preference unlockChannels = findPreference("unlock_channels");
            if (unlockChannels != null) {
                unlockChannels.setOnPreferenceClickListener(preference -> {
                    billing.requestPurchase(settings);
                    return true;
                });
            }

            Preference restoreDefaults = findPreference("restore_defaults");
            if (restoreDefaults != null) {
                restoreDefaults.setOnPreferenceClickListener(preference -> {
                    new android.app.AlertDialog.Builder(getActivity())
                            .setTitle(R.string.restore_defaults)
                            .setMessage(R.string.confirm_revert)
                            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                MainActivity.getSharedPref().edit().putBoolean(SettingsActivity.restoredefaults, true).apply();
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .show();
                    return true;
                });
            }
        }

        private void updateServerPreference() {
            Context context = getPreferenceManager().getContext();
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            String protocol = pref.getString("select_protocol", "");

            Preference serverPref = findPreference("dmx_server_settings");
            if (serverPref == null) {
                serverPref = new Preference(context);
                serverPref.setKey("dmx_server_settings");
                serverPref.setOrder(2); // Set order to 2 to place it right below protocol (order 1)
                getPreferenceScreen().addPreference(serverPref);
            }

            if ("SACNUNI".equals(protocol)) {
                serverPref.setTitle(R.string.pref_header_sacn_unicast);
                serverPref.setFragment(SacnUnicastPreferenceFragment.class.getName());
            } else if ("SACN".equals(protocol)) {
                serverPref.setTitle(R.string.pref_header_sacn);
                serverPref.setFragment(SacnPreferenceFragment.class.getName());
            } else {
                serverPref.setTitle(R.string.pref_header_artnet);
                serverPref.setFragment(ArtnetPreferenceFragment.class.getName());
            }
        }
    }

    public static class ArtnetPreferenceFragment extends PreferenceFragmentCompat {
        CheckBoxPreference checkboxPrefManual;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.pref_artnet);
            setHasOptionsMenu(true);

            checkboxPrefManual = findPreference("checkboxPrefManual");
            if (checkboxPrefManual != null) {
                final String server = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(SettingsActivity.manualserver, "192.168.0.0:0");
                checkboxPrefManual.setSummary(server);
                checkboxPrefManual.setOnPreferenceChangeListener((preference, newValue) -> {
                    checkboxPrefManual.setChecked(true);
                    PreferenceCategory targetCategory = findPreference("targetCategory");
                    if (targetCategory != null) {
                        ArrayList<CheckBoxPreference> list = getPreferenceList(targetCategory, new ArrayList<>());
                        for (CheckBoxPreference p : list) p.setChecked(false);
                    }
                    if (newValue.equals(true)) ManualServerIP.askForString(getActivity(), checkboxPrefManual);
                    return true;
                });
            }
            refreshServers();
        }

        @Override
        public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
            inflater.inflate(R.menu.server, menu);
            try {
                FontAwesomeManager.addFAIcon(menu, R.id.menu_server_refresh, FontAwesomeIcons.fa_refresh, getActivity());
            } catch (IllegalStateException e) {
                Log.w(getClass().getSimpleName(), "Icons not setup " + e.getMessage());
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == R.id.menu_server_refresh) {
                refreshServers();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        public void refreshServers() {
            PreferenceCategory targetCategory = findPreference("targetCategory");
            if (targetCategory != null) targetCategory.removeAll();
            MainActivity.progressDialog = ProgressDialog.show(getActivity(), "", "Searching for ArtNet devices...");
            SendArtnetPoll poll = new SendArtnetPoll();
            poll.setContext(getActivity().getApplicationContext());
            t = new Thread(poll);
            t.start();

            new Thread(() -> {
                while (t.isAlive()) {
                    try {
                        t.join(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (getActivity() != null) getActivity().runOnUiThread(this::finishedSearch);
            }).start();
        }

        public void finishedSearch() {
            PreferenceCategory targetCategory = findPreference("targetCategory");
            if (targetCategory == null) return;
            ArrayList<ArtPollReply> foundServers = MainActivity.foundServers;
            for (ArtPollReply artPollReply : foundServers) {
                for (int i = 0; i < artPollReply.getOutputStatus().length; i++) {
                    if (artPollReply.getOutputStatus()[i].dataTransmitted) {
                        String ipPort = artPollReply.getIp() + ":" + artPollReply.getOutputSubswitch()[i];
                        CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getContext());
                        checkBoxPreference.setTitle(ipPort);
                        checkBoxPreference.setSummary(artPollReply.getShortName());
                        checkBoxPreference.setKey("keyName" + ipPort);
                        checkBoxPreference.setChecked(false);
                        targetCategory.addPreference(checkBoxPreference);
                    }
                }
            }
            ArrayList<CheckBoxPreference> list = getPreferenceList(targetCategory, new ArrayList<>());
            for (final CheckBoxPreference p : list) {
                p.setOnPreferenceChangeListener((preference, newValue) -> {
                    p.setChecked(true);
                    PreferenceCategory category = findPreference("targetCategory");
                    if (category != null) {
                        ArrayList<CheckBoxPreference> othersList = getPreferenceList(category, new ArrayList<>());
                        for (CheckBoxPreference others : othersList) {
                            if (!others.getKey().equalsIgnoreCase(p.getKey())) others.setChecked(false);
                        }
                    }
                    MainActivity.getSharedPref().edit().putString(SettingsActivity.serveraddress, (String) preference.getTitle()).apply();
                    checkboxPrefManual = findPreference("checkboxPrefManual");
                    if (checkboxPrefManual != null) checkboxPrefManual.setChecked(false);
                    return true;
                });
            }
        }

        private ArrayList<CheckBoxPreference> getPreferenceList(Preference p, ArrayList<CheckBoxPreference> list) {
            if (p instanceof PreferenceCategory || p instanceof PreferenceScreen) {
                PreferenceGroup pGroup = (PreferenceGroup) p;
                for (int i = 0; i < pGroup.getPreferenceCount(); i++) getPreferenceList(pGroup.getPreference(i), list);
            } else if (p instanceof CheckBoxPreference) {
                list.add((CheckBoxPreference) p);
            }
            return list;
        }
    }

    public static class SacnPreferenceFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.pref_sacn);
            bindPreferenceSummaryToValue(findPreference("protocol_sacn_universe"));
        }
    }

    public static class SacnUnicastPreferenceFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.pref_sacn_unicast);
            bindPreferenceSummaryToValue(findPreference("protocol_sacn_universe"));
            bindPreferenceSummaryToValue(findPreference("protocol_sacn_unicast_ip"));
        }
    }
}
