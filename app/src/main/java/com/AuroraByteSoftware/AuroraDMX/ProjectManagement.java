package com.AuroraByteSoftware.AuroraDMX;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

public class ProjectManagement extends MainActivity {

	private final MainActivity mainActivity;

	private static final String PREF_OLD_POINTER = "StoredDataInPref";
	private static final String PREF_OLD_POINTER_HUMAN = "Default Project";
	private static final String PREF_SAVES = "LIST_OF_SAVED_PROJECTS";
	private static final String PREF_DEF = "DEFAULT_PROJECT_TO_LOAD";
	
	private static boolean DeleteInProgress = false;

	public ProjectManagement(MainActivity mainActivity) {
		// TODO Auto-generated constructor stub
		this.mainActivity = mainActivity;
	}

	void save(String key) {
		if (key == null) {
			key = sharedPref.getString(PREF_DEF, PREF_OLD_POINTER);
		}
		HashSet<String> listOfProjects = (HashSet<String>) sharedPref.getStringSet(PREF_SAVES, new HashSet<String>());
		listOfProjects.add(key);

		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objectOutput = new ObjectOutputStream(arrayOutputStream);
			objectOutput.writeObject(alColumns.size());
			objectOutput.writeObject(alCues);
			objectOutput.writeObject(patch);
			objectOutput.writeObject(cueCount);
			objectOutput.writeObject(sharedPref.getString(SettingsActivity.serveraddress, ""));
			objectOutput.writeObject(getCurrentChannelArray());
			byte[] data = arrayOutputStream.toByteArray();

			objectOutput.close();
			arrayOutputStream.close();

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Base64OutputStream b64 = new Base64OutputStream(out, Base64.DEFAULT);
			b64.write(data);
			b64.close();
			out.close();

			SharedPreferences.Editor ed = sharedPref.edit();
			ed.putString(key, new String(out.toByteArray()));
			ed.putStringSet(PREF_SAVES, listOfProjects);
			ed.commit();

			// Storage.writeFile(data);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("save complete");
	}

