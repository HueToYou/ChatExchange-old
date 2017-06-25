package com.huetoyou.chatexchange.backend;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.huetoyou.chatexchange.net.RequestFactory;

/**
 * Service that runs in the background and creates an appropriate backend according to user settings
 */
public class BackendService extends Service {

    private static final String TAG = "BackendService";

    private static final int INITIAL_ROOM_ID = 1;  // start off in the sandbox

    /**
     * List of valid backends (currently only one)
     */
    public static final String BACKEND_NONE = "none";
    public static final String BACKEND_WEBSOCKET = "websocket";

    /**
     * Actions for starting and stopping the service
     */
    private static final String ACTION_START = "com.huetoyou.chatexchange.ACTION_START";
    private static final String ACTION_STOP = "com.huetoyou.chatexchange.ACTION_STOP";
    private static final String EXTRA_TOKEN = "com.huetoyou.chatexchange.EXTRA_TOKEN";

    /**
     * Broadcast for new events being received
     */
    private static final String EVENT_RECEIVED = "com.huetoyou.chatexchange.EVENT_RECEIVED";
    private static final String EXTRA_EVENT = "com.huetoyou.chatexchange.EXTRA_EVENT";

    /**
     * Broadcaster for chat events
     */
    class Broadcaster {

        /**
         * Broadcast the specified event
         */
        void broadcastEvent(Event event) {
            Intent intent = new Intent();
            intent.setAction(EVENT_RECEIVED);
            intent.putExtra(EXTRA_EVENT, event);
            BackendService.this.sendBroadcast(intent);
        }
    }

    /**
     * Start the service
     * @param context context to use for sending the intent
     * @param token account token to use for initialization
     */
    public static void startService(Context context, String token) {
        Log.i(TAG, "starting service");
        Intent intent = new Intent(context, BackendService.class);
        intent.setAction(ACTION_START);
        intent.putExtra(EXTRA_TOKEN, token);
        context.startService(intent);
    }

    private SharedPreferences mSharedPreferences;

    private RequestFactory mRequestFactory;
    private String mBackend = BACKEND_NONE;
    private Broadcaster mBroadcaster = new Broadcaster();

    private WebSocketBackend mWebSocketBackend;

    @Override
    public void onCreate() {
        super.onCreate();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    /**
     * Stop the currently running backend
     */
    @SuppressWarnings("SameReturnValue")
    private int stop() {
        switch (mBackend) {
            case BACKEND_WEBSOCKET:
                mWebSocketBackend.close();
        }
        mBackend = BACKEND_NONE;
        stopSelf();
        return START_NOT_STICKY;
    }

    /**
     * Start the WebSocket backend
     */
    private void startWebSocketBackend() {
        mWebSocketBackend = new WebSocketBackend(mRequestFactory, INITIAL_ROOM_ID, mBroadcaster);
    }

    /**
     * Start the service with the current backend
     * @param token account token
     *
     * If an existing backend is running, it is stopped.
     */
    @SuppressWarnings("SameReturnValue")
    private int start(String token) {
        stop();
        mRequestFactory = new RequestFactory(token);
        mBackend = mSharedPreferences.getString("backend", BACKEND_WEBSOCKET);
        switch (mBackend) {
            case BACKEND_WEBSOCKET:
                startWebSocketBackend();
        }
        // TODO: enter foreground
        return START_STICKY;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, String.format("received intent: %s", intent.getAction()));

        switch (intent.getAction()) {
            case ACTION_START:
                return start(intent.getStringExtra(EXTRA_TOKEN));
            case ACTION_STOP:
                return stop();
        }
        return START_NOT_STICKY;
    }

    /**
     * Retrieve data from a page
     * @param pageUrl URL of the page to retrieve
     * @param listener listener for page retrieval status
     */
    public void retrievePage(String pageUrl, PageRetriever.Listener listener) {
        new PageRetriever(mRequestFactory, pageUrl, listener);
    }

    /**
     * Binder for interacting with the service
     */
    public class BackendBinder extends Binder {
        BackendService getService() {
            return BackendService.this;
        }
    }

    private final IBinder mBinder = new BackendBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
