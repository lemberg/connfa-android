package com.connfa.ui.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.connfa.R;
import com.connfa.analytics.Analytics;
import com.connfa.model.Model;
import com.connfa.model.UpdatesManager;
import com.connfa.model.dao.EventDao;
import com.connfa.model.data.Level;
import com.connfa.model.data.Speaker;
import com.connfa.model.data.SpeakerDetailsEvent;
import com.connfa.model.managers.SpeakerManager;
import com.connfa.ui.view.CircleImageView;
import com.connfa.ui.view.NotifyingScrollView;
import com.connfa.utils.DateUtils;
import com.connfa.utils.WebviewUtils;

import java.util.List;

public class SpeakerDetailsActivity extends StackKeeperActivity implements View.OnClickListener {

    public static final String EXTRA_SPEAKER = "EXTRA_SPEAKER";
    public static final String EXTRA_SPEAKER_ID = "EXTRA_SPEAKER_ID";

    private static final String TWITTER_URL = "https://twitter.com/";
    private static final String TWITTER_APP_URL = "twitter://user?screen_name=";

    private SpeakerManager mSpeakerManager;
    private long mSpeakerId = -1;
    private String mSpeakerName;
    private Speaker mSpeaker;

    private View mViewToolbar;
    private TextView mTitle;
    private NotifyingScrollView mScrollView;
    private View mLayoutPlaceholder;

    private boolean mIsDataLoaded;
    private boolean mIsWebLoaded;

