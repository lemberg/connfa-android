package com.ls.ui.drawer;

import com.ls.drupalcon.model.Model;
import com.ls.drupalcon.model.managers.BofsManager;
import com.ls.drupalcon.model.managers.ProgramManager;
import com.ls.utils.L;

import java.util.ArrayList;
import java.util.List;

public class BofsStrategy extends BaseFragmentStrategy {

    public BofsStrategy(int imageResId, int textResId) {
        this.imageResId = imageResId;
        this.textResId = textResId;
    }

    @Override
    public List<Long> getDayList() {
        List<Long> dayList = new ArrayList<>();
        BofsManager manager = Model.instance().getBofsManager();
        dayList.addAll(manager.getBofsDays());
        L.e("dayList = " + dayList);
        return dayList;
    }
}
