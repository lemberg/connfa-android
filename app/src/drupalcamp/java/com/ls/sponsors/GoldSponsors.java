package com.ls.sponsors;

import android.content.Context;


import com.ls.drupalcon.R;

import java.util.ArrayList;
import java.util.List;

public class GoldSponsors {

    public static List<SponsorItem> getSponsorsList(Context context) {
        List<SponsorItem> sponsors = new ArrayList<>();
        sponsors.add(new SponsorItem(context.getString(R.string.sponsor_1), R.drawable.event_details_header_sponsor1));
        sponsors.add(new SponsorItem(context.getString(R.string.sponsor_2), R.drawable.event_details_header_sponsor2));
        sponsors.add(new SponsorItem(context.getString(R.string.sponsor_3), R.drawable.event_details_header_sponsor3));
        sponsors.add(new SponsorItem(context.getString(R.string.sponsor_4), R.drawable.event_details_header_sponsor4));
        sponsors.add(new SponsorItem(context.getString(R.string.sponsor_5), R.drawable.event_details_header_sponsor5));
        sponsors.add(new SponsorItem(context.getString(R.string.sponsor_6), R.drawable.event_details_header_sponsor6));
        return sponsors;
    }

}
