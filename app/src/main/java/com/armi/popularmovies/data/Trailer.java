package com.armi.popularmovies.data;

/**
 * Trailer Data object
 */
public class Trailer {

    String key;
    String name;
    String type;
    String site;

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getSite() {
        return site;
    }

    @Override
    public String toString() {
        return "Trailer{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", site='" + site + '\'' +
                '}';
    }
}
