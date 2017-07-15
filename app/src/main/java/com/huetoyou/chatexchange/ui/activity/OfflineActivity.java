package com.huetoyou.chatexchange.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.ui.activity.main.MainActivity;
import com.huetoyou.chatexchange.ui.misc.Utils;
import com.huetoyou.chatexchange.ui.misc.hue.ThemeHue;

public class OfflineActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ThemeHue.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_offline);
        startAsyncPingThread();
    }

    public void checkOnlineStatus(View v)
    {
        if(Utils.areWeOnANetwork(this) && Utils.areWeOnline())
        {
            reloadApp();
        }
        else
        {
            Toast.makeText(this, "You're still offline :\\", Toast.LENGTH_SHORT).show();
        }
    }

    private void reloadApp()
    {
        Toast.makeText(OfflineActivity.this, "Connection established, reloading app :)", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
        finish();
    }

    private void startAsyncPingThread()
    {
        try
        {
            Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    while (true)
                    {
                        if(Utils.areWeOnline())
                        {
                            OfflineActivity.this.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    reloadApp();
                                }
                            });
                            break;
                        }
                        else
                        {
                            try
                            {
                                Thread.sleep(250);
                            }
                            catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });

            thread.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
