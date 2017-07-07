package com.ls.drupalcon.model.managers;

import com.ls.drupal.AbstractBaseDrupalEntity;
import com.ls.drupal.DrupalClient;
import com.ls.drupalcon.model.data.PostResponse;
import com.ls.drupalcon.model.data.SettingsHolder;

public class TestManager extends SynchronousItemManager<PostResponse, Object, String> {
    public TestManager(DrupalClient client) {
        super(client);
    }

    @Override
    protected AbstractBaseDrupalEntity getEntityToFetch(DrupalClient client, Object requestParams) {
        return null;
    }

    @Override
    protected String getEntityRequestTag(Object params) {
        return null;
    }

    @Override
    protected boolean storeResponse(PostResponse requestResponse, String tag) {
        return false;
    }
}
