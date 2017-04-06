package com.ls.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ls.drupalcon.R;
import com.ls.drupalcon.model.Model;
import com.ls.drupalcon.model.UpdateRequest;
import com.ls.drupalcon.model.UpdatesManager;
import com.ls.drupalcon.model.dao.EventDao;
import com.ls.drupalcon.model.dao.FriendsTestDao;
import com.ls.drupalcon.model.data.Event;
import com.ls.drupalcon.model.managers.EventManager;
import com.ls.drupalcon.model.managers.FriendsFavoriteManager;
import com.ls.util.ObserverHolder;
import com.ls.utils.L;

import java.util.List;

public class TestActivity extends AppCompatActivity {

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, TestActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        EventManager eventManager = Model.instance().getEventManager();
        final EventDao eventDao = eventManager.getEventDao();
        L.e("Weee = " + eventDao.getAllSafe());
//        List<Event> allSafe = eventDao.getAllSafe();
//        List<Event> eventsByIdsAndDaySafe = eventManager.getEventsByIdsAndDaySafe(1491339600000l);
//        L.e("eventsByIdsAndDaySafe = " + allSafe);
        final FriendsFavoriteManager friendsFavoriteManager = Model.instance().getFriendsFavoriteManager();

        new AsyncTask<Void, Void, List<Event>>() {

            @Override
            protected List<Event> doInBackground(Void... params) {
                return eventDao.getAllSafe();
            }

            @Override
            protected void onPostExecute(List<Event> events) {
                L.e("eventsByIdsAndDaySafe = " + events);
                L.e("Size = " + events.size());
                friendsFavoriteManager.setFavoriteEvent(events);
            }
        }.execute();

        FriendsFavoriteManager favoriteManager = new FriendsFavoriteManager();
//        List<Long> favoriteEventsSafe = favoriteManager.getFavoriteEventsSafe();
//        L.e("favoriteEventsSafe = " + favoriteEventsSafe);

        FriendsTestDao friendsTestDao = favoriteManager.getmFriendsTestDao();
        L.e("FriendsTestDao = " + friendsTestDao.getAllSafe());

    }
}
