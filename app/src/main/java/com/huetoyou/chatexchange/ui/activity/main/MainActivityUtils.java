package com.huetoyou.chatexchange.ui.activity.main;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.TypedValue;

import com.huetoyou.chatexchange.ui.activity.main.MainActivity;
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
}
