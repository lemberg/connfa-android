package com.ls.ui.drawer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.ls.ui.fragment.AboutFragment;
import com.ls.ui.fragment.EventHolderFragment;
import com.ls.ui.fragment.FloorPlanFragment;
import com.ls.ui.fragment.LocationFragment;
import com.ls.ui.fragment.SocialMediaFragment;
import com.ls.ui.fragment.SpeakersListFragment;

import org.jetbrains.annotations.NotNull;

public class DrawerManager {

    public enum EventMode {Program, Bofs, Social, Speakers, Favorites, Location, About}

    private FragmentManager fragmentManager;
    private int fragmentHolderId;
    private EventMode currentEventMode;

    public static DrawerManager getInstance(FragmentManager theFragmentManager, int theMainFragmentId) {
        return new DrawerManager(theFragmentManager, theMainFragmentId);
    }

    private DrawerManager(FragmentManager theFragmentManager, int theMainFragmentId) {
        this.fragmentManager = theFragmentManager;
        this.fragmentHolderId = theMainFragmentId;
    }

    public void setFragment(@NotNull DrawerMenu.DrawerItem mode) {
        Fragment fragment;
        String fragmentTag = null;

        switch (mode) {
            case PROGRAM:
                fragment = EventHolderFragment.newInstance(DrawerMenu.DrawerItem.PROGRAM.ordinal());
                fragmentTag = EventHolderFragment.TAG;
                break;

            case BOFS:
                fragment = EventHolderFragment.newInstance(DrawerMenu.DrawerItem.BOFS.ordinal());
                fragmentTag = EventHolderFragment.TAG;
                break;

            case SOCIAL:
                fragment = EventHolderFragment.newInstance(DrawerMenu.DrawerItem.SOCIAL.ordinal());
                fragmentTag = EventHolderFragment.TAG;
                break;

            case FAVORITES:
                fragment = EventHolderFragment.newInstance(DrawerMenu.DrawerItem.FAVORITES.ordinal());
                fragmentTag = EventHolderFragment.TAG;
                break;

            case SPEAKERS:
                fragment = new SpeakersListFragment();
                fragmentTag = SpeakersListFragment.TAG;
                break;

            case FLOOR_PLAN:
                fragment = new FloorPlanFragment();
                fragmentTag = FloorPlanFragment.TAG;
                break;

            case LOCATION:
                fragment = new LocationFragment();
                fragmentTag = LocationFragment.TAG;
                break;
            case SOCIAL_MEDIA:
                fragment = new SocialMediaFragment();
                fragmentTag = SocialMediaFragment.TAG;
                break;
            case ABOUT:
                fragment = new AboutFragment();
                fragmentTag = SocialMediaFragment.TAG;
                break;
            default:
                fragment = EventHolderFragment.newInstance(EventMode.Program.ordinal());
        }
        fragmentManager.beginTransaction().replace(fragmentHolderId, fragment, fragmentTag).commit();
    }

    public void reloadPrograms(@NotNull DrawerMenu.DrawerItem mode) {
        Fragment fragment;
        switch (mode) {
            case PROGRAM:
                fragment = EventHolderFragment.newInstance(DrawerMenu.DrawerItem.PROGRAM.ordinal());
                break;

            case BOFS:
                fragment = EventHolderFragment.newInstance(DrawerMenu.DrawerItem.BOFS.ordinal());
                break;

            case SOCIAL:
                fragment = EventHolderFragment.newInstance(DrawerMenu.DrawerItem.SOCIAL.ordinal());
                break;
            default:
                fragment = EventHolderFragment.newInstance(EventMode.Program.ordinal());
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(fragmentHolderId, fragment, EventHolderFragment.TAG);
        ft.commitAllowingStateLoss();
    }
}
