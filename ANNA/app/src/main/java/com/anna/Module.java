package com.anna;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;

import com.anna.util.PreferencesHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by PARSEA on 11.10.2016.
 */

public class Module {

    public static List<Module> modules = new ArrayList<>();
    public static final List<String> moduleNames = new ArrayList(Arrays.asList("Maps", "WhatsApp", "Hangouts", "Messenger", "Telegram", "Viber", "Wire",
            "Signal", "Threema"));
    public static List<String> packageNames = new ArrayList<>();
    public static List<String> enabledAppNames = new ArrayList<>();
    public static List<String> disabledAppNames = moduleNames;
    private boolean active;
    private final String name;
    private final String packageName;
    private final Drawable icon;
    private static final PreferencesHelper sharedPreferences = new PreferencesHelper("module");

    public String getPackageName() {
        return packageName;
    }

    public Module(String name, String packageName, Drawable icon) {
        this.name = name;
        this.packageName = packageName;
        Module.packageNames.add(packageName);
        this.active = false;
        this.icon = icon;

        Module.modules.add(this);
    }

    public boolean isEnabled() {
        return active;
    }

    public String getName() {
        return name;
    }

    public Drawable getIcon() {
        return icon;
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
