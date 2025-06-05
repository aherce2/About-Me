package edu.illinois.cs465.lockedin.models;

import android.graphics.drawable.Drawable;

public class AppItem {
    private String name;
    private Drawable icon;
    private String packageName;

    public AppItem(String name, Drawable icon, String packageName) {
        this.name = name;
        this.icon = icon;
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getPackageName() {
        return packageName;
    }
}