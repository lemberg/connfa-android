package com.ls.ui.drawer;


import com.ls.drupalcon.model.UpdateRequest;

import java.util.List;

public interface EventHolderFragmentStrategy {
    List<Long> getDayList();

    int getTextResId();

    int getImageResId();

    boolean enableOptionMenu();

    boolean updateFavorites();

    boolean update(List<UpdateRequest> requests);

    EventMode getEventMode();
}
