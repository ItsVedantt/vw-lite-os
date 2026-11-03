package com.vw.liteos.model;

public class DeviceInfo {
    private String manufacturer;
    private String model;
    private String board;
    private String hardware;
    private int cpuCores;
    private String gpuRenderer;
    private long totalRamBytes;
    private long availRamBytes;
    private long totalStorageBytes;
    private long availStorageBytes;
    private String androidVersion;
    private int apiLevel;
    private String firmwareBuild;
    private String[] supportedAbis;

    public DeviceInfo() {}

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getBoard() { return board; }
    public void setBoard(String board) { this.board = board; }

    public String getHardware() { return hardware; }
    public void setHardware(String hardware) { this.hardware = hardware; }

    public int getCpuCores() { return cpuCores; }
    public void setCpuCores(int cpuCores) { this.cpuCores = cpuCores; }

    public String getGpuRenderer() { return gpuRenderer; }
    public void setGpuRenderer(String gpuRenderer) { this.gpuRenderer = gpuRenderer; }

    public long getTotalRamBytes() { return totalRamBytes; }
    public void setTotalRamBytes(long totalRamBytes) { this.totalRamBytes = totalRamBytes; }

    public long getAvailRamBytes() { return availRamBytes; }
    public void setAvailRamBytes(long availRamBytes) { this.availRamBytes = availRamBytes; }

    public long getTotalStorageBytes() { return totalStorageBytes; }
    public void setTotalStorageBytes(long totalStorageBytes) { this.totalStorageBytes = totalStorageBytes; }

    public long getAvailStorageBytes() { return availStorageBytes; }
    public void setAvailStorageBytes(long availStorageBytes) { this.availStorageBytes = availStorageBytes; }

    public String getAndroidVersion() { return androidVersion; }
    public void setAndroidVersion(String androidVersion) { this.androidVersion = androidVersion; }

    public int getApiLevel() { return apiLevel; }
    public void setApiLevel(int apiLevel) { this.apiLevel = apiLevel; }

    public String getFirmwareBuild() { return firmwareBuild; }
    public void setFirmwareBuild(String firmwareBuild) { this.firmwareBuild = firmwareBuild; }

    public String[] getSupportedAbis() { return supportedAbis; }
    public void setSupportedAbis(String[] supportedAbis) { this.supportedAbis = supportedAbis; }

    public int getTotalRamMb() { return (int) (totalRamBytes / (1024 * 1024)); }
    public int getAvailRamMb() { return (int) (availRamBytes / (1024 * 1024)); }
    public int getUsedRamPercent() {
        if (totalRamBytes <= 0) return 0;
        long used = totalRamBytes - availRamBytes;
        return (int) ((used * 100) / totalRamBytes);
    }
}
