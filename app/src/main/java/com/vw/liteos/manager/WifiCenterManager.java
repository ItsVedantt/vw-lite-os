package com.vw.liteos.manager;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class WifiCenterManager {
    private final Context context;
    private final WifiManager wifiManager;

    public WifiCenterManager(Context context) {
        this.context = context.getApplicationContext();
        this.wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
    }

    public boolean isWifiEnabled() {
        return wifiManager != null && wifiManager.isWifiEnabled();
    }

    public String getCurrentSsid() {
        if (wifiManager == null) return "Disabled";
        WifiInfo info = wifiManager.getConnectionInfo();
        if (info != null && info.getSSID() != null) {
            String ssid = info.getSSID();
            if (ssid.startsWith("\"") && ssid.endsWith("\"") && ssid.length() > 2) {
                return ssid.substring(1, ssid.length() - 1);
            }
            return ssid;
        }
        return "Not Connected";
    }

    public int getSignalRssi() {
        if (wifiManager == null) return -100;
        WifiInfo info = wifiManager.getConnectionInfo();
        return info != null ? info.getRssi() : -100;
    }

    public List<ScanResult> getNearbyNetworks() {
        if (wifiManager == null) return new ArrayList<>();
        try {
            wifiManager.startScan();
            List<ScanResult> results = wifiManager.getScanResults();
            return results != null ? results : new ArrayList<>();
        } catch (SecurityException ignored) {
            return new ArrayList<>();
        }
    }

    public void openNativeWifiSettings(Context activityContext) {
        try {
            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activityContext.startActivity(intent);
        } catch (Exception e) {
            try {
                Intent alt = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                alt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activityContext.startActivity(alt);
            } catch (Exception ex) {
                Toast.makeText(context, "Cannot open Wi-Fi settings", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
