package com.huetoyou.chatexchange.ui.misc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.huetoyou.chatexchange.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Zacha on 7/4/2017.
 */

public class CustomWebView
{
    private Activity mContext;
    private WebView mWebView;
    private boolean mOverrideTitle;
    private final Button mOpenInWV;
    private final Button mBack;
    private final Button mForward;

    public CustomWebView(Activity context, View view, WebView webView, boolean shouldOverrideTitle) {
        mContext = context;
        mWebView = webView;
        mOverrideTitle = shouldOverrideTitle;

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT x.y; rv:10.0) Gecko/20100101 Firefox/10.0");
        client();

        mOpenInWV = view.findViewById(R.id.open_in_webview);
        mBack = view.findViewById(R.id.go_back);
        mForward = view.findViewById(R.id.go_forward);

        mOpenInWV.setVisibility(View.GONE);

        mBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mWebView.canGoBack())
                {
                    mWebView.goBack();
                }
            }
        });

        mForward.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mWebView.canGoForward())
                {
                    mWebView.goForward();
                }
            }
        });
    }

    public void loadUrl(String url) {
        if (mOverrideTitle) mContext.setTitle(url);
        mWebView.loadUrl(url);
    }


    /**
     * Set client for specified WebView (so we can intercept URL presses, and they open in the WebView itself by default)
     */

    private void client() {
        mWebView.setWebViewClient(new WebViewClient()
        {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                if (url.contains("/rooms/"))
                {
                    String id = "";
                    Pattern p = Pattern.compile("rooms/(.+?)\\b");
                    Matcher m = p.matcher(url);

                    while (!m.hitEnd())
                    {
                        if (m.find())
                        {
                            id = m.group().replace("rooms/", "");
                        }
                    }

                    if (!id.isEmpty())
                    {
                        String key = "id";
                        if (url.contains("exchange"))
                        {
                            key = key.concat("SE");
                        }
                        else if (url.contains("overflow"))
                        {
                            key = key.concat("SO");
                        }

                        Intent urlIntent = new Intent("idAdd").putExtra(key, id);
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(urlIntent);
                        mContext.finish();
                    }
                }
                else
                {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                if (mOverrideTitle) mContext.setTitle(view.getTitle());
                super.onPageFinished(view, url);
            }
        });
    }
}
