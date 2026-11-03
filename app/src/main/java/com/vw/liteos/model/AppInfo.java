package com.vw.liteos.model;

import android.graphics.drawable.Drawable;

public class AppInfo implements Comparable<AppInfo> {
    private String packageName;
    private String label;
    private Drawable icon;
    private boolean isSystem;
    private boolean isFavorite;
    private long lastUsedTime;
    private int launchCount;
    private String versionName;
    private int versionCode;

    public AppInfo() {}

    public AppInfo(String packageName, String label, Drawable icon, boolean isSystem) {
        this.packageName = packageName;
        this.label = label;
        this.icon = icon;
        this.isSystem = isSystem;
    }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public Drawable getIcon() { return icon; }
    public void setIcon(Drawable icon) { this.icon = icon; }

    public boolean isSystem() { return isSystem; }
    public void setSystem(boolean system) { isSystem = system; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public long getLastUsedTime() { return lastUsedTime; }
    public void setLastUsedTime(long lastUsedTime) { this.lastUsedTime = lastUsedTime; }

    public int getLaunchCount() { return launchCount; }
    public void setLaunchCount(int launchCount) { this.launchCount = launchCount; }

    public String getVersionName() { return versionName; }
    public void setVersionName(String versionName) { this.versionName = versionName; }

    public int getVersionCode() { return versionCode; }
    public void setVersionCode(int versionCode) { this.versionCode = versionCode; }

    @Override
    public int compareTo(AppInfo o) {
        if (this.isFavorite != o.isFavorite) {
            return this.isFavorite ? -1 : 1;
        }
        if (this.label != null && o.label != null) {
            return this.label.compareToIgnoreCase(o.label);
        }
        return 0;
    }
}
