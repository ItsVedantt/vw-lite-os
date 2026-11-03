package com.vw.liteos.manager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

import com.vw.liteos.model.AppInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppManager {
    private static final String PREF_NAME = "vw_liteos_apps";
    private static final String KEY_FAVORITES = "fav_packages";
    private static final String KEY_RECENTS = "recent_packages";

    private final Context context;
    private final PackageManager pm;
    private final SharedPreferences prefs;

    public AppManager(Context context) {
        this.context = context.getApplicationContext();
        this.pm = this.context.getPackageManager();
        this.prefs = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public List<AppInfo> getInstalledApps() {
        List<AppInfo> apps = new ArrayList<>();
        Set<String> favs = getFavoritePackageNames();
        Set<String> processedPackages = new HashSet<>();

        // Query standard launcher activities
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> standardApps = pm.queryIntentActivities(mainIntent, 0);

        // Query Leanback TV launcher activities
        Intent leanbackIntent = new Intent(Intent.ACTION_MAIN, null);
        leanbackIntent.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);
        List<ResolveInfo> tvApps = pm.queryIntentActivities(leanbackIntent, 0);

        List<ResolveInfo> combined = new ArrayList<>();
        if (standardApps != null) combined.addAll(standardApps);
        if (tvApps != null) combined.addAll(tvApps);

        String myPackage = context.getPackageName();

        for (ResolveInfo ri : combined) {
            if (ri.activityInfo == null) continue;
            String pkg = ri.activityInfo.packageName;
            if (pkg.equals(myPackage) || processedPackages.contains(pkg)) continue;

            processedPackages.add(pkg);

            try {
                ApplicationInfo ai = pm.getApplicationInfo(pkg, 0);
                String label = ri.loadLabel(pm).toString();
                Drawable icon = ri.loadIcon(pm);
                boolean isSys = (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;

                AppInfo appInfo = new AppInfo(pkg, label, icon, isSys);
                appInfo.setFavorite(favs.contains(pkg));
                apps.add(appInfo);
            } catch (Exception ignored) {}
        }

        Collections.sort(apps);
        return apps;
    }

    public List<AppInfo> getFavoriteApps() {
        List<AppInfo> all = getInstalledApps();
        List<AppInfo> favs = new ArrayList<>();
        for (AppInfo a : all) {
            if (a.isFavorite()) favs.add(a);
        }
        return favs;
    }

    public List<AppInfo> getRecentApps(int maxCount) {
        List<String> recentPkgs = getRecentPackageNames();
        List<AppInfo> all = getInstalledApps();
        List<AppInfo> recents = new ArrayList<>();

        for (String pkg : recentPkgs) {
            for (AppInfo a : all) {
                if (a.getPackageName().equals(pkg)) {
                    recents.add(a);
                    break;
                }
            }
            if (recents.size() >= maxCount) break;
        }
        return recents;
    }

    public void launchApp(String packageName) {
        try {
            Intent intent = pm.getLaunchIntentForPackage(packageName);
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                context.startActivity(intent);
                recordRecentApp(packageName);
            } else {
                Toast.makeText(context, "Cannot open " + packageName, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Launch failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void openAppDetails(String packageName) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + packageName));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "Unable to open app details", Toast.LENGTH_SHORT).show();
        }
    }

    public void requestUninstall(String packageName) {
        try {
            Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
            intent.setData(Uri.parse("package:" + packageName));
            intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            try {
                Intent deleteIntent = new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + packageName));
                deleteIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(deleteIntent);
            } catch (Exception ex) {
                Toast.makeText(context, "Uninstall failed: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean toggleFavorite(String packageName) {
        Set<String> favs = new HashSet<>(getFavoritePackageNames());
        boolean nowFav;
        if (favs.contains(packageName)) {
            favs.remove(packageName);
            nowFav = false;
        } else {
            favs.add(packageName);
            nowFav = true;
        }
        prefs.edit().putStringSet(KEY_FAVORITES, favs).apply();
        return nowFav;
    }

    public boolean isFavorite(String packageName) {
        return getFavoritePackageNames().contains(packageName);
    }

    private Set<String> getFavoritePackageNames() {
        return prefs.getStringSet(KEY_FAVORITES, new HashSet<>());
    }

    private void recordRecentApp(String packageName) {
        List<String> list = getRecentPackageNames();
        list.remove(packageName);
        list.add(0, packageName);
        while (list.size() > 10) {
            list.remove(list.size() - 1);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) sb.append(",");
        }
        prefs.edit().putString(KEY_RECENTS, sb.toString()).apply();
    }

    private List<String> getRecentPackageNames() {
        String raw = prefs.getString(KEY_RECENTS, "");
        List<String> list = new ArrayList<>();
        if (!raw.isEmpty()) {
            String[] parts = raw.split(",");
            for (String p : parts) {
                if (!p.trim().isEmpty()) list.add(p.trim());
            }
        }
        return list;
    }
}
