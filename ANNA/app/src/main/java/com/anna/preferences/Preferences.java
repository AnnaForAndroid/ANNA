package com.anna.preferences; /**
 * Created by PARSEA on 13.02.2017.
 */

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.preference.PreferenceScreen;
import android.support.annotation.Nullable;

import android.support.v7.preference.PreferenceCategory;

import com.anna.R;
import com.anna.util.Module;
import com.anna.util.MyApplication;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

public class Preferences extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {

    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        addModulesToPreferenceScreen();
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

        PreferenceScreen screen = this.getPreferenceScreen();

        PreferenceCategory category = createCategory(getString(R.string.modules_title));

        for (String name : Module.enabledAppNames) {
            CheckBoxPreference checkBoxPref = new CheckBoxPreference(screen.getContext());
            checkBoxPref.setKey(name + "_ENABLED");
            checkBoxPref.setTitle(name);
            checkBoxPref.setChecked(true);

            category.addPreference(checkBoxPref);
        }

        for (String name : Module.disabledAppNames) {
            CheckBoxPreference checkBoxPref = new CheckBoxPreference(screen.getContext());
            checkBoxPref.setKey(name + "_DISABLED");
            checkBoxPref.setTitle(name);
            checkBoxPref.setChecked(false);

            category.addPreference(checkBoxPref);
        }
    }

}

