package com.vw.liteos.manager;

import android.content.Context;

import com.vw.liteos.model.StoreApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AppCatalogRepository {
    private final Context context;
    private final List<StoreApp> catalog = new ArrayList<>();

    public AppCatalogRepository(Context context) {
        this.context = context.getApplicationContext();
        loadBuiltInCatalog();
    }

    private void loadBuiltInCatalog() {
        catalog.clear();

        // 1. SmartTube (Video Streaming / Android TV / Popular / Featured)
        catalog.add(new StoreApp(
                "smarttube",
                "SmartTube",
                "com.teamsmart.videomanager.tv",
                "Yuliskov",
                "Video Streaming",
                "Fast, advanced YouTube client for Android TV boxes and TV sets. No ads, SponsorBlock, background playback, and 4K support.",
                "22.56",
                21500000L,
                "https://raw.githubusercontent.com/yuliskov/SmartTube/master/res/icon.png",
                "https://github.com/yuliskov/SmartTube/releases/download/latest/smarttube_beta.apk",
                "GitHub Releases",
                21,
                "arm64-v8a, armeabi-v7a"
        ));

        // 2. VLC for Android (Media Players / Entertainment / Popular)
        catalog.add(new StoreApp(
                "vlc",
                "VLC for Android",
                "org.videolan.vlc",
                "VideoLAN",
                "Media Players",
                "The best open-source video and music player. Plays all formats, network streams, and local media with hardware acceleration.",
                "3.5.4",
                34200000L,
                "https://www.videolan.org/images/icons-VLC/vlc.png",
                "https://get.videolan.org/vlc-android/3.5.4/VLC-Android-3.5.4-arm64-v8a.apk",
                "Official VideoLAN",
                21,
                "arm64-v8a, armeabi-v7a"
        ));

        // 3. Nova Video Player (Media Players / Video Streaming / Android TV)
        catalog.add(new StoreApp(
                "nova_video_player",
                "Nova Video Player",
                "org.courville.nova",
                "Courville",
                "Media Players",
                "Open-source video player designed specifically for Android TV, with SMB, FTP, UPnP/DLNA integration and movie scrapers.",
                "6.2.49",
                48000000L,
                "https://raw.githubusercontent.com/nova-video-player/aos-AVP/master/MediaLib/res/drawable-xxxhdpi/ic_launcher.png",
                "https://github.com/nova-video-player/aos-AVP/releases/download/v6.2.49/nova-arm64-v8a-release-6.2.49.apk",
                "GitHub Releases",
                21,
                "arm64-v8a, armeabi-v7a"
        ));

        // 4. Kodi (Entertainment / Media Players / Featured)
        catalog.add(new StoreApp(
                "kodi",
                "Kodi",
                "org.xbmc.kodi",
                "XBMC Foundation",
                "Entertainment",
                "Ultimate entertainment hub for digital media, home theater, music, movies, TV shows, and IPTV add-ons.",
                "21.0-Omega",
                65000000L,
                "https://kodi.tv/images/kodi-logo.png",
                "https://mirrors.kodi.tv/releases/android/arm64-v8a/kodi-20240406-60199d5-Omega-arm64-v8a.apk",
                "Official Kodi Mirror",
                21,
                "arm64-v8a, armeabi-v7a"
        ));

        // 5. NewPipe (Video Streaming / Music / Utilities)
        catalog.add(new StoreApp(
                "newpipe",
                "NewPipe",
                "org.schabi.newpipe",
                "Team NewPipe",
                "Video Streaming",
                "Lightweight, privacy-respecting video and audio streaming application without proprietary Google Play Services.",
                "0.27.2",
                12400000L,
                "https://newpipe.net/img/logo.svg",
                "https://github.com/TeamNewPipe/NewPipe/releases/download/v0.27.2/NewPipe_v0.27.2.apk",
                "GitHub Releases",
                21,
                "arm64-v8a, armeabi-v7a"
        ));

        // 6. TV Bro (Browsers / Utilities / Android TV)
        catalog.add(new StoreApp(
                "tv_bro",
                "TV Bro Web Browser",
                "com.phlox.tvwebbrowser",
                "Phlox",
                "Browsers",
                "Simple, lightweight web browser tailored specifically for TV remotes with built-in download manager, tabs, and voice search.",
                "2.0.1",
                8200000L,
                "https://raw.githubusercontent.com/truefedex/tv-bro/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png",
                "https://github.com/truefedex/tv-bro/releases/download/v2.0.1/tv-bro-v2.0.1.apk",
                "GitHub Releases",
                21,
                "arm64-v8a, armeabi-v7a"
        ));

        // 7. Send Files to TV (Utilities / File Managers)
        catalog.add(new StoreApp(
                "sftv",
                "Send Files to TV",
                "com.yablio.sendfilestotv",
                "Yablio",
                "Utilities",
                "Easily transfer movies, APK files, music, and documents from your smartphone or PC directly to your TV over Wi-Fi.",
                "1.3.4",
                5200000L,
                "https://play-lh.googleusercontent.com/PZc3K...",
                "https://sendfilestotv.app/download/sftv.apk",
                "Official Developer Website",
                21,
                "arm64-v8a, armeabi-v7a"
        ));

        // 8. FX File Explorer (File Managers / Utilities)
        catalog.add(new StoreApp(
                "fx_file_explorer",
                "FX File Explorer",
                "nextapp.fx",
                "NextApp, Inc.",
                "File Managers",
                "Clean, ad-free file manager with dual-pane TV layout, USB flash drive access, APK installation, and network shares.",
                "9.0.1",
                16400000L,
                "https://www.nextapp.com/fx/fx-icon.png",
                "https://android.nextapp.com/r/fx/FX_9.0.1.apk",
                "Official NextApp",
                21,
                "arm64-v8a, armeabi-v7a"
        ));

        // 9. RetroArch (Games / Android TV)
        catalog.add(new StoreApp(
                "retroarch",
                "RetroArch TV",
                "com.retroarch",
                "Libretro",
                "Games",
                "Multi-system game and emulator frontend with D-pad menu support for classic retro gaming on your television.",
                "1.19.1",
                42000000L,
                "https://www.retroarch.com/images/retroarch-logo.png",
                "https://buildbot.libretro.com/stable/1.19.1/android/RetroArch.apk",
                "Official Libretro",
                21,
                "arm64-v8a, armeabi-v7a"
        ));

        // 10. F-Droid (Utilities / Android TV Applications)
        catalog.add(new StoreApp(
                "fdroid",
                "F-Droid",
                "org.fdroid.fdroid",
                "F-Droid Limited",
                "Utilities",
                "Installable catalogue of FOSS (Free and Open Source Software) applications for Android.",
                "1.21.0",
                9500000L,
                "https://f-droid.org/assets/fdroid-logo.png",
                "https://f-droid.org/F-Droid.apk",
                "F-Droid Official Repository",
                21,
                "arm64-v8a, armeabi-v7a"
        ));

        // 11. JioCinema (Entertainment / Video Streaming)
        catalog.add(new StoreApp(
                "jiocinema",
                "JioCinema TV",
                "com.jio.media.ondemand",
                "Jio Platforms",
                "Video Streaming",
                "Watch live sports, Indian cinema, popular TV shows, and Hollywood blockbusters directly on Android TV.",
                "Latest",
                24000000L,
                "",
                "",
                "Google Play Store",
                21,
                "arm64-v8a, armeabi-v7a"
        ));

        // 12. Disney+ Hotstar (Entertainment / Video Streaming)
        catalog.add(new StoreApp(
                "hotstar",
                "Disney+ Hotstar TV",
                "in.startv.hotstar",
                "Novi Digital",
                "Entertainment",
                "Streaming platform featuring live cricket, Indian blockbusters, Disney, Marvel, Star Wars, and original specials.",
                "Latest",
                28000000L,
                "",
                "",
                "Google Play Store",
                21,
                "arm64-v8a, armeabi-v7a"
        ));
    }

    public List<StoreApp> getAllApps() {
        return new ArrayList<>(catalog);
    }

    public List<StoreApp> getAppsByCategory(String category) {
        if (category == null || category.equalsIgnoreCase("All") || category.equalsIgnoreCase("Featured")) {
            return getAllApps();
        }
        List<StoreApp> results = new ArrayList<>();
        for (StoreApp app : catalog) {
            if (app.getCategory().equalsIgnoreCase(category)) {
                results.add(app);
            }
        }
        return results;
    }

    public List<StoreApp> searchApps(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllApps();
        }
        String q = query.toLowerCase(Locale.ROOT).trim();
        List<StoreApp> results = new ArrayList<>();
        for (StoreApp app : catalog) {
            if (app.getTitle().toLowerCase(Locale.ROOT).contains(q)
                    || app.getDescription().toLowerCase(Locale.ROOT).contains(q)
                    || app.getDeveloper().toLowerCase(Locale.ROOT).contains(q)
                    || app.getCategory().toLowerCase(Locale.ROOT).contains(q)) {
                results.add(app);
            }
        }
        return results;
    }

    public StoreApp findById(String id) {
        for (StoreApp a : catalog) {
            if (a.getId().equalsIgnoreCase(id)) return a;
        }
        return null;
    }
}
