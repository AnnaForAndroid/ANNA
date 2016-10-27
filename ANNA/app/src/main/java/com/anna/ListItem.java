package com.anna;

/**
 * Created by D062427 on 27.10.2016.
 */

public class ListItem {

    private String moduleName;
    private String moduleCBox;

    public ListItem(String moduleName){
        super();
        this.moduleName = moduleName;
        this.moduleCBox = moduleCBox;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleCBox() {
        return moduleCBox;
    }

    public void setModuleCBox(String moduleCBox) {
        this.moduleCBox = moduleCBox;
    }
}
