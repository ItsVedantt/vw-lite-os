package com.vw.liteos.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vw.liteos.R;
import com.vw.liteos.adapter.AppGridAdapter;
import com.vw.liteos.adapter.IPTVChannelAdapter;
import com.vw.liteos.adapter.StoreAppAdapter;
import com.vw.liteos.adapter.TVInputAdapter;
import com.vw.liteos.manager.APKCompatibilityChecker;
import com.vw.liteos.manager.APKDownloadManager;
import com.vw.liteos.manager.AppDiscoveryManager;
import com.vw.liteos.manager.AppInstallReceiver;
import com.vw.liteos.manager.AppManager;
import com.vw.liteos.manager.DeviceInfoManager;
import com.vw.liteos.manager.EthernetManager;
import com.vw.liteos.manager.IPTVManager;
import com.vw.liteos.manager.LiveTVManager;
import com.vw.liteos.manager.MemoryMonitor;
import com.vw.liteos.manager.NetworkManager;
import com.vw.liteos.manager.PackageInstallerManager;
import com.vw.liteos.manager.RemoteNavigationManager;
import com.vw.liteos.manager.TVInputManager;
import com.vw.liteos.manager.WifiCenterManager;
import com.vw.liteos.model.AppInfo;
import com.vw.liteos.model.DeviceInfo;
import com.vw.liteos.model.IPTVChannel;
import com.vw.liteos.model.StoreApp;
import com.vw.liteos.model.TVInputSource;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class LauncherActivity extends AppCompatActivity implements
        AppGridAdapter.OnAppClickListener,
        StoreAppAdapter.OnStoreAppClickListener,
        IPTVChannelAdapter.OnChannelClickListener,
        TVInputAdapter.OnInputClickListener {

    // Header Views
    private TextView tvHeaderNetStatus;
    private ImageView ivHeaderNetIcon;
    private TextView tvHeaderRam;

    // Navigation Tabs
    private TextView tabHome, tabApps, tabStore, tabLiveTv, tabHdmi, tabIptv, tabNetwork, tabSettings;
    private List<TextView> navTabs;

    // Content Views
    private View viewTabHome, viewTabApps, viewTabStore, viewTabInputs, viewTabIptv, viewTabNetwork, viewTabSettings;

    // Home Section Views
    private RecyclerView rvHomeFavorites, rvHomeRecents, rvHomeIptvFavs, rvHomeInputs;
    private TextView tvEmptyFavorites, tvEmptyRecents;
    private AppGridAdapter homeFavsAdapter, homeRecentsAdapter;
    private IPTVChannelAdapter homeIptvFavsAdapter;
    private TVInputAdapter homeInputsAdapter;
    private Button btnHeroLaunch;

    // Apps Tab Views
    private RecyclerView rvAllApps;
    private TextView tvAppsCount;
    private AppGridAdapter allAppsAdapter;

    // Store Tab Views
    private EditText etStoreSearch;
    private Button btnStoreSearch, btnStoreClearCache;
    private LinearLayout layoutStoreCategories;
    private RecyclerView rvStoreApps;
    private StoreAppAdapter storeAdapter;
    private String selectedStoreCategory = "Featured";

    // Inputs Tab Views
    private RecyclerView rvInputsTab;
    private TVInputAdapter inputsTabAdapter;

    // IPTV Tab Views
    private EditText etIptvSearch;
    private Button btnIptvRefresh;
    private LinearLayout layoutIptvCategories;
    private RecyclerView rvIptvChannels;
    private IPTVChannelAdapter iptvAdapter;
    private String selectedIptvCategory = "All";

    // Network Tab Views
    private TextView tvNetTypeTitle, tvNetDetails, tvNetDiagnosticResult;
    private Button btnNetTest, btnNetOpenWifi, btnNetOpenEthernet;

    // Settings Tab Views
    private TextView tvSettingsHardware;
    private Button btnSettingsCleanRam, btnSettingsOpenNative, btnSettingsResetLauncher;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        initManagers();
        initViews();
        setupNavigation();
        setupHomeTab();
        setupAppsTab();
        setupStoreTab();
        setupInputsTab();
        setupIptvTab();
        setupNetworkTab();
        setupSettingsTab();

        // Listen for package additions/removals to refresh UI in real time
        AppInstallReceiver.setListener(this::refreshAllAppData);

        // Start Memory Monitor (vital for 512 MB TV)
        memoryMonitor.startMonitoring((totalMb, freeMb, percentUsed, isLow) -> {
            tvHeaderRam.setText("RAM: " + freeMb + " MB Free (" + percentUsed + "% used)");
            if (isLow) {
                tvHeaderRam.setTextColor(getResources().getColor(R.color.accent_red));
            } else {
                tvHeaderRam.setTextColor(getResources().getColor(R.color.accent_cyan));
            }
        });

        // Set default active tab
        selectTab(tabHome, viewTabHome);
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
    }

    private void initViews() {
        tvHeaderNetStatus = findViewById(R.id.tvHeaderNetStatus);
        ivHeaderNetIcon = findViewById(R.id.ivHeaderNetIcon);
        tvHeaderRam = findViewById(R.id.tvHeaderRam);

        tabHome = findViewById(R.id.tabHome);
        tabApps = findViewById(R.id.tabApps);
        tabStore = findViewById(R.id.tabStore);
        tabLiveTv = findViewById(R.id.tabLiveTv);
        tabHdmi = findViewById(R.id.tabHdmi);
        tabIptv = findViewById(R.id.tabIptv);
        tabNetwork = findViewById(R.id.tabNetwork);
        tabSettings = findViewById(R.id.tabSettings);

        navTabs = Arrays.asList(tabHome, tabApps, tabStore, tabLiveTv, tabHdmi, tabIptv, tabNetwork, tabSettings);

        viewTabHome = findViewById(R.id.viewTabHome);
        viewTabApps = findViewById(R.id.viewTabApps);
        viewTabStore = findViewById(R.id.viewTabStore);
        viewTabInputs = findViewById(R.id.viewTabInputs);
        viewTabIptv = findViewById(R.id.viewTabIptv);
        viewTabNetwork = findViewById(R.id.viewTabNetwork);
        viewTabSettings = findViewById(R.id.viewTabSettings);

        findViewById(R.id.pillNetwork).setOnClickListener(v -> selectTab(tabNetwork, viewTabNetwork));
        findViewById(R.id.pillRam).setOnClickListener(v -> selectTab(tabSettings, viewTabSettings));
    }

    private void setupNavigation() {
        tabHome.setOnClickListener(v -> selectTab(tabHome, viewTabHome));
        tabApps.setOnClickListener(v -> selectTab(tabApps, viewTabApps));
        tabStore.setOnClickListener(v -> selectTab(tabStore, viewTabStore));
        tabLiveTv.setOnClickListener(v -> selectTab(tabLiveTv, viewTabInputs));
        tabHdmi.setOnClickListener(v -> selectTab(tabHdmi, viewTabInputs));
        tabIptv.setOnClickListener(v -> selectTab(tabIptv, viewTabIptv));
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
        viewTabInputs.setVisibility(activeView == viewTabInputs ? View.VISIBLE : View.GONE);
        viewTabIptv.setVisibility(activeView == viewTabIptv ? View.VISIBLE : View.GONE);
        viewTabNetwork.setVisibility(activeView == viewTabNetwork ? View.VISIBLE : View.GONE);
        viewTabSettings.setVisibility(activeView == viewTabSettings ? View.VISIBLE : View.GONE);

        updateHeaderNetworkStatus();

        if (activeView == viewTabApps) {
            loadAllApps();
        } else if (activeView == viewTabStore) {
            loadStoreApps();
        } else if (activeView == viewTabIptv) {
            loadIptvChannels();
        } else if (activeView == viewTabNetwork) {
            updateNetworkCard();
        }
    }

    // ==========================================
    // 1. HOME TAB SETUP
    // ==========================================
    private void setupHomeTab() {
        btnHeroLaunch = findViewById(R.id.btnHeroLaunch);
        btnHeroLaunch.setOnClickListener(v -> selectTab(tabStore, viewTabStore));

        rvHomeFavorites = findViewById(R.id.rvHomeFavorites);
        rvHomeRecents = findViewById(R.id.rvHomeRecents);
        rvHomeIptvFavs = findViewById(R.id.rvHomeIptvFavs);
        rvHomeInputs = findViewById(R.id.rvHomeInputs);

        tvEmptyFavorites = findViewById(R.id.tvEmptyFavorites);
        tvEmptyRecents = findViewById(R.id.tvEmptyRecents);

        homeFavsAdapter = new AppGridAdapter(this);
        rvHomeFavorites.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvHomeFavorites.setAdapter(homeFavsAdapter);

        homeRecentsAdapter = new AppGridAdapter(this);
        rvHomeRecents.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvHomeRecents.setAdapter(homeRecentsAdapter);

        homeIptvFavsAdapter = new IPTVChannelAdapter(this);
        rvHomeIptvFavs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvHomeIptvFavs.setAdapter(homeIptvFavsAdapter);

        homeInputsAdapter = new TVInputAdapter(this);
        rvHomeInputs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvHomeInputs.setAdapter(homeInputsAdapter);

        loadHomeData();
    }

    private void loadHomeData() {
        List<AppInfo> favs = appManager.getFavoriteApps();
        homeFavsAdapter.setApps(favs);
        tvEmptyFavorites.setVisibility(favs.isEmpty() ? View.VISIBLE : View.GONE);

        List<AppInfo> recents = appManager.getRecentApps(8);
        homeRecentsAdapter.setApps(recents);
        tvEmptyRecents.setVisibility(recents.isEmpty() ? View.VISIBLE : View.GONE);

        List<IPTVChannel> iptvFavs = iptvManager.getFavorites();
        if (iptvFavs.isEmpty()) {
            iptvFavs = iptvManager.getChannelsByCategory("News");
        }
        homeIptvFavsAdapter.setChannels(iptvFavs);

        homeInputsAdapter.setInputs(tvInputManager.getAvailableInputs());
    }

    // ==========================================
    // 2. APPS TAB SETUP
    // ==========================================
    private void setupAppsTab() {
        rvAllApps = findViewById(R.id.rvAllApps);
        tvAppsCount = findViewById(R.id.tvAppsCount);

        allAppsAdapter = new AppGridAdapter(this);
        rvAllApps.setLayoutManager(new GridLayoutManager(this, 5));
        rvAllApps.setAdapter(allAppsAdapter);
    }

    private void loadAllApps() {
        List<AppInfo> apps = appManager.getInstalledApps();
        allAppsAdapter.setApps(apps);
        tvAppsCount.setText("Installed Applications (" + apps.size() + ")");
    }

    // ==========================================
    // 3. STORE TAB SETUP
    // ==========================================
    private void setupStoreTab() {
        etStoreSearch = findViewById(R.id.etStoreSearch);
        btnStoreSearch = findViewById(R.id.btnStoreSearch);
        btnStoreClearCache = findViewById(R.id.btnStoreClearCache);
        layoutStoreCategories = findViewById(R.id.layoutStoreCategories);
        rvStoreApps = findViewById(R.id.rvStoreApps);

        storeAdapter = new StoreAppAdapter(this);
        rvStoreApps.setLayoutManager(new LinearLayoutManager(this));
        rvStoreApps.setAdapter(storeAdapter);

        btnStoreSearch.setOnClickListener(v -> performStoreSearch());
        btnStoreClearCache.setOnClickListener(v -> {
            downloadManager.clearCache();
            Toast.makeText(this, "Store download cache cleared.", Toast.LENGTH_SHORT).show();
        });

        loadStoreCategoryPills();
    }

    private void loadStoreCategoryPills() {
        layoutStoreCategories.removeAllViews();
        for (String cat : AppDiscoveryManager.CATEGORIES) {
            Button pill = new Button(this, null, 0, R.style.TvNavTab);
            pill.setText(cat);
            pill.setFocusable(true);
            pill.setClickable(true);
            RemoteNavigationManager.applyFocusAnimation(pill);

            pill.setOnClickListener(v -> {
                selectedStoreCategory = cat;
                loadStoreApps();
            });

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(0, 0, 10, 0);
            pill.setLayoutParams(lp);
            layoutStoreCategories.addView(pill);
        }
    }

    private void loadStoreApps() {
        List<StoreApp> apps = storeManager.getAppsByCategory(selectedStoreCategory);
        storeAdapter.setApps(apps);
    }

    private void performStoreSearch() {
        String query = etStoreSearch.getText().toString();
        List<StoreApp> apps = storeManager.search(query);
        storeAdapter.setApps(apps);
    }

    // ==========================================
    // 4. TV INPUTS TAB SETUP
    // ==========================================
    private void setupInputsTab() {
        rvInputsTab = findViewById(R.id.rvInputsTab);
        inputsTabAdapter = new TVInputAdapter(this);
        rvInputsTab.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvInputsTab.setAdapter(inputsTabAdapter);
        inputsTabAdapter.setInputs(tvInputManager.getAvailableInputs());
    }

    // ==========================================
    // 5. IPTV TAB SETUP
    // ==========================================
    private void setupIptvTab() {
        etIptvSearch = findViewById(R.id.etIptvSearch);
        btnIptvRefresh = findViewById(R.id.btnIptvRefresh);
        layoutIptvCategories = findViewById(R.id.layoutIptvCategories);
        rvIptvChannels = findViewById(R.id.rvIptvChannels);

        iptvAdapter = new IPTVChannelAdapter(this);
        rvIptvChannels.setLayoutManager(new GridLayoutManager(this, 5));
        rvIptvChannels.setAdapter(iptvAdapter);

        btnIptvRefresh.setOnClickListener(v -> {
            Toast.makeText(this, "Refreshing India playlist from iptv-org…", Toast.LENGTH_SHORT).show();
            iptvManager.loadPlaylist(iptvManager.getCustomPlaylistUrl(), new IPTVManager.PlaylistCallback() {
                @Override
                public void onLoaded(List<IPTVChannel> channels) {
                    iptvAdapter.setChannels(channels);
                    Toast.makeText(LauncherActivity.this, "Loaded " + channels.size() + " live channels.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(LauncherActivity.this, "Playlist load: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        loadIptvCategoryPills();
    }

    private void loadIptvCategoryPills() {
        layoutIptvCategories.removeAllViews();
        List<String> cats = Arrays.asList("All", "India", "News", "Sports", "Entertainment", "Movies", "Music", "Kids", "Favorites");
        for (String cat : cats) {
            Button pill = new Button(this, null, 0, R.style.TvNavTab);
            pill.setText(cat);
            pill.setFocusable(true);
            pill.setClickable(true);
            RemoteNavigationManager.applyFocusAnimation(pill);

            pill.setOnClickListener(v -> {
                selectedIptvCategory = cat;
                loadIptvChannels();
            });

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(0, 0, 10, 0);
            pill.setLayoutParams(lp);
            layoutIptvCategories.addView(pill);
        }
    }

    private void loadIptvChannels() {
        List<IPTVChannel> list = iptvManager.getChannelsByCategory(selectedIptvCategory);
        iptvAdapter.setChannels(list);
    }

    // ==========================================
    // 6. NETWORK TAB SETUP
    // ==========================================
    private void setupNetworkTab() {
        tvNetTypeTitle = findViewById(R.id.tvNetTypeTitle);
        tvNetDetails = findViewById(R.id.tvNetDetails);
        tvNetDiagnosticResult = findViewById(R.id.tvNetDiagnosticResult);
        btnNetTest = findViewById(R.id.btnNetTest);
        btnNetOpenWifi = findViewById(R.id.btnNetOpenWifi);
        btnNetOpenEthernet = findViewById(R.id.btnNetOpenEthernet);

        btnNetTest.setOnClickListener(v -> {
            tvNetDiagnosticResult.setText("Testing internet connectivity & ping…");
            networkManager.testInternetConnectivity((isOnline, latencyMs, summary) -> {
                tvNetDiagnosticResult.setText("Status: " + summary);
            });
        });

        btnNetOpenWifi.setOnClickListener(v -> wifiManager.openNativeWifiSettings(this));
        btnNetOpenEthernet.setOnClickListener(v -> ethernetManager.openNativeEthernetSettings(this));
    }

    private void updateNetworkCard() {
        boolean wifi = networkManager.isWifi();
        boolean eth = networkManager.isEthernet();

        String ip = networkManager.getLocalIpAddress();
        String gateway = ethernetManager.getGateway();
        String dns = ethernetManager.getDnsServers();

        if (eth) {
            tvNetTypeTitle.setText("Active Connection: Ethernet (Cable)");
            tvNetDetails.setText("IP Address: " + ip + "\nGateway: " + gateway + "\nDNS Servers: " + dns);
        } else if (wifi) {
            String ssid = wifiManager.getCurrentSsid();
            int rssi = wifiManager.getSignalRssi();
            tvNetTypeTitle.setText("Active Connection: Wi-Fi (" + ssid + ", " + rssi + " dBm)");
            tvNetDetails.setText("IP Address: " + ip + "\nGateway: " + gateway + "\nDNS Servers: " + dns);
        } else {
            tvNetTypeTitle.setText("Connection: Offline / Disconnected");
            tvNetDetails.setText("No active network connection.");
        }
    }

    private void updateHeaderNetworkStatus() {
        if (networkManager.isEthernet()) {
            ivHeaderNetIcon.setImageResource(R.drawable.ic_ethernet);
            tvHeaderNetStatus.setText("Ethernet");
        } else if (networkManager.isWifi()) {
            ivHeaderNetIcon.setImageResource(R.drawable.ic_wifi);
            tvHeaderNetStatus.setText(wifiManager.getCurrentSsid());
        } else {
            ivHeaderNetIcon.setImageResource(R.drawable.ic_wifi);
            tvHeaderNetStatus.setText("Offline");
        }
    }

    // ==========================================
    // 7. SETTINGS TAB SETUP
    // ==========================================
    private void setupSettingsTab() {
        tvSettingsHardware = findViewById(R.id.tvSettingsHardware);
        btnSettingsCleanRam = findViewById(R.id.btnSettingsCleanRam);
        btnSettingsOpenNative = findViewById(R.id.btnSettingsOpenNative);
        btnSettingsResetLauncher = findViewById(R.id.btnSettingsResetLauncher);

        DeviceInfo info = DeviceInfoManager.getDeviceInfo(this);
        String details = "Manufacturer: " + info.getManufacturer() +
                "\nMotherboard: " + info.getBoard() +
                "\nCPU: ARM 4-Core" +
                "\nGPU: " + info.getGpuRenderer() +
                "\nRAM Total / Free: " + info.getTotalRamMb() + " MB / " + info.getAvailRamMb() + " MB (Reported: 512 MB)" +
                "\nAndroid Version: " + info.getAndroidVersion() + " (Pie, API " + info.getApiLevel() + ")" +
                "\nFirmware Build: " + info.getFirmwareBuild() +
                "\nSupported ABIs: " + Arrays.toString(info.getSupportedAbis());
        tvSettingsHardware.setText(details);

        btnSettingsCleanRam.setOnClickListener(v -> {
            memoryMonitor.trimMemory();
            Toast.makeText(this, "Memory trimmed and caches cleaned.", Toast.LENGTH_SHORT).show();
        });

        btnSettingsOpenNative.setOnClickListener(v -> {
            try {
                Intent i = new Intent(Settings.ACTION_SETTINGS);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            } catch (Exception e) {
                Toast.makeText(this, "Cannot open native Android settings.", Toast.LENGTH_SHORT).show();
            }
        });

        btnSettingsResetLauncher.setOnClickListener(v -> {
            getSharedPreferences("vw_liteos_apps", MODE_PRIVATE).edit().clear().apply();
            getSharedPreferences("vw_liteos_iptv", MODE_PRIVATE).edit().clear().apply();
            loadHomeData();
            Toast.makeText(this, "Launcher preferences reset to defaults.", Toast.LENGTH_SHORT).show();
        });
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
        IPTVPlayerActivity.start(this, channel, 0);
    }

    @Override
    public void onFavoriteClick(IPTVChannel channel) {
        iptvManager.toggleFavorite(channel);
        iptvAdapter.notifyDataSetChanged();
        homeIptvFavsAdapter.setChannels(iptvManager.getFavorites());
    }

    @Override
    public void onInputClick(TVInputSource source) {
        tvInputManager.switchInput(source, this);
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
        if (RemoteNavigationManager.isBackKey(keyCode)) {
            // Pressing BACK when in any other tab takes user to Home tab
            if (viewTabHome.getVisibility() != View.VISIBLE) {
                selectTab(tabHome, viewTabHome);
                return true;
            }
            // If already on Home tab, do nothing (keep launcher active)
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAllAppData();
        updateHeaderNetworkStatus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        memoryMonitor.stopMonitoring();
        AppInstallReceiver.setListener(null);
    }
}
