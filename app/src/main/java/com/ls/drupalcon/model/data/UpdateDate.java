package com.ls.drupalcon.model.data;

import com.google.gson.annotations.SerializedName;
import com.ls.drupalcon.model.UpdateRequest;
import java.util.List;

public class UpdateDate {

    @SerializedName("Last-Modified")
    private String mTime;

    private List<UpdateRequest> idsForUpdate;

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public List<UpdateRequest> getIdsForUpdate() {
        return idsForUpdate;
    }
}
