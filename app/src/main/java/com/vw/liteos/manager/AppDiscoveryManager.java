package com.vw.liteos.manager;

import android.content.Context;

import com.vw.liteos.model.StoreApp;

import java.util.Arrays;
import java.util.List;

public class AppDiscoveryManager {
    private final AppCatalogRepository repository;
    private final InstalledAppsManager installedManager;

    public static final List<String> CATEGORIES = Arrays.asList(
            "Featured",
            "Popular",
            "Entertainment",
            "Video Streaming",
            "Media Players",
            "Music",
            "Games",
            "Utilities",
            "File Managers",
            "IPTV Players",
            "Browsers",
            "Android TV"
    );

    public AppDiscoveryManager(Context context) {
        this.repository = new AppCatalogRepository(context);
        this.installedManager = new InstalledAppsManager(context);
    }

    public List<StoreApp> getAllApps() {
        List<StoreApp> apps = repository.getAllApps();
        installedManager.updateStoreAppStatuses(apps);
        return apps;
    }

    public List<StoreApp> getAppsByCategory(String category) {
        List<StoreApp> apps = repository.getAppsByCategory(category);
        installedManager.updateStoreAppStatuses(apps);
        return apps;
    }

    public List<StoreApp> search(String query) {
        List<StoreApp> apps = repository.searchApps(query);
        installedManager.updateStoreAppStatuses(apps);
        return apps;
    }

    public StoreApp getAppDetails(String id) {
        StoreApp app = repository.findById(id);
        if (app != null) {
            installedManager.checkAndSetStatus(app);
        }
        return app;
    }
}
