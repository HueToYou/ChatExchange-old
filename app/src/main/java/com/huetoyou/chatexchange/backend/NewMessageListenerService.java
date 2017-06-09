package com.huetoyou.chatexchange.backend;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import hello.Hello;

public class NewMessageListenerService extends IntentService
{
    public NewMessageListenerService()
    {
        super("NewMessageListenerService");
    }

    @Override
    protected void onHandleIntent(Intent workIntent)
    {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();

        // Do work here, based on the contents of dataString

        while(true)
        {
            final String messg = Hello.waitForMessage();

            Intent localIntent = new Intent("org.golang.example.bind.BROADCAST").putExtra("org.golang.example.bind.STATUS", messg);
            // Broadcasts the Intent to receivers in this app.
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        }
    }
}
