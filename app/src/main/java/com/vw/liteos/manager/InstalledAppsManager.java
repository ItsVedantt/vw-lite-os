package com.vw.liteos.manager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.vw.liteos.model.StoreApp;

import java.util.List;

public class InstalledAppsManager {
    private final PackageManager pm;

    public InstalledAppsManager(Context context) {
        this.pm = context.getPackageManager();
    }

    public void updateStoreAppStatuses(List<StoreApp> storeApps) {
        if (storeApps == null) return;
        for (StoreApp app : storeApps) {
            checkAndSetStatus(app);
        }
    }

    public void checkAndSetStatus(StoreApp app) {
        try {
            PackageInfo pi = pm.getPackageInfo(app.getPackageName(), 0);
            app.setInstalled(true);
            app.setInstalledVersion(pi.versionName);

            // Compare versions if numeric or string difference exists
            if (app.getVersion() != null && !app.getVersion().equalsIgnoreCase("Latest")) {
                if (!app.getVersion().equalsIgnoreCase(pi.versionName)) {
                    app.setUpdateAvailable(true);
                } else {
                    app.setUpdateAvailable(false);
                }
            } else {
                app.setUpdateAvailable(false);
            }
        } catch (PackageManager.NameNotFoundException e) {
            app.setInstalled(false);
            app.setInstalledVersion(null);
            app.setUpdateAvailable(false);
        }
    }
}
