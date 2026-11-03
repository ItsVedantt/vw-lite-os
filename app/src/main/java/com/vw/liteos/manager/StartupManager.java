package com.vw.liteos.manager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * StartupManager manages boot destination and playback preferences for VW Lite OS V4.
 * Target device: VW Smart TV (T.PSK518D.PB806 / Android 9.0 API 28)
 */
public class StartupManager {

    private static final String PREFS_NAME = "vw_lite_os_startup_prefs";

    public static final String DEST_HOME = "home";
    public static final String DEST_IPTV = "iptv";
    public static final String DEST_IPTV_GUIDE = "iptv-guide";
    public static final String DEST_INPUTS = "inputs";
    public static final String DEST_APPS = "apps";
    public static final String DEST_STORE = "store";
    public static final String DEST_NETWORK = "network";

    public static final String THEME_MIDNIGHT = "midnight";
    public static final String THEME_CYBER = "cyber";
    public static final String THEME_EMERALD = "emerald";
    public static final String THEME_COSMIC = "cosmic";

    private static final String KEY_STARTUP_DEST = "pref_startup_destination";
    private static final String KEY_AUTO_RESUME = "pref_auto_resume";
    private static final String KEY_AUTO_PLAY = "pref_auto_play";
    private static final String KEY_NET_RECONNECT = "pref_net_reconnect";
    private static final String KEY_RESTORE_ASPECT = "pref_restore_aspect";
    private static final String KEY_STARTUP_DELAY = "pref_startup_delay";
    private static final String KEY_WALLPAPER_THEME = "pref_wallpaper_theme";

    private final SharedPreferences prefs;

    public StartupManager(Context context) {
        this.prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public String getStartupDestination() {
        return prefs.getString(KEY_STARTUP_DEST, DEST_IPTV);
    }

    public void setStartupDestination(String dest) {
        prefs.edit().putString(KEY_STARTUP_DEST, dest).apply();
    }

    public boolean isAutoResumeEnabled() {
        return prefs.getBoolean(KEY_AUTO_RESUME, true);
    }

    public void setAutoResumeEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_AUTO_RESUME, enabled).apply();
    }

    public boolean isAutoPlayEnabled() {
        return prefs.getBoolean(KEY_AUTO_PLAY, true);
    }

    public void setAutoPlayEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_AUTO_PLAY, enabled).apply();
    }

    public boolean isNetReconnectEnabled() {
        return prefs.getBoolean(KEY_NET_RECONNECT, true);
    }

    public void setNetReconnectEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_NET_RECONNECT, enabled).apply();
    }

    public boolean isRestoreAspectEnabled() {
        return prefs.getBoolean(KEY_RESTORE_ASPECT, true);
    }

    public void setRestoreAspectEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_RESTORE_ASPECT, enabled).apply();
    }

    public int getStartupDelaySeconds() {
        return prefs.getInt(KEY_STARTUP_DELAY, 3);
    }

    public void setStartupDelaySeconds(int seconds) {
        prefs.edit().putInt(KEY_STARTUP_DELAY, seconds).apply();
    }

    public String getWallpaperTheme() {
        return prefs.getString(KEY_WALLPAPER_THEME, THEME_MIDNIGHT);
    }

    public void setWallpaperTheme(String theme) {
        prefs.edit().putString(KEY_WALLPAPER_THEME, theme).apply();
    }

    public void resetDefaults() {
        prefs.edit().clear().apply();
    }
}
