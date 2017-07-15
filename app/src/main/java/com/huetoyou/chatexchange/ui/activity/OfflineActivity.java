package com.huetoyou.chatexchange.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.net.RequestFactory;
import com.huetoyou.chatexchange.ui.activity.main.MainActivity;
import com.huetoyou.chatexchange.ui.misc.Utils;
import com.huetoyou.chatexchange.ui.misc.hue.ThemeHue;

import java.net.URL;

public class OfflineActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ThemeHue.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_offline);
        waitForOnline();
    }

    public void checkOnlineStatus(View v)
    {
        new RequestFactory().get("http://google.com", true, new RequestFactory.Listener()
        {
            @Override
            public void onSucceeded(URL url, String data)
            {
                reloadApp();
            }

            @Override
            public void onFailed(String message)
            {
                Toast.makeText(OfflineActivity.this, "You're still offline :\\", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void reloadApp()
    {
        Toast.makeText(OfflineActivity.this, "Connection established, reloading app :)", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
        finish();
    }

    private void waitForOnline()
    {
        new RequestFactory().get("http://google.com", true, new RequestFactory.Listener()
        {
            @Override
            public void onSucceeded(URL url, String data)
            {
                reloadApp();
            }

            @Override
            public void onFailed(String message)
            {
                waitForOnline();
            }
        });
    }
}
