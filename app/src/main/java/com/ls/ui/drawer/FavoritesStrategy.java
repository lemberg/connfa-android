package com.ls.ui.drawer;

import com.ls.drupalcon.R;
import com.ls.drupalcon.model.Model;
import com.ls.drupalcon.model.UpdateRequest;
import com.ls.drupalcon.model.managers.EventManager;
import com.ls.drupalcon.model.managers.FavoriteManager;
import com.ls.drupalcon.model.managers.ProgramManager;
import com.ls.utils.L;

import java.util.ArrayList;
import java.util.List;

public class FavoritesStrategy implements DrawerFragmentStrategy {

    @Override
    public List<Long> getDayList() {
        List<Long> dayList = new ArrayList<>();
        FavoriteManager manager = Model.instance().getFavoriteManager();
        dayList.addAll(manager.getFavoriteEventDays());
        L.e("dayList = " + dayList);
        return dayList;
    }

    @Override
    public int getTextResId() {
        return R.string.placeholder_schedule;
    }

    @Override
    public int getImageResId() {
        return R.drawable.ic_no_my_schedule;
    }

    @Override
    public boolean enableOptionMenu() {
        return false;
    }

    @Override
    public boolean updateFavorites() {
        return true;
    }

    @Override
    public boolean update(List<UpdateRequest> requests) {
        return true;
    }
}
