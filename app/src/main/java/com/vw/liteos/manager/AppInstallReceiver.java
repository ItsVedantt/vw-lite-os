package com.vw.liteos.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AppInstallReceiver extends BroadcastReceiver {
    public interface PackageChangeListener {
        void onPackageChanged();
    }

    private static PackageChangeListener listener;

    public static void setListener(PackageChangeListener l) {
        listener = l;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;
        String action = intent.getAction();
        if (Intent.ACTION_PACKAGE_ADDED.equals(action)
                || Intent.ACTION_PACKAGE_REMOVED.equals(action)
                || Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
            if (listener != null) {
                listener.onPackageChanged();
            }
        }
    }
}
