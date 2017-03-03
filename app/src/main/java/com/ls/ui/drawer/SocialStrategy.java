package com.ls.ui.drawer;

import com.ls.drupalcon.model.Model;
import com.ls.drupalcon.model.managers.FavoriteManager;
import com.ls.drupalcon.model.managers.SocialManager;
import com.ls.utils.L;

import java.util.ArrayList;
import java.util.List;

public class SocialStrategy extends BaseFragmentStrategy {

    public SocialStrategy(int imageResId, int textResId) {
        this.imageResId = imageResId;
        this.textResId = textResId;
    }

    @Override
    public List<Long> getDayList() {
        List<Long> dayList = new ArrayList<>();
        SocialManager manager = Model.instance().getSocialManager();
        dayList.addAll(manager.getSocialsDays());
        L.e("dayList = " + dayList);
        return dayList;
    }
}
