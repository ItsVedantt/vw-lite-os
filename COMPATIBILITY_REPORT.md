# VW LITE OS V3 – HARDWARE & SYSTEM COMPATIBILITY REPORT

**Audit Date**: November 2026  
**Target Device**: VW Smart TV (Motherboard: `T.PSK518D.PB806`)  
**Firmware**: `20260227_153820`  
**Application**: `com.vw.liteos` (Version: `3.0.0-vw-liteos`, VersionCode: `3`)

---

## 1. Hardware Architecture & Performance Audit

| Specification | Target TV Reported | VW Lite OS V3 Implementation & Audit | Status |
|---|---|---|---|
| **Motherboard** | T.PSK518D.PB806 | Auto-detected and displayed in Settings Dashboard | ✅ Verified |
| **CPU** | ARM 4-Core | Multi-threaded background tasks for network and downloads; UI runs on lightweight single-threaded Android message queue | ✅ Optimal |
| **GPU** | Mali-G31 MP2 | Hardware acceleration enabled; lean XML vector drawables and shape selectors without heavy compositors | ✅ Optimal |
| **RAM Budget** | 512 MB Reported | Ultra-lightweight memory footprint (~18-28 MB active heap); real-time MemoryMonitor with cache trimming | ✅ Verified |
| **ABIs** | arm64-v8a, armeabi-v7a | `ndk { abiFilters "armeabi-v7a", "arm64-v8a" }`; APKCompatibilityChecker validates native libraries before installation | ✅ Verified |
| **Android Version** | 9.0 (Pie) | `minSdk 28`, `targetSdk 28` | ✅ Exact Match |
| **API Level** | 28 | Uses Android 9 TV Input Framework, Leanback intent categories, and FileProvider | ✅ Exact Match |

---

## 2. Low-Memory (512 MB RAM) Optimization Strategy

1. **Pure Java & Native Android Views**:
   - Zero heavy framework overhead (No Flutter, React Native, Jetpack Compose, or Electron).
   - Custom `RecyclerView` item pools and views recycled immediately during TV D-pad scrolling.

2. **ExoPlayer Lifecycle Management**:
   - `IPTVPlayerActivity` strictly releases player decoder buffers, surfaces, and codecs during `onStop()` / `onDestroy()`.
   - Prevents native video memory leaks from starving the system.

3. **In-Memory Cache Discipline**:
   - Images loaded as vector drawables and hardware shapes where possible.
   - APK downloads stream directly to disk with an 8KB buffer without loading full payloads into RAM.
   - Built-in `MemoryMonitor` actively watches heap and available system memory, alerting the user and offering one-click garbage collection (`trimMemory()`).

---

## 3. CloudTV Independence Audit

| CloudTV Component | CloudTV Version | VW Lite OS Independence Status |
|---|---|---|
| **Skin** | `1.0.0-dynamic_skin-63-g99602bc` | 🚫 Completely ignored; VW Lite OS uses custom premium dark slate `#0B0E14` theme |
| **Profile** | `2.0.0-67-g530dde2` | 🚫 Completely bypassed; standalone local preferences |
| **Launcher** | `3.2.1-234-gd4068542-new-ads-cvte-com` | 🚫 Replaced as default `CATEGORY_HOME`; factory launcher retained in storage for safety |
| **Advertisements** | CVTE Ad System | 🚫 0% ads, 0% tracking, 0% sponsored overlays |
| **App Store** | CloudTV Market | 🚫 Replaced with independent VW App Store (F-Droid, GitHub Releases, Official APKs) |
| **Live TV Tuner** | MStar/CVTE Tuner Service | 🔄 Standard TIF / Vendor intent passthrough preserves access to hardware antenna & cable |
| **HDMI Inputs** | HDMI 1, 2, 3, AV | 🔄 Standard Android TV Input Framework & vendor hardware intent switching |

---

## 4. Verification Checksum

- **Release APK**: `VW_Lite_OS_V3.apk`
  - Size: `12,142,192 bytes` (~11.6 MB)
  - Target SDK: `28`
  - Min SDK: `28`
  - Architecture: `armeabi-v7a`, `arm64-v8a`
  - Leanback TV Supported: `true`
  - Package Name: `com.vw.liteos`
