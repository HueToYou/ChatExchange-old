package com.huetoyou.chatexchange.ui.activity;

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

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.Util;
import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.ui.misc.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.net.URL;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class MainActivityUtils
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
            mAddListListener.onFinish();
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

    static void showChatSliderTutorial(Activity activity, SlidingMenu mChatroomSlidingMenu)
    {
        final FloatingActionMenu chatFam = activity.findViewById(R.id.chat_slide_menu);
        final FloatingActionButton home = activity.findViewById(R.id.home_fab);
        final FloatingActionButton add = activity.findViewById(R.id.add_chat_fab);
        final FloatingActionButton removeAll = activity.findViewById(R.id.remove_all_chats_fab);

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(activity, "ChatSliderTutorial");
        sequence.setConfig(config);

        sequence.setOnItemDismissedListener(new MaterialShowcaseSequence.OnSequenceItemDismissedListener()
        {
            int position = 0;

            @Override
            public void onDismiss(MaterialShowcaseView materialShowcaseView, int i)
            {
                switch (position)
                {
                    case 1:
                        chatFam.open(true);
                        break;
                    case 4:
                        chatFam.close(true);
                        break;
                }

                position++;
            }
        });

        ShowcaseConfig config1 = new ShowcaseConfig();
//        config1.setShape(new RectangleShape(mChatroomSlidingMenu.getWidth(), mChatroomSlidingMenu.getHeight()));
        config1.setShapePadding(Util.dpToPx(activity, 300));

        sequence.setConfig(config1);

        sequence.addSequenceItem(mChatroomSlidingMenu.findViewById(R.id.chatrooms_list_title),
                "Chatrooms",
                "OK");

        sequence.setConfig(config);

        sequence.addSequenceItem(chatFam.getMenuButton(),
                "Menu",
                "OK");

        sequence.addSequenceItem(home,
                "Home",
                "OK");

        sequence.addSequenceItem(add,
                "Add Chat",
                "OK");

        sequence.addSequenceItem(removeAll,
                "Remove All Chats",
                "OK");

        sequence.start();
    }
}
