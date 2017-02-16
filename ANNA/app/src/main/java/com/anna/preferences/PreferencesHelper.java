package com.anna.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.anna.util.MyApplication;
import com.google.gson.Gson;

import java.util.Set;

/**
 * Created by PARSEA on 19.10.2016.
 */

public class PreferencesHelper {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Preferences preferences;

    public PreferencesHelper(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = sharedPreferences.edit();
        this.preferences = new Preferences();
    }

    public PreferencesHelper() {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        this.editor = sharedPreferences.edit();
        this.preferences = new Preferences();
    }

    public Object getPreferences(String key, Class objectType) {
        Object value;
        switch (objectType.getSimpleName().toLowerCase()) {
            case "boolean":
                value = sharedPreferences.getBoolean(key, false);
                break;
            case "string":
                value = sharedPreferences.getString(key, "");
                break;
            case "int":
                value = sharedPreferences.getInt(key, 0);
                break;
            case "float":
                value = sharedPreferences.getFloat(key, 0);
                break;
            case "long":
                value = sharedPreferences.getLong(key, 0);
                break;
            case "stringset":
                value = sharedPreferences.getStringSet(key, null);
                break;
            default:
                Gson gson = new Gson();
                String json = sharedPreferences.getString(key, "");
                value = gson.fromJson(json, objectType);
                break;
        }
        return value;
    }


    public void savePreferences(String key, Object value, Class objectType) {
        switch (objectType.getSimpleName().toLowerCase()) {
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
            default:
                Gson gson = new Gson();
                String json = gson.toJson(value);
                editor.putString(key, json);
                break;
        }
        editor.commit();
    }

}
