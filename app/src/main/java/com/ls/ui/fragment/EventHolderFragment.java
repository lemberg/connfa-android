package com.ls.ui.fragment;

import com.astuetz.PagerSlidingTabStrip;
import com.ls.drupalcon.model.Listener;
import com.ls.drupalcon.model.managers.SharedScheduleManager;
import com.ls.drupalcon.model.managers.ToastManager;
import com.ls.http.base.ResponseData;
import com.ls.ui.dialog.AddScheduleDialog;
import com.ls.ui.dialog.CreateScheduleDialog;
import com.ls.ui.dialog.EditScheduleDialog;
import com.ls.ui.drawer.FriendFavoritesStrategy;
import com.ls.ui.view.MaterialTapTargetPrompt;
import com.ls.drupalcon.R;
import com.ls.drupalcon.app.App;
import com.ls.drupalcon.model.Model;
import com.ls.drupalcon.model.PreferencesManager;
import com.ls.drupalcon.model.UpdateRequest;
import com.ls.drupalcon.model.UpdatesManager;
import com.ls.ui.activity.HomeActivity;
import com.ls.ui.adapter.BaseEventDaysPagerAdapter;
import com.ls.ui.drawer.BofsStrategy;
import com.ls.ui.drawer.EventHolderFragmentStrategy;
import com.ls.ui.drawer.EventMode;
import com.ls.ui.drawer.FavoritesStrategy;
import com.ls.ui.drawer.ProgramStrategy;
import com.ls.ui.drawer.SocialStrategy;
import com.ls.ui.receiver.ReceiverManager;
import com.ls.utils.DateUtils;
import com.ls.utils.L;
import com.ls.utils.NetworkUtils;

import org.jetbrains.annotations.NotNull;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

public class EventHolderFragment extends Fragment {

    public static final String TAG = "ProjectsFragment";
    private static final String EXTRAS_ARG_MODE = "EXTRAS_ARG_MODE";
    public static final int ADD_SCHEDULE_DIALOG_REQUEST_CODE = 855;
    public static final int CHANGE_SCHEDULE_NAME_DIALOG_REQUEST_CODE = 8255;
    public static final int SET_SCHEDULE_NAME_DIALOG_REQUEST_CODE = 82555;
    public static final String SHARED_SCHEDULE_CODE_EXTRAS = "shared_schedule_code_extras";

    private ViewPager mViewPager;
    private PagerSlidingTabStrip mPagerTabs;
    private BaseEventDaysPagerAdapter mAdapter;

    private View mLayoutPlaceholder;
    private ImageView mImageViewNoContent;
    private TextView mTextViewNoContent;
    private boolean mIsFilterUsed;
    private EventHolderFragmentStrategy strategy;
    private boolean isItemRefreshEnabled = true;
    private ArrayAdapter<String> spinnerAdapter;
    private Spinner navigationSpinner;
    private SharedScheduleManager scheduleManager = Model.instance().getSharedScheduleManager();
    private ProgressBar mProgressBar;

    private UpdatesManager.DataUpdatedListener updateReceiver = new UpdatesManager.DataUpdatedListener() {
        @Override
        public void onDataUpdated(List<UpdateRequest> requests) {
            mProgressBar.setVisibility(View.GONE);
            isItemRefreshEnabled = true;
            updateData(requests);
            getActivity().invalidateOptionsMenu();

        }
    };
    private ReceiverManager favoriteReceiver = new ReceiverManager(new ReceiverManager.FavoriteUpdatedListener() {
        @Override
        public void onFavoriteUpdated(long eventId, boolean isFavorite) {
            updateFavorites();
        }
    });

    public static EventHolderFragment newInstance(EventMode eventMode) {
        EventHolderFragment fragment = new EventHolderFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRAS_ARG_MODE, eventMode);
        fragment.setArguments(bundle);

