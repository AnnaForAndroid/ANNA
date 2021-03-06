package com.anna.preferences; /**
 * Created by PARSEA on 13.02.2017.
 */

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v14.preference.MultiSelectListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;

import com.anna.R;
import com.anna.util.Module;
import com.anna.util.MyApplication;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

import java.util.Set;

public class Preferences extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
        // Needed for Superclass
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        addModulesToPreferenceScreen();
        addVoiceRecognitionFeedbackSetting();
    }

    public PreferenceCategory createCategory(String categoryTitle) {
        PreferenceScreen preferenceScreen = this.getPreferenceScreen();

        PreferenceCategory preferenceCategory = new PreferenceCategory(preferenceScreen.getContext());
        preferenceCategory.setTitle(categoryTitle);
        preferenceScreen.addPreference(preferenceCategory);

        return preferenceCategory;
    }

    public void addPreference(PreferenceCategory preferenceCategory, String title) {
        PreferenceScreen preferenceScreen = this.getPreferenceScreen();

        Preference preference = new Preference(preferenceScreen.getContext());
        preference.setKey(title);
        preference.setTitle(title);
        preferenceCategory.addPreference(preference);
    }

    public void addModulesToPreferenceScreen() {

        final MultiSelectListPreference listPreference = (MultiSelectListPreference) findPreference("modules");
        String[] keys = new String[Module.modules.size()];
        String[] values = new String[Module.modules.size()];

        for (int i = 0; i < Module.modules.size(); i++) {
            keys[i] = Module.modules.get(i).getName();
            values[i] = Integer.toString(i);
        }
        listPreference.setEntries(keys);
        listPreference.setEntryValues(values);
        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                for (String s : (Set<String>) newValue) {
                    Module module = Module.modules.get(Integer.parseInt(s));
                    module.toogleStatus();
                }
                MyApplication.application.getDashboard().updateDashBoard();
                return false;
            }
        });
    }

    public void addVoiceRecognitionFeedbackSetting(){
        PreferenceCategory preferenceCategory = createCategory(getString(R.string.voice_recognition));
    }

}

