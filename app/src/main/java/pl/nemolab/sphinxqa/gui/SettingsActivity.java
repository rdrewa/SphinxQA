package pl.nemolab.sphinxqa.gui;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.Locale;

import pl.nemolab.sphinxqa.Config;
import pl.nemolab.sphinxqa.R;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Preference prefStorageFolder;
    private Preference prefStorageType;
    private Preference prefLang;
    private PreferenceCategory prefCatSaving;
    private String defaultStorageFolder;
    private final static String TAG = "PREFS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        prefStorageFolder = findPreference(Config.KEY_STORAGE_FOLDER);
        prefStorageType = findPreference(Config.KEY_STORAGE_TYPE);
        prefLang = findPreference(Config.KEY_LANG);
        String lang = getLanguage();
        prefLang.setDefaultValue(lang);
        ((ListPreference) prefLang).setValue(lang);
        prefLang.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String lang = (String) newValue;
                Resources resources = getBaseContext().getResources();
                Configuration configuration = resources.getConfiguration();
                Locale locale = new Locale(lang);
                configuration.locale = locale;
                resources.updateConfiguration(configuration, resources.getDisplayMetrics());
                return true;
            }
        });
        String keyCatSaving = "prefCatSaving";
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        defaultStorageFolder = root + Config.DEFAULT_STORAGE_FOLDER;
        prefStorageFolder.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String folder = (String) newValue;
                File dir = new File(folder);
                if (dir.exists()) {
                    if (dir.isDirectory()) {
                        return true;
                    }
                } else {
                    if (dir.mkdir()) {
                        if (dir.isDirectory()) {
                            String msg = getString(R.string.storage_folder_created) + folder;
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            return true;
                        }
                    }
                }
                String msg = getString(R.string.storage_folder_fail) + folder;
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                return false;
            }
        });
        prefStorageType.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String storageType = (String) newValue;
                if (storageType.equals(Config.STORAGE_TYPE_APP_FOLDER)) {
                    SharedPreferences sharedPreferences = preference.getSharedPreferences();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Config.KEY_STORAGE_FOLDER, defaultStorageFolder);
                    editor.commit();
                    prefStorageFolder.setDefaultValue(defaultStorageFolder);
                }
                return true;
            }
        });
        prefCatSaving = (PreferenceCategory) findPreference(keyCatSaving);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        serveStorage(sharedPreferences);
        ListPreference preference;
        preference = (ListPreference) findPreference("prefCharset");
        if (preference.getValue() == null) {
            preference.setValueIndex(0);
        }
        preference = (ListPreference) findPreference("prefPlayerShowSubtitles");
        if (preference.getValue() == null) {
            preference.setValueIndex(0);
        }
    }

    private String getLanguage() {
        return getResources().getConfiguration().locale.getLanguage();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Config.KEY_STORAGE_TYPE)) {
            serveStorage(sharedPreferences);
            String folder = sharedPreferences.getString(Config.KEY_STORAGE_FOLDER, defaultStorageFolder);
            String msg = "on" + key + "changed, folder: " + folder;
            Log.i(TAG, msg);
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
            prefStorageFolder.setEnabled(true);
            prefStorageFolder.setSelectable(false);
            prefStorageFolder.setTitle(R.string.storage_app_folder);
            prefStorageFolder.setDefaultValue(defaultStorageFolder);
            prefStorageFolder.setSummary(defaultStorageFolder);
            ((EditTextPreference) prefStorageFolder).getEditText().setText(defaultStorageFolder);
        }
        if (storageType.equals("USER_FOLDER")) {
            prefCatSaving.addPreference(prefStorageFolder);
            prefStorageFolder.setEnabled(true);
            prefStorageFolder.setSelectable(true);
            prefStorageFolder.setTitle(R.string.storage_user_folder);
            prefStorageFolder.setDefaultValue(defaultStorageFolder);
            prefStorageFolder.setSummary(R.string.pref_StorageUserFolder_title);
            ((EditTextPreference) prefStorageFolder).getEditText().setText(defaultStorageFolder);
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
