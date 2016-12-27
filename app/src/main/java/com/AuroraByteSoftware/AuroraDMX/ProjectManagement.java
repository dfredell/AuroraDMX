package com.AuroraByteSoftware.AuroraDMX;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.AuroraByteSoftware.AuroraDMX.fixture.Fixture;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

public class ProjectManagement extends MainActivity {

    private final MainActivity mainActivity;

    private static final String PREF_OLD_POINTER = "StoredDataInPref";
    private static final String PREF_OLD_POINTER_HUMAN = "Default Project";
    private static final String PREF_SAVES = "LIST_OF_SAVED_PROJECTS";
    private static final String PREF_DEF = "DEFAULT_PROJECT_TO_LOAD";
    private static final String TAG = "AuroraDMX";
    private static boolean DeleteInProgress = false;

    public ProjectManagement() {
        this.mainActivity = null;
    }

    ProjectManagement(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    void onShare() {
        save(null, true);
    }

    void save(String key) {
        save(key, false);
    }

    private void save(String key, boolean share) {
        HashSet<String> listOfProjects = (HashSet<String>) getSharedPref().getStringSet(PREF_SAVES, new HashSet<String>());
        if (key == null) {
            key = getSharedPref().getString(PREF_DEF, PREF_OLD_POINTER);
        }
        if (!share) {
            listOfProjects.add(key);
        }
        //Get ch levels
        final List<Integer> currentChannelArray = getCurrentChannelArray();
        int[] currentChannelLevels = ArrayUtils.toPrimitive(currentChannelArray.toArray(new Integer[currentChannelArray.size()]));

        //Get the channel names as an array
        String channelNames[] = new String[alColumns.size()];
        for (int i = 0; i < alColumns.size(); i++) {
            channelNames[i] = alColumns.get(i).getChText();
        }

        // Get an array of isRGB channels
        boolean[] isRGB = new boolean[alColumns.size()];
        for (int i = 0; i < alColumns.size(); i++) {
            isRGB[i] = alColumns.get(i).isRGB();
        }

        //Get the presets as an array
        String valuePresets[] = new String[alColumns.size()];
        for (int i = 0; i < alColumns.size(); i++) {
            valuePresets[i] = alColumns.get(i).getValuePresets();
        }

        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutput = new ObjectOutputStream(arrayOutputStream);
            objectOutput.writeObject(alColumns.size());
            objectOutput.writeObject(alCues);
            objectOutput.writeObject(patchList);
            objectOutput.writeObject(cueCount);
            objectOutput.writeObject(getSharedPref().getString(SettingsActivity.serveraddress, ""));
            objectOutput.writeObject(currentChannelLevels);
            objectOutput.writeObject(channelNames);
            objectOutput.writeObject(isRGB);
            objectOutput.writeObject(valuePresets);
            byte[] data = arrayOutputStream.toByteArray();

            objectOutput.close();
            arrayOutputStream.close();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Base64OutputStream b64 = new Base64OutputStream(out, Base64.DEFAULT);
            b64.write(data);
            b64.close();
            out.close();

            SharedPreferences.Editor ed = getSharedPref().edit();
            ed.putString(key, new String(out.toByteArray()));
            ed.putStringSet(PREF_SAVES, listOfProjects);
            ed.apply();

            if (share) {
                if (mainActivity == null) {
                    return;
                }
                clearCache(mainActivity);
                File dir = mainActivity.getCacheDir();
                Log.d(TAG, "Mkdir response " + dir.mkdirs());
                String fileName = (key.equals(PREF_OLD_POINTER) ? PREF_OLD_POINTER_HUMAN : key) + ".AuroraDMX";
                File file = new File(dir, fileName);
                FileOutputStream fileStream = new FileOutputStream(file);
                out.writeTo(fileStream);
                fileStream.flush();
                fileStream.close();

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("application/octet-stream");
                sendIntent.putExtra(Intent.EXTRA_TEXT, "AuroraDMX Project");
                sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("content://com.AuroraByteSoftware.AuroraDMX/" + fileName));
                mainActivity.startActivity(Intent.createChooser(sendIntent, "Share AuroraDMX Project"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.w(TAG, "Unable to save project ", e);
        }
        Log.d(TAG, "save complete");
    }

    /**
     * Clear out any previous exported files
     * http://stackoverflow.com/a/7600257/327011
     *
     * @param context used t ofind temp dir
     */
    private void clearCache(Context context) {
        File cacheDir = context.getCacheDir();

        File[] files = cacheDir.listFiles();

        if (files != null) {
            for (File file : files)
                Log.d(TAG, "File delete success: " + file.delete());
        }
    }

    void openFile(String uri) throws IOException {
        Log.d(TAG, "open URI " + uri);

        InputStream stream = mainActivity.getContentResolver().openInputStream(Uri.parse(uri));
        byte[] data = readBytes(stream);
        loadData(data);
        mainActivity.setTitle(mainActivity.getString(R.string.openedFile));
    }

    /**
     * @see  <a href="http://stackoverflow.com/a/2436413/288568">http://stackoverflow.com/a/2436413/288568</a>
     * @param inputStream file to load
     * @return convertered inputStream file
     * @throws IOException
     */
    private byte[] readBytes(InputStream inputStream) throws IOException {
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }

    @SuppressWarnings("unchecked")
    void open(String key) {
        Log.d(TAG, "open " + key);
        if (key == null) {
            key = getSharedPref().getString(PREF_DEF, PREF_OLD_POINTER);
        }
        // Read data back
        byte[] bytes = getSharedPref().getString(key, "{}").getBytes();

        loadData(bytes);

        // Save a new default
        SharedPreferences.Editor ed = getSharedPref().edit();
        ed.putString(PREF_DEF, key);
        ed.apply();

        // Set the title to the project
        if (PREF_OLD_POINTER.equals(key))
            key = PREF_OLD_POINTER_HUMAN;
        mainActivity.setTitle(key);

        mainActivity.setUpNetwork();
    }

    private void loadData(byte[] bytes) {
        ByteArrayInputStream byteArray = new ByteArrayInputStream(bytes);
        Base64InputStream base64InputStream = new Base64InputStream(byteArray, Base64.DEFAULT);
        ObjectInputStream in;
        List<Integer> chLvls = new ArrayList<>();
        String[] fixtureNames = null;
        boolean[] isRGB = null;
        String[] valuePresets = null;
        int[][] patch = new int[0][0];

        try {
            // Stop ArtNet
            if (null != clientSocket && !clientSocket.isClosed())
                clientSocket.close();

            //Clear current screen
            alCues.clear();
            for (Fixture columns : alColumns) {
                columns.getViewGroup().removeAllViews();
            }
            alColumns.clear();
            ((LinearLayout) mainActivity.findViewById(R.id.ChanelLayout)).removeAllViews();

            //Read objects
            in = new ObjectInputStream(base64InputStream);
            Object readObject1FixtureCount = in.readObject();
            Object readObject2Cues = in.readObject();
            Object readObject3Patch = in.readObject();
            Object readObject4CueCount = in.readObject();
            Object readObject5IPAdr = in.readObject();
            Object readObject6ChAry = null;
            Object readObject7FixtureNames = null;
            Object readObject8isRGB = null;
            Object readObject9valuePresets = null;

            try {
                readObject6ChAry = in.readObject();
                readObject7FixtureNames = in.readObject();
                readObject8isRGB = in.readObject();
                readObject9valuePresets = in.readObject();
            } catch (EOFException e) {
                // Do nothing we hit the end of the stored data
            }

            if (readObject1FixtureCount.getClass().equals(Integer.class)) {
                if (readObject7FixtureNames != null && readObject7FixtureNames.getClass().equals(String[].class))
                    fixtureNames = (String[]) readObject7FixtureNames;
                if (readObject8isRGB != null && readObject8isRGB.getClass().equals(boolean[].class))
                    isRGB = (boolean[]) readObject8isRGB;
                if (readObject9valuePresets != null && readObject9valuePresets.getClass().equals(String[].class))
                    valuePresets = (String[]) readObject9valuePresets;

                mainActivity.setNumberOfFixtures((Integer) readObject1FixtureCount, fixtureNames, isRGB, valuePresets);
                getSharedPref().edit().putString(SettingsActivity.channels, readObject1FixtureCount + "").apply();
            }
            if (readObject2Cues.getClass().equals(alCues.getClass()))
                alCues = (ArrayList<CueObj>) readObject2Cues;
            if (readObject3Patch.getClass().equals(patch.getClass()))
                patch = (int[][]) readObject3Patch;
            if (readObject3Patch.getClass().equals(patchList.getClass()))
                patchList = new ArrayList<>((List<ChPatch>) readObject3Patch);
            if (readObject4CueCount.getClass().equals(Double.class))
                cueCount = (Double) readObject4CueCount;
            if (readObject5IPAdr.getClass().equals(String.class))
                getSharedPref().edit().putString(SettingsActivity.serveraddress, (String) readObject5IPAdr).apply();
            if (readObject6ChAry != null && readObject6ChAry.getClass().equals(int[].class))
                chLvls = Arrays.asList(ArrayUtils.toObject((int[]) readObject6ChAry));


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
            cue.setButton(CueClickListener.makeButton(cue.getCueName(), mainActivity));
            cueLine.addView(cue.getButton());
            cue.setHighlight(0, 0, 0);
            cue.setFadeInProgress(false);
        }

        // create a new "Add Cue" button
        ((LinearLayout) mainActivity.findViewById(R.id.CueLine)).addView(CueClickListener.makeButton(mainActivity.getString(R.string.AddCue), mainActivity));

        migrateData(patch);

        // Set ch levels
        int chIndex = 0;
        for (int i = 0; i < alColumns.size() && chLvls.size() > chIndex; i++) {
            Fixture fixture = alColumns.get(i);
            int fixtureUses = fixture.getChLevels().size();
            if (chIndex + fixtureUses > chLvls.size()) {
                Log.e(TAG, "Channel levels are out of sync with fixtures");
                break;
            }
            fixture.setChLevels(new ArrayList<>(chLvls.subList(chIndex, chIndex + fixtureUses)));
            chIndex += fixtureUses;
        }

        //Set fixtureNames
        for (int i = 0; fixtureNames != null && i < fixtureNames.length && i < alColumns.size(); i++) {
            alColumns.get(i).setColumnText(fixtureNames[i]);
        }
    }

    /**
     * Migrating from 1.8 to 2.0 move Cue.levels -> Cue.levelsList
     *
     * @param patch array to be converted to patchList
     */
    private void migrateData(int[][] patch) {
        for (CueObj cue : alCues) {
            if (cue.getOriginalLevels().length > 1 && cue.getLevels() == null) {
                List<Integer> levelsList = new ArrayList<>(Arrays.asList(ArrayUtils.toObject(cue.getOriginalLevels())));
                cue.setLevelsList(levelsList);
                cue.setOriginalLevels(new int[0]);
            }
        }
        //Migrate patch[][] to patchList
        if (patch.length > 0) {
            patchList.clear();
            for (int i = 0; i < patch.length; i++) {
                patchList.add(new ChPatch());
                for (int x = 0; x < patch[i].length; x++) {
                    patchList.get(i).addDimmer(patch[i][x]);
                }
            }
        }
    }

    public void onSaveClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("Save Project");
        builder.setIcon(
                new IconDrawable(mainActivity, FontAwesomeIcons.fa_save)
                        .alpha(204)
                        .actionBarSize());

        final EditText editText = new EditText(mainActivity);

        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Log.v(TAG, "entered " + editText.getText());
                if (!"".equals(editText.getText().toString()))
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
        TreeSet<String> listOfProjects = new TreeSet<>(getSharedPref().getStringSet(PREF_SAVES, new HashSet<String>()));
        if (listOfProjects.contains(PREF_OLD_POINTER)) {
            listOfProjects.remove(PREF_OLD_POINTER);
            listOfProjects.add(PREF_OLD_POINTER_HUMAN);
        }
        final String[] listOfProjectsArray = listOfProjects.toArray(new String[listOfProjects.size()]);
        final ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(mainActivity, android.R.layout.simple_list_item_1, android.R.id.text1, listOfProjectsArray);

        DeleteInProgress = false;
        // Long press to remove
        OnItemLongClickListener longPressListener = new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
                DeleteInProgress = true;
                AlertDialog.Builder adb = new AlertDialog.Builder(mainActivity);
                adb.setTitle("Delete");
                adb.setIcon(new IconDrawable(mainActivity, FontAwesomeIcons.fa_exclamation_triangle)
                                .colorRes(R.color.white)
                                .alpha(204)
                                .actionBarSize());
                final String proj = listOfProjectsArray[position];
                String proj_human = PREF_OLD_POINTER.equals(proj) ? PREF_OLD_POINTER_HUMAN : proj;
                adb.setMessage("Are you sure you want to delete project '" + proj_human + "'?");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // MyDataObject.remove(positionToRemove);
                        // adapter.notifyDataSetChanged();
                        HashSet<String> listOfProjects = (HashSet<String>) getSharedPref().getStringSet(PREF_SAVES, new HashSet<String>());
                        listOfProjects.remove(proj);

                        //Remove the project
                        SharedPreferences.Editor ed = getSharedPref().edit();
                        ed.remove(proj);
                        ed.putStringSet(PREF_SAVES, listOfProjects);
                        ed.apply();
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
        builder.setIcon(
                new IconDrawable(mainActivity, FontAwesomeIcons.fa_folder_open)
                        .alpha(204)
                        .actionBarSize());

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
                if (!DeleteInProgress)
                    open(proj);
                dialog.dismiss();
            }
        };
        listView.setOnItemClickListener(listener);

        dialog.show();

    }
}
