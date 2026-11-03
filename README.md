# VW LITE OS V3 – INDEPENDENT ANDROID TV LAUNCHER

**VW Lite OS V3** is a high-performance, independent Android TV launcher, application marketplace, IPTV player, TV input switcher, and system controller engineered specifically for the **VW Smart TV** (Motherboard: `T.PSK518D.PB806`, Mali-G31 MP2 GPU, ARM 4-Core CPU, 512 MB RAM, Android 9.0 Pie API 28).

---

## 🚀 Key Modules & Capabilities

### 1. Main Home Screen
- **Modern Cinematic Dark Theme**: Pure `#0B0E14` slate background with vibrant VW cyan `#00D2FF` and gold accents.
- **Top Navigation Bar**: Smooth D-pad focus tabs: `[Home]`, `[Apps]`, `[Store]`, `[Live TV]`, `[HDMI]`, `[IPTV]`, `[Network]`, `[Settings]`.
- **System Header**:
  - Real-time digital clock and localized date format.
  - Active network badge (Wi-Fi SSID + signal RSSI, or Ethernet indicator).
  - Real-time RAM budget indicator (`RAM: Free MB / 512 MB Budget`).
- **Home Rows**:
  - Featured Application Hero Card with one-click direct explore.
  - Favourite Applications row.
  - Recently Opened Applications row.
  - IPTV Favourites row.
  - Quick TV Inputs switcher.

### 2. VW App Store – Integrated TV Marketplace
- **Categories**: Featured Applications, Popular Applications, Entertainment, Video Streaming, Music, Games, Utilities, File Managers, IPTV Players, Media Players, Browsers, Android TV Applications.
- **Curated Verified Sources**:
  - **F-Droid Official Repository**
  - **Verified GitHub Releases** (SmartTube, NewPipe, Nova Video Player, TV Bro Browser, etc.)
  - **Official Developer Builds** (VLC for Android, Kodi, FX File Explorer, RetroArch, etc.)
  - **Google Play Store Direct Intents** (Hotstar, JioCinema, etc.)
- **Built-in APK Download Manager**:
  - Background multi-threaded streaming with chunked byte counting.
  - Live percentage progress bar and byte counters.
  - User cancellation support.
  - Download cache cleanup.
- **APK Compatibility Verification**:
  - Checks package manifest, target API, minSdk <= 28.
  - Native architecture verification (`arm64-v8a` and `armeabi-v7a` library inspection).
  - Package signature integrity checks.
- **Standard Package Installation**:
  - Uses Android 9 standard `FileProvider` and `PackageInstaller` (`Intent.ACTION_VIEW`).
  - Checks `canRequestPackageInstalls()` and opens Android's unknown sources permission screen when needed.
  - 100% compliant with Android security (No root, no silent installs).

### 3. Built-in VW IPTV Player
- **Legal Public Streams**: Integrated with the verified `iptv-org` repository.
- **Categories**: India Live TV, News, Entertainment, Sports, Movies, Music, Kids.
- **Media Engine**: ExoPlayer 2.18.7 HLS stream engine optimized for Android 9.0 TV.
- **TV Remote Integration**:
  - Channel Up / Channel Down on physical remote.
  - On-Screen Display (OSD) channel banner with title, group, and stream status.
  - Buffering indicators and playback error handling with retry.
  - Full player release on `onStop()` / `onDestroy()` to prevent RAM leaks.
- **Custom Playlists**: Supports loading any custom M3U/M3U8 URL or local playlist file.

### 4. TV Input Center
- Dedicated hardware source switching for:
  - **Live TV (Antenna / Cable Tuner)**: Launches vendor TV tuner app (`com.android.tv`, MStar, CVTE).
  - **HDMI 1, HDMI 2, HDMI 3**.
  - **AV Composite Input**.
- Uses Android TV Input Framework (`TvInputManager`) and vendor intent passthrough.

### 5. Network Center
- **Wi-Fi Management**: Network discovery, SSID display, RSSI signal strength, and direct shortcut to native Wi-Fi settings for password entry.
- **Ethernet Management**: Cable link detection, IP address, Gateway, and DNS servers.
- **Diagnostics**: Built-in HTTP and DNS ping latency test.

### 6. System Settings & RAM Optimization
- **Hardware Specs Dashboard**: Motherboard, CPU, GPU, Storage, RAM, Android version, firmware build.
- **512 MB RAM Optimization**:
  - Pure Java + Native XML views (zero heavy framework overhead).
  - Memory monitor polling system memory every 5 seconds.
  - One-click RAM cache trimmer (`trimMemory()`).
  - Shortcuts to native Android Display, Sound, and Device settings.

---

## 📁 Project Structure

```
d:\tvLauncherrr\
├── VW_Lite_OS_V3.apk          <-- Ready-to-install Release APK (12.1 MB)
├── VW_Lite_OS_V3_debug.apk    <-- Debug APK (14.5 MB)
├── INSTALLATION_GUIDE.md      <-- Step-by-step install instructions (USB & ADB)
├── RECOVERY_GUIDE.md          <-- CloudTV restoration and rollback instructions
├── COMPATIBILITY_REPORT.md    <-- Hardware audit for VW T.PSK518D.PB806
├── build.gradle               <-- Root Gradle build file
├── settings.gradle            <-- Project settings
├── gradle.properties          <-- Build flags
├── gradlew.bat / gradlew      <-- Gradle wrapper scripts
├── gradle/wrapper/            <-- Gradle 8.4 wrapper distribution
└── app/
    ├── build.gradle           <-- Module build configuration (SDK 28 / 33)
    ├── proguard-rules.pro     <-- Proguard optimization rules
    └── src/main/
        ├── AndroidManifest.xml<-- Leanback & Home launcher intent filters
        ├── java/com/vw/liteos/
        │   ├── adapter/       <-- AppGridAdapter, StoreAppAdapter, IPTVChannelAdapter, TVInputAdapter
        │   ├── manager/       <-- DeviceInfoManager, MemoryMonitor, AppManager, AppCatalogRepository,
        │   │                      AppDiscoveryManager, APKDownloadManager, APKCompatibilityChecker,
        │   │                      PackageInstallerManager, InstalledAppsManager, NetworkManager,
        │   │                      WifiCenterManager, EthernetManager, TVInputManager, LiveTVManager,
        │   │                      IPTVManager, PlaylistManager, RemoteNavigationManager, AppInstallReceiver
        │   ├── model/         <-- AppInfo, StoreApp, IPTVChannel, DeviceInfo, TVInputSource
        │   └── ui/            <-- LauncherActivity, IPTVPlayerActivity, AppStoreActivity, SettingsActivity
        └── res/
            ├── color/         <-- Dynamic TV focus color selectors
            ├── drawable/      <-- Vector TV icons, focus borders, card shapes
            ├── layout/        <-- activity_launcher, activity_iptv_player, dialog_app_details, items
            ├── values/        <-- colors.xml, strings.xml, styles.xml
            └── xml/           <-- file_paths.xml (FileProvider), network_security_config.xml
```

---

## 🔨 How to Build

To re-build the project from source at any time:

```powershell
# Set Java Home to JDK 17
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-17.0.21.21-hotspot"
$env:ANDROID_HOME = "C:\Users\Kishor\AppData\Local\Android\Sdk"

# Compile and Assemble Release APK
.\gradlew.bat assembleRelease

# Compile and Assemble Debug APK
.\gradlew.bat assembleDebug
```
