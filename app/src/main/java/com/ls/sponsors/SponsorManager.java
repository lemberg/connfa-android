package com.ls.sponsors;

public class SponsorManager {
    private static SponsorManager instance;
    private int sponsorId;

    public static SponsorManager getInstance()
    {
        if (instance == null)
        {
            instance = new SponsorManager();
        }
        return instance;
    }

    public int getSponsorId() {
        return sponsorId;
    }

    public void setSponsorId(int sponsorId) {
        this.sponsorId = sponsorId;
    }
}
