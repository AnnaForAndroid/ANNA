package com.anna.util;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import com.anna.BuildConfig;
import com.anna.R;
import com.anna.preferences.PreferencesHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by PARSEA on 11.10.2016.
 */

public class Module {

    public static List<Module> modules = new ArrayList<>();
    public static List<String> messengerNames = new ArrayList(Arrays.asList("WhatsApp", "Hangouts", "Messenger", "Telegram", "Viber", "Wire", "Signal", "Threema"));
    private static List<String> supportedModuleNames = messengerNames;
    public static List<String> enabledAppNames = new ArrayList<>();
    private static List<String> disabledAppNames = new ArrayList<>();
    private static List<String> moduleNames = new ArrayList<>();
    private boolean active;
    private final String name;
    private final String packageName;
    private static final PreferencesHelper sharedPreferences = new PreferencesHelper(MyApplication.dashboard.getApplicationContext());

    public String getPackageName() {
        return packageName;
    }

    private Module(String name, String packageName) {
        this.name = name;
        this.packageName = packageName;
        this.active = false;

        Module.modules.add(this);
    }

    public static void initializeModules() {
        initializePhoneApp();
        initializeSMSApp();
        initializeMusicApp();
        initializeHereMaps();
        initializeOthers();
        disabledAppNames = moduleNames;
    }

    private static void initializePhoneApp() {
        final Intent mainIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"));
        PackageManager pm = MyApplication.dashboard.getPackageManager();
        final ResolveInfo mInfo = pm.resolveActivity(mainIntent, 0);
        supportedModuleNames.add(mInfo.loadLabel(pm).toString());
    }

    private static void initializeSMSApp() {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_APP_MESSAGING);
        PackageManager pm = MyApplication.dashboard.getPackageManager();
        final List<ResolveInfo> pkgAppsList = pm.queryIntentActivities(mainIntent, 0);
        for (ResolveInfo info : pkgAppsList) {
            supportedModuleNames.add(pm.getApplicationLabel(info.activityInfo.applicationInfo).toString());
        }
    }

    private static void initializeMusicApp() {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_APP_MUSIC);
        PackageManager pm = MyApplication.dashboard.getPackageManager();
        final List<ResolveInfo> pkgAppsList = pm.queryIntentActivities(mainIntent, 0);
        for (ResolveInfo info : pkgAppsList) {
            supportedModuleNames.add(pm.getApplicationLabel(info.activityInfo.applicationInfo).toString());
        }
    }

    private static void initializeHereMaps() {
        new Module("Here Maps", "");
        moduleNames.add("Here Maps");
    }

    private static void initializeOthers() {
        PackageManager pm = MyApplication.dashboard.getPackageManager();
        List<ApplicationInfo> appsInfos = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo info : appsInfos) {
            String appLabel = pm.getApplicationLabel(info).toString();
            if (Module.supportedModuleNames.contains(appLabel)) {
                new Module(appLabel, info.packageName);
                moduleNames.add(appLabel);
            }
        }
    }

    public static Module getModule(String moduleName) {
        for (Module module : modules) {
            if (module.getName().equals(moduleName)) {
                return module;
            }
        }
        return null;
    }

    public boolean isEnabled() {
        return active;
    }

    public String getName() {
        return name;
    }

    public Drawable getIcon() {
        PackageManager pm = MyApplication.dashboard.getPackageManager();
        try {
            return pm.getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            if ("Here Maps".equals(name)) {
                return MyApplication.dashboard.getResources().getDrawable(R.drawable.here_maps);
            } else if (BuildConfig.DEBUG) {
                Log.e("NameNotFoundException", e.getMessage());
            }
            return null;
        }
    }

    private void enable() {
        this.active = true;
        Module.enabledAppNames.add(this.getName());
        Module.disabledAppNames.remove(this.getName());
        save();
    }

    private void disable() {
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

    public void toogleStatus() {
        setSelected(!isEnabled());
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
