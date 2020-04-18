package com.NKRCreations.mi;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

public class AppLoader {

    private Context context;
    private String name = "hello";
    private int noOfApps = 0;
    private ArrayList<App> allApps;
    private ArrayList<App> systemApps;
    private ArrayList<App> userApps;

    public AppLoader(Context context) {
        this.context = context;
    }

    public void loadAllApps(){
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> applications = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        ArrayList<App> apps = new ArrayList<App>();
        systemApps = new ArrayList<App>();
        userApps = new ArrayList<App>();
        for(ApplicationInfo app : applications){
            App a = new App(app.loadIcon(pm),(String) app.loadLabel(pm), app.packageName);
            if((app.flags & (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP | ApplicationInfo.FLAG_SYSTEM)) > 0) {
                systemApps.add(a);
            } else {
                userApps.add(a);
            }
            apps.add(a);
        }
        applications.clear();
        allApps = apps;
    }

    public ArrayList<App> getAllApps(){
        return allApps;
    }

    public ArrayList<App> getSystemApps() {
        return systemApps;
    }

    public ArrayList<App> getUserApps() {
        return userApps;
    }

    @Override
    public String toString() {
        return "AppLoader{" +
                "name='" + name + '\'' +
                '}';
    }
}
