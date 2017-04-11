package com.ls.drupalcon.model.dao;

import com.ls.drupalcon.model.AppDatabaseInfo;
import com.ls.drupalcon.model.data.FloorPlan;
import com.ls.drupalcon.model.data.SharedSchedule;
import com.ls.drupalcon.model.database.AbstractEntityDAO;


public class SharedScheduleDao extends AbstractEntityDAO<SharedSchedule, String> {

    public static final String TABLE_NAME = "table_shared_schedules";

    @Override
    protected String getSearchCondition() {
        return FloorPlan.COLUMN_ID + "=?";
    }

    @Override
    protected String[] getSearchConditionArguments(String theId) {
        return new String[]{theId};
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getDatabaseName() {
        return AppDatabaseInfo.DATABASE_NAME;
    }

    @Override
    protected SharedSchedule newInstance() {
        return new SharedSchedule();
    }

    @Override
    protected String[] getKeyColumns() {
        return new String[0];
    }
}
