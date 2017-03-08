package com.ls.ui.drawer;

import com.ls.drupalcon.R;
import com.ls.drupalcon.model.Model;
import com.ls.drupalcon.model.UpdateRequest;
import com.ls.drupalcon.model.managers.BofsManager;
import com.ls.drupalcon.model.managers.ProgramManager;
import com.ls.utils.L;

import java.util.ArrayList;
import java.util.List;

public class BofsStrategy implements DrawerFragmentStrategy {

     @Override
    public List<Long> getDayList() {
        List<Long> dayList = new ArrayList<>();
        BofsManager manager = Model.instance().getBofsManager();
        dayList.addAll(manager.getBofsDays());
        return dayList;
    }

    @Override
    public int getTextResId() {
        return R.string.placeholder_sessions;
    }

    @Override
    public int getImageResId() {
        return R.drawable.ic_no_session;
    }

    @Override
    public boolean enableOptionMenu() {
        return true;
    }

    @Override
    public boolean updateFavorites() {
        return false;
    }

    @Override
    public boolean update(List<UpdateRequest> requests) {
        return true;
    }

    @Override
    public EventMode getEventMode() {
        return EventMode.Bofs;
    }
}
