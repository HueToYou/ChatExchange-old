package com.huetoyou.chatexchange.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.huetoyou.chatexchange.net.RequestFactory;
import com.huetoyou.chatexchange.ui.activity.main.MainActivity;
import com.huetoyou.chatexchange.ui.misc.Utils;

import java.net.URL;

public class SplashActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        new RequestFactory().get("http://google.com", true, new RequestFactory.Listener()
        {
            @Override
            public void onSucceeded(URL url, String data)
            {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.putExtras(getIntent());
                startActivity(intent);

                finish();
            }

            @Override
            public void onFailed(String message)
            {
                Intent intent = new Intent(SplashActivity.this, OfflineActivity.class);
                startActivity(intent);

                finish();
            }
        });
    }
}