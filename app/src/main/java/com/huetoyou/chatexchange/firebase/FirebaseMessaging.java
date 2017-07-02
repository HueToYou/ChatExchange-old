package com.huetoyou.chatexchange.firebase;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.huetoyou.chatexchange.R;

public class FirebaseMessaging extends FirebaseMessagingService
{
    private static final String TAG = "ChatExchange";
    private static final int NOTIF_ID = 100;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        Looper.prepare();
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0)
        {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null)
        {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

        sendNotification(remoteMessage.getNotification().getBody());
    }

    //this won't work until we use the API
    private void sendNotification(String messageBody)
    {
        @SuppressWarnings("deprecation") NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Event tracker")
                .setContentText("Events received");

        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        String[] events = new String[6];
        events[0] = messageBody;

        inboxStyle.setBigContentTitle("Event tracker details:");

        for (String s : events)
        {
            inboxStyle.addLine(s);
        }

        mBuilder.setStyle(inboxStyle);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(NOTIF_ID, mBuilder.build());
    }
}
