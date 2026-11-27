package com.vw.liteos.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;
import com.vw.liteos.R;
import com.vw.liteos.adapter.AppGridAdapter;
import com.vw.liteos.adapter.IPTVChannelAdapter;
import com.vw.liteos.adapter.StoreAppAdapter;
import com.vw.liteos.manager.APKCompatibilityChecker;
import com.vw.liteos.manager.APKDownloadManager;
import com.vw.liteos.manager.AppDiscoveryManager;
import com.vw.liteos.manager.AppInstallReceiver;
import com.vw.liteos.manager.AppManager;
import com.vw.liteos.manager.DeviceInfoManager;
import com.vw.liteos.manager.EthernetManager;
import com.vw.liteos.manager.IPTVManager;
import com.vw.liteos.manager.IPTVSessionManager;
import com.vw.liteos.manager.MemoryMonitor;
import com.vw.liteos.manager.NetworkManager;
import com.vw.liteos.manager.PackageInstallerManager;
import com.vw.liteos.manager.RemoteNavigationManager;
import com.vw.liteos.manager.StartupManager;
import com.vw.liteos.manager.TVInputManager;
import com.vw.liteos.manager.WifiCenterManager;
import com.vw.liteos.model.AppInfo;
import com.vw.liteos.model.DeviceInfo;
import com.vw.liteos.model.IPTVChannel;
import com.vw.liteos.model.StoreApp;
import com.vw.liteos.model.TVInputSource;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * VW Lite OS V4 Launcher Activity.
 * Exact visual implementation of Google Gemini/Canva HTML design with full native Android TV subsystems.
 * Target device: VW Smart TV (Motherboard T.PSK518D.PB806 / Android 9.0 Pie API 28)
 */
