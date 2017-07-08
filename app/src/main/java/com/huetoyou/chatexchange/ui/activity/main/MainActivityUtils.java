package com.huetoyou.chatexchange.ui.activity.main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.TypedValue;

import com.huetoyou.chatexchange.ui.activity.main.MainActivity;
import com.huetoyou.chatexchange.ui.misc.CustomWebView;
import com.huetoyou.chatexchange.ui.misc.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.net.URL;

class MainActivityUtils
{
    static class AddList extends AsyncTask<String, Void, Void>
    {
        private final String mHtmlData;
        private final String mChatId;
        private final String mChatUrl;
        private final MainActivity.AddListListener mAddListListener;
        private final SharedPreferences mSharedPreferences;
        private Activity mainActivity;
        private String mName;
        private Drawable mIcon;
        private Integer mColor;

        static AddList newInstance(Activity mainActivity, SharedPreferences sharedPreferences, String data, String id, String url, MainActivity.AddListListener addListListener)
        {
            return new AddList(mainActivity, sharedPreferences, data, id, url, addListListener);
        }

        AddList(Activity mainActivityHue, SharedPreferences sharedPreferences, String data, String id, String url, MainActivity.AddListListener addListListener)
        {
            mSharedPreferences = sharedPreferences;
            mHtmlData = data;
            mChatId = id;
            mChatUrl = url;
            mAddListListener = addListListener;
            mainActivity = mainActivityHue;
        }

        @Override
        protected Void doInBackground(String... strings)
        {
            mAddListListener.onStart();
            mName = getName(mHtmlData, mChatUrl);
            mIcon = getIcon(mHtmlData, mChatUrl);
            mColor = Utils.getColorInt(mainActivity, mChatUrl);

            publishProgress();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            mAddListListener.onFinish(mName, mChatUrl, mIcon, mColor);
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
            mAddListListener.onProgress(mName, mIcon, mColor);
            super.onProgressUpdate(values);
        }

        @Nullable
        private String getName(String html, String url)
        {
            try
            {
                Elements spans = Jsoup.parse(html).select("span");

                for (Element e : spans)
                {
                    if (e.hasAttr("id") && e.attr("id").equals("roomname"))
                    {
                        mSharedPreferences.edit().putString(url + "Name", e.ownText()).apply();
                        return e.ownText();
                    }
                }
                String ret = Jsoup.connect(url).get().title().replace("<title>", "").replace("</title>", "").replace(" | chat.stackexchange.com", "").replace(" | chat.stackoverflow.com", "");
                mSharedPreferences.edit().putString(url + "Name", ret).apply();
                return ret;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }

        @Nullable
        private Drawable getIcon(String html, String chatUrl)
        {
            try
            {
                Document document = Jsoup.parse(html);
                Element head = document.head();
                Element link = head.select("link").first();

                String fav = link.attr("href");
                if (!fav.contains("http"))
                {
                    fav = "https:".concat(fav);
                }
                URL url = new URL(fav);

                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                String FILENAME = "FAVICON_" + chatUrl.replace("/", "");
                FileOutputStream fos = mainActivity.openFileOutput(FILENAME, Context.MODE_PRIVATE);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();

                Resources r = mainActivity.getResources();
                int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());

                return new BitmapDrawable(Resources.getSystem(), Bitmap.createScaledBitmap(bmp, px, px, true));

            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }
    }

    static class NotificationHandler extends AsyncTask<Void, Void, Void>
    {
        final MainActivity.NHInterface mInterface;
        final String mKey;

        static NotificationHandler newInstance(MainActivity.NHInterface nhInterface, String key)
        {
            return new NotificationHandler(nhInterface, key);
        }

        NotificationHandler(MainActivity.NHInterface nhInterface, String key)
        {
            mInterface = nhInterface;
            mKey = key;
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            if (mKey.contains("overflow"))
            {
                while (true)
                {
                    if (!mInterface.soContainsId())
                    {
                        continue;
                    }
                    break;
                }
            }
            else if (mKey.contains("exchange"))
            {
                while (true)
                {
                    if (!mInterface.seContainsId())
                    {
                        continue;
                    }
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            mInterface.onFinish();
            super.onPostExecute(aVoid);
        }
    }

    /**
     * BroadcastReceiver listening for click on chat URL from WebViewActivity
     *
     * @see CustomWebView#client()
     */
    static void setupACBR(final MainActivity mainActivity)
    {
        mainActivity.mAddChatReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                final Bundle extras = intent.getExtras();
                if (extras != null)
                {
                    if (extras.containsKey("idSE"))
                    {
                        mainActivity.addIdToSEList(extras.getString("idSE"));
                        FragStuff.doFragmentStuff(mainActivity);

                        ReceiveACB.newInstance(new ACBInterface()
                        {
                            @Override
                            public boolean urlFound()
                            {
                                return mainActivity.mSEChatUrls.get(Integer.decode(extras.getString("idSE"))) != null;
                            }

                            @Override
                            public boolean fragmentFound()
                            {
                                return mainActivity.mFragmentManager.findFragmentByTag(mainActivity.mSEChatUrls.get(Integer.decode(extras.getString("idSE")))) != null;
                            }

                            @Override
                            public void onFinish()
                            {
                                try
                                {
                                    mainActivity.setFragmentByChatId(extras.getString("idSE"), "exchange");
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                                if (mainActivity.mCurrentUsers_SlidingMenu.isMenuShowing())
                                {
                                    mainActivity.mCurrentUsers_SlidingMenu.toggle();
                                }
                            }
                        }, "idSE").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    }
                    else if (extras.containsKey("idSO"))
                    {
                        mainActivity.addIdToSOList(extras.getString("idSO"));
                        FragStuff.doFragmentStuff(mainActivity);

                        ReceiveACB.newInstance(new ACBInterface()
                        {
                            @Override
                            public boolean urlFound()
                            {
                                return mainActivity.mSOChatUrls.get(Integer.decode(extras.getString("idSO"))) != null;
                            }

                            @Override
                            public boolean fragmentFound()
                            {
                                return mainActivity.mFragmentManager.findFragmentByTag(mainActivity.mSOChatUrls.get(Integer.decode(extras.getString("idSO")))) != null;
                            }

                            @Override
                            public void onFinish()
                            {
                                try
                                {
                                    mainActivity.setFragmentByChatId(extras.getString("idSO"), "overflow");
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                                if (mainActivity.mCurrentUsers_SlidingMenu.isMenuShowing())
                                {
                                    mainActivity.mCurrentUsers_SlidingMenu.toggle();
                                }
                            }
                        }, "idSO").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("idAdd");

        LocalBroadcastManager.getInstance(mainActivity).registerReceiver(mainActivity.mAddChatReceiver, intentFilter);
    }

    static private class ReceiveACB extends AsyncTask<Void, Void, Void>
    {
        final ACBInterface mInterface;
        final String mKey;

        static ReceiveACB newInstance(ACBInterface acbInterface, String key)
        {
            return new ReceiveACB(acbInterface, key);
        }

        ReceiveACB(ACBInterface acbInterface, String key)
        {
            mInterface = acbInterface;
            mKey = key;
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            while (true)
            {
                if (!mInterface.urlFound())
                {
                    continue;
                }
                if (!mInterface.fragmentFound())
                {
                    continue;
                }
                break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            mInterface.onFinish();
            super.onPostExecute(aVoid);
        }
    }

    private interface ACBInterface
    {
        boolean urlFound();

        boolean fragmentFound();

        void onFinish();
    }
}