        return fragment;
    }

    public static EventHolderFragment newInstance(EventMode eventMode, long code) {
        EventHolderFragment fragment = new EventHolderFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRAS_ARG_MODE, eventMode);
        bundle.putLong(SHARED_SCHEDULE_CODE_EXTRAS, code);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_holder_event, container, false);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (!isFavoriteScreen()) {
            inflater.inflate(R.menu.menu_filter, menu);
            MenuItem filter = menu.findItem(R.id.actionFilter);
            if (filter != null) {
                updateFilterState(filter);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (isFavoriteScreen()) {
            menu.clear();
            MenuInflater menuInflater = getActivity().getMenuInflater();
            if (strategy.isMySchedule()) {
                showSearchPrompt();
                menuInflater.inflate(R.menu.menu_my_schedule, menu);
            } else {
                menuInflater.inflate(R.menu.menu_added_schedule, menu);
            }
        }

        MenuItem item = menu.findItem(R.id.actionRefresh);

        if (isItemRefreshEnabled) {
            item.setEnabled(true);
            item.getIcon().setAlpha(255);
        } else {
            item.setEnabled(false);
            item.getIcon().setAlpha(130);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionFilter:
                showFilter();
                break;
            case R.id.actionAddSchedule:
                if (NetworkUtils.isOn(getContext())) {
                    showAddScheduleDialog();
                } else {
                    ToastManager.message(getContext(), getString(R.string.NoConnectionMessage));
                }
                break;
            case R.id.actionShareMySchedule:
                shareSchedule();
                break;
            case R.id.actionEditSchedule:
                showChangeScheduleNameDialog(Model.instance().getSharedScheduleManager().getCurrentScheduleId(), Model.instance().getSharedScheduleManager().getCurrentFriendScheduleName());
                break;
            case R.id.actionRemoveSchedule:
                undo(Model.instance().getSharedScheduleManager().getCurrentFriendScheduleName() + " is removed");
                Model.instance().getSharedScheduleManager().deleteSharedSchedule();
                refreshSpinner();
                new LoadData().execute();
                break;

            case R.id.actionRefresh:
                refreshContent();
                break;
        }
        return true;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Model.instance().getUpdatesManager().registerUpdateListener(updateReceiver);
        favoriteReceiver.register(getActivity());
        initData();
        initView();
        new LoadData().execute();
        final long sharedScheduleCode = getArguments().getLong(SHARED_SCHEDULE_CODE_EXTRAS);
        L.e("New schedule code = " + sharedScheduleCode);
        if (sharedScheduleCode > 0) {
            if (Model.instance().getSharedScheduleManager().checkIfCodeIsExist(sharedScheduleCode)) {
                refreshSpinner();
            } else {
                fetchSharedEventsByCode(sharedScheduleCode, "Schedule " + sharedScheduleCode);
            }
        }

    }

    @Override
    public void onDestroyView() {
        Model.instance().getUpdatesManager().unregisterUpdateListener(updateReceiver);
        favoriteReceiver.unregister(getActivity());
        disableCustomToolBar();
        super.onDestroyView();
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            EventMode eventMode = (EventMode) bundle.getSerializable(EXTRAS_ARG_MODE);
            if (eventMode != null) {
                switch (eventMode) {
                    case Program:
                        strategy = new ProgramStrategy();
                        break;
                    case Bofs:
                        strategy = new BofsStrategy();
                        break;
                    case Social:
                        strategy = new SocialStrategy();
                        break;
                    case Favorites:
                        strategy = new FavoritesStrategy();
                        setCustomToolBar();
                        postAllSchedulesAsynchronously();
                        break;
                }
            }
        }
    }

    private void initView() {
        View view = getView();
        if (view == null) {
            return;
        }

        mLayoutPlaceholder = view.findViewById(R.id.layout_placeholder);

        mAdapter = new BaseEventDaysPagerAdapter(getChildFragmentManager());
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mViewPager.setAdapter(mAdapter);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
        mPagerTabs = (PagerSlidingTabStrip) getView().findViewById(R.id.pager_tab_strip);
        mPagerTabs.setTypeface(typeface, 0);
        mPagerTabs.setViewPager(mViewPager);
        mTextViewNoContent = (TextView) view.findViewById(R.id.text_view_placeholder);
        mImageViewNoContent = (ImageView) view.findViewById(R.id.image_view_placeholder);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        setHasOptionsMenu(true);
    }


    class LoadData extends AsyncTask<Void, Void, List<Long>> {

        @Override
        protected List<Long> doInBackground(Void... params) {
            return strategy.getDayList();
        }

        @Override
        protected void onPostExecute(List<Long> result) {
            if (getActivity() != null) {
                updateViews(result);
            }
        }
    }


    private void updateViews(List<Long> dayList) {
        if (dayList.isEmpty()) {
            mPagerTabs.setVisibility(View.GONE);
            mLayoutPlaceholder.setVisibility(View.VISIBLE);

            if (mIsFilterUsed) {
                mImageViewNoContent.setVisibility(View.GONE);
                mTextViewNoContent.setText(getString(R.string.placeholder_no_matching_events));
            } else {
                mImageViewNoContent.setVisibility(View.VISIBLE);
                mImageViewNoContent.setImageResource(strategy.getImageResId());
                mTextViewNoContent.setText(App.getContext().getText(strategy.getTextResId()));
            }
        } else {
            mLayoutPlaceholder.setVisibility(View.GONE);
            mPagerTabs.setVisibility(View.VISIBLE);
        }
        mAdapter.setData(dayList, strategy);
        switchToCurrentDay(dayList);
    }

    private void switchToCurrentDay(List<Long> days) {
        int item = 0;
        for (Long millis : days) {
            if (DateUtils.getInstance().isToday(millis) ||
                    DateUtils.getInstance().isAfterCurrentDate(millis)) {
                mViewPager.setCurrentItem(item);
                break;
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
        List<Long> levelIds = PreferencesManager.getInstance().loadExpLevel();
        List<Long> trackIds = PreferencesManager.getInstance().loadTracks();

        if (!levelIds.isEmpty() || !trackIds.isEmpty()) {
            mIsFilterUsed = true;
        }

        if (mIsFilterUsed) {
            filter.setIcon(getResources().getDrawable(R.drawable.ic_filter));
        } else {
            filter.setIcon(getResources().getDrawable(R.drawable.ic_filter_empty));
        }
    }

    private void updateData(List<UpdateRequest> requests) {
        if (strategy.update(requests)) {
            new LoadData().execute();
        }
    }


    private void updateFavorites() {
        if (getView() != null) {
            if (strategy.updateFavorites()) {
                new LoadData().execute();
            }
        }
    }


    public void showSearchPrompt() {
        if (!PreferencesManager.getInstance().getFirstRunFlag()) {
            PreferencesManager.getInstance().saveFirstRunFlag();
            new MaterialTapTargetPrompt.Builder(getActivity())
                    .setPrimaryText(R.string.share_your_schedule_with_friends)
                    .setSecondaryText(R.string.tap_the_three_dots)
                    .setAnimationInterpolator(new FastOutSlowInInterpolator())
                    .setMaxTextWidth(1000f)
                    .setIcon(R.drawable.ic_menu_more)
                    .setTarget(R.id.promptAnchor)
                    .setBackgroundColour(getResources().getColor(R.color.primary))
                    .setIconDrawableColourFilter(getResources().getColor(R.color.primary))
                    .show();
        }
    }

    private void setCustomToolBar() {
        if (Model.instance().getSharedScheduleManager().getAllSchedulesNameList().size() == 1) {
            return;
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        android.support.v7.app.ActionBar toolbar = activity.getSupportActionBar();

        SharedScheduleManager sharedScheduleManager = Model.instance().getSharedScheduleManager();
        spinnerAdapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner, sharedScheduleManager.getAllSchedulesNameList());
        spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        navigationSpinner = new Spinner(getContext());
        navigationSpinner.setAdapter(spinnerAdapter);
        if (toolbar != null) {
            toolbar.setCustomView(navigationSpinner);
            toolbar.setDisplayShowCustomEnabled(true);
            toolbar.setDisplayShowTitleEnabled(false);
        }
        navigationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Model.instance().getSharedScheduleManager().setCurrentSchedule(position);
                if (position == 0) {
                    strategy = new FavoritesStrategy();
                    getActivity().invalidateOptionsMenu();

                } else {
                    strategy = new FriendFavoritesStrategy();
                    getActivity().invalidateOptionsMenu();
                }
                new LoadData().execute();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void disableCustomToolBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        android.support.v7.app.ActionBar toolbar = activity.getSupportActionBar();
        if (toolbar != null) {
            toolbar.setDisplayShowTitleEnabled(true);
            toolbar.setDisplayShowCustomEnabled(false);
        }
    }

    private void setToolbarTitle() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        android.support.v7.app.ActionBar toolbar = activity.getSupportActionBar();
        if (toolbar != null) {
            toolbar.setTitle(getString(R.string.my_schedule));
        }
    }

    private boolean isFavoriteScreen() {
        return strategy.getEventMode() == EventMode.Favorites || strategy.getEventMode() == EventMode.SharedSchedules;
    }

    private void shareSchedule() {

        if (!NetworkUtils.isOn(getContext())) {
            ToastManager.message(getContext(), getString(R.string.NoConnectionMessage));
            return;
        }

        if (strategy.getDayList().isEmpty()) {
            ToastManager.message(getContext(), "Currently you have no favorites");
            return;
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "My Schedule");
        sendIntent.putExtra(Intent.EXTRA_TEXT, getEmailBody().toString());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);


    }

    private StringBuilder getEmailBody() {
        StringBuilder builder = new StringBuilder();
        builder.append("Hi, I have just published/shared my schedule for ")
                .append(getString(R.string.app_name))
                .append(" where I will be an attendee.")
                .append(" Here is the link to add my schedule into the app: ")
                .append("https://connfa-integration.uat.link/schedule/share?code=" + scheduleManager.getMyScheduleCode())
                .append("\n If you have any issues with the link, use the Schedule Unique Code in the app to add my schedule manually.\n")
                .append("\nSchedule Unique Code: ")
                .append(scheduleManager.getMyScheduleCode());
        return builder;
    }

    void showAddScheduleDialog() {
        DialogFragment newFragment = AddScheduleDialog.newInstance();
        newFragment.setTargetFragment(this, ADD_SCHEDULE_DIALOG_REQUEST_CODE);
        newFragment.show(getChildFragmentManager(), AddScheduleDialog.TAG);
    }

    void showChangeScheduleNameDialog(long code, String name) {
        DialogFragment newFragment = CreateScheduleDialog.newEditDialogInstance(code, name);
        newFragment.setTargetFragment(this, CHANGE_SCHEDULE_NAME_DIALOG_REQUEST_CODE);
        newFragment.show(getChildFragmentManager(), EditScheduleDialog.TAG);
    }

    void showSetNameDialog(long code) {
        mProgressBar.setVisibility(View.VISIBLE);
        DialogFragment newFragment = CreateScheduleDialog.newCreateDialogInstance(code);
        newFragment.setTargetFragment(this, SET_SCHEDULE_NAME_DIALOG_REQUEST_CODE);
        newFragment.show(getChildFragmentManager(), CreateScheduleDialog.TAG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADD_SCHEDULE_DIALOG_REQUEST_CODE:
                mProgressBar.setVisibility(View.GONE);
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        long newScheduleCode = data.getLongExtra(AddScheduleDialog.EXTRA_SCHEDULE_CODE, SharedScheduleManager.MY_DEFAULT_SCHEDULE_CODE);
                        showSetNameDialog(newScheduleCode);
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                    case AddScheduleDialog.RESULT_OK_CODE_IS_EXIST:
                        refreshSpinner();
                        break;
                }
                break;
            case SET_SCHEDULE_NAME_DIALOG_REQUEST_CODE:
                mProgressBar.setVisibility(View.GONE);
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        String name = data.getStringExtra(CreateScheduleDialog.EXTRA_SCHEDULE_NAME);
                        Model.instance().getSharedScheduleManager().renameSchedule(name);
                        refreshSpinner();

                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
            case CHANGE_SCHEDULE_NAME_DIALOG_REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        String newName = data.getStringExtra(EditScheduleDialog.EXTRA_SCHEDULE_NAME);
                        Model.instance().getSharedScheduleManager().renameSchedule(newName);
                        refreshSpinner();
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
        }

    }

    private void undo(String message) {
        Snackbar snack = Snackbar.make(getView(), message, Snackbar.LENGTH_LONG);
        snack.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedScheduleManager manager = Model.instance().getSharedScheduleManager();
                manager.restoreSchedule();
                refreshSpinner();
            }
        });
        snack.setActionTextColor(Color.parseColor("#65B6AA"));
        snack.show();
    }

    private void refreshSpinner() {
        SharedScheduleManager sharedScheduleManager = Model.instance().getSharedScheduleManager();
        List<String> allSchedulesNameList = sharedScheduleManager.getAllSchedulesNameList();
        if (allSchedulesNameList.size() == 1) {
            disableCustomToolBar();
            setToolbarTitle();
            //todo check this: setSpinnerPosition
            setSpinnerPosition(0);
            strategy = new FavoritesStrategy();
        } else {
            setCustomToolBar();
            spinnerAdapter.clear();
            spinnerAdapter.addAll(allSchedulesNameList);
            setSpinnerPosition(Model.instance().getSharedScheduleManager().getItemPosition());
            strategy = new FriendFavoritesStrategy();
        }
        getActivity().invalidateOptionsMenu();
    }

    private void setSpinnerPosition(int position) {
        navigationSpinner.setSelection(position);
    }

    private void postAllSchedulesAsynchronously() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Model.instance().getSharedScheduleManager().postAllSchedules();
            }
        }).run();
    }

    private void refreshContent() {

        if (NetworkUtils.isOn(getContext())) {
            mProgressBar.setVisibility(View.VISIBLE);
            UpdatesManager manager = Model.instance().getUpdatesManager();
            manager.startLoading(null);

            isItemRefreshEnabled = false;
            getActivity().invalidateOptionsMenu();
        } else {
            ToastManager.messageSync(getContext(), getString(R.string.NoConnectionMessage));
        }
    }

    public void fetchSharedEventsByCode(final long scheduleCode, final String name) {
        mProgressBar.setVisibility(View.VISIBLE);
        Model.instance().getSharedScheduleManager().fetchSharedEventsByCode(scheduleCode, name, new Listener<ResponseData, ResponseData>() {
            @Override
            public void onSucceeded(ResponseData result) {
                showSetNameDialog(scheduleCode);
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailed(ResponseData result) {
                ToastManager.messageSync(App.getContext(), "Schedule not found. Please check your code");
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }


}