	@SuppressWarnings("unchecked")
	void open(String key) {
		// System.out.println("open");
		if (key == null) {
			key = sharedPref.getString(PREF_DEF, PREF_OLD_POINTER);
		}
		// Read data back
		byte[] bytes = sharedPref.getString(key, "{}").getBytes();

		ByteArrayInputStream byteArray = new ByteArrayInputStream(bytes);
		Base64InputStream base64InputStream = new Base64InputStream(byteArray, Base64.DEFAULT);
		ObjectInputStream in;
		int[] chLvls = new int[0];

		try {
			in = new ObjectInputStream(base64InputStream);

			// Stop ArtNet
			if (null != clientSocket && !clientSocket.isClosed())
				clientSocket.close();
			
			//Clear current screen
			alCues.clear();
			for (ColumnObj columns : alColumns) {
				columns.getViewGroup().removeAllViews();
			}
			alColumns.clear();
			((LinearLayout) mainActivity.findViewById(R.id.ChanelLayout)).removeAllViews();
			//Read objects
			Object readObject1Channels = in.readObject();
			Object readObject2Cues = in.readObject();
			Object readObject3Patch = in.readObject();
			Object readObject4CueCount = in.readObject();
			Object readObject5IPAdr = in.readObject();
			Object readObject6ChAry = null;
			try {
				readObject6ChAry = in.readObject();
			} catch (EOFException e) {
				// Do nothing we hit the end of the stored data
			}

			if (readObject1Channels.getClass().equals(Integer.class)) {
				mainActivity.setNumberOfChannels((Integer) readObject1Channels);
				sharedPref.edit().putString(SettingsActivity.channels, readObject1Channels + "").commit();
			}
			if (readObject2Cues.getClass().equals(alCues.getClass()))
				alCues = (ArrayList<CueObj>) readObject2Cues;
			if (readObject3Patch.getClass().equals(patch.getClass()))
				patch = (int[][]) readObject3Patch;
			if (readObject4CueCount.getClass().equals(Double.class))
				cueCount = (Double) readObject4CueCount;
			if (readObject5IPAdr.getClass().equals(String.class))
				sharedPref.edit().putString(SettingsActivity.serveraddress, (String) readObject5IPAdr).commit();
			if (readObject6ChAry != null && readObject6ChAry.getClass().equals(int[].class))
				chLvls = (int[]) readObject6ChAry;

		} catch (Throwable t) {
			t.printStackTrace();
		}
		try {
			base64InputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Refresh views
		LinearLayout cueLine = ((LinearLayout) mainActivity.findViewById(R.id.CueLine));
		cueLine.removeAllViews();
		for (CueObj cue : alCues) {
			// create a new "Add Cue" button
			cue.setButton(mainActivity.makeButton(cue.getCueName()));
			cueLine.addView(cue.getButton());
			cue.setHighlight(0, 0, 0);
			cue.setFadeInProgress(false);
		}

		// create a new "Add Cue" button
		((LinearLayout) mainActivity.findViewById(R.id.CueLine)).addView(mainActivity.makeButton("Add Cue"));
		mainActivity.setUpNetwork();
		// System.out.println("open complete");

		// Set ch levels
		for (int x = 0; x < chLvls.length && x < alColumns.size(); x++) {
			alColumns.get(x).setChLevel(chLvls[x]);
		}

		// Save a new default
		SharedPreferences.Editor ed = sharedPref.edit();
		ed.putString(PREF_DEF, key);
		ed.commit();

		// Set the title to the project
		if (PREF_OLD_POINTER.equals(key))
			key = PREF_OLD_POINTER_HUMAN;
		mainActivity.setTitle(key);
	}

	public void onSaveClick() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
		builder.setTitle("Save Project");
		builder.setIcon(R.drawable.content_save);

		final EditText editText = new EditText(mainActivity);

		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				System.out.println("entered " + editText.getText());
				if (null != editText.getText().toString() && !"".equals(editText.getText().toString()))
					save(editText.getText().toString());
			}
		};

		builder.setPositiveButton("Save", listener);
		builder.setView(editText);
		editText.requestFocus();
		Dialog dialog = builder.create();
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

		dialog.show();

	}

	/**
	 * Dialog to load a saved project
	 */
	public void onLoadClick() {
		HashSet<String> listOfProjects = (HashSet<String>) sharedPref.getStringSet(PREF_SAVES, new HashSet<String>());
		if (listOfProjects.contains(PREF_OLD_POINTER)) {
			listOfProjects.remove(PREF_OLD_POINTER);
			listOfProjects.add(PREF_OLD_POINTER_HUMAN);
		}
		final String[] listOfProjectsArray = listOfProjects.toArray(new String[listOfProjects.size()]);
		final ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_list_item_1, android.R.id.text1, listOfProjectsArray);

		DeleteInProgress = false;
		// Long press to remove
		OnItemLongClickListener longPressListener = new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
				DeleteInProgress = true;
				AlertDialog.Builder adb = new AlertDialog.Builder(mainActivity);
				adb.setTitle("Delete");
				adb.setIcon(R.drawable.alerts_and_states_warning);
				final String proj = listOfProjectsArray[position];
				String proj_human = PREF_OLD_POINTER.equals(proj) ? PREF_OLD_POINTER_HUMAN:proj;
				adb.setMessage("Are you sure you want to delete project '" + proj_human + "'?");
				adb.setNegativeButton("Cancel", null);
				adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// MyDataObject.remove(positionToRemove);
						// adapter.notifyDataSetChanged();
						HashSet<String> listOfProjects = (HashSet<String>) sharedPref.getStringSet(PREF_SAVES, new HashSet<String>());
						listOfProjects.remove(proj);
						
						//Remove the project 
						SharedPreferences.Editor ed = sharedPref.edit();
						ed.remove(proj);
						ed.putStringSet(PREF_SAVES, listOfProjects);
						ed.commit();
					}
				});
				adb.show();

				return false;
			}
		};
		ListView listView = new ListView(mainActivity);
		listView.setLongClickable(true);
		listView.setClickable(true);
		listView.setOnItemLongClickListener(longPressListener);
		listView.setAdapter(modeAdapter);

		AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
		builder.setTitle("Load Project");
		builder.setIcon(R.drawable.collections_collection);
		// builder.setAdapter(modeAdapter, listener);
		builder.setView(listView);

		final Dialog dialog = builder.create();

		// Short press to load
		OnItemClickListener listener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				//Save the current proj before opening another
				save(null);
				String proj = listOfProjectsArray[position];
				if (PREF_OLD_POINTER_HUMAN.equals(proj))
					proj = PREF_OLD_POINTER;
				if(!DeleteInProgress)
					open(proj);
				dialog.dismiss();
			}
		};
		listView.setOnItemClickListener(listener);

		dialog.show();

	}
}
