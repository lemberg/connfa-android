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

	private FragmentManager fragmentManager;
	private int fragmentHolderId;

	public static DrawerManager getInstance(FragmentManager theFragmentManager, int theMainFragmentId) {
		return new DrawerManager(theFragmentManager, theMainFragmentId);
	}

	private DrawerManager(FragmentManager theFragmentManager, int theMainFragmentId) {
		this.fragmentManager = theFragmentManager;
		this.fragmentHolderId = theMainFragmentId;
	}

	public void setFragment(@NotNull DrawerMenu.EventMode mode) {
		Fragment fragment;

		fragment = EventHolderFragment.newInstance(mode);
		fragmentManager.beginTransaction().replace(fragmentHolderId, fragment).commit();
	}

	public void reloadPrograms(@NotNull DrawerMenu.EventMode mode) {
		FragmentTransaction ft = fragmentManager.beginTransaction();
		ft.replace(fragmentHolderId, EventHolderFragment.newInstance(mode), EventHolderFragment.TAG);
		ft.commitAllowingStateLoss();
	}
}