    private UpdatesManager.DataUpdatedListener updateListener = new UpdatesManager.DataUpdatedListener() {
        @Override
        public void onDataUpdated(List<Integer> requestIds) {
            loadSpeakerFromDb();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_speaker_details);

        Model.getInstance().getUpdatesManager().registerUpdateListener(updateListener);
        mSpeakerManager = Model.getInstance().getSpeakerManager();

        initData();
        initToolbar();
        initView();
        loadSpeakerFromDb();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Model.getInstance()
                .getUpdatesManager()
                .unregisterUpdateListener(updateListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initData() {
        mSpeaker = getIntent().getParcelableExtra(EXTRA_SPEAKER);
        mSpeakerId = getIntent().getLongExtra(EXTRA_SPEAKER_ID, -1);
        if (mSpeaker != null) {
            mSpeakerName = String.format("%s %s", mSpeaker.getFirstName(), mSpeaker.getLastName());
        }

        Analytics.from(this)
                .trackEvent(
                        getString(R.string.speaker_category), getString(R.string.action_open), mSpeakerId + " " + mSpeakerName
                );
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("");
            setSupportActionBar(toolbar);

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        mViewToolbar = findViewById(R.id.viewToolbar);
        mViewToolbar.setAlpha(0);

        mTitle = (TextView) findViewById(R.id.toolbarTitle);
        mTitle.setText(mSpeakerName);
        mTitle.setAlpha(0);
    }

    private void initView() {
        mScrollView = (NotifyingScrollView) findViewById(R.id.scrollView);
        mScrollView.setOnScrollChangedListener(onScrollChangedListener);
        mLayoutPlaceholder = findViewById(R.id.layout_placeholder);
    }

    private void loadSpeakerFromDb() {
        if (mSpeakerId == -1) {
            return;
        }

        new AsyncTask<Void, Void, Speaker>() {
            @Override
            protected Speaker doInBackground(Void... params) {
                return mSpeakerManager.getSpeakerById(mSpeakerId);
            }

            @Override
            protected void onPostExecute(Speaker speaker) {
                if (speaker != null) {
                    fillView(speaker);
                } else {
                    finish();
                }
            }
        }.execute();
    }

    private void fillView(Speaker speaker) {
        mSpeaker = speaker;
        fillSpeakerInfo();
        fillSpeakerDescription();
        fillSocialNetworks();
        loadSpeakerEvents(mSpeaker);
    }

    private void fillSpeakerInfo() {
        CircleImageView imgPhoto = (CircleImageView) findViewById(R.id.imgPhoto);
        String imageUrl = mSpeaker.getAvatarImageUrl();
        imgPhoto.setImageWithURL(imageUrl);

        String speakerName = TextUtils.isEmpty(mSpeaker.getFirstName()) ? "" : mSpeaker.getFirstName() + " ";
        speakerName += TextUtils.isEmpty(mSpeaker.getLastName()) ? "" : mSpeaker.getLastName();
        ((TextView) findViewById(R.id.txtSpeakerName)).setText(speakerName);

        if (TextUtils.isEmpty(mSpeaker.getJobTitle()) && TextUtils.isEmpty(mSpeaker.getOrganization())) {
            findViewById(R.id.txtSpeakerPosition).setVisibility(View.GONE);
        } else {
            findViewById(R.id.txtSpeakerPosition).setVisibility(View.VISIBLE);
        }

        TextView jobTxt = (TextView) findViewById(R.id.txtSpeakerPosition);
        String jobValue = mSpeaker.getJobTitle() + " at " + mSpeaker.getOrganization();

        if (TextUtils.isEmpty(mSpeaker.getJobTitle()) || TextUtils.isEmpty(mSpeaker.getOrganization())) {
            jobValue = jobValue.replace(" at ", "");
        }

        jobTxt.setText(jobValue);
    }

    private void fillSpeakerDescription() {
        WebView webView = (WebView) findViewById(R.id.webView);
        if (!TextUtils.isEmpty(mSpeaker.getCharact())) {

            String html = WebviewUtils.getHtml(this, mSpeaker.getCharact());
            webView.setVisibility(View.VISIBLE);

            webView.setHorizontalScrollBarEnabled(false);
            webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    mIsWebLoaded = true;
                    completeLoading();
                }

                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        return false;
                    }
                    WebviewUtils.openUrl(SpeakerDetailsActivity.this, request.getUrl().toString());
                    return true;
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        return false;
                    }
                    WebviewUtils.openUrl(SpeakerDetailsActivity.this, url);
                    return true;
                }
            });

        } else {
            webView.setVisibility(View.GONE);
            mIsWebLoaded = true;
            completeLoading();
        }
    }

    private void fillSocialNetworks() {
        if (TextUtils.isEmpty(mSpeaker.getTwitterName())) {
            findViewById(R.id.holderBtnTwitter).setVisibility(View.GONE);
        } else {
            findViewById(R.id.btnTwitter).setOnClickListener(this);
        }

        if (TextUtils.isEmpty(mSpeaker.getWebSite())) {
            findViewById(R.id.holderBtnWebsite).setVisibility(View.GONE);
        } else {
            findViewById(R.id.btnWebsite).setOnClickListener(this);
        }
    }

    private void loadSpeakerEvents(final Speaker speaker) {
        // This still sucks but as a temporary workaround I'd rather keep using the app context
        // rather than leaking the activity context.
        final EventDao eventDao = new EventDao(getApplicationContext());

        new AsyncTask<Void, Void, List<SpeakerDetailsEvent>>() {

            @Override
            protected List<SpeakerDetailsEvent> doInBackground(Void... params) {
                return eventDao.getEventsBySpeakerId(speaker.getId());
            }

            @Override
            protected void onPostExecute(List<SpeakerDetailsEvent> events) {
                mIsDataLoaded = true;
                addSpeakerEvents(events);
                completeLoading();
                updatePlaceholderVisibility(events);
            }
        }.execute();
    }

    private void addSpeakerEvents(List<SpeakerDetailsEvent> events) {
        LinearLayout layoutEvents = (LinearLayout) findViewById(R.id.layoutEvents);
        LayoutInflater inflater = LayoutInflater.from(SpeakerDetailsActivity.this);
        layoutEvents.removeAllViews();

        for (SpeakerDetailsEvent event : events) {
            View eventView = inflater.inflate(R.layout.item_speakers_event, layoutEvents, false);
            fillEventView(event, eventView);
            layoutEvents.addView(eventView);
        }
    }

    private void fillEventView(final SpeakerDetailsEvent event, View eventView) {
        ((TextView) eventView.findViewById(R.id.txtArticleName)).setText(event.getEventName());
        TextView txtTrack = (TextView) eventView.findViewById(R.id.txtTrack);
        if (!TextUtils.isEmpty(event.getTrackName())) {
            txtTrack.setText(event.getTrackName());
            txtTrack.setVisibility(View.VISIBLE);
        }

        String weekDay = DateUtils.getWeekDay(this, event.getFrom());
        String fromTime = DateUtils.getTime(this, event.getFrom());
        String toTime = DateUtils.getTime(this, event.getTo());

        TextView txtWhere = (TextView) eventView.findViewById(R.id.txtWhere);
        String date = String.format("%s, %s - %s", weekDay, fromTime, toTime);
        txtWhere.setText(date);
        if (!event.getPlace().equals("")) {
            txtWhere.append(String.format(" in %s", event.getPlace()));
        }

        initEventExpLevel(eventView, event);
        eventView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventDetailsActivity.startThisActivity(SpeakerDetailsActivity.this, event.getEventId(), event.getFrom());
            }
        });
    }

    private void initEventExpLevel(View eventView, SpeakerDetailsEvent event) {
        TextView txtExpLevel = (TextView) eventView.findViewById(R.id.txtExpLevel);
        ImageView imgExpLevel = (ImageView) eventView.findViewById(R.id.imgExpLevel);

        String expLevel = event.getLevelName();
        if (!TextUtils.isEmpty(expLevel)) {

            String expText = String.format("%s %s", getResources().getString(R.string.experience_level), expLevel);
            txtExpLevel.setText(expText);
            txtExpLevel.setVisibility(View.VISIBLE);

            int expIcon = Level.getIcon(expLevel);
            if (expIcon != 0) {
                imgExpLevel.setImageResource(expIcon);
                imgExpLevel.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnTwitter:
                try {
                    String url = TWITTER_APP_URL + mSpeaker.getTwitterName();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
                    String url = TWITTER_URL + mSpeaker.getTwitterName();
                    openBrowser(url);
                }
                break;
            case R.id.btnWebsite:
                String url = mSpeaker.getWebSite();
                openBrowser(url);
                break;
        }
    }

    private NotifyingScrollView.OnScrollChangedListener onScrollChangedListener = new NotifyingScrollView.OnScrollChangedListener() {

        @Override
        public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
            int headerHeight = findViewById(R.id.imgHeader).getHeight();
            float headerRatio = (float) Math.min(Math.max(t, 0), headerHeight) / headerHeight;

            fadeActionBar(headerRatio);
        }

        private void fadeActionBar(float headerRatio) {
            mTitle.setAlpha(headerRatio);
            mViewToolbar.setAlpha(headerRatio);
        }
    };

    private void openBrowser(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.no_apps_can_perform_this_action), Toast.LENGTH_SHORT).show();
        }
    }

    private void completeLoading() {
        if (mIsDataLoaded && mIsWebLoaded) {
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            mScrollView.setVisibility(View.VISIBLE);
        }
    }

    private void updatePlaceholderVisibility(List<SpeakerDetailsEvent> events) {
        if (TextUtils.isEmpty(mSpeaker.getTwitterName()) &&
                TextUtils.isEmpty(mSpeaker.getWebSite()) &&
                TextUtils.isEmpty(mSpeaker.getCharact()) &&
                events.isEmpty()) {
            mLayoutPlaceholder.setVisibility(View.VISIBLE);
        } else {
            mLayoutPlaceholder.setVisibility(View.GONE);
        }
    }
}
