package com.vw.liteos.model;

public class TVInputSource {
    public enum Type {
        TUNER,
        HDMI1,
        HDMI2,
        HDMI3,
        AV
    }

    private String id;
    private String title;
    private Type type;
    private boolean isAvailable;
    private String customPackage;
    private String customAction;

    public TVInputSource(String id, String title, Type type, boolean isAvailable) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.isAvailable = isAvailable;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public Type getType() { return type; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public String getCustomPackage() { return customPackage; }
    public void setCustomPackage(String customPackage) { this.customPackage = customPackage; }
    public String getCustomAction() { return customAction; }
    public void setCustomAction(String customAction) { this.customAction = customAction; }
}
