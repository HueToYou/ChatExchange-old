package com.huetoyou.chatexchange.backend.database;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;

import com.huetoyou.chatexchange.ui.activity.main.MainActivity;
import com.huetoyou.chatexchange.ui.misc.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class HtmlDataScraper extends AsyncTask<String, Void, Void>
{
    private final String mHtmlData;
    private final String mChatId;
    private final String mChatUrl;
    private final HtmlDataScraper mAddListListener;
    private final SharedPreferences mSharedPreferences;
    //private Activity mainActivity;
    private String mName;
    private Drawable mIcon;
    private Integer mColor;

    HtmlDataScraper newInstance(Activity mainActivity, SharedPreferences sharedPreferences, String data, String id, String url, HtmlDataScraper addListListener)
    {
        return new HtmlDataScraper(mainActivity, sharedPreferences, data, id, url, addListListener);
    }

    HtmlDataScraper(Activity mainActivityHue, SharedPreferences sharedPreferences, String data, String id, String url, HtmlDataScraper addListListener)
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

    public static int getColorInt(Activity activity, String url)
    {

        if (mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        try
        {
            Document doc = Jsoup.connect(url).get();

            Elements styles = doc.select("link");
            Element element = new Element("hue");

            for (int i = 0; i < styles.size(); i++)
            {
                Element current = styles.get(i);

                if (current.hasAttr("href") && current.attr("rel").equals("stylesheet"))
                {
                    element = current;
                    break;
                }
            }

            String link = "";
            if (element.hasAttr("href"))
            {
                link = element.attr("href");
                if (!(link.contains("http://") || link.contains("https://")))
                {
                    link = "https:".concat(link);
                }
            }


//                Log.e("UR", link);
            URL url1 = new URL(link);

            InputStream inStr = url1.openStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inStr));
            String line;
            String css = "";

            while ((line = bufferedReader.readLine()) != null)
            {
                css = css.concat(line);
            }


            Pattern p = Pattern.compile("\\.msparea\\{(.+?)\\}");
            Matcher m = p.matcher(css);
            String a = "";

            if (m.find())
            {
                a = m.group();
            }

            p = Pattern.compile("color:(.*?);");
            m = p.matcher(a);

            String colorHex = "#000000";

            if (m.find())
            {
                colorHex = m.group().replace("color", "").replace(":", "").replace(";", "").replaceAll(" ", "");
            }

            try
            {
                mSharedPreferences.edit().putInt(url + "Color", Color.parseColor(colorHex)).apply();
                return Color.parseColor(colorHex);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.e("UNKNOWN COLOR", url);
                String threeChar = colorHex.replace("#", "");
                String one = threeChar.substring(0, 1);
                String two = threeChar.substring(1, 2);
                String three = threeChar.substring(2);

                colorHex = "#".concat(one).concat(one).concat(two).concat(two).concat(three).concat(three);

                mSharedPreferences.edit().putInt(url + "Color", Color.parseColor(colorHex)).apply();
                return Color.parseColor(colorHex);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return Color.parseColor("#000000");
        }
    }
}