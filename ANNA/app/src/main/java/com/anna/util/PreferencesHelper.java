package com.anna.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by PARSEA on 19.10.2016.
 */

public class PreferencesHelper {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public PreferencesHelper(Context context, String preferencesName) {
        this.sharedPreferences = context.getSharedPreferences(preferencesName, 0);
        this.editor = sharedPreferences.edit();
    }

    public Object getPreferences(String key, String type) {
        switch (type.toLowerCase()) {
            case "boolean":
                return sharedPreferences.getBoolean(key, false);
            case "string":
                return sharedPreferences.getString(key, "");
            case "int":
                return sharedPreferences.getInt(key, 0);
            case "float":
                return sharedPreferences.getFloat(key, 0);
            case "long":
                return sharedPreferences.getLong(key, 0);
            case "stringset":
                return sharedPreferences.getStringSet(key, null);
        }
        return null;
    }


    public void savePreferences(String key, Object value, String objectType) {
        switch (objectType.toLowerCase()) {
            case "boolean":
                editor.putBoolean(key, (Boolean) value);
                break;
            case "string":
                editor.putString(key, (String) value);
                break;
            case "int":
                editor.putInt(key, (int) value);
                break;
            case "float":
                editor.putFloat(key, (float) value);
                break;
            case "long":
                editor.putLong(key, (long) value);
                break;
            case "stringset":
                editor.putStringSet(key, (Set<String>) value);
                break;
        }
        editor.commit();
    }
}

