package com.AuroraByteSoftware.AuroraDMX.ui.chase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.AuroraByteSoftware.AuroraDMX.AuroraNetwork;
import com.AuroraByteSoftware.AuroraDMX.MainActivity;
import com.AuroraByteSoftware.AuroraDMX.R;
import com.AuroraByteSoftware.AuroraDMX.chase.ChaseClickListener;
import com.AuroraByteSoftware.AuroraDMX.chase.ChaseObj;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;


public class EditChaseActivity extends Activity implements
        AddCueToChaseDialog.AddCueDialogListener,
        EditChaseDetailsDialog.EditChaseDialogListener,
        OnStartDragListener,
        EditChaseDetailsDialog.DialogDismissed {

    private RecyclerView mRecyclerView;
    private ChaseObj chase;
    private ItemTouchHelper mItemTouchHelper;


    /**
     * from https://developer.android.com/training/material/lists-cards.html
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_chase);
        mRecyclerView = findViewById(R.id.edit_chase_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // specify an adapter (see also next example)
        int chaseIndex = this.getIntent().getIntExtra(ChaseClickListener.CHASE_EXTRA, -1);
        if (chaseIndex < 0) {
            throw new IndexOutOfBoundsException("Couldn't transfer Chase Index");
        }
        chase = MainActivity.getAlChases().get(chaseIndex);
        mRecyclerView.setAdapter(new EditChaseAdaptor(chase, this));

        setUpItemTouchHelper();
        setUpAnimationDecoratorHelper();

        // Set title
        setTitle(chase.getName());

        AuroraNetwork.setUpNetwork(this);
    }


    /**
     * This is the standard support library way of implementing "swipe to delete" feature. You can do custom drawing in onChildDraw method
     * but whatever you draw will disappear once the swipe is over, and while the items are animating to their new position the recycler view
     * background will be visible. That is rarely an desired effect.
     */
    private void setUpItemTouchHelper() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new CueItemTouchHelper(0, ItemTouchHelper.LEFT, mRecyclerView, chase);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    /**
     * We're gonna setup another ItemDecorator that will draw the red background in the empty space while the items are animating to thier new positions
     * after an item is removed.
     */

    private void setUpAnimationDecoratorHelper() {
        mRecyclerView.addItemDecoration(new CueItemDecorator());
    }

    // Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.chase_edit, menu);
        MainActivity.addFAIcon(menu, R.id.chase_edit_menu_delete, FontAwesomeIcons.fa_trash, this);
        MainActivity.addFAIcon(menu, R.id.chase_edit_menu_add, FontAwesomeIcons.fa_plus, this);
        MainActivity.addFAIcon(menu, R.id.chase_edit_menu_settings, FontAwesomeIcons.fa_cog, this);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Event Handling for Individual menu item selected Identify single menu
     * item by it's id
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chase_edit_menu_delete:
                MainActivity.getAlChases().remove(chase);
                finish();
                return true;
            case R.id.chase_edit_menu_add:
                AddCueToChaseDialog addCueToChaseDialog = new AddCueToChaseDialog();
                addCueToChaseDialog.show(getFragmentManager(), "AddCueToChaseDialog");
                return true;
            case R.id.chase_edit_menu_settings:
                EditChaseDetailsDialog editChaseDetailsDialog = new EditChaseDetailsDialog();
                editChaseDetailsDialog.show(getFragmentManager(), "EditChaseDetailsDialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        AuroraNetwork.stopNetwork();
        super.onPause();
    }


    @Override
    public void finish() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(ChaseClickListener.CHASE_EXTRA, chase);
        setResult(Activity.RESULT_OK, resultIntent);
        super.finish();
    }

    @Override
    public void onClickAddCue(int which) {
        chase.getCues().add(MainActivity.alCues.get(which));
        mRecyclerView.invalidateItemDecorations();
    }

    @Override
    public String[] getAddCueItems() {
        int size = MainActivity.alCues.size();
        String[] strings = new String[size];
        for (int i = 0; i < size; i++) {
            strings[i] = MainActivity.alCues.get(i).getCueName();
        }
        return strings;
    }

    @Override
    public void onEditChaseDetails(String name, int fadeTime, int waitTime) {
        chase.setName(name);
        chase.setFadeTime(fadeTime);
        chase.setWaitTime(waitTime);
    }

    @Override
    public EditChaseDetailsDialog.EditChaseDetailsPOJO loadEditChaseDetails() {
        EditChaseDetailsDialog.EditChaseDetailsPOJO detailsPojo = new EditChaseDetailsDialog.EditChaseDetailsPOJO();
        detailsPojo.name = chase.getName();
        detailsPojo.fade = chase.getFadeTime();
        detailsPojo.wait = chase.getWaitTime();
        return detailsPojo;
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onDismiss() {
        // Set title
        setTitle(chase.getName());
    }

}
