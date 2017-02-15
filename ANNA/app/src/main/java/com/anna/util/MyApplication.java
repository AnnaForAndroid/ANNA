package com.anna.util;

import android.app.Application;
import android.content.Context;

import com.anna.preferences.Preferences;

/**
 * Created by PARSEA on 08.11.2016.
 */

public class MyApplication extends Application {

    private static Context context;
    public static Preferences preferences;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
