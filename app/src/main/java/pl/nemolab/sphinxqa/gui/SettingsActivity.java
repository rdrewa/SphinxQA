package pl.nemolab.sphinxqa.gui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import pl.nemolab.sphinxqa.Config;
import pl.nemolab.sphinxqa.R;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Preference prefStorageFolder;
    private PreferenceCategory prefCatSaving;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        prefStorageFolder = findPreference(Config.KEY_STORAGE_FOLDER);
        String keyCatSaving = "prefCatSaving";
        prefCatSaving = (PreferenceCategory) findPreference(keyCatSaving);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        serveStorage(sharedPreferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Config.KEY_STORAGE_TYPE)) {
            serveStorage(sharedPreferences);
        }
    }

    private void serveStorage(SharedPreferences sharedPreferences) {
        String storageType = sharedPreferences.getString(
           Config.KEY_STORAGE_TYPE,
           Config.DEFAULT_STORAGE_TYPE
       );
        if (storageType.equals("MOVIE_FOLDER")) {
            prefCatSaving.removePreference(prefStorageFolder);
        }
        if (storageType.equals("APP_FOLDER")) {
            prefCatSaving.addPreference(prefStorageFolder);
            prefStorageFolder.setShouldDisableView(true);
            prefStorageFolder.setEnabled(true);
            prefStorageFolder.setSelectable(false);
            prefStorageFolder.setTitle(R.string.storage_app_folder);
            prefStorageFolder.setSummary(Config.DEFAULT_STORAGE_FOLDER);
        }
        if (storageType.equals("USER_FOLDER")) {
            prefCatSaving.addPreference(prefStorageFolder);
            prefStorageFolder.setEnabled(true);
            prefStorageFolder.setSelectable(true);
            prefStorageFolder.setTitle(R.string.storage_user_folder);
            prefStorageFolder.setSummary(R.string.pref_StorageUserFolder_title);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
