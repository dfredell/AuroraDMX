package com.AuroraByteSoftware.AuroraDMX.ui.chase;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.GridView;

import com.AuroraByteSoftware.AuroraDMX.AuroraNetwork;
import com.AuroraByteSoftware.AuroraDMX.CueObj;
import com.AuroraByteSoftware.AuroraDMX.MainActivity;
import com.AuroraByteSoftware.AuroraDMX.R;
import com.AuroraByteSoftware.AuroraDMX.chase.ChaseClickListener;
import com.AuroraByteSoftware.AuroraDMX.chase.ChaseObj;
import com.AuroraByteSoftware.AuroraDMX.chase.ChaseRunner;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;

/**
 * Display a full screen grid of chase buttons
 */
public class ChaseActivity extends Activity {
    public GridView gridView;
    ChaseGridCell adapter;
    private static ChaseRunner chaseRunner = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chase);

        gridView = findViewById(R.id.chase_grid);
        adapter = new ChaseGridCell(this);
        gridView.setAdapter(adapter);

        AuroraNetwork.setUpNetwork(this);
    }

    @Override
    protected void onPause() {
//        ChaseRunner.stopAll();
        AuroraNetwork.stopNetwork();
        MainActivity.pm.save(null);
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(getClass().getSimpleName(), "Stopping");
        getChaseRunner().stopAll();
        MainActivity.pm.save(null);
        super.onStop();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
    }

    @Override
    protected void onResume() {
        adapter.notifyDataSetChanged();
        AuroraNetwork.setUpNetwork(this);
        super.onResume();
    }

    // Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.chase, menu);
        MainActivity.addFAIcon(menu, R.id.menu_add, FontAwesomeIcons.fa_plus, this);
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * Event Handling for Individual menu item selected Identify single menu
     * item by it's id
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                addChase();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public static ChaseRunner getChaseRunner() {
        if (chaseRunner == null) {
            Log.d("ChaseActivity", "Creating ChaseRunner");
            chaseRunner = new ChaseRunner();
        }
        return chaseRunner;
    }

    private void addChase() {
        String name = "Chase " + (MainActivity.getAlChases().size() + 1);
        int chaseFadeTime = Integer.parseInt(MainActivity.getSharedPref().getString("chase_fade_time", "5"));
        int waitTime = Integer.parseInt(MainActivity.getSharedPref().getString("chase_wait_time", "5"));

        ChaseObj chaseObj = new ChaseObj(name, chaseFadeTime, waitTime, new ArrayList<CueObj>(), null);
        chaseObj.setButton(ChaseClickListener.makeButton(chaseObj, this));
        MainActivity.getAlChases().add(chaseObj);
        gridView.invalidateViews();
    }
}
