package com.connfa.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.connfa.R;
import com.connfa.analytics.Analytics;
import com.connfa.model.Model;
import com.connfa.model.PreferencesManager;
import com.connfa.model.UpdatesManager;
import com.connfa.model.data.EventDetailsEvent;
import com.connfa.model.data.Level;
import com.connfa.model.data.Speaker;
import com.connfa.model.managers.EventManager;
import com.connfa.model.managers.FavoriteManager;
import com.connfa.model.managers.SpeakerManager;
import com.connfa.ui.receiver.ReceiverManager;
import com.connfa.ui.view.CircleImageView;
import com.connfa.ui.view.NotifyingScrollView;
import com.connfa.utils.DateUtils;
import com.connfa.utils.ScheduleManager;
import com.connfa.utils.WebviewUtils;

import java.util.ArrayList;
import java.util.List;

public class EventDetailsActivity extends StackKeeperActivity {

    public static final String EXTRA_EVENT_ID = "EXTRA_EVENT_ID";
    public static final String EXTRA_DAY = "EXTRA_DAY";

    private PreferencesManager preferencesManager;

    private TextView mToolbarTitle;
    private View mViewToolbar;

    private NotifyingScrollView mScrollView;
    private MenuItem mItemShare;

    private EventDetailsEvent mEvent;
    private List<Speaker> mSpeakerList;
    private long mEventId;
    private long mEventStartDate;
    private boolean mIsFavorite;

    private ReceiverManager receiverManager = new ReceiverManager(
            new ReceiverManager.FavoriteUpdatedListener() {
                @Override
                public void onFavoriteUpdated(long eventId, boolean isFavorite) {
                    mIsFavorite = isFavorite;
                }
            });

    private UpdatesManager.DataUpdatedListener updateListener = new UpdatesManager.DataUpdatedListener() {
        @Override
        public void onDataUpdated(List<Integer> requestIds) {
            loadEvent();
        }
    };

    public static void startThisActivity(Activity activity, long eventId, long day) {
        Intent intent = new Intent(activity, EventDetailsActivity.class);
        intent.putExtra(EXTRA_EVENT_ID, eventId);
        intent.putExtra(EXTRA_DAY, day);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_event_details);

        receiverManager.register(this);
        Model.getInstance().getUpdatesManager().registerUpdateListener(updateListener);
        preferencesManager = PreferencesManager.create(this);

