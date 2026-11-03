package com.vw.liteos.manager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;

public class PackageInstallerManager {
    private final Context context;

    public PackageInstallerManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public boolean canInstallPackages() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return context.getPackageManager().canRequestPackageInstalls();
        }
        return true;
    }

    public void requestInstallPermission(Context activityContext) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activityContext.startActivity(intent);
                Toast.makeText(context, "Please allow 'Install unknown apps' for VW Lite OS", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                // Fallback to security settings
                try {
                    Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activityContext.startActivity(intent);
                } catch (Exception ignored) {}
            }
        }
    }

    public void installApk(File apkFile, Context activityContext) {
        if (apkFile == null || !apkFile.exists()) {
            Toast.makeText(context, "APK file not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!canInstallPackages()) {
            requestInstallPermission(activityContext);
            return;
        }

        try {
            Uri apkUri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".fileprovider",
                    apkFile
            );

            Intent installIntent = new Intent(Intent.ACTION_VIEW);
            installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activityContext.startActivity(installIntent);
        } catch (Exception e) {
            Toast.makeText(context, "Failed to launch installer: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
