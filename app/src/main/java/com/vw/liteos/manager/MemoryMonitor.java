package com.vw.liteos.manager;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

public class MemoryMonitor {
    private static MemoryMonitor instance;
    private final Context appContext;
    private final ActivityManager activityManager;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private MemoryListener listener;
    private boolean isMonitoring = false;

    public interface MemoryListener {
        void onMemoryUpdate(long totalMb, long freeMb, int percentUsed, boolean isLow);
    }

    public static synchronized MemoryMonitor getInstance(Context context) {
        if (instance == null) {
            instance = new MemoryMonitor(context.getApplicationContext());
        }
        return instance;
    }

    private MemoryMonitor(Context context) {
        this.appContext = context;
        this.activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    public void startMonitoring(MemoryListener listener) {
        this.listener = listener;
        if (!isMonitoring) {
            isMonitoring = true;
            poll();
        }
    }

    public void stopMonitoring() {
        isMonitoring = false;
        handler.removeCallbacksAndMessages(null);
    }

    private void poll() {
        if (!isMonitoring) return;

        if (activityManager != null) {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(mi);

            long totalMb = mi.totalMem / (1024 * 1024);
            long freeMb = mi.availMem / (1024 * 1024);
            int percentUsed = totalMb > 0 ? (int) (((mi.totalMem - mi.availMem) * 100) / mi.totalMem) : 0;

            if (listener != null) {
                listener.onMemoryUpdate(totalMb, freeMb, percentUsed, mi.lowMemory);
            }
        }

        // Poll every 5 seconds to minimize battery/CPU usage on 512 MB RAM
        handler.postDelayed(this::poll, 5000);
    }

    public void trimMemory() {
        System.gc();
        Runtime.getRuntime().gc();
    }
}
