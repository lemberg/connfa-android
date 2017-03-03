package com.ls.ui.drawer;

import com.ls.drupalcon.model.Model;
import com.ls.drupalcon.model.managers.ProgramManager;
import com.ls.utils.L;

import java.util.ArrayList;
import java.util.List;

public class ProgramStrategy extends BaseFragmentStrategy {

    public ProgramStrategy(int imageResId, int textResId) {
        this.imageResId = imageResId;
        this.textResId = textResId;
    }

    @Override
    public List<Long> getDayList() {
        List<Long> dayList = new ArrayList<>();
        ProgramManager programManager = Model.instance().getProgramManager();
        dayList.addAll(programManager.getProgramDays());
        L.e("dayList = " + dayList);
        return dayList;
    }
}
