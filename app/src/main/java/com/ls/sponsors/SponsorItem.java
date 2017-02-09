package com.ls.sponsors;

public class SponsorItem {
    private String name;
    private int resourceId;

    public SponsorItem(String name, int resourceId) {
        this.name = name;
        this.resourceId = resourceId;
    }

    public String getName() {
        return name;
    }

    public int getResourceId() {
        return resourceId;
    }

    @Override
    public String toString() {
        return "SponsorItem{" +
                "name='" + name + '\'' +
                ", resourceId=" + resourceId +
                '}';
    }
}
