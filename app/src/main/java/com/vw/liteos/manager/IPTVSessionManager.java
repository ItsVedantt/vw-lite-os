package com.vw.liteos.manager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * IPTVSessionManager stores and restores the active/last IPTV playback session.
 * Used for auto-resuming playback upon TV boot or launcher startup.
 */
public class IPTVSessionManager {

    private static final String PREFS_NAME = "vw_lite_os_iptv_session";

    private static final String KEY_PLAYER = "sess_player";
    private static final String KEY_PLAYLIST_NAME = "sess_playlist_name";
    private static final String KEY_PLAYLIST_URL = "sess_playlist_url";
    private static final String KEY_CHANNEL_ID = "sess_channel_id";
    private static final String KEY_CHANNEL_NAME = "sess_channel_name";
    private static final String KEY_STREAM_URL = "sess_stream_url";
    private static final String KEY_CATEGORY = "sess_category";

    private static final String DEFAULT_PLAYER = "VW IPTV Player (Built-in ExoPlayer)";
    private static final String DEFAULT_PLAYLIST_NAME = "iptv-org / India Channels (Public M3U)";
    private static final String DEFAULT_PLAYLIST_URL = "https://iptv-org.github.io/iptv/countries/in.m3u";
    private static final String DEFAULT_CHANNEL_NAME = "NDTV 24x7 (Live HD)";
    private static final String DEFAULT_STREAM_URL = "https://www.sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4";
    private static final String DEFAULT_CATEGORY = "News & Info";

    private final SharedPreferences prefs;

    public IPTVSessionManager(Context context) {
        this.prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public String getLastPlayer() {
        return prefs.getString(KEY_PLAYER, DEFAULT_PLAYER);
    }

    public String getLastPlaylistName() {
        return prefs.getString(KEY_PLAYLIST_NAME, DEFAULT_PLAYLIST_NAME);
    }

    public String getLastPlaylistUrl() {
        return prefs.getString(KEY_PLAYLIST_URL, DEFAULT_PLAYLIST_URL);
    }

    public String getLastChannelName() {
        return prefs.getString(KEY_CHANNEL_NAME, DEFAULT_CHANNEL_NAME);
    }

    public String getLastStreamUrl() {
        return prefs.getString(KEY_STREAM_URL, DEFAULT_STREAM_URL);
    }

    public String getLastCategory() {
        return prefs.getString(KEY_CATEGORY, DEFAULT_CATEGORY);
    }

    public void saveSession(String channelName, String streamUrl, String category, String playlistName) {
        prefs.edit()
                .putString(KEY_CHANNEL_NAME, channelName != null ? channelName : DEFAULT_CHANNEL_NAME)
                .putString(KEY_STREAM_URL, streamUrl != null ? streamUrl : DEFAULT_STREAM_URL)
                .putString(KEY_CATEGORY, category != null ? category : DEFAULT_CATEGORY)
                .putString(KEY_PLAYLIST_NAME, playlistName != null ? playlistName : DEFAULT_PLAYLIST_NAME)
                .apply();
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }
}
