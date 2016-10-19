package com.anna;

import java.util.ArrayList;

/**
 * Created by PARSEA on 11.10.2016.
 */

public class Module {

    public static ArrayList<Module> modules = new ArrayList<Module>();
    private boolean active;
    private String name;

    public Module(String name) {
        this.name = name;
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
    }

    public void disable() {
        this.active = false;
    }

    public void setSelected(boolean selected) {
        this.active = selected;
    }
}
