package com.vw.liteos.manager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

public class LiveTVManager {
    private final Context context;

    public LiveTVManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public void launchLiveTV(Context activityContext) {
        PackageManager pm = context.getPackageManager();

        // 1. Check known TV tuner packages
        String[] tunerPackages = {
                "com.mstar.tv.tvplayer.ui",
                "com.cvte.tv",
                "com.android.tv",
                "com.google.android.tv",
                "com.mediatek.tv",
                "com.realtek.tv"
        };

        for (String pkg : tunerPackages) {
            Intent launch = pm.getLaunchIntentForPackage(pkg);
            if (launch != null) {
                launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activityContext.startActivity(launch);
                return;
            }
        }

        // 2. Try standard Android TV Tuner intent
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("content://android.media.tv/passthrough/tuner"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activityContext.startActivity(intent);
            return;
        } catch (Exception ignored) {}

        // 3. Fallback to vendor source switch intent
        try {
            Intent vendorIntent = new Intent("com.mstar.android.intent.action.START_TV_PLAYER");
            vendorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activityContext.startActivity(vendorIntent);
            return;
        } catch (Exception ignored) {}

        Toast.makeText(context, "No hardware Live TV Tuner application detected.", Toast.LENGTH_LONG).show();
    }
}
