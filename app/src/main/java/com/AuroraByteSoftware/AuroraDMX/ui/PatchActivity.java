package com.AuroraByteSoftware.AuroraDMX.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;
import com.AuroraByteSoftware.AuroraDMX.MainActivity;
import com.AuroraByteSoftware.AuroraDMX.R;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

public class PatchActivity extends Activity {
    public static GridView dimGridView;
	public static GridView chGridView;
	public static int currentCh = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patch);

		chGridView = (GridView) findViewById(R.id.gridView1);
		chGridView.setAdapter(new GridCell(this, MainActivity.alColumns.size(), true));

		dimGridView = (GridView) findViewById(R.id.gridView2);
        GridCell dimGridCell = new GridCell(this, MainActivity.MAX_DIMMERS, false);
		dimGridView.setAdapter(dimGridCell);
		

	}

	/**
	 * Event Handling for Individual menu item selected Identify single menu item by it's id
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_patch_onetoone:
			patchOneToOne();
			dimGridView.invalidateViews();
			Toast.makeText(this, "Patched one to one", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menu_patch_clear:
			for (int x = 0; x < MainActivity.patch.length; x++) {
				for (int y = 0; y < MainActivity.ALLOWED_PATCHED_DIMMERS; y++) {
					MainActivity.patch[x][y] = 0;
				}
			}
			dimGridView.invalidateViews();
			Toast.makeText(this, "Patch cleared", Toast.LENGTH_SHORT).show();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}

	public static void patchOneToOne() {
		for (int x = 0; x < MainActivity.patch.length; x++) {
			MainActivity.patch[x][0] = x;//Set the first dimmer to its self
			for (int y = 1; y < MainActivity.ALLOWED_PATCHED_DIMMERS; y++) {
				MainActivity.patch[x][y] = 0;//set all other dimmers to 0
			}
		}
	}

	public static boolean chContainsDimmer(int dim) {
		dim = dim + 1;
		for (int x = 0; x < MainActivity.ALLOWED_PATCHED_DIMMERS; x++) {
			if (MainActivity.patch[currentCh][x] == dim)
				return true;
		}
		return false;
	}

	public static void addDimToCh(int ch, int dim) {
		for (int x = 0; x < MainActivity.ALLOWED_PATCHED_DIMMERS; x++) {
            if(MainActivity.patch.length < ch)
                break;
			if (MainActivity.patch[ch][x] == dim) {//remove dim
				MainActivity.patch[ch][x] = 0;
				break;
			} else if (MainActivity.patch[ch][x] == 0) {//add dim
				MainActivity.patch[ch][x] = dim;
				break;
			}
		}
        Arrays.sort(MainActivity.patch[ch]);
        ArrayUtils.reverse(MainActivity.patch[ch]);
    }

	// Initiating Menu XML file (patch.xml)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.patch, menu);
		return (super.onCreateOptionsMenu(menu));
	}

}
