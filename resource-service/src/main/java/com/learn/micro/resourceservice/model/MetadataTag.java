package com.learn.micro.resourceservice.model;

public enum MetadataTag {
    NAME("dc:title"),
    ARTIST("xmpDM:artist"),
    ALBUM("xmpDM:album"),
    DURATION("xmpDM:duration"),
    YEAR("xmpDM:releaseDate");

    private final String key;

    MetadataTag(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
