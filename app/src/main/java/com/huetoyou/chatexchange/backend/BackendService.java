package com.huetoyou.chatexchange.backend;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    public static final String ACTION_START = "com.huetoyou.chatexchange.ACTION_START";
    public static final String ACTION_STOP = "com.huetoyou.chatexchange.ACTION_STOP";

    /**
     * Broadcast for new events being received
     */
    public static final String EVENT_RECEIVED = "com.huetoyou.chatexchange.EVENT_RECEIVED";
    public static final String EXTRA_EVENT = "com.huetoyou.chatexchange.EVENT";

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
     * Start or stop the service
     * @param context context to use for sending the intent
     * @param start true to start the service; false to stop it
     */
    public static void startStopService(Context context, boolean start) {
        Intent intent = new Intent(context, BackendService.class);
        if (start) {
            Log.i(TAG, "starting service");
            intent.setAction(ACTION_START);
        } else {
            Log.i(TAG, "stopping service");
            intent.setAction(ACTION_STOP);
        }
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

        // TODO: need the "auth" token to make this work
        // TODO: decide how this works with multiple accounts
        mRequestFactory = new RequestFactory();
    }

    /**
     * Stop the currently running backend
     */
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
     *
     * If an existing backend is running, it is stopped.
     */
    private int start() {
        stop();
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
                return start();
            case ACTION_STOP:
                return stop();
        }
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
