package com.huetoyou.chatexchange.ui.misc;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;
import com.huetoyou.chatexchange.R;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomWebView
{
    private Activity mContext;
    private WebView mWebView;
    private boolean mOverrideTitle;
    private final Button mOpenInWV;
    private final Button mBack;
    private final Button mForward;
    private HueListener listener;

    public CustomWebView(Activity context, View view, WebView webView, boolean shouldOverrideTitle)
    {
        mContext = context;
        mWebView = webView;
        mOverrideTitle = shouldOverrideTitle;

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT x.y; rv:10.0) Gecko/20100101 Firefox/10.0");
        webView.getSettings().setAppCacheEnabled(true);
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

        listener = null;
    }

    public void loadUrl(final String url)
    {
        if (mOverrideTitle)
        {
            mContext.setTitle(url);
        }
        CookieManager cookieManager = CookieManager.getInstance();
        Log.e("AUTHTOKEN", cookieManager.getCookie("https://stackexchange.com"));

        Map<String, String> abc = new HashMap<>();
        abc.put("Set-Cookie", cookieManager.getCookie("https://stackexchange.com"));

        mWebView.loadUrl(url, abc);

        cookieManager.setAcceptCookie(true);
    }


    /**
     * Set client for specified WebView (so we can intercept URL presses, and they open in the WebView itself by default)
     */

    private void client()
    {
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
                            try
                            {
                                Integer.decode(m.group().replace("rooms/", ""));
                                id = m.group().replace("rooms/", "");
                            }
                            catch (Exception e)
                            {
                                view.loadUrl(url);
                                e.printStackTrace();
                            }
                        }
                    }

                    if (!id.isEmpty())
                    {
                        String key = "id";
                        if (url.contains("exchange"))
                        {
                            key = key.concat("SE");
                            Toast.makeText(mContext, "Adding SE room #" + id, Toast.LENGTH_LONG).show();
                        }
                        else if (url.contains("overflow"))
                        {
                            key = key.concat("SO");
                            Toast.makeText(mContext, "Adding SO room #" + id, Toast.LENGTH_LONG).show();
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
                if (mOverrideTitle)
                {
                    mContext.setTitle(view.getTitle());
                }
                super.onPageFinished(view, url);
                if (listener != null)
                {
                    listener.onFinishedLoading();
                }
            }
        });
    }

    public interface HueListener
    {
        public void onFinishedLoading();
    }

    public void setHueListener(HueListener listener)
    {
        this.listener = listener;
    }
}