        initData();
        initToolbar();
        initViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_share, menu);
        mItemShare = menu.findItem(R.id.actionShare);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        loadEvent();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.actionShare:
                if (mEvent != null) {
                    shareEvent(mEvent.getLink());
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        receiverManager.unregister(this);
        Model.getInstance().getUpdatesManager().unregisterUpdateListener(updateListener);
    }

    private void initData() {
        mEventId = getIntent().getLongExtra(EXTRA_EVENT_ID, -1);
        mEventStartDate = getIntent().getLongExtra(EXTRA_DAY, -1);
    }

    private void initToolbar() {
        mToolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        mViewToolbar = findViewById(R.id.viewToolbar);

        mToolbarTitle.setAlpha(0f);
        mViewToolbar.setAlpha(0f);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("");
            setSupportActionBar(toolbar);

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private void initViews() {
        mSpeakerList = new ArrayList<>();
        mScrollView = (NotifyingScrollView) findViewById(R.id.scrollView);
        mScrollView.setOnScrollChangedListener(scrollListener);
        mScrollView.setAlpha(0);
    }

    private void loadEvent() {
        if (mEventId == -1) {
            return;
        }

        new AsyncTask<Void, Void, EventDetailsEvent>() {
            @Override
            protected EventDetailsEvent doInBackground(Void... params) {
                SpeakerManager speakerManager = Model.getInstance().getSpeakerManager();
                mSpeakerList.clear();
                mSpeakerList.addAll(speakerManager.getSpeakersByEventId(mEventId));

                EventManager eventManager = Model.getInstance().getEventManager();
                return eventManager.getEventById(mEventId);
            }

            @Override
            protected void onPostExecute(EventDetailsEvent event) {
                if (event != null) {
                    fillEventView(event);
                }
            }
        }.execute();
    }

    private void fillEventView(@NonNull EventDetailsEvent event) {
        mEvent = event;
        fillToolbar(mEvent);
        fillDate(mEvent);
        fillPreDescription(mEvent);
        fillFavoriteState(mEvent);
        fillSpeakers();
        fillDescription(mEvent);
        updatePlaceholderVisibility(mEvent);
    }

    private void fillToolbar(@NonNull EventDetailsEvent event) {
        if (mToolbarTitle != null && !TextUtils.isEmpty(event.getEventName())) {
            mToolbarTitle.setText(event.getEventName());
        }

        if (mItemShare != null && !TextUtils.isEmpty(event.getLink())) {
            mItemShare.setVisible(true);
        }
    }

    private void fillDate(@NonNull EventDetailsEvent event) {
        String eventName = event.getEventName();
        TextView txtEventName = (TextView) findViewById(R.id.txtEventName);
        txtEventName.setText(eventName);

        String fromTime = DateUtils.getTime(this, event.getFrom());
        String toTime = DateUtils.getTime(this, event.getTo());
        String eventLocation = DateUtils.getWeekDay(this, mEventStartDate) + ", " + fromTime + " - " + toTime;

        if (!TextUtils.isEmpty(event.getPlace())) {
            String eventPlace = String.format(" in %s", event.getPlace());
            eventLocation += eventPlace;
        }

        TextView txtEventLocation = (TextView) findViewById(R.id.label_where);
        txtEventLocation.setText(eventLocation);
    }

    private void fillPreDescription(@NonNull EventDetailsEvent event) {
        LinearLayout layoutPreDescription = (LinearLayout) findViewById(R.id.layoutPreDescription);
        TextView txtTrack = (TextView) findViewById(R.id.txtTrack);
        TextView txtExpLevel = (TextView) findViewById(R.id.txtExpLevel);
        ImageView imgExpIcon = (ImageView) findViewById(R.id.imgExperience);

        if (TextUtils.isEmpty(event.getTrack()) && TextUtils.isEmpty(event.getLevel())) {
            layoutPreDescription.setVisibility(View.GONE);
        } else {

            if (!TextUtils.isEmpty(event.getTrack())) {
                txtTrack.setText(event.getTrack());
            } else {
                txtTrack.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(event.getLevel())) {
                txtExpLevel.setText(String.format("%s %s", getString(R.string.exp_level), event.getLevel()));
                imgExpIcon.setImageResource(Level.getIcon(event.getLevelId()));
            } else {
                txtExpLevel.setVisibility(View.GONE);
                imgExpIcon.setVisibility(View.GONE);
            }
        }
    }

    private void fillDescription(@NonNull EventDetailsEvent event) {
        WebView webView = (WebView) findViewById(R.id.webView);
        if (!TextUtils.isEmpty(event.getDescription())) {

            webView.setVisibility(View.VISIBLE);

            String html = WebviewUtils.getHtml(this, event.getDescription());
            webView.setHorizontalScrollBarEnabled(false);
            webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    completeLoading();
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    WebviewUtils.openUrl(EventDetailsActivity.this, url);
                    return true;
                }
            });

        } else {
            webView.setVisibility(View.GONE);
            completeLoading();
        }
    }

    private void updatePlaceholderVisibility(EventDetailsEvent event) {
        if (TextUtils.isEmpty(event.getTrack()) &&
                TextUtils.isEmpty(event.getLevel()) &&
                TextUtils.isEmpty(event.getDescription()) &&
                mSpeakerList.isEmpty()) {
            findViewById(R.id.imgEmptyView).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.imgEmptyView).setVisibility(View.GONE);
        }
    }

    private void fillFavoriteState(@NonNull EventDetailsEvent event) {
        mIsFavorite = event.isFavorite();

        final CheckBox checkBoxFavorite = (CheckBox) findViewById(R.id.checkBoxFavorite);
        checkBoxFavorite.setChecked(mIsFavorite);

        RelativeLayout layoutFavorite = (RelativeLayout) findViewById(R.id.layoutFavorite);
        layoutFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxFavorite.setChecked(!checkBoxFavorite.isChecked());
                mIsFavorite = checkBoxFavorite.isChecked();
                setFavorite();
            }
        });
    }

    private void fillSpeakers() {
        List<Speaker> speakerList = new ArrayList<>();
        speakerList.addAll(mSpeakerList);

        LayoutInflater inflater = LayoutInflater.from(EventDetailsActivity.this);
        LinearLayout holderSpeakers = (LinearLayout) findViewById(R.id.holderSpeakers);
        holderSpeakers.removeAllViewsInLayout();

        if (!speakerList.isEmpty()) {
            for (Speaker speaker : speakerList) {
                View speakerView = inflater.inflate(R.layout.item_speaker_no_letter, holderSpeakers, false);
                fillSpeakerView(speaker, speakerView);
                holderSpeakers.addView(speakerView);
            }
            findViewById(R.id.botDivider).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.botDivider).setVisibility(View.GONE);
        }
    }

    private void fillSpeakerView(final Speaker speaker, View speakerView) {
        CircleImageView imgPhoto = (CircleImageView) speakerView.findViewById(R.id.imgPhoto);
        String imageUrl = speaker.getAvatarImageUrl();
        imgPhoto.setImageWithURL(imageUrl);

        TextView txtName = (TextView) speakerView.findViewById(R.id.txtName);
        txtName.setText(String.format("%s %s", speaker.getFirstName(), speaker.getLastName()));

        String organization = speaker.getOrganization();
        String jobTitle = speaker.getJobTitle();
        String space = (TextUtils.isEmpty(organization) && TextUtils.isEmpty(jobTitle)) || TextUtils.isEmpty(organization) || TextUtils.isEmpty(jobTitle) ? "" : " / ";

        TextView txtJob = (TextView) speakerView.findViewById(R.id.txtOrgAndJobTitle);
        txtJob.setText(organization + space + jobTitle);

        speakerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventDetailsActivity.this, SpeakerDetailsActivity.class);
                intent.putExtra(SpeakerDetailsActivity.EXTRA_SPEAKER_ID, speaker.getId());
                intent.putExtra(SpeakerDetailsActivity.EXTRA_SPEAKER, speaker);
                startActivity(intent);
            }
        });
    }

    private void setFavorite() {
        final FavoriteManager manager = new FavoriteManager(EventDetailsActivity.this.getApplicationContext());
        new Thread(new Runnable() {
            @Override
            public void run() {
                manager.setFavoriteEvent(mEventId, mIsFavorite);
            }
        }).start();
        setToNotificationQueue();

        int actionId = R.string.action_remove_from_favorites;
        if (mIsFavorite) {
            actionId = R.string.action_add_to_favorites;
        }
        Analytics.from(this)
                .trackEvent(
                        getString(R.string.event_category), getString(actionId), mEventId + " " + mEvent.getEventName()
                );
        ReceiverManager.updateFavorites(EventDetailsActivity.this, mEventId, mIsFavorite);
    }

    private void setToNotificationQueue() {
        ScheduleManager manager = new ScheduleManager(this);

        if (mIsFavorite) {

            long currMillis = System.currentTimeMillis();
            long eventMillis = mEvent.getFrom();

            if (eventMillis > currMillis) {
                manager.setAlarmForNotification(mEvent, eventMillis, mEventStartDate);
            }

        } else {
            manager.cancelAlarm(mEventId);
        }
    }

    public void shareEvent(String url) {
        startActivity(Intent.createChooser(
                createShareIntent(url),
                getString(R.string.title_share)));
    }

    public Intent createShareIntent(String url) {
        String shareSubject = preferencesManager.getMajorInfoTitle();
        ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setSubject(shareSubject)
                .setText(getString(R.string.body_share) + " " + url);
        return builder.getIntent();
    }

    private void completeLoading() {
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        mScrollView.setAlpha(1.0f);
    }

    private NotifyingScrollView.OnScrollChangedListener scrollListener = new NotifyingScrollView.OnScrollChangedListener() {

        @Override
        public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
            int headerHeight = findViewById(R.id.imgHeader).getHeight();
            float headerRatio = (float) Math.min(Math.max(t, 0), headerHeight) / headerHeight;
            fadeActionBar(headerRatio);
        }

        private void fadeActionBar(float headerRatio) {
            mToolbarTitle.setAlpha(headerRatio);
            mViewToolbar.setAlpha(headerRatio);
        }
    };
}
