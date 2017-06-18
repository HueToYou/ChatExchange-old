package com.huetoyou.chatexchange.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.ui.misc.HueUtils;

public class HelpActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        HueUtils hueUtils = new HueUtils();
        hueUtils.setTheme(HelpActivity.this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);


        hueUtils.setActionBarColorToSharedPrefsValue(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void showTutorial(View v)
    {
        Intent intent = new Intent(this, TutorialActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return true;
    }
}
