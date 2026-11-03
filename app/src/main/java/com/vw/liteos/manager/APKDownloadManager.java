package com.vw.liteos.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.vw.liteos.model.StoreApp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class APKDownloadManager {
    public interface DownloadCallback {
        void onProgress(int progressPercent, long downloadedBytes, long totalBytes);
        void onSuccess(File apkFile, String sha256);
        void onError(String errorMessage);
    }

    private final Context context;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private Future<?> currentTask;
    private volatile boolean isCancelled = false;

    public APKDownloadManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public void downloadApk(StoreApp app, DownloadCallback callback) {
        isCancelled = false;
        if (currentTask != null && !currentTask.isDone()) {
            currentTask.cancel(true);
        }

        currentTask = executor.submit(() -> {
            HttpURLConnection conn = null;
            InputStream in = null;
            FileOutputStream out = null;

            try {
                if (app.getApkUrl() == null || app.getApkUrl().trim().isEmpty()) {
                    postError(callback, "No direct APK URL provided. App can be opened via Google Play.");
                    return;
                }

                File downloadDir = new File(context.getExternalCacheDir(), "apks");
                if (!downloadDir.exists()) downloadDir.mkdirs();

                File outputFile = new File(downloadDir, app.getPackageName() + "_" + app.getVersion() + ".apk");
                if (outputFile.exists()) {
                    outputFile.delete();
                }

                URL url = new URL(app.getApkUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(30000);
                conn.setInstanceFollowRedirects(true);
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 9; VW Smart TV) VW Lite OS");
                conn.connect();

                int responseCode = conn.getResponseCode();
                // Handle 301/302 redirects manually if necessary
                if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                    String newUrl = conn.getHeaderField("Location");
                    conn.disconnect();
                    url = new URL(newUrl);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(15000);
                    conn.setReadTimeout(30000);
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 9; VW Smart TV) VW Lite OS");
                    conn.connect();
                    responseCode = conn.getResponseCode();
                }

                if (responseCode != HttpURLConnection.HTTP_OK) {
                    postError(callback, "HTTP Server Error: " + responseCode);
                    return;
                }

                long totalBytes = conn.getContentLengthLong();
                in = conn.getInputStream();
                out = new FileOutputStream(outputFile);

                byte[] buffer = new byte[8192];
                long downloadedBytes = 0;
                int bytesRead;
                MessageDigest digest = MessageDigest.getInstance("SHA-256");

                while ((bytesRead = in.read(buffer)) != -1) {
                    if (isCancelled || Thread.currentThread().isInterrupted()) {
                        out.close();
                        outputFile.delete();
                        postError(callback, "Download cancelled by user");
                        return;
                    }

                    out.write(buffer, 0, bytesRead);
                    digest.update(buffer, 0, bytesRead);
                    downloadedBytes += bytesRead;

                    if (totalBytes > 0) {
                        int progress = (int) ((downloadedBytes * 100) / totalBytes);
                        long currentDown = downloadedBytes;
                        mainHandler.post(() -> callback.onProgress(progress, currentDown, totalBytes));
                    }
                }

                out.flush();

                byte[] hash = digest.digest();
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }

                String sha256 = hexString.toString();
                mainHandler.post(() -> callback.onSuccess(outputFile, sha256));

            } catch (Exception e) {
                postError(callback, "Download error: " + e.getMessage());
            } finally {
                try { if (in != null) in.close(); } catch (Exception ignored) {}
                try { if (out != null) out.close(); } catch (Exception ignored) {}
                if (conn != null) conn.disconnect();
            }
        });
    }

    public void cancelDownload() {
        isCancelled = true;
        if (currentTask != null) {
            currentTask.cancel(true);
        }
    }

    public void clearCache() {
        executor.submit(() -> {
            try {
                File downloadDir = new File(context.getExternalCacheDir(), "apks");
                if (downloadDir.exists() && downloadDir.isDirectory()) {
                    File[] files = downloadDir.listFiles();
                    if (files != null) {
                        for (File f : files) f.delete();
                    }
                }
            } catch (Exception ignored) {}
        });
    }

    private void postError(DownloadCallback callback, String msg) {
        mainHandler.post(() -> callback.onError(msg));
    }
}
