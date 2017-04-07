package com.ls.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ls.drupalcon.R;
import com.ls.drupalcon.model.dao.FriendsFavoriteDao;
import com.ls.drupalcon.model.managers.FriendsFavoriteManager;
import com.ls.utils.L;

public class TestActivity extends AppCompatActivity {

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, TestActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        FriendsFavoriteManager favoriteManager = new FriendsFavoriteManager();

        FriendsFavoriteDao friendsTestDao = favoriteManager.getFriendsTestDao();
        L.e("FriendsFavoriteDao = " + friendsTestDao.getAllSafe());

    }
}
