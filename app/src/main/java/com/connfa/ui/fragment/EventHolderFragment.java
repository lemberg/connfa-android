package com.connfa.ui.fragment;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.connfa.R;
import com.connfa.model.Model;
import com.connfa.model.PreferencesManager;
import com.connfa.model.UpdatesManager;
import com.connfa.model.managers.BofsManager;
import com.connfa.model.managers.FavoriteManager;
import com.connfa.model.managers.ProgramManager;
import com.connfa.model.managers.SocialManager;
import com.connfa.ui.activity.HomeActivity;
import com.connfa.ui.adapter.BaseEventDaysPagerAdapter;
import com.connfa.ui.drawer.DrawerManager;
import com.connfa.ui.receiver.ReceiverManager;
import com.connfa.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

public class EventHolderFragment extends Fragment {

    public static final String TAG = "ProjectsFragment";
    private static final String EXTRAS_ARG_MODE = "EXTRAS_ARG_MODE";

    private PreferencesManager preferencesManager;

    private ViewPager mViewPager;
    private PagerSlidingTabStrip mPagerTabs;
    private BaseEventDaysPagerAdapter mAdapter;

    private DrawerManager.EventMode mEventMode;

    private View mLayoutPlaceholder;
    private ImageView mImageViewNoContent;
    private TextView mTextViewNoContent;

    private boolean mIsFilterUsed;

    private UpdatesManager.DataUpdatedListener updateReceiver = new UpdatesManager.DataUpdatedListener() {
        @Override
        public void onDataUpdated(List<Integer> requestIds) {
            updateData(requestIds);
        }
    };
    private ReceiverManager favoriteReceiver = new ReceiverManager(new ReceiverManager.FavoriteUpdatedListener() {
        @Override
        public void onFavoriteUpdated(long eventId, boolean isFavorite) {
            updateFavorites();
        }
    });

    public static EventHolderFragment newInstance(int modePos) {
        EventHolderFragment fragment = new EventHolderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRAS_ARG_MODE, modePos);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_holder_event, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferencesManager = PreferencesManager.create(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_filter, menu);

        MenuItem filter = menu.findItem(R.id.actionFilter);
        if (filter != null) {
            updateFilterState(filter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionFilter:
                showFilter();
                break;
        }
        return true;
    }

//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        Model.createInstance().getUpdatesManager().registerUpdateListener(updateReceiver);
//        favoriteReceiver.register(getActivity());
//
//        initData();
//        initView();
//        new LoadData().execute();
//    }
//
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Model.createInstance().getUpdatesManager().unregisterUpdateListener(updateReceiver);
//        favoriteReceiver.unregister(getActivity());
//    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Model.getInstance().getUpdatesManager().registerUpdateListener(updateReceiver);
        favoriteReceiver.register(getActivity());

