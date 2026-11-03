package com.vw.liteos.model;

import java.io.Serializable;

public class IPTVChannel implements Serializable {
    private String id;
    private String name;
    private String logoUrl;
    private String streamUrl;
    private String category;
    private String country;
    private String groupTitle;
    private boolean isFavorite;
    private long lastWatchedTime;

    public IPTVChannel() {}

    public IPTVChannel(String id, String name, String logoUrl, String streamUrl,
                       String category, String country, String groupTitle) {
        this.id = id;
        this.name = name;
        this.logoUrl = logoUrl;
        this.streamUrl = streamUrl;
        this.category = category;
        this.country = country;
        this.groupTitle = groupTitle;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getStreamUrl() { return streamUrl; }
    public void setStreamUrl(String streamUrl) { this.streamUrl = streamUrl; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getGroupTitle() { return groupTitle; }
    public void setGroupTitle(String groupTitle) { this.groupTitle = groupTitle; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public long getLastWatchedTime() { return lastWatchedTime; }
    public void setLastWatchedTime(long lastWatchedTime) { this.lastWatchedTime = lastWatchedTime; }
}
