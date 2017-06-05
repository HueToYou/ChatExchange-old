package com.huetoyou.chatexchange.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.TutorialActivity;
import com.huetoyou.chatexchange.ui.misc.HueUtils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class AboutActivity extends AppCompatActivity {

    private SlidingMenu mSlidingMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        HueUtils hueUtils = new HueUtils();
        hueUtils.setActionBarColorDefault(this);

        Button button = (Button) findViewById(R.id.buttonIntroTest);

        Intent intent = new Intent(this, TutorialActivity.class);
        startActivity(intent);
    }
}
