package com.huetoyou.chatexchange.backend;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Service that runs in the background and creates an appropriate backend according to user settings
 */
public class BackendService extends Service {

    private static final String TAG = "BackendService";

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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
