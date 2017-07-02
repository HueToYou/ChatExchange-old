package com.huetoyou.chatexchange.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.ui.misc.hue.ActionBarHue;
import com.huetoyou.chatexchange.ui.misc.hue.ThemeHue;

import uk.co.deanwild.materialshowcaseview.PrefsManager;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class HelpActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ThemeHue.setTheme(HelpActivity.this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        ActionBarHue.setActionBarColorToSharedPrefsValue(this);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void showTutorial(View v)
    {
        PrefsManager prefsManager = new PrefsManager(this, "HomeFragTutorial");
        prefsManager.resetShowcase();

        prefsManager = new PrefsManager(this, "ChatFragTutorial");
        prefsManager.resetShowcase();

        prefsManager = new PrefsManager(this, "ChatSliderTutorial");
        prefsManager.resetShowcase();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return true;
    }
}
