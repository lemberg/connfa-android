package com.ls.ui.drawer;

import com.ls.drupalcon.model.Model;
import com.ls.drupalcon.model.managers.EventManager;
import com.ls.drupalcon.model.managers.FavoriteManager;
import com.ls.drupalcon.model.managers.ProgramManager;
import com.ls.utils.L;

import java.util.ArrayList;
import java.util.List;

public class FavoritesStrategy extends BaseFragmentStrategy {

    public FavoritesStrategy(int imageResId, int textResId) {
        this.imageResId = imageResId;
        this.textResId = textResId;
    }

    @Override
    public List<Long> getDayList() {
        List<Long> dayList = new ArrayList<>();
        FavoriteManager manager = Model.instance().getFavoriteManager();
        dayList.addAll(manager.getFavoriteEventDays());
        L.e("dayList = " + dayList);
        return dayList;
    }
}
