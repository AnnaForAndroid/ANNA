package com.anna;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by PARSEA on 11.10.2016.
 */

public class Module {

    public static List<Module> modules = new ArrayList<Module>();
    public static final List<String> moduleNames = new ArrayList(Arrays.asList("Maps", "WhatsApp", "Hangouts", "Pushbullet", "Telegram"));
    public static List<String> packageNames = new ArrayList<String>();
    public static List<String> enabledAppNames = new ArrayList<String>();
    public static List<String> disabledAppNames = moduleNames;
    private boolean active;
    private final String name;
    private final String packageName;

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

    public boolean isEnabled() {
        return active;
    }

    public String getName() {
        return name;
    }

    public void enable() {
        this.active = true;
        Module.enabledAppNames.add(this.getName());
        Module.disabledAppNames.remove(this.getName());
    }

    public void disable() {
        this.active = false;
        Module.disabledAppNames.add(this.getName());
        Module.enabledAppNames.remove(this.getName());
    }

    public void setSelected(boolean selected) {
        if (selected) {
            enable();
        } else {
            disable();
        }
    }
}