        initData();
        initView();
        new LoadData().execute();
    }

    @Override
    public void onDestroyView() {
        Model.getInstance().getUpdatesManager().unregisterUpdateListener(updateReceiver);
        favoriteReceiver.unregister(getActivity());
        super.onDestroyView();
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            int eventPos = bundle.getInt(EXTRAS_ARG_MODE, DrawerManager.EventMode.PROGRAM.ordinal());
            mEventMode = DrawerManager.EventMode.values()[eventPos];
        }
    }

    private void initView() {
        View view = getView();
        if (view == null) {
            return;
        }

        mAdapter = new BaseEventDaysPagerAdapter(getActivity(), getChildFragmentManager());
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mViewPager.setAdapter(mAdapter);

        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
        mPagerTabs = (PagerSlidingTabStrip) getView().findViewById(R.id.pager_tab_strip);
        mPagerTabs.setTypeface(typeface, 0);
        mPagerTabs.setViewPager(mViewPager);

        mLayoutPlaceholder = view.findViewById(R.id.layout_placeholder);
        mTextViewNoContent = (TextView) view.findViewById(R.id.text_view_placeholder);
        mImageViewNoContent = (ImageView) view.findViewById(R.id.image_view_placeholder);

        if (mEventMode == DrawerManager.EventMode.PROGRAM ||
                mEventMode == DrawerManager.EventMode.BOFS ||
                mEventMode == DrawerManager.EventMode.SOCIAL) {
            setHasOptionsMenu(true);
        } else {
            setHasOptionsMenu(false);
        }
    }

    class LoadData extends AsyncTask<Void, Void, List<Long>> {

        @Override
        protected List<Long> doInBackground(Void... params) {
            return getDayList();
        }

        @Override
        protected void onPostExecute(List<Long> result) {
            updateViews(result);
        }
    }

    private List<Long> getDayList() {
        List<Long> dayList = new ArrayList<>();
        switch (mEventMode) {
            case BOFS:
                BofsManager bofsManager = Model.getInstance().getBofsManager();
                dayList.addAll(bofsManager.getBofsDays());
                break;
            case SOCIAL:
                SocialManager socialManager = Model.getInstance().getSocialManager();
                dayList.addAll(socialManager.getSocialsDays());
                break;
            case FAVORITES:
                FavoriteManager favoriteManager = Model.getInstance().getFavoriteManager();
                dayList.addAll(favoriteManager.getFavoriteEventDays());
                break;
            default:
                ProgramManager programManager = Model.getInstance().getProgramManager();
                dayList.addAll(programManager.getProgramDays());
                break;
        }
        return dayList;
    }

    private void updateViews(List<Long> dayList) {

//        if(!isResumed()){
//            return;
//        }

        if (dayList.isEmpty()) {
            mPagerTabs.setVisibility(View.GONE);
            mLayoutPlaceholder.setVisibility(View.VISIBLE);

            if (mIsFilterUsed) {
                mImageViewNoContent.setVisibility(View.GONE);
                mTextViewNoContent.setText(getString(R.string.placeholder_no_matching_events));
            } else {
                mImageViewNoContent.setVisibility(View.VISIBLE);

                int imageResId = 0, textResId = 0;

                switch (mEventMode) {
                    case PROGRAM:
                        imageResId = R.drawable.ic_no_session;
                        textResId = R.string.placeholder_sessions;
                        break;
                    case BOFS:
                        imageResId = R.drawable.ic_no_bofs;
                        textResId = R.string.placeholder_bofs;
                        break;
                    case SOCIAL:
                        imageResId = R.drawable.ic_no_social_events;
                        textResId = R.string.placeholder_social_events;
                        break;
                    case FAVORITES:
                        imageResId = R.drawable.ic_no_my_schedule;
                        textResId = R.string.placeholder_schedule;
                        break;
                }

                mImageViewNoContent.setImageResource(imageResId);
                mTextViewNoContent.setText(getString(textResId));
            }
        } else {
            mLayoutPlaceholder.setVisibility(View.GONE);
            mPagerTabs.setVisibility(View.VISIBLE);
        }

        mAdapter.setData(dayList, mEventMode);
        switchToCurrentDay(dayList);
    }

    private void switchToCurrentDay(List<Long> days) {
        int item = 0;
        for (Long millis : days) {
            if (DateUtils.isToday(getActivity(), millis) || DateUtils.isAfterCurrentFate(millis)) {
                mViewPager.setCurrentItem(item);
                return;
            }
            item++;
        }
    }

    private void showFilter() {
        Activity activity = getActivity();
        if (activity instanceof HomeActivity) {

            if (!((HomeActivity) activity).mFilterDialog.isAdded()) {
                ((HomeActivity) activity).mFilterDialog.show(getActivity().getSupportFragmentManager(), "filter");
            }
        }
    }

    private void updateFilterState(@NotNull MenuItem filter) {
        mIsFilterUsed = false;
        List<Long> levelIds = preferencesManager.getExpLevels();
        List<Long> trackIds = preferencesManager.getTracks();

        if (!levelIds.isEmpty() || !trackIds.isEmpty()) {
            mIsFilterUsed = true;
        }

        if (mIsFilterUsed) {
            filter.setIcon(getResources().getDrawable(R.drawable.ic_filter));
        } else {
            filter.setIcon(getResources().getDrawable(R.drawable.ic_filter_empty));
        }
    }

    private void updateData(List<Integer> requestIds) {
        for (int id : requestIds) {
            int eventModePos = UpdatesManager.convertEventIdToEventModePos(id);
            if (eventModePos == mEventMode.ordinal() ||
                    (mEventMode == DrawerManager.EventMode.FAVORITES && isEventItem(id))) {
                new LoadData().execute();
                break;
            }
        }
    }

    private boolean isEventItem(int id) {
        return id == UpdatesManager.PROGRAMS_REQUEST_ID ||
                id == UpdatesManager.BOFS_REQUEST_ID ||
                id == UpdatesManager.SOCIALS_REQUEST_ID;
    }

    private void updateFavorites() {
        if (getView() != null) {
            if (mEventMode == DrawerManager.EventMode.FAVORITES) {
                new LoadData().execute();
            }
        }
    }
}
