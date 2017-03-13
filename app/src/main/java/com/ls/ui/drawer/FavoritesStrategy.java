package com.ls.ui.drawer;

import com.ls.drupalcon.R;
import com.ls.drupalcon.model.Model;
import com.ls.drupalcon.model.UpdateRequest;
import com.ls.drupalcon.model.managers.FavoriteManager;

import java.util.ArrayList;
import java.util.List;

public class FavoritesStrategy implements EventHolderFragmentStrategy {

    @Override
    public List<Long> getDayList() {
        List<Long> dayList = new ArrayList<>();
        FavoriteManager manager = Model.instance().getFavoriteManager();
        dayList.addAll(manager.getFavoriteEventDays());
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

    @Override
    public EventMode getEventMode() {
        return EventMode.Favorites;
    }
}
