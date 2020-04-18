package com.NKRCreations.mi;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

public class App {

    private Drawable icon;
    private String name;
    private String packageName;

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public App(Drawable icon, String name, String packageName) {
        this.icon = icon;
        this.name = name;
        this.packageName = packageName;
    }
}
