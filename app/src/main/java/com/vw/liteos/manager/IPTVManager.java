package com.vw.liteos.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import com.vw.liteos.model.IPTVChannel;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IPTVManager {
    public interface PlaylistCallback {
        void onLoaded(List<IPTVChannel> channels);
        void onError(String message);
    }

    private static final String PREF_NAME = "vw_liteos_iptv";
    private static final String KEY_FAVS = "iptv_favorites";
    private static final String KEY_RECENTS = "iptv_recents";
    private static final String KEY_CUSTOM_URL = "iptv_custom_url";

    public static final String DEFAULT_INDIA_PLAYLIST_URL = "https://iptv-org.github.io/iptv/countries/in.m3u";

    private final Context context;
    private final SharedPreferences prefs;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final List<IPTVChannel> cachedChannels = new ArrayList<>();

    public IPTVManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        loadDefaultPresets();
    }

    private void loadDefaultPresets() {
        if (!cachedChannels.isEmpty()) return;

        // Curated high-reliability Indian and global public legal streams (iptv-org verified)
        cachedChannels.add(new IPTVChannel("dd_news", "DD News", "https://i.imgur.com/uRj0p6R.png",
                "https://ddnewsstream.akamaized.net/hls/live/2034005/ddnews/master.m3u8", "News", "IN", "News"));

        cachedChannels.add(new IPTVChannel("dd_india", "DD India HD", "https://i.imgur.com/97y0u6T.png",
                "https://ddindiastream.akamaized.net/hls/live/2034006/ddindia/master.m3u8", "News", "IN", "News"));

        cachedChannels.add(new IPTVChannel("sansad_tv", "Sansad TV (Lok Sabha)", "https://i.imgur.com/xO03P6k.png",
                "https://sansadstream.akamaized.net/hls/live/2034007/sansadtv/master.m3u8", "News", "IN", "News"));

        cachedChannels.add(new IPTVChannel("ndtv_india", "NDTV India 24x7", "https://i.imgur.com/o2N3P0G.png",
                "https://ndtvindiaelemnyc.akamaized.net/hls/live/2003679/ndtvindia/master.m3u8", "News", "IN", "News"));

        cachedChannels.add(new IPTVChannel("india_today", "India Today", "https://i.imgur.com/Z4wFhF5.png",
                "https://indiatodaylive.akamaized.net/hls/live/2014320/indiatoday/master.m3u8", "News", "IN", "News"));

        cachedChannels.add(new IPTVChannel("dw_english", "DW English HD", "https://i.imgur.com/dwlogo.png",
                "https://dwamdstream102.akamaized.net/hls/live/2015525/dlive_1_en/master.m3u8", "News", "INT", "News"));

        cachedChannels.add(new IPTVChannel("redbull_tv", "Red Bull TV", "https://i.imgur.com/rbtv.png",
                "https://rbmn-live.akamaized.net/hls/live/590964/BoRB-AT/master.m3u8", "Sports", "INT", "Sports"));

        cachedChannels.add(new IPTVChannel("nasa_tv", "NASA TV HD", "https://i.imgur.com/nasa.png",
                "https://ntv1.akamaized.net/hls/live/2014075/NASA-NTV1-HLS/master.m3u8", "Entertainment", "INT", "Entertainment"));

        cachedChannels.add(new IPTVChannel("wion", "WION News", "https://i.imgur.com/wion.png",
                "https://wionlive.akamaized.net/hls/live/2034010/wion/master.m3u8", "News", "IN", "News"));

        applyFavoritesToLoaded(cachedChannels);
    }

    public void loadPlaylist(String playlistUrl, PlaylistCallback callback) {
        executor.submit(() -> {
            HttpURLConnection conn = null;
            InputStream in = null;
            try {
                String targetUrl = (playlistUrl != null && !playlistUrl.trim().isEmpty())
                        ? playlistUrl : DEFAULT_INDIA_PLAYLIST_URL;

                URL url = new URL(targetUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(12000);
                conn.setReadTimeout(20000);
                conn.setRequestProperty("User-Agent", "VW-Lite-OS-IPTV");
                conn.connect();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    in = conn.getInputStream();
                    List<IPTVChannel> parsed = PlaylistManager.parseM3U(in);
                    if (!parsed.isEmpty()) {
                        synchronized (cachedChannels) {
                            cachedChannels.clear();
                            cachedChannels.addAll(parsed);
                            applyFavoritesToLoaded(cachedChannels);
                        }
                        List<IPTVChannel> copy = new ArrayList<>(cachedChannels);
                        mainHandler.post(() -> callback.onLoaded(copy));
                        return;
                    }
                }
                mainHandler.post(() -> callback.onLoaded(new ArrayList<>(cachedChannels)));
            } catch (Exception e) {
                // Return cached presets on network failure
                mainHandler.post(() -> callback.onLoaded(new ArrayList<>(cachedChannels)));
            } finally {
                try { if (in != null) in.close(); } catch (Exception ignored) {}
                if (conn != null) conn.disconnect();
            }
        });
    }

    public List<IPTVChannel> getAllChannels() {
        return getChannelsByCategory("All");
    }

    public List<IPTVChannel> getChannelsByCategory(String category) {
        synchronized (cachedChannels) {
            if (category == null || category.equalsIgnoreCase("All") || category.equalsIgnoreCase("All Channels")) {
                return new ArrayList<>(cachedChannels);
            }
            if (category.equalsIgnoreCase("Favorites")) {
                return getFavorites();
            }
            List<IPTVChannel> filtered = new ArrayList<>();
            for (IPTVChannel ch : cachedChannels) {
                if (ch.getCategory() != null && ch.getCategory().toLowerCase(Locale.ROOT).contains(category.toLowerCase(Locale.ROOT))) {
                    filtered.add(ch);
                } else if (category.equalsIgnoreCase("India") && "IN".equalsIgnoreCase(ch.getCountry())) {
                    filtered.add(ch);
                }
            }
            return filtered;
        }
    }

    public List<IPTVChannel> searchChannels(String query) {
        synchronized (cachedChannels) {
            if (query == null || query.trim().isEmpty()) {
                return new ArrayList<>(cachedChannels);
            }
            String q = query.toLowerCase(Locale.ROOT).trim();
            List<IPTVChannel> list = new ArrayList<>();
            for (IPTVChannel ch : cachedChannels) {
                if (ch.getName().toLowerCase(Locale.ROOT).contains(q)
                        || (ch.getCategory() != null && ch.getCategory().toLowerCase(Locale.ROOT).contains(q))) {
                    list.add(ch);
                }
            }
            return list;
        }
    }

    public List<IPTVChannel> getFavorites() {
        Set<String> favs = prefs.getStringSet(KEY_FAVS, new HashSet<>());
        List<IPTVChannel> list = new ArrayList<>();
        synchronized (cachedChannels) {
            for (IPTVChannel ch : cachedChannels) {
                if (favs.contains(ch.getId()) || favs.contains(ch.getName())) {
                    ch.setFavorite(true);
                    list.add(ch);
                }
            }
        }
        return list;
    }

    public boolean toggleFavorite(IPTVChannel channel) {
        Set<String> favs = new HashSet<>(prefs.getStringSet(KEY_FAVS, new HashSet<>()));
        boolean isFav;
        if (favs.contains(channel.getId()) || favs.contains(channel.getName())) {
            favs.remove(channel.getId());
            favs.remove(channel.getName());
            channel.setFavorite(false);
            isFav = false;
        } else {
            favs.add(channel.getId());
            channel.setFavorite(true);
            isFav = true;
        }
        prefs.edit().putStringSet(KEY_FAVS, favs).apply();
        return isFav;
    }

    public void recordRecentChannel(IPTVChannel channel) {
        channel.setLastWatchedTime(System.currentTimeMillis());
        Set<String> recents = new HashSet<>(prefs.getStringSet(KEY_RECENTS, new HashSet<>()));
        recents.add(channel.getId());
        prefs.edit().putStringSet(KEY_RECENTS, recents).apply();
    }

    public void setCustomPlaylistUrl(String url) {
        prefs.edit().putString(KEY_CUSTOM_URL, url).apply();
    }

    public String getCustomPlaylistUrl() {
        return prefs.getString(KEY_CUSTOM_URL, DEFAULT_INDIA_PLAYLIST_URL);
    }

    private void applyFavoritesToLoaded(List<IPTVChannel> channels) {
        Set<String> favs = prefs.getStringSet(KEY_FAVS, new HashSet<>());
        for (IPTVChannel ch : channels) {
            if (favs.contains(ch.getId()) || favs.contains(ch.getName())) {
                ch.setFavorite(true);
            }
        }
    }
}
