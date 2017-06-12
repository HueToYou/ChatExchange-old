package com.huetoyou.chatexchange.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.ui.misc.HueUtils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import hello.Hello;

public class AboutActivity extends AppCompatActivity {

    private SlidingMenu mSlidingMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        HueUtils hueUtils = new HueUtils();
        hueUtils.setActionBarColorDefault(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Intent intent = new Intent(this, GoBindActivity.class);
        //startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return true;
    }
}
