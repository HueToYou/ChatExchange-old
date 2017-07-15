package com.huetoyou.chatexchange.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.huetoyou.chatexchange.ui.activity.main.MainActivity;
import com.huetoyou.chatexchange.ui.misc.Utils;

public class SplashActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(Utils.areWeOnANetwork(this) && Utils.areWeOnline())
        {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtras(getIntent());
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(this, OfflineActivity.class);
            startActivity(intent);
        }
        finish();
    }
}