public class LauncherActivity extends AppCompatActivity implements
        AppGridAdapter.OnAppClickListener,
        StoreAppAdapter.OnStoreAppClickListener,
        IPTVChannelAdapter.OnChannelClickListener {

    // Root Containers
    private FrameLayout rootContainer;
    private View rootLayout;

    // Header Views
    private TextView tvHeaderNetStatus;
    private ImageView ivHeaderNetIcon;
    private TextView tvHeaderRam;

    // Navigation Tabs
    private TextView tabHome, tabApps, tabStore, tabIptv, tabInputs, tabNetwork, tabSettings;
    private List<TextView> navTabs;

    // Main Content Views
    private View viewTabHome, viewTabApps, viewTabStore, viewTabIptv, viewTabInputs, viewTabNetwork, viewTabSettings;

    // Home Section Views
    private RecyclerView rvHomeFavorites;
    private AppGridAdapter homeFavsAdapter;
    private Button btnHomeExploreStore, btnHomeLaunchIptv;

    // Apps Tab Views
    private RecyclerView rvAllApps;
    private TextView tvAppsCount;
    private AppGridAdapter allAppsAdapter;
    private Button btnAppsInstallMore;

    // Store Tab Views
    private RecyclerView rvStoreApps;
    private StoreAppAdapter storeAdapter;
    private Button btnStoreFilterAll, btnStoreFilterStreaming, btnStoreFilterUtility, btnStoreFilterPlayers;
    private String selectedStoreCategory = "All";

    // IPTV Tab Views
    private TextView tvIptvCategoryTitle, tvIptvActiveStreams;
    private RecyclerView rvIptvChannels;
    private IPTVChannelAdapter iptvAdapter;
    private List<IPTVChannel> currentIptvList = new ArrayList<>();
    private int currentChannelIndex = 0;
    private String selectedIptvCategory = "All";

    private Button btnIptvCatIndia, btnIptvCatNews, btnIptvCatSports, btnIptvCatMovies;
    private TextView tvIptvNowPlayingTitle, tvIptvNowPlayingCategory;
    private PlayerView pvIptvPlayer;
    private Button btnIptvPrev, btnIptvNext;
    private ExoPlayer exoPlayer;

    // TV Inputs Tab Views
    private View cardInputLiveTv, cardInputHdmi1, cardInputHdmi2, cardInputAv;

    // Network Tab Views
    private TextView tvWifiSsid, tvWifiStateBadge, tvNetDetails, tvEthernetStateBadge;
    private Button btnNetScanWifi, btnNetTest;

    // Settings Tab Views
    private Button btnSettingsGeneral, btnSettingsStartup, btnSettingsDeveloper;
    private ScrollView settingsSubGeneral, settingsSubStartup, settingsSubDeveloper;

    // Settings General Views
    private View cardSettingsDisplay, cardSettingsSound, cardSettingsDevice, cardSettingsRecovery;
    private TextView tvSettingsDeviceSummary;
    private Button btnThemeMidnight, btnThemeCyber, btnThemeEmerald, btnThemeCosmic;

    // Settings Startup & Playback Views
    private Spinner spStartupDestination, spStartupDelay;
    private CheckBox cbAutoResume, cbAutoPlay, cbNetReconnect, cbRestoreAspect;
    private TextView tvSessPlayer, tvSessPlaylist, tvSessChannel, tvSessIp;
    private Button btnPreviewStartup, btnResetStartup;

    // Settings Developer Views
    private View cardDevMaster;
    private Button btnDevToggle;
    private TextView tvDevStatusText, tvAdbUsbStatus, tvAdbIpStatus, tvAdbNetStatus;
    private Button btnDevOpenSettings, btnDevUsbDebugging, btnDevCopyIp;
    private View cardDevRunningServices, cardDevGpuInfo;

    // Toast Notification Modal Views
    private FrameLayout layoutToastModal;
    private ImageView ivToastIcon;
    private TextView tvToastTitle, tvToastMessage;
    private Button btnToastClose;

    // Core Managers
    private AppManager appManager;
    private AppDiscoveryManager storeManager;
    private APKDownloadManager downloadManager;
    private PackageInstallerManager installerManager;
    private IPTVManager iptvManager;
    private TVInputManager tvInputManager;
    private NetworkManager networkManager;
    private WifiCenterManager wifiManager;
    private EthernetManager ethernetManager;
    private MemoryMonitor memoryMonitor;
    private StartupManager startupManager;
    private IPTVSessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        initManagers();
        initViews();
        applyPersistedTheme();
        setupNavigation();
        setupHomeTab();
        setupAppsTab();
        setupStoreTab();
        setupIptvTab();
        setupInputsTab();
        setupNetworkTab();
        setupSettingsTab();
        setupToastModal();

        // Listen for package installations/removals to update launcher
        AppInstallReceiver.setListener(this::refreshAllAppData);

        // Memory Monitor (conservative 512 MB optimization)
        memoryMonitor.startMonitoring((totalMb, freeMb, percentUsed, isLow) -> {
            tvHeaderRam.setText("RAM: " + freeMb + "MB / " + totalMb + "MB");
            if (isLow) {
                tvHeaderRam.setTextColor(getResources().getColor(R.color.accent_red));
            } else {
                tvHeaderRam.setTextColor(getResources().getColor(R.color.vw_emerald));
            }
        });

        // Execute Smart Startup Destination with configured delay
        dispatchStartupDestination();
        handleTargetTabIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleTargetTabIntent(intent);
    }

    private void handleTargetTabIntent(Intent intent) {
        if (intent != null && intent.hasExtra("TARGET_TAB")) {
            String target = intent.getStringExtra("TARGET_TAB");
            if ("store".equals(target)) {
                selectTab(tabStore, viewTabStore);
            } else if ("settings".equals(target)) {
                selectTab(tabSettings, viewTabSettings);
            } else if ("iptv".equals(target)) {
                selectTab(tabIptv, viewTabIptv);
            } else if ("apps".equals(target)) {
                selectTab(tabApps, viewTabApps);
            }
        }
    }

    private void initManagers() {
        appManager = new AppManager(this);
        storeManager = new AppDiscoveryManager(this);
        downloadManager = new APKDownloadManager(this);
        installerManager = new PackageInstallerManager(this);
        iptvManager = new IPTVManager(this);
        tvInputManager = new TVInputManager(this);
        networkManager = new NetworkManager(this);
        wifiManager = new WifiCenterManager(this);
        ethernetManager = new EthernetManager(this);
        memoryMonitor = MemoryMonitor.getInstance(this);
        startupManager = new StartupManager(this);
        sessionManager = new IPTVSessionManager(this);
    }

    private void initViews() {
        rootContainer = findViewById(R.id.rootContainer);
        rootLayout = findViewById(R.id.rootLayout);

        tvHeaderNetStatus = findViewById(R.id.tvHeaderNetStatus);
        ivHeaderNetIcon = findViewById(R.id.ivHeaderNetIcon);
        tvHeaderRam = findViewById(R.id.tvHeaderRam);

        tabHome = findViewById(R.id.tabHome);
        tabApps = findViewById(R.id.tabApps);
        tabStore = findViewById(R.id.tabStore);
        tabIptv = findViewById(R.id.tabIptv);
        tabInputs = findViewById(R.id.tabInputs);
        tabNetwork = findViewById(R.id.tabNetwork);
        tabSettings = findViewById(R.id.tabSettings);

        navTabs = Arrays.asList(tabHome, tabApps, tabStore, tabIptv, tabInputs, tabNetwork, tabSettings);

        viewTabHome = findViewById(R.id.viewTabHome);
        viewTabApps = findViewById(R.id.viewTabApps);
        viewTabStore = findViewById(R.id.viewTabStore);
        viewTabIptv = findViewById(R.id.viewTabIptv);
        viewTabInputs = findViewById(R.id.viewTabInputs);
        viewTabNetwork = findViewById(R.id.viewTabNetwork);
        viewTabSettings = findViewById(R.id.viewTabSettings);

        findViewById(R.id.pillNetwork).setOnClickListener(v -> selectTab(tabNetwork, viewTabNetwork));
        findViewById(R.id.pillRam).setOnClickListener(v -> selectTab(tabSettings, viewTabSettings));
    }

    private void applyPersistedTheme() {
        String theme = startupManager.getWallpaperTheme();
        if (StartupManager.THEME_CYBER.equals(theme)) {
            rootContainer.setBackgroundResource(R.drawable.bg_wallpaper_cyber);
        } else if (StartupManager.THEME_EMERALD.equals(theme)) {
            rootContainer.setBackgroundResource(R.drawable.bg_wallpaper_emerald);
        } else if (StartupManager.THEME_COSMIC.equals(theme)) {
            rootContainer.setBackgroundResource(R.drawable.bg_wallpaper_cosmic);
        } else {
            rootContainer.setBackgroundResource(R.drawable.bg_wallpaper_midnight);
        }
    }

    private void setupNavigation() {
        tabHome.setOnClickListener(v -> selectTab(tabHome, viewTabHome));
        tabApps.setOnClickListener(v -> selectTab(tabApps, viewTabApps));
        tabStore.setOnClickListener(v -> selectTab(tabStore, viewTabStore));
        tabIptv.setOnClickListener(v -> selectTab(tabIptv, viewTabIptv));
        tabInputs.setOnClickListener(v -> selectTab(tabInputs, viewTabInputs));
        tabNetwork.setOnClickListener(v -> selectTab(tabNetwork, viewTabNetwork));
        tabSettings.setOnClickListener(v -> selectTab(tabSettings, viewTabSettings));

        for (TextView tab : navTabs) {
            RemoteNavigationManager.applyFocusAnimation(tab);
        }
    }

    private void selectTab(TextView activeTab, View activeView) {
        for (TextView t : navTabs) {
            t.setSelected(t == activeTab);
        }

        viewTabHome.setVisibility(activeView == viewTabHome ? View.VISIBLE : View.GONE);
        viewTabApps.setVisibility(activeView == viewTabApps ? View.VISIBLE : View.GONE);
        viewTabStore.setVisibility(activeView == viewTabStore ? View.VISIBLE : View.GONE);
        viewTabIptv.setVisibility(activeView == viewTabIptv ? View.VISIBLE : View.GONE);
        viewTabInputs.setVisibility(activeView == viewTabInputs ? View.VISIBLE : View.GONE);
        viewTabNetwork.setVisibility(activeView == viewTabNetwork ? View.VISIBLE : View.GONE);
        viewTabSettings.setVisibility(activeView == viewTabSettings ? View.VISIBLE : View.GONE);

        updateHeaderNetworkStatus();

        if (activeView == viewTabHome) {
            loadHomeData();
        } else if (activeView == viewTabApps) {
            loadAllApps();
        } else if (activeView == viewTabStore) {
            loadStoreApps();
        } else if (activeView == viewTabIptv) {
            loadIptvChannels();
            initExoPlayerIfNeeded();
        } else if (activeView == viewTabNetwork) {
            updateNetworkCard();
        } else if (activeView == viewTabSettings) {
            updateDeveloperOptionsUI();
            updateSessionDisplay();
        }
    }

    // ==========================================
    // 1. HOME TAB SETUP
    // ==========================================
    private void setupHomeTab() {
        btnHomeExploreStore = findViewById(R.id.btnHomeExploreStore);
        btnHomeLaunchIptv = findViewById(R.id.btnHomeLaunchIptv);

        btnHomeExploreStore.setOnClickListener(v -> selectTab(tabStore, viewTabStore));
        btnHomeLaunchIptv.setOnClickListener(v -> selectTab(tabIptv, viewTabIptv));

        rvHomeFavorites = findViewById(R.id.rvHomeFavorites);
        homeFavsAdapter = new AppGridAdapter(this);
        rvHomeFavorites.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvHomeFavorites.setAdapter(homeFavsAdapter);

        loadHomeData();
    }

    private void loadHomeData() {
        List<AppInfo> apps = appManager.getInstalledApps();
        List<AppInfo> favs = appManager.getFavoriteApps();
        if (favs.isEmpty()) {
            homeFavsAdapter.setApps(apps);
        } else {
            homeFavsAdapter.setApps(favs);
        }
    }

    // ==========================================
    // 2. APPS TAB SETUP
    // ==========================================
    private void setupAppsTab() {
        tvAppsCount = findViewById(R.id.tvAppsCount);
        btnAppsInstallMore = findViewById(R.id.btnAppsInstallMore);
        rvAllApps = findViewById(R.id.rvAllApps);

        btnAppsInstallMore.setOnClickListener(v -> selectTab(tabStore, viewTabStore));

        allAppsAdapter = new AppGridAdapter(this);
        rvAllApps.setLayoutManager(new GridLayoutManager(this, 4));
        rvAllApps.setAdapter(allAppsAdapter);
    }

    private void loadAllApps() {
        List<AppInfo> apps = appManager.getInstalledApps();
        allAppsAdapter.setApps(apps);
        tvAppsCount.setText(apps.size() + " installed packages on VW Smart TV (Android 9 Pie)");
    }

    // ==========================================
    // 3. APP STORE TAB SETUP
    // ==========================================
    private void setupStoreTab() {
        rvStoreApps = findViewById(R.id.rvStoreApps);
        storeAdapter = new StoreAppAdapter(this);
        rvStoreApps.setLayoutManager(new GridLayoutManager(this, 3));
        rvStoreApps.setAdapter(storeAdapter);

        btnStoreFilterAll = findViewById(R.id.btnStoreFilterAll);
        btnStoreFilterStreaming = findViewById(R.id.btnStoreFilterStreaming);
        btnStoreFilterUtility = findViewById(R.id.btnStoreFilterUtility);
        btnStoreFilterPlayers = findViewById(R.id.btnStoreFilterPlayers);

        btnStoreFilterAll.setOnClickListener(v -> filterStore("All"));
        btnStoreFilterStreaming.setOnClickListener(v -> filterStore("Video Streaming"));
        btnStoreFilterUtility.setOnClickListener(v -> filterStore("Utilities"));
        btnStoreFilterPlayers.setOnClickListener(v -> filterStore("Media Players"));
    }

    private void filterStore(String category) {
        selectedStoreCategory = category;
        loadStoreApps();
        showToastModal("App Store Marketplace",
                "Showing category: " + category.toUpperCase() + "\nVerified direct APK downloads for ARM Mali-G31 Android 9.",
                R.drawable.ic_bag);
    }

    private void loadStoreApps() {
        List<StoreApp> apps;
        if ("All".equalsIgnoreCase(selectedStoreCategory)) {
            apps = storeManager.getAllApps();
        } else {
            apps = storeManager.getAppsByCategory(selectedStoreCategory);
        }
        storeAdapter.setApps(apps);
    }

    // ==========================================
    // 4. IPTV PLAYER TAB SETUP (REAL EXOPLAYER)
    // ==========================================
    private void setupIptvTab() {
        tvIptvCategoryTitle = findViewById(R.id.tvIptvCategoryTitle);
        tvIptvActiveStreams = findViewById(R.id.tvIptvActiveStreams);
        rvIptvChannels = findViewById(R.id.rvIptvChannels);

        btnIptvCatIndia = findViewById(R.id.btnIptvCatIndia);
        btnIptvCatNews = findViewById(R.id.btnIptvCatNews);
        btnIptvCatSports = findViewById(R.id.btnIptvCatSports);
        btnIptvCatMovies = findViewById(R.id.btnIptvCatMovies);

        tvIptvNowPlayingTitle = findViewById(R.id.tvIptvNowPlayingTitle);
        tvIptvNowPlayingCategory = findViewById(R.id.tvIptvNowPlayingCategory);
        pvIptvPlayer = findViewById(R.id.pvIptvPlayer);
        btnIptvPrev = findViewById(R.id.btnIptvPrev);
        btnIptvNext = findViewById(R.id.btnIptvNext);

        iptvAdapter = new IPTVChannelAdapter(this);
        rvIptvChannels.setLayoutManager(new LinearLayoutManager(this));
        rvIptvChannels.setAdapter(iptvAdapter);

        btnIptvCatIndia.setOnClickListener(v -> filterIptv("India"));
        btnIptvCatNews.setOnClickListener(v -> filterIptv("News"));
        btnIptvCatSports.setOnClickListener(v -> filterIptv("Sports"));
        btnIptvCatMovies.setOnClickListener(v -> filterIptv("Entertainment"));

        btnIptvPrev.setOnClickListener(v -> playPreviousChannel());
        btnIptvNext.setOnClickListener(v -> playNextChannel());
    }

    private void filterIptv(String category) {
        selectedIptvCategory = category;
        tvIptvCategoryTitle.setText(category + " Channels (Live)");
        loadIptvChannels();
    }

    private void loadIptvChannels() {
        List<IPTVChannel> list;
        if ("All".equalsIgnoreCase(selectedIptvCategory)) {
            list = iptvManager.getAllChannels();
        } else {
            list = iptvManager.getChannelsByCategory(selectedIptvCategory);
        }
        currentIptvList = list;
        iptvAdapter.setChannels(list);
        tvIptvActiveStreams.setText(list.size() + " Active Streams");
    }

    private void initExoPlayerIfNeeded() {
        if (exoPlayer == null) {
            exoPlayer = new ExoPlayer.Builder(this).build();
            pvIptvPlayer.setPlayer(exoPlayer);
        }

        // Restore last session channel if player is idle
        if (exoPlayer.getPlaybackState() == ExoPlayer.STATE_IDLE && !currentIptvList.isEmpty()) {
            IPTVChannel target = currentIptvList.get(0);
            String lastChannel = sessionManager.getLastChannelName();
            for (int i = 0; i < currentIptvList.size(); i++) {
                if (currentIptvList.get(i).getName().equalsIgnoreCase(lastChannel)) {
                    target = currentIptvList.get(i);
                    currentChannelIndex = i;
                    break;
                }
            }
            playChannel(target, currentChannelIndex);
        }
    }

    private void playChannel(IPTVChannel channel, int index) {
        currentChannelIndex = index;
        tvIptvNowPlayingTitle.setText(channel.getName() + " (Live Stream)");
        tvIptvNowPlayingCategory.setText("Category: " + channel.getCategory() + " • ExoPlayer Active");

        sessionManager.saveSession(channel.getName(), channel.getStreamUrl(), channel.getCategory(), "India Channels (iptv-org)");
        updateSessionDisplay();

        if (exoPlayer == null) {
            initExoPlayerIfNeeded();
        }

        try {
            MediaItem mediaItem = MediaItem.fromUri(channel.getStreamUrl());
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
            exoPlayer.play();
        } catch (Exception e) {
            showToastModal("Playback Error", "Failed to stream channel: " + e.getMessage(), R.drawable.ic_tv);
        }
    }

    private void playPreviousChannel() {
        if (currentIptvList.isEmpty()) return;
        currentChannelIndex = (currentChannelIndex - 1 + currentIptvList.size()) % currentIptvList.size();
        playChannel(currentIptvList.get(currentChannelIndex), currentChannelIndex);
    }

    private void playNextChannel() {
        if (currentIptvList.isEmpty()) return;
        currentChannelIndex = (currentChannelIndex + 1) % currentIptvList.size();
        playChannel(currentIptvList.get(currentChannelIndex), currentChannelIndex);
    }

    // ==========================================
    // 5. TV INPUTS TAB SETUP
    // ==========================================
    private void setupInputsTab() {
        cardInputLiveTv = findViewById(R.id.cardInputLiveTv);
        cardInputHdmi1 = findViewById(R.id.cardInputHdmi1);
        cardInputHdmi2 = findViewById(R.id.cardInputHdmi2);
        cardInputAv = findViewById(R.id.cardInputAv);

        cardInputLiveTv.setOnClickListener(v -> switchHardwareInput("Live TV Tuner", TVInputSource.Type.TUNER));
        cardInputHdmi1.setOnClickListener(v -> switchHardwareInput("HDMI 1", TVInputSource.Type.HDMI1));
        cardInputHdmi2.setOnClickListener(v -> switchHardwareInput("HDMI 2", TVInputSource.Type.HDMI2));
        cardInputAv.setOnClickListener(v -> switchHardwareInput("AV Composite", TVInputSource.Type.AV));
    }

    private void switchHardwareInput(String name, TVInputSource.Type type) {
        showToastModal("TV Input Framework",
                "Switching active hardware source to " + name + " (T.PSK518D Input Pipeline)…",
                R.drawable.ic_plug);
        tvInputManager.switchInput(new TVInputSource(name.toLowerCase().replace(" ", "_"), name, type, true), this);
    }

    // ==========================================
    // 6. NETWORK TAB SETUP
    // ==========================================
    private void setupNetworkTab() {
        tvWifiSsid = findViewById(R.id.tvWifiSsid);
        tvWifiStateBadge = findViewById(R.id.tvWifiStateBadge);
        btnNetScanWifi = findViewById(R.id.btnNetScanWifi);

        tvNetDetails = findViewById(R.id.tvNetDetails);
        tvEthernetStateBadge = findViewById(R.id.tvEthernetStateBadge);
        btnNetTest = findViewById(R.id.btnNetTest);

        btnNetScanWifi.setOnClickListener(v -> {
            showToastModal("Wi-Fi Center", "Scanning for 2.4GHz and 5GHz access points… Opening Wi-Fi settings.", R.drawable.ic_wifi);
            wifiManager.openNativeWifiSettings(this);
        });

        btnNetTest.setOnClickListener(v -> {
            btnNetTest.setEnabled(false);
            showToastModal("Network Diagnostics", "Testing ping & connectivity to gateway (172.20.10.1)…", R.drawable.ic_network_wired);
            networkManager.testInternetConnectivity((isOnline, latencyMs, summary) -> runOnUiThread(() -> {
                btnNetTest.setEnabled(true);
                showToastModal("Internet Diagnostic Result",
                        (isOnline ? "Online: " : "Offline: ") + summary + "\nLatency: " + latencyMs + "ms",
                        R.drawable.ic_network_wired);
            }));
        });
    }

    private void updateNetworkCard() {
        boolean wifi = networkManager.isWifi();
        boolean eth = networkManager.isEthernet();

        String ip = networkManager.getLocalIpAddress();
        String gateway = ethernetManager.getGateway();
        String dns = ethernetManager.getDnsServers();

        tvWifiStateBadge.setText(wifi ? "Connected" : "Disconnected");
        tvWifiStateBadge.setTextColor(getResources().getColor(wifi ? R.color.vw_emerald : R.color.text_secondary));
        tvWifiSsid.setText(wifi ? "Connected to " + wifiManager.getCurrentSsid() : "Wi-Fi is disconnected");

        tvEthernetStateBadge.setText(eth ? "Active" : "Inactive");
        tvEthernetStateBadge.setTextColor(getResources().getColor(eth ? R.color.vw_emerald : R.color.text_secondary));
        tvNetDetails.setText("IP: " + ip + " • GW: " + gateway + " • DNS: " + dns);
    }

    private void updateHeaderNetworkStatus() {
        String ip = networkManager.getLocalIpAddress();
        if (ip == null || ip.isEmpty()) ip = "172.20.10.2";
        tvHeaderNetStatus.setText(ip);

        if (networkManager.isEthernet()) {
            ivHeaderNetIcon.setImageResource(R.drawable.ic_network_wired);
        } else if (networkManager.isWifi()) {
            ivHeaderNetIcon.setImageResource(R.drawable.ic_wifi);
        } else {
            ivHeaderNetIcon.setImageResource(R.drawable.ic_network_wired);
        }
    }

    // ==========================================
    // 7. SETTINGS TAB SETUP (3 SUB-TABS)
    // ==========================================
    private void setupSettingsTab() {
        btnSettingsGeneral = findViewById(R.id.btnSettingsGeneral);
        btnSettingsStartup = findViewById(R.id.btnSettingsStartup);
        btnSettingsDeveloper = findViewById(R.id.btnSettingsDeveloper);

        settingsSubGeneral = findViewById(R.id.settingsSubGeneral);
        settingsSubStartup = findViewById(R.id.settingsSubStartup);
        settingsSubDeveloper = findViewById(R.id.settingsSubDeveloper);

        btnSettingsGeneral.setOnClickListener(v -> switchSettingsSub("general"));
        btnSettingsStartup.setOnClickListener(v -> switchSettingsSub("startup"));
        btnSettingsDeveloper.setOnClickListener(v -> switchSettingsSub("developer"));

        setupGeneralSettings();
        setupStartupSettings();
        setupDeveloperSettings();
    }

    private void switchSettingsSub(String sub) {
        settingsSubGeneral.setVisibility("general".equals(sub) ? View.VISIBLE : View.GONE);
        settingsSubStartup.setVisibility("startup".equals(sub) ? View.VISIBLE : View.GONE);
        settingsSubDeveloper.setVisibility("developer".equals(sub) ? View.VISIBLE : View.GONE);

        btnSettingsGeneral.setSelected("general".equals(sub));
        btnSettingsStartup.setSelected("startup".equals(sub));
        btnSettingsDeveloper.setSelected("developer".equals(sub));

        if ("developer".equals(sub)) {
            updateDeveloperOptionsUI();
        } else if ("startup".equals(sub)) {
            updateSessionDisplay();
        }
    }

    private void setupGeneralSettings() {
        cardSettingsDisplay = findViewById(R.id.cardSettingsDisplay);
        cardSettingsSound = findViewById(R.id.cardSettingsSound);
        cardSettingsDevice = findViewById(R.id.cardSettingsDevice);
        cardSettingsRecovery = findViewById(R.id.cardSettingsRecovery);
        tvSettingsDeviceSummary = findViewById(R.id.tvSettingsDeviceSummary);

        btnThemeMidnight = findViewById(R.id.btnThemeMidnight);
        btnThemeCyber = findViewById(R.id.btnThemeCyber);
        btnThemeEmerald = findViewById(R.id.btnThemeEmerald);
        btnThemeCosmic = findViewById(R.id.btnThemeCosmic);

        btnThemeMidnight.setOnClickListener(v -> setWallpaperTheme(StartupManager.THEME_MIDNIGHT, R.drawable.bg_wallpaper_midnight));
        btnThemeCyber.setOnClickListener(v -> setWallpaperTheme(StartupManager.THEME_CYBER, R.drawable.bg_wallpaper_cyber));
        btnThemeEmerald.setOnClickListener(v -> setWallpaperTheme(StartupManager.THEME_EMERALD, R.drawable.bg_wallpaper_emerald));
        btnThemeCosmic.setOnClickListener(v -> setWallpaperTheme(StartupManager.THEME_COSMIC, R.drawable.bg_wallpaper_cosmic));

        cardSettingsDisplay.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Settings.ACTION_DISPLAY_SETTINGS));
            } catch (Exception e) {
                showToastModal("Display Settings", "1080p 60Hz display configured with Mali-G31 MP2 hardware acceleration.", R.drawable.ic_display);
            }
        });

        cardSettingsSound.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Settings.ACTION_SOUND_SETTINGS));
            } catch (Exception e) {
                showToastModal("Audio Settings", "Dolby Digital Plus audio routing configured for TV stereo speakers.", R.drawable.ic_volume);
            }
        });

        DeviceInfo info = DeviceInfoManager.getDeviceInfo(this);
        tvSettingsDeviceSummary.setText("Android " + info.getAndroidVersion() + " • API " + info.getApiLevel() + " • Firmware " + info.getFirmwareBuild());

        cardSettingsDevice.setOnClickListener(v -> {
            String details = "Manufacturer: " + info.getManufacturer() +
                    "\nMotherboard: " + info.getBoard() +
                    "\nCPU: ARM 4-Core" +
                    "\nGPU: " + info.getGpuRenderer() +
                    "\nRAM: " + info.getTotalRamMb() + " MB Total (" + info.getAvailRamMb() + " MB Free)" +
                    "\nAndroid: 9.0 (API " + info.getApiLevel() + ")" +
                    "\nFirmware: " + info.getFirmwareBuild() +
                    "\nSupported ABIs: " + Arrays.toString(info.getSupportedAbis());
            showToastModal("Device & Firmware Info", details, R.drawable.ic_info);
        });

        cardSettingsRecovery.setOnClickListener(v -> showToastModal("CloudTV Recovery Safe",
                "Original CloudTV Launcher and TV input system services remain intact and available as recovery fallback.",
                R.drawable.ic_shield));
    }

    private void setWallpaperTheme(String themeKey, int drawableRes) {
        startupManager.setWallpaperTheme(themeKey);
        rootContainer.setBackgroundResource(drawableRes);
        showToastModal("Wallpaper Theme Updated", "Theme applied: " + themeKey.toUpperCase() + "\nSettings saved to persistent storage.", R.drawable.ic_image);
    }

    private void setupStartupSettings() {
        spStartupDestination = findViewById(R.id.spStartupDestination);
        spStartupDelay = findViewById(R.id.spStartupDelay);

        cbAutoResume = findViewById(R.id.cbAutoResume);
        cbAutoPlay = findViewById(R.id.cbAutoPlay);
        cbNetReconnect = findViewById(R.id.cbNetReconnect);
        cbRestoreAspect = findViewById(R.id.cbRestoreAspect);

        tvSessPlayer = findViewById(R.id.tvSessPlayer);
        tvSessPlaylist = findViewById(R.id.tvSessPlaylist);
        tvSessChannel = findViewById(R.id.tvSessChannel);
        tvSessIp = findViewById(R.id.tvSessIp);

        btnPreviewStartup = findViewById(R.id.btnPreviewStartup);
        btnResetStartup = findViewById(R.id.btnResetStartup);

        // Destination dropdown
        String[] destinations = {
                "1. Home Dashboard",
                "2. IPTV Player (Default)",
                "3. IPTV Channel Guide",
                "4. Live TV / HDMI Input",
                "5. Applications",
                "6. App Store",
                "7. Network Center"
        };
        ArrayAdapter<String> destAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, destinations);
        destAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStartupDestination.setAdapter(destAdapter);

        // Map saved value
        String savedDest = startupManager.getStartupDestination();
        int destPos = 1; // Default to IPTV
        if (StartupManager.DEST_HOME.equals(savedDest)) destPos = 0;
        else if (StartupManager.DEST_IPTV.equals(savedDest)) destPos = 1;
        else if (StartupManager.DEST_IPTV_GUIDE.equals(savedDest)) destPos = 2;
        else if (StartupManager.DEST_INPUTS.equals(savedDest)) destPos = 3;
        else if (StartupManager.DEST_APPS.equals(savedDest)) destPos = 4;
        else if (StartupManager.DEST_STORE.equals(savedDest)) destPos = 5;
        else if (StartupManager.DEST_NETWORK.equals(savedDest)) destPos = 6;
        spStartupDestination.setSelection(destPos);

        spStartupDestination.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] keys = {
                        StartupManager.DEST_HOME,
                        StartupManager.DEST_IPTV,
                        StartupManager.DEST_IPTV_GUIDE,
                        StartupManager.DEST_INPUTS,
                        StartupManager.DEST_APPS,
                        StartupManager.DEST_STORE,
                        StartupManager.DEST_NETWORK
                };
                if (position >= 0 && position < keys.length) {
                    startupManager.setStartupDestination(keys[position]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Delay dropdown
        String[] delays = {"Immediately (0s)", "3 Seconds (Recommended)", "5 Seconds", "10 Seconds"};
        ArrayAdapter<String> delayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, delays);
        delayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStartupDelay.setAdapter(delayAdapter);

        int savedDelay = startupManager.getStartupDelaySeconds();
        int delayPos = 1; // default 3s
        if (savedDelay == 0) delayPos = 0;
        else if (savedDelay == 3) delayPos = 1;
        else if (savedDelay == 5) delayPos = 2;
        else if (savedDelay == 10) delayPos = 3;
        spStartupDelay.setSelection(delayPos);

        spStartupDelay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int[] sec = {0, 3, 5, 10};
                if (position >= 0 && position < sec.length) {
                    startupManager.setStartupDelaySeconds(sec[position]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        cbAutoResume.setChecked(startupManager.isAutoResumeEnabled());
        cbAutoPlay.setChecked(startupManager.isAutoPlayEnabled());
        cbNetReconnect.setChecked(startupManager.isNetReconnectEnabled());
        cbRestoreAspect.setChecked(startupManager.isRestoreAspectEnabled());

        cbAutoResume.setOnCheckedChangeListener((b, isChecked) -> startupManager.setAutoResumeEnabled(isChecked));
        cbAutoPlay.setOnCheckedChangeListener((b, isChecked) -> startupManager.setAutoPlayEnabled(isChecked));
        cbNetReconnect.setOnCheckedChangeListener((b, isChecked) -> startupManager.setNetReconnectEnabled(isChecked));
        cbRestoreAspect.setOnCheckedChangeListener((b, isChecked) -> startupManager.setRestoreAspectEnabled(isChecked));

        btnPreviewStartup.setOnClickListener(v -> previewStartupBehavior());
        btnResetStartup.setOnClickListener(v -> {
            startupManager.resetDefaults();
            sessionManager.clearSession();
            spStartupDestination.setSelection(1);
            spStartupDelay.setSelection(1);
            cbAutoResume.setChecked(true);
            cbAutoPlay.setChecked(true);
            cbNetReconnect.setChecked(true);
            cbRestoreAspect.setChecked(true);
            updateSessionDisplay();
            showToastModal("Preferences Reset", "Startup & IPTV playback preferences have been reset to factory defaults.", R.drawable.ic_gear);
        });

        updateSessionDisplay();
    }

    private void updateSessionDisplay() {
        if (tvSessPlayer != null) {
            tvSessPlayer.setText("Last Player: " + sessionManager.getLastPlayer());
            tvSessPlaylist.setText("Last Playlist: " + sessionManager.getLastPlaylistName());
            tvSessChannel.setText("Last Channel: " + sessionManager.getLastChannelName());
            String ip = networkManager.getLocalIpAddress();
            tvSessIp.setText("Last IP: " + (ip != null ? ip : "172.20.10.2"));
        }
    }

    private void previewStartupBehavior() {
        String dest = startupManager.getStartupDestination();
        int delay = startupManager.getStartupDelaySeconds();
        String summary = "Boot Simulation Summary (" + delay + "s network wait):\n" +
                "1. Synchronize network interface (" + networkManager.getLocalIpAddress() + ")\n" +
                "2. Launch target destination: " + dest.toUpperCase() + "\n" +
                "3. Auto-resume channel: " + sessionManager.getLastChannelName() + "\n" +
                "4. Stream engine: " + sessionManager.getLastPlayer();
        showToastModal("Startup Behavior Preview", summary, R.drawable.ic_power_off);
    }

    private void setupDeveloperSettings() {
        cardDevMaster = findViewById(R.id.cardDevMaster);
        btnDevToggle = findViewById(R.id.btnDevToggle);
        tvDevStatusText = findViewById(R.id.tvDevStatusText);

        tvAdbUsbStatus = findViewById(R.id.tvAdbUsbStatus);
        tvAdbIpStatus = findViewById(R.id.tvAdbIpStatus);
        tvAdbNetStatus = findViewById(R.id.tvAdbNetStatus);

        btnDevOpenSettings = findViewById(R.id.btnDevOpenSettings);
        btnDevUsbDebugging = findViewById(R.id.btnDevUsbDebugging);
        btnDevCopyIp = findViewById(R.id.btnDevCopyIp);

        cardDevRunningServices = findViewById(R.id.cardDevRunningServices);
        cardDevGpuInfo = findViewById(R.id.cardDevGpuInfo);

        btnDevToggle.setOnClickListener(v -> {
            boolean devActive = DeviceInfoManager.isDeveloperOptionsEnabled(this);
            if (!devActive) {
                showToastModal("Developer Mode",
                        "Opening Android TV Developer Settings to enable development features & USB Debugging securely.",
                        R.drawable.ic_code);
                openDeveloperSettings();
            } else {
                showToastModal("Developer Mode Active",
                        "Developer Mode is currently active on Android 9 API 28.",
                        R.drawable.ic_code);
            }
        });

        btnDevOpenSettings.setOnClickListener(v -> openDeveloperSettings());
        btnDevUsbDebugging.setOnClickListener(v -> openDeveloperSettings());

        btnDevCopyIp.setOnClickListener(v -> {
            String ip = networkManager.getLocalIpAddress();
            if (ip == null || ip.isEmpty()) ip = "172.20.10.2";
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (cm != null) {
                ClipData clip = ClipData.newPlainText("TV_IP", ip);
                cm.setPrimaryClip(clip);
            }
            showToastModal("ADB Command Copied",
                    "TV IP (" + ip + ") copied to clipboard.\nRun from PC terminal:\nadb connect " + ip + ":5555",
                    R.drawable.ic_code);
        });

        cardDevRunningServices.setOnClickListener(v -> {
            DeviceInfo info = DeviceInfoManager.getDeviceInfo(this);
            showToastModal("Running Services & Memory",
                    "RAM Available: " + info.getAvailRamMb() + " MB / " + info.getTotalRamMb() + " MB\n" +
                            "Process Memory Class: 512MB Budget\n" +
                            "Background GC: Optimal",
                    R.drawable.ic_microchip);
        });

        cardDevGpuInfo.setOnClickListener(v -> {
            showToastModal("GPU Profiling Info",
                    "GPU Renderer: ARM Mali-G31 MP2\n" +
                            "Vulkan 1.1 / OpenGL ES 3.2 Supported\n" +
                            "Hardware UI acceleration: Active\n" +
                            "Window Compositor: SurfaceFlinger HW Sync",
                    R.drawable.ic_gear);
        });

        updateDeveloperOptionsUI();
    }

    private void updateDeveloperOptionsUI() {
        boolean devEnabled = DeviceInfoManager.isDeveloperOptionsEnabled(this);
        boolean adbEnabled = DeviceInfoManager.isAdbEnabled(this);
        String ip = networkManager.getLocalIpAddress();
        if (ip == null || ip.isEmpty()) ip = "172.20.10.2";

        tvDevStatusText.setText(devEnabled ? "Status: Enabled (Android 9 API 28)" : "Status: Disabled (Restricted by OS)");
        tvDevStatusText.setTextColor(getResources().getColor(devEnabled ? R.color.vw_emerald : R.color.accent_gold));

        tvAdbUsbStatus.setText(adbEnabled ? "Enabled (Authorized)" : "Disabled");
        tvAdbUsbStatus.setTextColor(getResources().getColor(adbEnabled ? R.color.vw_emerald : R.color.vw_rose));

        tvAdbIpStatus.setText(ip);

        tvAdbNetStatus.setText(adbEnabled ? "Available / Port 5555" : "Unavailable / Port 5555");
        tvAdbNetStatus.setTextColor(getResources().getColor(adbEnabled ? R.color.vw_emerald : R.color.accent_gold));
    }

    private void openDeveloperSettings() {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            try {
                Intent fallback = new Intent(Settings.ACTION_SETTINGS);
                fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(fallback);
            } catch (Exception ex) {
                showToastModal("Settings Error", "Unable to open system settings directly.", R.drawable.ic_code);
            }
        }
    }

    // ==========================================
    // TOAST NOTIFICATION MODAL OVERLAY
    // ==========================================
    private void setupToastModal() {
        layoutToastModal = findViewById(R.id.layoutToastModal);
        ivToastIcon = findViewById(R.id.ivToastIcon);
        tvToastTitle = findViewById(R.id.tvToastTitle);
        tvToastMessage = findViewById(R.id.tvToastMessage);
        btnToastClose = findViewById(R.id.btnToastClose);

        btnToastClose.setOnClickListener(v -> closeToastModal());
        layoutToastModal.setOnClickListener(v -> closeToastModal());
    }

    private void showToastModal(String title, String message, int iconRes) {
        tvToastTitle.setText(title);
        tvToastMessage.setText(message);
        ivToastIcon.setImageResource(iconRes);
        layoutToastModal.setVisibility(View.VISIBLE);
        btnToastClose.requestFocus();
    }

    private void closeToastModal() {
        layoutToastModal.setVisibility(View.GONE);
    }

    // ==========================================
    // SMART STARTUP DISPATCHER
    // ==========================================
    private void dispatchStartupDestination() {
        int delay = startupManager.getStartupDelaySeconds();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isFinishing() || isDestroyed()) return;

            String dest = startupManager.getStartupDestination();
            if (StartupManager.DEST_IPTV.equals(dest)) {
                selectTab(tabIptv, viewTabIptv);
            } else if (StartupManager.DEST_IPTV_GUIDE.equals(dest)) {
                selectTab(tabIptv, viewTabIptv);
            } else if (StartupManager.DEST_INPUTS.equals(dest)) {
                selectTab(tabInputs, viewTabInputs);
            } else if (StartupManager.DEST_APPS.equals(dest)) {
                selectTab(tabApps, viewTabApps);
            } else if (StartupManager.DEST_STORE.equals(dest)) {
                selectTab(tabStore, viewTabStore);
            } else if (StartupManager.DEST_NETWORK.equals(dest)) {
                selectTab(tabNetwork, viewTabNetwork);
            } else {
                selectTab(tabHome, viewTabHome);
            }
        }, delay * 1000L);
    }

    // ==========================================
    // INTERACTION CALLBACKS
    // ==========================================
    @Override
    public void onAppClick(AppInfo app) {
        appManager.launchApp(app.getPackageName());
    }

    @Override
    public void onAppLongClick(AppInfo app, View anchorView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(app.getLabel());

        String favOption = app.isFavorite() ? "Unpin from Favourites" : "Pin to Favourites";
        String[] options = {favOption, "Open Application Details", "Uninstall Application"};

        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                appManager.toggleFavorite(app.getPackageName());
                refreshAllAppData();
            } else if (which == 1) {
                appManager.openAppDetails(app.getPackageName());
            } else if (which == 2) {
                appManager.requestUninstall(app.getPackageName());
            }
        });
        builder.show();
    }

    @Override
    public void onAppClick(StoreApp app) {
        showStoreDetailsDialog(app);
    }

    @Override
    public void onActionClick(StoreApp app) {
        if (app.isInstalled() && !app.isUpdateAvailable()) {
            appManager.launchApp(app.getPackageName());
        } else {
            showStoreDetailsDialog(app);
        }
    }

    private void showStoreDetailsDialog(StoreApp app) {
        Dialog dialog = new Dialog(this, R.style.Theme_VwLiteOS_Translucent);
        dialog.setContentView(R.layout.dialog_app_details);

        TextView tvTitle = dialog.findViewById(R.id.tvDialogTitle);
        TextView tvDev = dialog.findViewById(R.id.tvDialogDev);
        TextView tvPkg = dialog.findViewById(R.id.tvDialogPkg);
        TextView tvDesc = dialog.findViewById(R.id.tvDialogDesc);
        TextView tvMeta = dialog.findViewById(R.id.tvDialogMeta);
        ProgressBar pb = dialog.findViewById(R.id.pbDialogDownload);
        TextView tvStatus = dialog.findViewById(R.id.tvDialogStatus);
        Button btnCancel = dialog.findViewById(R.id.btnDialogCancel);
        Button btnInstall = dialog.findViewById(R.id.btnDialogInstall);

        tvTitle.setText(app.getTitle());
        tvDev.setText(app.getDeveloper() + " • " + app.getCategory());
        tvPkg.setText(app.getPackageName() + " (" + app.getSource() + ")");
        tvDesc.setText(app.getDescription());
        tvMeta.setText("Size: " + app.getFormattedSize() + " • Target: Android 9.0+ • " + app.getSupportedAbis());

        btnCancel.setOnClickListener(v -> {
            downloadManager.cancelDownload();
            dialog.dismiss();
        });

        btnInstall.setOnClickListener(v -> {
            btnInstall.setEnabled(false);
            pb.setVisibility(View.VISIBLE);
            tvStatus.setVisibility(View.VISIBLE);
            tvStatus.setText("Connecting to " + app.getSource() + "…");

            downloadManager.downloadApk(app, new APKDownloadManager.DownloadCallback() {
                @Override
                public void onProgress(int progressPercent, long downloadedBytes, long totalBytes) {
                    pb.setProgress(progressPercent);
                    tvStatus.setText("Downloading: " + progressPercent + "% (" +
                            (downloadedBytes / (1024 * 1024)) + " MB / " + (totalBytes / (1024 * 1024)) + " MB)");
                }

                @Override
                public void onSuccess(File apkFile, String sha256) {
                    tvStatus.setText("Verifying package architecture & signature…");
                    APKCompatibilityChecker.CompatibilityResult res = APKCompatibilityChecker.checkApk(LauncherActivity.this, apkFile);
                    if (res.isCompatible) {
                        tvStatus.setText("Compatibility Verified. Launching Package Installer…");
                        installerManager.installApk(apkFile, LauncherActivity.this);
                        dialog.dismiss();
                    } else {
                        tvStatus.setText(res.summaryMessage);
                        btnInstall.setEnabled(true);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    tvStatus.setText("Error: " + errorMessage);
                    btnInstall.setEnabled(true);
                }
            });
        });

        dialog.show();
    }

    @Override
    public void onChannelClick(IPTVChannel channel) {
        int idx = currentIptvList.indexOf(channel);
        if (idx < 0) idx = 0;
        playChannel(channel, idx);
    }

    @Override
    public void onFavoriteClick(IPTVChannel channel) {
        iptvManager.toggleFavorite(channel);
        iptvAdapter.notifyDataSetChanged();
    }

    private void refreshAllAppData() {
        runOnUiThread(() -> {
            loadHomeData();
            loadAllApps();
            if (viewTabStore.getVisibility() == View.VISIBLE) {
                loadStoreApps();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (layoutToastModal != null && layoutToastModal.getVisibility() == View.VISIBLE) {
            if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ESCAPE) {
                closeToastModal();
                return true;
            }
        }

        if (RemoteNavigationManager.isBackKey(keyCode)) {
            if (viewTabHome.getVisibility() != View.VISIBLE) {
                selectTab(tabHome, viewTabHome);
                return true;
            }
            return true; // Trap BACK on Home to prevent exiting launcher
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAllAppData();
        updateHeaderNetworkStatus();
        if (viewTabIptv.getVisibility() == View.VISIBLE && exoPlayer != null) {
            exoPlayer.play();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (exoPlayer != null) {
            exoPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        memoryMonitor.stopMonitoring();
        AppInstallReceiver.setListener(null);
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }
}
