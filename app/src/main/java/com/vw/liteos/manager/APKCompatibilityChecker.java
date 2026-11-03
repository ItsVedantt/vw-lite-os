package com.vw.liteos.manager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.File;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class APKCompatibilityChecker {

    public static class CompatibilityResult {
        public boolean isCompatible;
        public String packageName;
        public String versionName;
        public int versionCode;
        public int minSdk;
        public int targetSdk;
        public boolean abiMatches;
        public boolean sdkMatches;
        public String summaryMessage;

        @Override
        public String toString() {
            return summaryMessage;
        }
    }

    public static CompatibilityResult checkApk(Context context, File apkFile) {
        CompatibilityResult result = new CompatibilityResult();

        if (apkFile == null || !apkFile.exists() || apkFile.length() == 0) {
            result.isCompatible = false;
            result.summaryMessage = "APK file does not exist or is empty.";
            return result;
        }

        PackageManager pm = context.getPackageManager();
        PackageInfo pi = pm.getPackageArchiveInfo(apkFile.getAbsolutePath(),
                PackageManager.GET_ACTIVITIES | PackageManager.GET_SIGNATURES);

        if (pi == null || pi.applicationInfo == null) {
            result.isCompatible = false;
            result.summaryMessage = "Invalid APK: Package parser failed to read manifest.";
            return result;
        }

        result.packageName = pi.packageName;
        result.versionName = pi.versionName;
        result.versionCode = pi.versionCode;
        result.targetSdk = pi.applicationInfo.targetSdkVersion;
        result.minSdk = pi.applicationInfo.minSdkVersion;

        // Check Min SDK against current Android 9.0 (API 28)
        if (result.minSdk > Build.VERSION.SDK_INT) {
            result.sdkMatches = false;
            result.isCompatible = false;
            result.summaryMessage = "Incompatible: Requires Android API " + result.minSdk +
                    ", but TV runs API " + Build.VERSION.SDK_INT + " (Android 9.0).";
            return result;
        } else {
            result.sdkMatches = true;
        }

        // Check Native ABI support (arm64-v8a / armeabi-v7a)
        Set<String> supportedAbis = new HashSet<>(Arrays.asList(Build.SUPPORTED_ABIS));
        boolean hasNativeLibs = false;
        boolean matchedAbi = false;

        try (ZipFile zip = new ZipFile(apkFile)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.startsWith("lib/")) {
                    hasNativeLibs = true;
                    String[] parts = name.split("/");
                    if (parts.length >= 2) {
                        String abi = parts[1];
                        if (supportedAbis.contains(abi)) {
                            matchedAbi = true;
                            break;
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            // If checking zip fails, rely on standard installer
        }

        if (hasNativeLibs && !matchedAbi) {
            result.abiMatches = false;
            result.isCompatible = false;
            result.summaryMessage = "Incompatible CPU Architecture: APK does not contain libraries for "
                    + Arrays.toString(Build.SUPPORTED_ABIS);
            return result;
        } else {
            result.abiMatches = true;
        }

        result.isCompatible = true;
        result.summaryMessage = "Verified Compatible (Android API " + result.minSdk + "+, ABI supported)";
        return result;
    }
}
