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
import android.widget.Toast;

import com.huetoyou.chatexchange.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebViewActivity extends AppCompatActivity {

    private String mURL;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FrameLayout mParentView = new FrameLayout(this);
//        mWebView = new WebView(this);

//        mParentView.addView(mWebView);
        setContentView(R.layout.fragment_star_webview);
        Intent intent = getIntent();

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (intent.getAction().equals(Intent.ACTION_VIEW)) {
            mURL = intent.getStringExtra("url");
        }

        FrameLayout parent = findViewById(R.id.webview_parent);
        parent.setPadding(0, 0, 0, 0);

        final WebView webView = findViewById(R.id.stars_view);
        Button openInWV = findViewById(R.id.open_in_webview);
        Button back = findViewById(R.id.go_back);
        Button forward = findViewById(R.id.go_forward);

        webView.loadUrl(mURL);
//                webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
//                webView.setInitialScale();
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setJavaScriptEnabled(true);
        client(webView);

        openInWV.setVisibility(View.GONE);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoBack()) webView.goBack();
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoForward()) webView.goForward();
            }
        });
    }

    /**
     * Set client for specified WebView (so we can intercept URL presses, and they open in the WebView itself by default)
     * @param webView the WebView to have its client set
     */

    private void client(WebView webView) {
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                if (url.contains("/rooms/")) {
                    String id = "";
                    Pattern p = Pattern.compile("rooms/(.+?)\\b");
                    Matcher m = p.matcher(url);

                    while (!m.hitEnd()) {
                        if (m.find()) id = m.group().replace("rooms/", "");
                    }

                    if (!id.isEmpty()) {
                        String key = "id";
                        if (url.contains("exchange")) key = key.concat("SE");
                        else if (url.contains("overflow")) key = key.concat("SO");

                        Intent urlIntent = new Intent("idAdd").putExtra(key, id);
                        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(urlIntent);
                        finish();
                    }
                }
                else view.loadUrl(url);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
