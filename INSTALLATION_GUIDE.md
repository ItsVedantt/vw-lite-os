# VW LITE OS V3 – INSTALLATION GUIDE

This guide details how to install **VW Lite OS V3** onto your VW Smart TV (Motherboard: `T.PSK518D.PB806`, Android 9.0 Pie, API 28).

---

## 📦 APK Locations
- **Release APK**: `d:\tvLauncherrr\VW_Lite_OS_V3.apk` (12.1 MB)
- **Debug APK**: `d:\tvLauncherrr\VW_Lite_OS_V3_debug.apk` (14.5 MB)

---

## 🛠️ Method 1: Installation via USB Flash Drive (Recommended)

1. **Format a USB Flash Drive**:
   - Format the USB flash drive to **FAT32** or **NTFS**.

2. **Copy the APK**:
   - Copy `VW_Lite_OS_V3.apk` from `d:\tvLauncherrr\` to the root of your USB flash drive.

3. **Plug into TV**:
   - Insert the USB flash drive into the USB port of your VW Smart TV.

4. **Open File Manager**:
   - On the TV, open the pre-installed File Manager (or Media Browser).
   - Navigate to the USB storage.
   - Click on `VW_Lite_OS_V3.apk`.

5. **Authorize Unknown Sources**:
   - If prompted: *"For your security, your TV is not allowed to install unknown apps from this source"*, click **Settings**.
   - Toggle **Allow from this source** to ON.
   - Press **Back** on your remote and click **Install**.

6. **Select VW Lite OS as Default Launcher**:
   - Once installed, press the **HOME** button on your VW physical remote.
   - Android will prompt: *"Select a Home app"*:
     - Choose **VW Lite OS**.
     - Select **ALWAYS**.
   - Your TV will now boot and operate directly in VW Lite OS!

---

## ⚡ Method 2: Installation via ADB (Network / Hotspot)

If ADB Debugging is enabled on your TV:

1. **Ensure TV and PC are on the same Wi-Fi / Hotspot**:
   - Check the TV IP in **Settings -> Network** (e.g., `172.20.10.2` on mobile hotspot, or `192.168.1.x` on local router).

2. **Connect via ADB**:
   Open PowerShell and run:
   ```powershell
   adb connect 172.20.10.2:5555
   ```
   *(If your TV IP has changed, replace `172.20.10.2` with the TV's current IP address)*.

3. **Install the APK**:
   ```powershell
   adb install -r -g "d:\tvLauncherrr\VW_Lite_OS_V3.apk"
   ```

4. **Launch VW Lite OS**:
   ```powershell
   adb shell am start -n com.vw.liteos/.ui.LauncherActivity
   ```

5. **Set as Default Launcher via ADB (Optional)**:
   ```powershell
   adb shell cmd package set-home-activity com.vw.liteos/.ui.LauncherActivity
   ```

---

## 🔒 Security & Safety Guarantee
- **No Root Required**: VW Lite OS operates completely within Android standard TV user-space.
- **No Firmware Modification**: The underlying TV firmware and bootloader remain 100% factory untouched.
- **CloudTV Intact**: CloudTV system services, audio/video drivers, and TV tuner services are preserved intact.
