package com.ls.ui.drawer;

import com.ls.drupalcon.R;
import com.ls.drupalcon.model.Model;
import com.ls.drupalcon.model.UpdateRequest;
import com.ls.drupalcon.model.managers.FavoriteManager;
import com.ls.drupalcon.model.managers.ProgramManager;

import java.util.ArrayList;
import java.util.List;

public class AddFavoritesStrategy implements EventHolderFragmentStrategy {

    @Override
    public List<Long> getDayList() {
        ProgramManager programManager = Model.instance().getProgramManager();
        return programManager.getProgramDays();
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
