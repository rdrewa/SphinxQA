package pl.nemolab.sphinxqa.gui;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import pl.nemolab.sphinxqa.R;

public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
