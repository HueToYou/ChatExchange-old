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
    }

    public void checkOnlineStatus(View v)
    {
        if(Utils.areWeOnANetwork(this) && Utils.areWeOnline())
        {
            Toast.makeText(this, "Connection established, reloading app :)", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            Toast.makeText(this, "You're still offline :\\", Toast.LENGTH_SHORT).show();
        }
    }
}
