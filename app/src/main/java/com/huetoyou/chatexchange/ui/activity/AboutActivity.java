package com.huetoyou.chatexchange.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.ui.misc.Utils;
import com.huetoyou.chatexchange.ui.misc.hue.ActionBarHue;
import com.huetoyou.chatexchange.ui.misc.hue.ThemeHue;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class AboutActivity extends AppCompatActivity {

    private SlidingMenu mSlidingMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ThemeHue themeHue = new ThemeHue();
        ActionBarHue actionBarHue = new ActionBarHue();
        themeHue.setTheme(AboutActivity.this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        actionBarHue.setActionBarColorToSharedPrefsValue(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return true;
    }
}
