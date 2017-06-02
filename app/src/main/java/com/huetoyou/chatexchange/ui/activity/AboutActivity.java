package com.huetoyou.chatexchange.ui.activity;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.huetoyou.chatexchange.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setActionBarColor();

        // configure the SlidingMenu
        SlidingMenu menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.RIGHT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(new ColorDrawable(getResources().getColor(R.color.transparentGrey)));
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.users_slideout);
    }

    private void setActionBarColor()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int initialColor = prefs.getInt("default_color", 0xFF000000);
        System.out.println(initialColor);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        ColorDrawable cd = new ColorDrawable(initialColor);
        bar.setBackgroundDrawable(cd);
    }
}
