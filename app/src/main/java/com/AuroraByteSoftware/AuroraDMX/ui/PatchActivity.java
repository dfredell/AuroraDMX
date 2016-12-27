package com.AuroraByteSoftware.AuroraDMX.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import com.AuroraByteSoftware.AuroraDMX.ChPatch;
import com.AuroraByteSoftware.AuroraDMX.MainActivity;
import com.AuroraByteSoftware.AuroraDMX.R;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

public class PatchActivity extends Activity {
    public static GridView dimGridView;
    public static GridView chGridView;
    public static int currentCh = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patch);

        chGridView = (GridView) findViewById(R.id.gridView1);
        chGridView.setAdapter(new GridCell(this, MainActivity.patchList.size() - 1, true));

        dimGridView = (GridView) findViewById(R.id.gridView2);
        GridCell dimGridCell = new GridCell(this, MainActivity.MAX_DIMMERS, false);
        dimGridView.setAdapter(dimGridCell);

        currentCh = 1;

    }

    /**
     * Event Handling for Individual menu item selected Identify single menu item by it's id
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_patch_onetoone:
                patchOneToOne();
                dimGridView.invalidateViews();
                Toast.makeText(this, "Patched one to one", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_patch_clear:
                for (ChPatch chPatch : MainActivity.patchList) {
                    chPatch.getDimmers().clear();
                }
                dimGridView.invalidateViews();
                Toast.makeText(this, "Patch cleared", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public static void patchOneToOne() {
        for (int x = 0; x < MainActivity.patchList.size(); x++) {
            MainActivity.patchList.get(x).getDimmers().clear();
            MainActivity.patchList.get(x).addDimmer(x);
        }
    }

    public static boolean chContainsDimmer(int dim) {
        dim = dim + 1;
        return MainActivity.patchList.get(currentCh).contains(dim);
    }

    public static void toggleDimToCh(int ch, int dim) {
        final ChPatch chPatch = MainActivity.patchList.get(ch);
        if (chPatch.contains(dim))
            chPatch.getDimmers().remove((Integer) dim);
        else
            chPatch.addDimmer(dim);
    }

    // Initiating Menu XML file (patch.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.patch, menu);

        menu.findItem(R.id.menu_patch_onetoone).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_arrows_h)
                        .colorRes(R.color.white)
                        .alpha(204)
                        .actionBarSize());
        menu.findItem(R.id.menu_patch_clear).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_eraser)
                        .colorRes(R.color.white)
                        .alpha(204)
                        .actionBarSize());

        return (super.onCreateOptionsMenu(menu));
    }

}
