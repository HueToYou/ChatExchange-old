package com.huetoyou.chatexchange.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;

import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.ui.misc.CustomWebView;

import org.jsoup.Jsoup;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebViewActivity extends AppCompatActivity
{

    private String mURL;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_star_webview);
        Intent intent = getIntent();

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (intent.getAction().equals(Intent.ACTION_VIEW))
        {
            mURL = intent.getStringExtra("url");
        }

        FrameLayout parent = findViewById(R.id.webview_parent);
        parent.setPadding(0, 0, 0, 0);

        final WebView webView = findViewById(R.id.stars_view);

        CustomWebView customWebView = new CustomWebView(this, parent, webView, true);
        customWebView.loadUrl(mURL);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
