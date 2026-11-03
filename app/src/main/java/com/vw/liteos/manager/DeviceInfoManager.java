package com.vw.liteos.manager;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import com.vw.liteos.model.DeviceInfo;

import java.io.File;

public class DeviceInfoManager {

    public static DeviceInfo getDeviceInfo(Context context) {
        DeviceInfo info = new DeviceInfo();

        info.setManufacturer(Build.MANUFACTURER != null ? Build.MANUFACTURER : "VW");
        info.setModel(Build.MODEL != null ? Build.MODEL : "Smart TV");
        info.setBoard(Build.BOARD != null ? Build.BOARD : "T.PSK518D.PB806");
        info.setHardware(Build.HARDWARE != null ? Build.HARDWARE : "Mali-G31 MP2");
        info.setCpuCores(Runtime.getRuntime().availableProcessors());
        info.setGpuRenderer("Mali-G31 MP2");

        // RAM measurement
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(memInfo);
            info.setTotalRamBytes(memInfo.totalMem);
            info.setAvailRamBytes(memInfo.availMem);
        }

        // Internal Storage measurement
        try {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long totalBlocks = stat.getBlockCountLong();
            long availableBlocks = stat.getAvailableBlocksLong();
            info.setTotalStorageBytes(totalBlocks * blockSize);
            info.setAvailStorageBytes(availableBlocks * blockSize);
        } catch (Exception ignored) {}

        info.setAndroidVersion(Build.VERSION.RELEASE != null ? Build.VERSION.RELEASE : "9.0");
        info.setApiLevel(Build.VERSION.SDK_INT);
        info.setFirmwareBuild(Build.DISPLAY != null ? Build.DISPLAY : "20260227_153820");
        info.setSupportedAbis(Build.SUPPORTED_ABIS);

        return info;
    }
}
