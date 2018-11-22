package com.AuroraByteSoftware.AuroraDMX.ui.chase;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.AuroraByteSoftware.AuroraDMX.AuroraNetwork;
import com.AuroraByteSoftware.AuroraDMX.CueObj;
import com.AuroraByteSoftware.AuroraDMX.MainActivity;
import com.AuroraByteSoftware.AuroraDMX.R;
import com.AuroraByteSoftware.AuroraDMX.chase.ChaseClickListener;
import com.AuroraByteSoftware.AuroraDMX.chase.ChaseObj;
import com.AuroraByteSoftware.AuroraDMX.chase.ChaseRunner;
import com.AuroraByteSoftware.AuroraDMX.ui.fontawesome.FontAwesomeIcons;
import com.AuroraByteSoftware.AuroraDMX.ui.fontawesome.FontAwesomeManager;
import com.jmedeisis.draglinearlayout.DragLinearLayout;

import java.util.ArrayList;
import java.util.Collections;

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
        FontAwesomeManager.addFAIcon(menu, R.id.menu_add, FontAwesomeIcons.fa_plus, this);
        FontAwesomeManager.addFAIcon(menu, R.id.chase_menu_reorder, FontAwesomeIcons.fa_list_ol, this);
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
            case R.id.chase_menu_reorder:
                reorderChase();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Create a popup that allows dragging to reorder the chases
     */
    private void reorderChase() {
        final Context context = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater li = LayoutInflater.from(context);
        final View reorderView = li.inflate(R.layout.dialog_cue_reorder, null, false);
        builder.setView(reorderView);
        final AlertDialog reorderAlert = builder.create();

        DragLinearLayout dragLinearLayout = reorderView.findViewById(R.id.cue_reorder_view);
        for (ChaseObj chaseObj : MainActivity.getAlChases()) {
            final View inflate = LayoutInflater.from(this).inflate(R.layout.edit_chase_row_view, dragLinearLayout, false);
            TextView titleTextView = inflate.findViewById(R.id.title_text_view);
            View handleView = inflate.findViewById(R.id.edit_chase_drag);
            titleTextView.setText(chaseObj.getName());
            dragLinearLayout.addView(inflate);
            dragLinearLayout.setViewDraggable(inflate, handleView);
        }
        //When items drag reorder them in their array
        dragLinearLayout.setOnViewSwapListener(new DragLinearLayout.OnViewSwapListener() {
            @Override
            public void onSwap(View firstView, int firstPosition,
                               View secondView, int secondPosition) {
                Collections.swap(MainActivity.getAlChases(), firstPosition, secondPosition);
                adapter.notifyDataSetChanged();
            }
        });
        reorderAlert.show();
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
