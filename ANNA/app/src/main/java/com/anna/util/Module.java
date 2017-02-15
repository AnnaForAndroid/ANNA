package com.anna.util;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import com.anna.BuildConfig;
import com.anna.preferences.PreferencesHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by PARSEA on 11.10.2016.
 */

public class Module {

    public static List<Module> modules = new ArrayList<>();
    public static List<String> moduleNames = new ArrayList(Arrays.asList("WhatsApp", "Hangouts", "Messenger", "Telegram", "Viber", "Wire", "Signal", "Threema"));
    public static List<String> packageNames = new ArrayList<>();
    public static List<String> enabledAppNames = new ArrayList<>();
    public static List<String> disabledAppNames = moduleNames;
    private boolean active;
    private final String name;
    private final String packageName;
    private static final PreferencesHelper sharedPreferences = new PreferencesHelper();

    public String getPackageName() {
        return packageName;
    }

    public Module(String name, String packageName) {
        this.name = name;
        this.packageName = packageName;
        Module.packageNames.add(packageName);
        this.active = false;

        Module.modules.add(this);
    }

    public static void initializeModules() {
        initializePhoneApp();
        initializeSMSApp();
        initializeMusicApp();
    }

    public static void initializePhoneApp() {
        Intent i = (new Intent(Intent.ACTION_CALL, Uri.parse("tel:")));
        PackageManager pm = MyApplication.getAppContext().getPackageManager();
        final ResolveInfo mInfo = pm.resolveActivity(i, 0);
        moduleNames.add(mInfo.loadLabel(pm).toString());
    }

    public static void initializeSMSApp() {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_APP_MESSAGING);
        PackageManager pm = MyApplication.getAppContext().getPackageManager();
        final List<ResolveInfo> pkgAppsList = pm.queryIntentActivities(mainIntent, 0);
        for (ResolveInfo info : pkgAppsList) {
            moduleNames.add(pm.getApplicationLabel(info.activityInfo.applicationInfo).toString());
        }
    }

    public static void initializeMusicApp() {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_APP_MUSIC);
        PackageManager pm = MyApplication.getAppContext().getPackageManager();
        final List<ResolveInfo> pkgAppsList = pm.queryIntentActivities(mainIntent, 0);
        for (ResolveInfo info : pkgAppsList) {
            moduleNames.add(pm.getApplicationLabel(info.activityInfo.applicationInfo).toString());
        }
    }

    public boolean isEnabled() {
        return active;
    }

    public String getName() {
        return name;
    }

    public Drawable getIcon() {
        PackageManager pm = MyApplication.getAppContext().getPackageManager();
        try {
            return pm.getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            if (BuildConfig.DEBUG) {
                Log.e("NameNotFoundException", e.getMessage());
            }
            return null;
        }
    }

    public void enable() {
        this.active = true;
        Module.enabledAppNames.add(this.getName());
        Module.disabledAppNames.remove(this.getName());
        save();
    }

    public void disable() {
        this.active = false;
        Module.disabledAppNames.add(this.getName());
        Module.enabledAppNames.remove(this.getName());
        save();
    }

    public void setSelected(boolean selected) {
        if (selected) {
            enable();
        } else {
            disable();
        }
    }

    private void save() {
        sharedPreferences.savePreferences(this.getName(), this, Module.class);
    }

    public static void loadModules() {
        if (Module.modules.isEmpty()) {
            for (String moduleName : Module.moduleNames) {
                Module module = (Module) sharedPreferences.getPreferences(moduleName, Module.class);
                if (module != null) {
                    Module.modules.add(module);
                    Module.packageNames.add(module.getPackageName());
                    if (module.isEnabled()) {
                        Module.enabledAppNames.add(module.getName());
                    } else {
                        Module.disabledAppNames.add(module.getName());
                    }
                }
            }
        }
    }
}
