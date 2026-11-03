# ORIGINAL LAUNCHER RECOVERY & RESTORATION GUIDE

This document provides step-by-step instructions for switching back to the factory CloudTV launcher or uninstalling VW Lite OS at any time.

---

## 🛡️ Non-Destructive Design Guarantee

VW Lite OS was architected specifically **NOT** to disable, modify, or uninstall underlying firmware, system services, or vendor drivers. 
The factory CloudTV launcher and vendor TV subsystems remain present in the TV storage at all times.

---

## Option 1: Switch Back Using Native Android Settings

1. Open **VW Lite OS**.
2. Navigate to the **Settings** tab.
3. Click **Open Native Android Settings**.
4. In Android Settings, navigate to:
   - **Apps & Notifications** -> **Default apps** -> **Home app**.
   *(On some Android 9 TV builds, this is located in **Device Preferences -> Home Screen** or **Settings -> More Settings -> Apps -> Configure Apps -> Default Apps**)*.
5. Select the factory **CloudTV Launcher** (e.g. `com.cvte.tv.launcher` or `CloudTV`).
6. Press **Home** on your remote. The factory CloudTV home screen will load immediately.

---

## Option 2: Uninstalling VW Lite OS

If you wish to remove VW Lite OS completely:

### Method A: From VW Lite OS Itself
1. In VW Lite OS, navigate to the **Apps** tab.
2. In the Apps list, find any user application or open **Settings -> Open Native Android Settings**.
3. Go to **Apps** -> **VW Lite OS** -> **Uninstall** -> **OK**.
4. As soon as VW Lite OS is uninstalled, Android automatically falls back to CloudTV as the only remaining Home application.

### Method B: Via ADB
```powershell
adb connect <TV_IP>:5555
adb uninstall com.vw.liteos
```

---

## Option 3: Recovering if the Home Button Fails

If pressing the Home button does not present the launcher selection dialog:

1. Connect via ADB:
   ```powershell
   adb connect <TV_IP>:5555
   ```
2. Reset preferred Home app:
   ```powershell
   adb shell pm clear-default-tasks com.vw.liteos
   ```
3. Or manually launch the factory CloudTV launcher:
   ```powershell
   adb shell am start -c android.intent.category.HOME -a android.intent.action.MAIN
   ```
4. Clear VW Lite OS package defaults:
   ```powershell
   adb shell cmd package clear-home-activity
   ```
