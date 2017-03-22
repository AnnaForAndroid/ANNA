package com.anna.util;

import android.app.Application;
import android.content.Context;

import com.anna.dashboard.Dashboard;

/**
 * Created by PARSEA on 08.11.2016.
 */

public class MyApplication extends Application {

    public static Dashboard dashboard;

    public void onCreate() {
        super.onCreate();
    }


}
