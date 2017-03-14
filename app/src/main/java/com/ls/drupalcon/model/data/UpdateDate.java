package com.ls.drupalcon.model.data;

import com.google.gson.annotations.SerializedName;
import com.ls.drupalcon.model.UpdateRequest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class UpdateDate {

    @SerializedName("Last-Modified")
    private String mTime;

    private List<Integer> idsForUpdate;

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public List<UpdateRequest> getUpdateList() {
        List<UpdateRequest> updateList = new ArrayList<>();

        if (idsForUpdate == null || idsForUpdate.isEmpty()) {
            return new LinkedList<>();
        }
        for (int i : idsForUpdate) {
            switch (i) {
                case 0:
                    updateList.add(UpdateRequest.SETTINGS);
                    break;
                case 1:
                    updateList.add(UpdateRequest.TYPES);
                    break;
                case 2:
                    updateList.add(UpdateRequest.LEVELS);
                    break;
                case 3:
                    updateList.add(UpdateRequest.TRACKS);
                    break;
                case 4:
                    updateList.add(UpdateRequest.SPEAKERS);
                    break;
                case 5:
                    updateList.add(UpdateRequest.LOCATIONS);
                    break;
                case 6:
                    updateList.add(UpdateRequest.FLOOR_PLANS);
                    break;
                case 7:
                    updateList.add(UpdateRequest.PROGRAMS);
                    break;
                case 8:
                    updateList.add(UpdateRequest.BOFS);
                    break;
                case 9:
                    updateList.add(UpdateRequest.SOCIALS);
                    break;
                case 10:
                    updateList.add(UpdateRequest.POIS);
                    break;
                case 11:
                    updateList.add(UpdateRequest.INFO);
                    break;

            }


        }

        return updateList;

    }

}
