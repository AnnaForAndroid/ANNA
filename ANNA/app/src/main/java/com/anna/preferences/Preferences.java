package com.anna.preferences; /**
 * Created by PARSEA on 13.02.2017.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;

import com.anna.R;
import com.anna.util.Module;
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

        for (final String name : Module.enabledAppNames) {
            CheckBoxPreference checkBoxPref = new CheckBoxPreference(screen.getContext());
            checkBoxPref.setKey(name + "_CHECKBOX");
            checkBoxPref.setTitle(name);
            checkBoxPref.setChecked(true);

            checkBoxPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Module module = Module.getModule(name);
                    module.toogleStatus();
                    return false;
                }
            });

            category.addPreference(checkBoxPref);
        }

        for (final String name : Module.disabledAppNames) {
            CheckBoxPreference checkBoxPref = new CheckBoxPreference(screen.getContext());
            checkBoxPref.setKey(name + "_CHECKBOX");
            checkBoxPref.setTitle(name);
            checkBoxPref.setChecked(false);

            checkBoxPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Module module = Module.getModule(name);
                    module.toogleStatus();
                    return false;
                }
            });

            category.addPreference(checkBoxPref);
        }
    }

}

