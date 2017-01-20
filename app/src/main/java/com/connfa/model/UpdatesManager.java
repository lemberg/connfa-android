package com.connfa.model;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.connfa.R;
import com.connfa.model.data.UpdateDate;
import com.connfa.model.database.ILAPIDBFacade;
import com.connfa.model.managers.SynchronousItemManager;
import com.connfa.ui.drawer.DrawerManager;
import com.ls.drupal.DrupalClient;
import com.ls.http.base.BaseRequest;
import com.ls.http.base.RequestConfig;
import com.ls.http.base.ResponseData;
import com.ls.util.ObserverHolder;

import java.util.LinkedList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public class UpdatesManager {

    public static final int SETTINGS_REQUEST_ID = 0;
    public static final int TYPES_REQUEST_ID = 1;
    public static final int LEVELS_REQUEST_ID = 2;
    public static final int TRACKS_REQUEST_ID = 3;
    public static final int SPEAKERS_REQUEST_ID = 4;
    public static final int LOCATIONS_REQUEST_ID = 5;
    public static final int FLOOR_PLANS_REQUEST_ID = 6;
    public static final int PROGRAMS_REQUEST_ID = 7;
    public static final int BOFS_REQUEST_ID = 8;
    public static final int SOCIALS_REQUEST_ID = 9;
    public static final int POIS_REQUEST_ID = 10;
    public static final int INFO_REQUEST_ID = 11;

    private final Context context;
    private final DrupalClient client;
    private final PreferencesManager preferencesManager;

    private ObserverHolder<DataUpdatedListener> updateListeners;
    public static final String IF_MODIFIED_SINCE_HEADER = "If-Modified-Since";

    public static final String LAST_MODIFIED_HEADER = "Last-Modified";

    public static int convertEventIdToEventModePos(int eventModePos) {
        switch (eventModePos) {
            case PROGRAMS_REQUEST_ID:
                return DrawerManager.EventMode.PROGRAM.ordinal();
            case BOFS_REQUEST_ID:
                return DrawerManager.EventMode.BOFS.ordinal();
            case SOCIALS_REQUEST_ID:
                return DrawerManager.EventMode.SOCIAL.ordinal();
        }
        return 0;
    }

    public UpdatesManager(Context context, DrupalClient client) {
        this.context = context;
        this.client = client;
        this.preferencesManager = PreferencesManager.create(context);
        updateListeners = new ObserverHolder<>();
    }

    public void startLoading(@NotNull final UpdateCallback callback) {
        new AsyncTask<Void, Void, List<Integer>>() {

            @Override
            protected List<Integer> doInBackground(Void... params) {
                return doPerformLoading();
            }

            @Override
            protected void onPostExecute(final List<Integer> result) {
                if (result != null) {
                    updateListeners.notifyAllObservers(new ObserverHolder.ObserverNotifier<DataUpdatedListener>() {
                        @Override
                        public void onNotify(DataUpdatedListener observer) {
                            observer.onDataUpdated(result);
                        }
                    });
                }

                if (result != null) {
                    callback.onDownloadSuccess();
                    updateListeners.notifyAllObservers(new ObserverHolder.ObserverNotifier<DataUpdatedListener>() {
                        @Override
                        public void onNotify(DataUpdatedListener observer) {
                            observer.onDataUpdated(result);
                        }
                    });
                } else {
                    callback.onDownloadError();
                }
            }
        }.execute();
    }

    public void registerUpdateListener(DataUpdatedListener listener) {
        this.updateListeners.registerObserver(listener);
    }

    public void unregisterUpdateListener(DataUpdatedListener listener) {
        this.updateListeners.unregisterObserver(listener);
    }

    /**
     * @return return updated request id's list in case of success or null in case of failure
     */

    private List<Integer> doPerformLoading() {
        RequestConfig config = new RequestConfig();
        config.setResponseFormat(BaseRequest.ResponseFormat.JSON);
        config.setRequestFormat(BaseRequest.RequestFormat.JSON);
        config.setResponseClassSpecifier(UpdateDate.class);
        String baseURL = context.getString(R.string.api_value_base_url);
        BaseRequest checkForUpdatesRequest = new BaseRequest(BaseRequest.RequestMethod.GET, baseURL + "checkUpdates", config);
        String lastDate = preferencesManager.getLastUpdateDate();
        checkForUpdatesRequest.addRequestHeader(IF_MODIFIED_SINCE_HEADER, lastDate);
        ResponseData updatesData = client.performRequest(checkForUpdatesRequest, true);

        int statusCode = updatesData.getStatusCode();
        if (statusCode > 0 && statusCode < 400) {
            UpdateDate updateDate = (UpdateDate) updatesData.getData();
            if (updateDate == null) {
                return new LinkedList<>();
            }
            updateDate.setTime(updatesData.getHeaders().get(LAST_MODIFIED_HEADER));
            return loadData(updateDate);
        } else {
            Timber.e("Update loading failed. Status code: %d", statusCode);
            return null;
        }
    }

    private List<Integer> loadData(UpdateDate updateDate) {

        List<Integer> updateIds = updateDate.getIdsForUpdate();
        if (updateIds == null || updateIds.isEmpty()) {
            return new LinkedList<>();
        }
        ILAPIDBFacade facade = Model.getInstance().getFacade();
        try {
            facade.open();
            facade.beginTransactions();
            boolean success = true;
            for (Integer i : updateIds) {
                success = sendRequestById(i);
                if (!success) {
                    break;
                }
            }
            if (success) {
                facade.setTransactionSuccesfull();
                if (!TextUtils.isEmpty(updateDate.getTime())) {
                    preferencesManager.saveLastUpdateDate(updateDate.getTime());
                }
            }
            return success ? updateIds : null;
        } finally {
            facade.endTransactions();
            facade.close();
        }

    }

    private boolean sendRequestById(int id) {

        SynchronousItemManager manager;
        switch (id) {
            case SETTINGS_REQUEST_ID:
                manager = Model.getInstance().getSettingsManager();
                break;

            case TYPES_REQUEST_ID:
                manager = Model.getInstance().getTypesManager();
                break;

            case LEVELS_REQUEST_ID:
                manager = Model.getInstance().getLevelsManager();
                break;

            case TRACKS_REQUEST_ID:
                manager = Model.getInstance().getTracksManager();
                break;

            case SPEAKERS_REQUEST_ID:
                manager = Model.getInstance().getSpeakerManager();
                break;

            case LOCATIONS_REQUEST_ID:
                manager = Model.getInstance().getLocationManager();
                break;

            case PROGRAMS_REQUEST_ID:
                manager = Model.getInstance().getProgramManager();
                break;

            case BOFS_REQUEST_ID:
                manager = Model.getInstance().getBofsManager();
                break;

            case SOCIALS_REQUEST_ID:
                manager = Model.getInstance().getSocialManager();
                break;

            case POIS_REQUEST_ID:
                manager = Model.getInstance().getPoisManager();
                break;

            case INFO_REQUEST_ID:
                manager = Model.getInstance().getInfoManager();
                break;

            case FLOOR_PLANS_REQUEST_ID:
                manager = Model.getInstance().getFloorPlansManager();
                break;

            default:
                return true;
        }

        if (manager != null) {
            return manager.fetchData();
        }

        return false;
    }

    public interface DataUpdatedListener {

        void onDataUpdated(List<Integer> requestIds);
    }

    public void checkForDatabaseUpdate() {
        ILAPIDBFacade facade = Model.getInstance().getFacade();
        facade.open();
        facade.close();
    }
}
