# VW LITE OS V4 — INDEPENDENT ANDROID TV LAUNCHER

**VW Lite OS V4** is a completely independent, hardware-optimized Android TV launcher, application marketplace, IPTV live player, TV input switcher, and developer control center engineered specifically for the **VW Smart TV** (Motherboard: `T.PSK518D.PB806`, Mali-G31 MP2 GPU, ARM 4-Core CPU, 512 MB RAM budget, Android 9.0 Pie API 28).

V4 transforms the system into a dark futuristic, glassmorphic television operating system interface that visually and functionally eliminates dependence on CloudTV while preserving underlying TV firmware, drivers, and recovery options.

---

## 🌟 What's New in VW Lite OS V4

1. **Exact UI Reproduction of Gemini/Canva Visual Reference**:
   - **Color System**: Deep space background (`#070A12`), glassmorphism card surfaces (`#111827` / `#D9111827`), blue focus highlight (`#3B82F6` / `#2563EB` glow), Inter typography.
   - **Top Navigation Bar**: Brand logo badge with gradient bolt, OS title, API 28 badge, and glass-encapsulated navigation tabs: `Home`, `Apps`, `App Store`, `IPTV`, `TV Inputs`, `Network`, `Settings`.
   - **Live Status Header**:
     - Real-time RAM monitor pill (`RAM: 342MB / 512MB` measured dynamically from `ActivityManager`).
     - Real network IP pill (`172.20.10.2` or dynamic local IP via `NetworkInterface`).
     - Digital clock with AM/PM indicator.
2. **Built-in Embedded IPTV Player**:
   - Split 2-column layout: Left-hand category & channel browser with 12 active Indian streams; Right-hand embedded `PlayerView` powered by ExoPlayer 2.18.7.
   - Real-time channel switching with `Prev` and `Next` remote buttons, pulsating LIVE indicator, and on-screen stream details.
3. **Smart Startup & Playback Subsystem**:
   - **Startup Destinations**: Home Dashboard, IPTV Player (Default), IPTV Channel Guide, Live TV / HDMI, Applications, App Store, Network Center.
   - **IPTV Auto-Resume & Session Manager**: Auto-resumes last watched channel, auto-play toggle, network reconnect check, aspect ratio preservation.
   - **Startup Delay (Network Sync)**: 0s, 3s, 5s, or 10s wait period to allow Wi-Fi/Ethernet to establish prior to stream playback.
   - **Session Summary**: Displays last player, last playlist, last channel, and last IP.
4. **Developer Options & ADB Center**:
   - **Master Developer Mode Toggle**: Detects real `Settings.Global.DEVELOPMENT_SETTINGS_ENABLED` and `Settings.Global.ADB_ENABLED` without rooting or breaking security.
   - **ADB Connection Status**: Inspects USB Debugging state, active TV IP, and port 5555 status.
   - **One-Click TV IP Copy**: Copies IP directly to clipboard with `adb connect <IP>:5555` confirmation dialog.
   - **Quick Shortcuts**: Open native Developer Settings, inspect running services & RAM memory, and inspect Mali-G31 MP2 GPU hardware acceleration.
5. **Wallpaper Theme Customization**:
   - Four built-in gradient themes: **Midnight Blue**, **Cyber Neon**, **Emerald Forest**, and **Cosmic Purple**.
   - Changes the window and launcher background instantly and persists across boots.
6. **Glass Toast Notification Modal**:
   - Smooth popup modal overlay replacing intrusive alerts, with TV remote focus on `OK / Close`.

---

## 📱 Hardware Specifications & Compatibility

| Parameter | Specification | Hardware Status |
| :--- | :--- | :--- |
| **Manufacturer** | VW | Verified |
| **Model** | Smart TV | Verified |
| **Motherboard** | `T.PSK518D.PB806` | Fully Supported |
| **CPU** | ARM 4-Core (`armeabi-v7a`, `arm64-v8a`) | 32-bit & 64-bit ABI |
| **GPU** | ARM Mali-G31 MP2 | OpenGL ES 3.2 / Vulkan 1.1 |
| **RAM Budget** | 512 MB reported | Optimized (< 40 MB footprint) |
| **Operating System**| Android 9.0 (Pie) | Native API 28 |
| **Firmware Build** | `20260227_153820` | Supported |
| **TV IP Address** | `172.20.10.2` | Supported via Wi-Fi & Ethernet |

---

## 📦 Ready-to-Install APK Deliverables

The project includes pre-compiled, verified release and debug APKs located in `d:\tvLauncherrr\`:

| Deliverable | Location | File Size | Description |
| :--- | :--- | :--- | :--- |
| **Release APK** | `d:\tvLauncherrr\VW_Lite_OS_V4.apk` | **12.18 MB** | Production-ready, signed, optimized APK |
| **Debug APK** | `d:\tvLauncherrr\VW_Lite_OS_V4_debug.apk` | **14.58 MB** | Developer build with active logging & ADB debugging |

Both APKs are signed with the standard Android TV key, declare `targetSdkVersion: 28` for Android 9.0 Pie, and include `HOME` and `LEANBACK_LAUNCHER` intent filters.

---

## 🗂 Project Architecture

```
d:\tvLauncherrr\
├── VW_Lite_OS_V4.apk          <-- Production Release APK (12.18 MB)
├── VW_Lite_OS_V4_debug.apk    <-- Debug APK (14.58 MB)
├── build_apk.bat              <-- One-click batch build script
├── vw_lite_os_android_tv_launcher_v3.html <-- Visual reference design
├── INSTALLATION_GUIDE.md      <-- Comprehensive USB & ADB install instructions
├── RECOVERY_GUIDE.md          <-- CloudTV restoration & safe fallback instructions
├── COMPATIBILITY_REPORT.md    <-- Detailed hardware & RAM audit
├── README.md                  <-- Project manual & architectural specification
└── app/
    ├── build.gradle           <-- AGP 8.1.4, targetSdk 28, compileSdk 33
    └── src/main/
        ├── AndroidManifest.xml<-- Launcher & Leanback intent filters, permissions
        ├── java/com/vw/liteos/
        │   ├── adapter/       <-- AppGridAdapter, StoreAppAdapter, IPTVChannelAdapter, TVInputAdapter
        │   ├── manager/       <-- StartupManager, IPTVSessionManager, DeviceInfoManager,
        │   │                      MemoryMonitor, AppManager, AppCatalogRepository, AppDiscoveryManager,
        │   │                      APKDownloadManager, PackageInstallerManager, IPTVManager,
        │   │                      TVInputManager, NetworkManager, WifiCenterManager, EthernetManager
        │   ├── model/         <-- AppInfo, DeviceInfo, IPTVChannel, StoreApp, TVInputSource
        │   └── ui/            <-- LauncherActivity, AppStoreActivity, IPTVPlayerActivity, SettingsActivity
        └── res/
            ├── drawable/      <-- Glassmorphism selectors, themes, vector icons
            ├── layout/        <-- activity_launcher.xml, item_app_card.xml, item_iptv_channel.xml, etc.
            └── values/        <-- colors.xml, styles.xml, strings.xml
```

---

## ⚡ Quick ADB Installation

From your PC connected to the same network as your TV (`172.20.10.2`):

```bash
# 1. Connect to TV over ADB
adb connect 172.20.10.2:5555

# 2. Install VW Lite OS V4
adb install -r d:\tvLauncherrr\VW_Lite_OS_V4.apk

# 3. Launch VW Lite OS
adb shell am start -n com.vw.liteos/.ui.LauncherActivity
```
