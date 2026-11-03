# VW LITE OS V4 — INSTALLATION GUIDE

This guide details how to install **VW Lite OS V4** onto your VW Smart TV (Motherboard: `T.PSK518D.PB806`, Android 9.0 Pie, API 28).

---

## 📦 Ready-to-Install APK Locations

- **Production Release APK**: `d:\tvLauncherrr\VW_Lite_OS_V4.apk` (**12.18 MB**)
- **Debug APK**: `d:\tvLauncherrr\VW_Lite_OS_V4_debug.apk` (**14.58 MB**)

---

## 🛠️ Method 1: Installation via USB Flash Drive (Recommended)

1. **Format a USB Flash Drive**:
   - Format the USB flash drive to **FAT32** or **NTFS**.

2. **Copy the APK**:
   - Copy `VW_Lite_OS_V4.apk` from `d:\tvLauncherrr\` to the root of your USB flash drive.

3. **Plug into TV**:
   - Insert the USB flash drive into any USB port on your VW Smart TV.

4. **Open File Manager**:
   - On the TV, open the pre-installed File Manager (or Media Browser).
   - Navigate to the USB storage.
   - Click on `VW_Lite_OS_V4.apk`.

5. **Authorize Unknown Sources**:
   - If prompted: *"For your security, your TV is not allowed to install unknown apps from this source"*, click **Settings**.
   - Toggle **Allow from this source** to ON.
   - Press **Back** on your physical remote and click **Install**.

6. **Select VW Lite OS as Default Launcher**:
   - Once installation completes, press the **HOME** button on your VW physical remote.
   - Android will prompt: *"Select a Home app"*:
     - Choose **VW Lite OS**.
     - Select **ALWAYS**.
   - Your TV will now boot and operate directly in VW Lite OS V4!

---

## ⚡ Method 2: Installation via ADB (Over Network / Hotspot)

If ADB Debugging is enabled on your TV:

1. **Ensure TV and PC are on the same Wi-Fi / Hotspot**:
   - Check the TV IP in **Settings -> Network** (e.g., `172.20.10.2` or dynamic local IP).

2. **Connect via ADB**:
   Open PowerShell or Command Prompt and run:
   ```bash
   adb connect 172.20.10.2:5555
   ```
   *(If your TV IP has changed, replace `172.20.10.2` with your TV's current IP address)*.

3. **Install the APK**:
   ```bash
   adb install -r -g "d:\tvLauncherrr\VW_Lite_OS_V4.apk"
   ```

4. **Launch VW Lite OS V4**:
   ```bash
   adb shell am start -n com.vw.liteos/.ui.LauncherActivity
   ```

5. **Verify Installation**:
   ```bash
   adb shell dumpsys package com.vw.liteos | findstr versionName
   ```
   Output will confirm `versionName=4.0.0-vw-liteos`.

---

## ⚙️ Post-Installation Setup & Startup Preferences

1. **Startup Destination**:
   - Open **Settings** -> **Startup & Playback** tab.
   - Set **Default Startup Destination** to:
     - `IPTV Player` (auto-tunes into your favorite channel upon TV power-on)
     - `Home Dashboard` (classic overview with featured banner & app row)
     - `Live TV / HDMI Input` (direct passthrough to set-top box or antenna)
2. **Startup Delay**:
   - Choose `3 Seconds` to allow your Wi-Fi or Ethernet connection to initialize and negotiate DHCP before streaming starts.
3. **Themes & Wallpapers**:
   - Open **Settings** -> **General** tab.
   - Select your preferred wallpaper: `Midnight Blue`, `Cyber Neon`, `Emerald Forest`, or `Cosmic Purple`.
