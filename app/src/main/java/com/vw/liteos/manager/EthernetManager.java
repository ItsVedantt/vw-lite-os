package com.vw.liteos.manager;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.RouteInfo;
import android.provider.Settings;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.List;

public class EthernetManager {
    private final Context context;
    private final ConnectivityManager cm;

    public EthernetManager(Context context) {
        this.context = context.getApplicationContext();
        this.cm = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public boolean isEthernetConnected() {
        if (cm == null) return false;
        Network activeNetwork = cm.getActiveNetwork();
        if (activeNetwork == null) return false;
        NetworkCapabilities caps = cm.getNetworkCapabilities(activeNetwork);
        return caps != null && caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
    }

    public String getGateway() {
        if (cm == null) return "Unavailable";
        Network active = cm.getActiveNetwork();
        if (active != null) {
            LinkProperties lp = cm.getLinkProperties(active);
            if (lp != null) {
                for (RouteInfo route : lp.getRoutes()) {
                    if (route.isDefaultRoute() && route.getGateway() != null) {
                        return route.getGateway().getHostAddress();
                    }
                }
            }
        }
        return "192.168.1.1";
    }

    public String getDnsServers() {
        if (cm == null) return "8.8.8.8";
        Network active = cm.getActiveNetwork();
        if (active != null) {
            LinkProperties lp = cm.getLinkProperties(active);
            if (lp != null) {
                List<InetAddress> dns = lp.getDnsServers();
                if (dns != null && !dns.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < dns.size(); i++) {
                        sb.append(dns.get(i).getHostAddress());
                        if (i < dns.size() - 1) sb.append(", ");
                    }
                    return sb.toString();
                }
            }
        }
        return "8.8.8.8, 8.8.4.4";
    }

    public void openNativeEthernetSettings(Context activityContext) {
        try {
            Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activityContext.startActivity(intent);
        } catch (Exception e) {
            try {
                Intent alt = new Intent(Settings.ACTION_SETTINGS);
                alt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activityContext.startActivity(alt);
            } catch (Exception ex) {
                Toast.makeText(context, "Cannot open Network settings", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
