package com.vw.liteos.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Handler;
import android.os.Looper;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkManager {
    public interface ConnectivityTestCallback {
        void onResult(boolean isOnline, long latencyMs, String summary);
    }

    private final Context context;
    private final ConnectivityManager cm;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public NetworkManager(Context context) {
        this.context = context.getApplicationContext();
        this.cm = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public boolean isConnected() {
        if (cm == null) return false;
        Network activeNetwork = cm.getActiveNetwork();
        if (activeNetwork == null) return false;
        NetworkCapabilities caps = cm.getNetworkCapabilities(activeNetwork);
        return caps != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    public boolean isWifi() {
        if (cm == null) return false;
        Network activeNetwork = cm.getActiveNetwork();
        if (activeNetwork == null) return false;
        NetworkCapabilities caps = cm.getNetworkCapabilities(activeNetwork);
        return caps != null && caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
    }

    public boolean isEthernet() {
        if (cm == null) return false;
        Network activeNetwork = cm.getActiveNetwork();
        if (activeNetwork == null) return false;
        NetworkCapabilities caps = cm.getNetworkCapabilities(activeNetwork);
        return caps != null && caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
    }

    public String getLocalIpAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        if (sAddr != null && sAddr.indexOf(':') < 0) {
                            return sAddr;
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        return "127.0.0.1";
    }

    public void testInternetConnectivity(ConnectivityTestCallback callback) {
        executor.submit(() -> {
            long start = System.currentTimeMillis();
            boolean reachable = false;
            long latency = -1;
            String note = "Offline";

            try {
                // Test Google HTTP generate_204 probe
                URL url = new URL("https://www.google.com/generate_204");
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setRequestProperty("User-Agent", "VW-Lite-OS-NetTest");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(3000);
                urlc.setReadTimeout(3000);
                urlc.connect();

                int code = urlc.getResponseCode();
                latency = System.currentTimeMillis() - start;
                reachable = (code == 204 || code == 200);
                note = reachable ? "Connected (Latency: " + latency + " ms)" : "HTTP Code " + code;
                urlc.disconnect();
            } catch (Exception e) {
                // Try fallback DNS probe
                try {
                    long dnsStart = System.currentTimeMillis();
                    InetAddress addr = InetAddress.getByName("8.8.8.8");
                    latency = System.currentTimeMillis() - dnsStart;
                    reachable = true;
                    note = "Connected via DNS (Latency: " + latency + " ms)";
                } catch (Exception ex) {
                    note = "Cannot reach internet: " + e.getMessage();
                }
            }

            final boolean finalReachable = reachable;
            final long finalLatency = latency;
            final String finalNote = note;
            mainHandler.post(() -> callback.onResult(finalReachable, finalLatency, finalNote));
        });
    }
}
