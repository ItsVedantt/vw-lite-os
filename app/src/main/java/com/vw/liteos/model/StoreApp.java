package com.vw.liteos.model;

import java.io.Serializable;

public class StoreApp implements Serializable {
    private String id;
    private String title;
    private String packageName;
    private String developer;
    private String category;
    private String description;
    private String version;
    private long sizeBytes;
    private String iconUrl;
    private String apkUrl;
    private String source; // "GitHub", "F-Droid", "Official", "PlayStore"
    private int minSdk;
    private String supportedAbis; // "arm64-v8a, armeabi-v7a"
    private boolean isInstalled;
    private String installedVersion;
    private boolean updateAvailable;

    public StoreApp() {}

    public StoreApp(String id, String title, String packageName, String developer,
                    String category, String description, String version, long sizeBytes,
                    String iconUrl, String apkUrl, String source, int minSdk, String supportedAbis) {
        this.id = id;
        this.title = title;
        this.packageName = packageName;
        this.developer = developer;
        this.category = category;
        this.description = description;
        this.version = version;
        this.sizeBytes = sizeBytes;
        this.iconUrl = iconUrl;
        this.apkUrl = apkUrl;
        this.source = source;
        this.minSdk = minSdk;
        this.supportedAbis = supportedAbis;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public String getDeveloper() { return developer; }
    public void setDeveloper(String developer) { this.developer = developer; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(long sizeBytes) { this.sizeBytes = sizeBytes; }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    public String getApkUrl() { return apkUrl; }
    public void setApkUrl(String apkUrl) { this.apkUrl = apkUrl; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public int getMinSdk() { return minSdk; }
    public void setMinSdk(int minSdk) { this.minSdk = minSdk; }

    public String getSupportedAbis() { return supportedAbis; }
    public void setSupportedAbis(String supportedAbis) { this.supportedAbis = supportedAbis; }

    public boolean isInstalled() { return isInstalled; }
    public void setInstalled(boolean installed) { isInstalled = installed; }

    public String getInstalledVersion() { return installedVersion; }
    public void setInstalledVersion(String installedVersion) { this.installedVersion = installedVersion; }

    public boolean isUpdateAvailable() { return updateAvailable; }
    public void setUpdateAvailable(boolean updateAvailable) { this.updateAvailable = updateAvailable; }

    public String getFormattedSize() {
        if (sizeBytes <= 0) return "Unknown size";
        double mb = sizeBytes / (1024.0 * 1024.0);
        return String.format(java.util.Locale.US, "%.1f MB", mb);
    }
}
