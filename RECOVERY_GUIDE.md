# ORIGINAL LAUNCHER RECOVERY & RESTORATION GUIDE

This document provides step-by-step instructions for switching back to the factory CloudTV launcher or uninstalling VW Lite OS at any time.

---

## 🛡️ Non-Destructive Design Guarantee

VW Lite OS V4 was architected specifically **NOT** to disable, modify, or uninstall underlying firmware, system services, or vendor drivers:
- Factory CloudTV skin, dynamic skin, and launcher packages remain intact in `/system/app/` and `/system/priv-app/`.
- TV Tuner, HDMI input pipeline, audio HAL, and video decoder services remain completely unmodified.
- The TV can be reverted to the factory CloudTV interface at any time in under 30 seconds.

---

## Option 1: Switch Back Using VW Lite OS Settings

1. In **VW Lite OS V4**, navigate to the **Settings** tab.
2. Under the **General** sub-tab, click on **Original Launcher Recovery** or **Open Native Settings**.
3. In Android Settings, navigate to:
   - **Apps & Notifications** -> **Default apps** -> **Home app**.
   *(On some Android 9 TV builds, this is located in **Device Preferences -> Home Screen** or **Settings -> More Settings -> Apps -> Configure Apps -> Default Apps**)*.
4. Select the factory **CloudTV Launcher** (e.g. `com.cvte.tv.launcher` or `CloudTV`).
5. Press **Home** on your remote. The factory CloudTV home screen will load immediately.

---

## Option 2: Uninstalling VW Lite OS V4

If you wish to remove VW Lite OS V4 completely:

### Method A: From TV Native Settings
1. In VW Lite OS, navigate to **Settings** -> **General** -> **Open Native Settings**.
2. Go to **Apps** -> **VW Lite OS** -> **Uninstall** -> **OK**.
3. As soon as VW Lite OS is uninstalled, Android automatically falls back to CloudTV as the only remaining Home application.

### Method B: Via ADB
```bash
adb connect 172.20.10.2:5555
adb uninstall com.vw.liteos
```

---

## Option 3: Recovering Default Launcher via ADB

If you need to reset the default launcher preference:

1. Connect via ADB:
   ```bash
   adb connect 172.20.10.2:5555
   ```
2. Clear the default home application:
   ```bash
   adb shell pm clear-default-tasks com.vw.liteos
   ```
3. Launch the factory CloudTV launcher explicitly:
   ```bash
   adb shell am start -c android.intent.category.HOME -a android.intent.action.MAIN
   ```
