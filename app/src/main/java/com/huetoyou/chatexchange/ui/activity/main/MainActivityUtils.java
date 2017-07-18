package com.huetoyou.chatexchange.ui.activity.main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.ui.activity.main.MainActivity;
import com.huetoyou.chatexchange.ui.misc.CustomWebView;
import com.huetoyou.chatexchange.ui.misc.RecyclerAdapter;
import com.huetoyou.chatexchange.ui.misc.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MainActivityUtils
{
    private MainActivity mainActivity;

    MainActivityUtils(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
    }

    class AddList extends AsyncTask<String, Void, Void>
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

        AddList newInstance(Activity mainActivity, SharedPreferences sharedPreferences, String data, String id, String url, MainActivity.AddListListener addListListener)
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

    class NotificationHandler extends AsyncTask<Void, Void, Void>
    {
        final MainActivity.NHInterface mInterface;
        final String mKey;

        NotificationHandler newInstance(MainActivity.NHInterface nhInterface, String key)
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
    void setupACBR()
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
                        mainActivity.fragStuff.doFragmentStuff(mainActivity);

                        new ReceiveACB(new ACBInterface()
                        {
                            @Override
                            public boolean urlFound()
                            {
                                return mainActivity.chatDataBundle.mSEChatUrls.get(Integer.decode(extras.getString("idSE"))) != null;
                            }

                            @Override
                            public boolean fragmentFound()
                            {
                                return mainActivity.mFragmentManager.findFragmentByTag(mainActivity.chatDataBundle.mSEChatUrls.get(Integer.decode(extras.getString("idSE")))) != null;
                            }

                            @Override
                            public void onFinish()
                            {
                                try
                                {
                                    mainActivity.fragStuff.setFragmentByChatId(mainActivity, extras.getString("idSE"), "exchange");
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                                if (MainActivity.mCurrentUsers_SlidingMenu.isMenuShowing())
                                {
                                    MainActivity.mCurrentUsers_SlidingMenu.toggle();
                                }
                            }
                        }, "idSE").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    }
                    else if (extras.containsKey("idSO"))
                    {
                        mainActivity.addIdToSOList(extras.getString("idSO"));
                        mainActivity.fragStuff.doFragmentStuff(mainActivity);

                        new ReceiveACB(new ACBInterface()
                        {
                            @Override
                            public boolean urlFound()
                            {
                                return mainActivity.chatDataBundle.mSOChatUrls.get(Integer.decode(extras.getString("idSO"))) != null;
                            }

                            @Override
                            public boolean fragmentFound()
                            {
                                return mainActivity.mFragmentManager.findFragmentByTag(mainActivity.chatDataBundle.mSOChatUrls.get(Integer.decode(extras.getString("idSO")))) != null;
                            }

                            @Override
                            public void onFinish()
                            {
                                try
                                {
                                    mainActivity.fragStuff.setFragmentByChatId(mainActivity, extras.getString("idSO"), "overflow");
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                                if (MainActivity.mCurrentUsers_SlidingMenu.isMenuShowing())
                                {
                                    MainActivity.mCurrentUsers_SlidingMenu.toggle();
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

    private class ReceiveACB extends AsyncTask<Void, Void, Void>
    {
        final ACBInterface mInterface;
        final String mKey;

        ReceiveACB newInstance(ACBInterface acbInterface, String key)
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

    /**
     * If Firebase notification comes with data, and that data is room info, open the room if added
     */

    void respondToNotificationClick()
    {
        if (mainActivity.getIntent().getExtras() != null)
        {
            Log.e("NOTIF", "NOTIF");
            final String chatId = mainActivity.mIntent.getExtras().getString("chatId");
            final String chatDomain = mainActivity.mIntent.getExtras().getString("chatDomain");

            if (chatId != null && chatDomain != null)
            {
                new NotificationHandler(new MainActivity.NHInterface()
                {
                    @Override
                    public boolean seContainsId()
                    {
                        return mainActivity.chatDataBundle.mSEChatUrls.get(Integer.decode(chatId)) != null;
                    }

                    @Override
                    public boolean soContainsId()
                    {
                        return mainActivity.chatDataBundle.mSOChatUrls.get(Integer.decode(chatId)) != null;
                    }

                    @Override
                    public void onFinish()
                    {
                        mainActivity.fragStuff.setFragmentByChatId(mainActivity, chatId, chatDomain);
                    }
                }, chatDomain).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }


    /**
     * Handle removing a chat
     *
     * @param position the position of the item in the chat list
     */

    void confirmClose(final int position)
    {

        Vibrator vib = (Vibrator) mainActivity.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 50 milliseconds
        vib.vibrate(50);

        String domain = "";
        String id = "";

        Pattern domP = Pattern.compile("//(.+?)\\.com");
        Matcher domM = domP.matcher(mainActivity.mWrappedAdapter.getItemAt(position).getUrl());

        while (!domM.hitEnd())
        {
            if (domM.find())
            {
                domain = domM.group();
            }
        }

        Pattern idP = Pattern.compile("rooms/(.+?)\\b");
        Matcher idM = idP.matcher(mainActivity.mWrappedAdapter.getItemAt(position).getUrl());

        while (!idM.hitEnd())
        {
            if (idM.find())
            {
                id = idM.group().replace("rooms/", "");
            }
        }

        Log.e("IDDDDD", id);
        Log.e("DOMAIN", domain);

        String soId = "";
        String seId = "";

        if (!domain.isEmpty() && !id.isEmpty())
        {
            if (domain.contains("overflow"))
            {
                mainActivity.removeIdFromSOList(id);
                soId = id;
            }
            else if (domain.contains("exchange"))
            {
                mainActivity.removeIdFromSEList(id);
                seId = id;
            }

            if (mainActivity.mWrappedAdapter.getItemAt(position).getUrl().equals(mainActivity.mCurrentFragment)) mainActivity.fragStuff.setFragmentByTag(mainActivity, "home");
//            mWrappedAdapter.getSwipeManager().performFakeSwipe(mWrappedAdapter.getViewHolderAt(position), 1);

            final String soId1 = soId;
            final String seId1 = seId;

            mainActivity.mWrappedAdapter.removeItemWithSnackbar(mainActivity, position, new RecyclerAdapter.SnackbarListener()
            {
                @Override
                public void onUndo()
                {
                    if (!soId1.isEmpty())
                    {
                        mainActivity.addIdToSOList(soId1);
                    } else if (!seId1.isEmpty())
                    {
                        mainActivity.addIdToSEList(seId1);
                    }
                }

                @Override
                public void onUndoExpire(String url)
                {
                    Log.e("UNDO", "Undo Expired");
                    mainActivity.mFragmentManager.getFragments().remove(mainActivity.mFragmentManager.findFragmentByTag(url));
                }
            });
        }
    }

    /**
     * Handle adding chats
     */

    void showAddTabDialog()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle(mainActivity.getResources().getText(R.string.activity_main_add_chat));

        View view = View.inflate(mainActivity, R.layout.add_chat_dialog, null);
        final EditText input = view.findViewById(R.id.url_edittext);

        final Spinner domains = view.findViewById(R.id.domain_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mainActivity,
                R.array.domain_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        domains.setAdapter(adapter);

        domains.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                input.setHint(mainActivity.getResources().getText(R.string.activity_main_chat_url_hint));
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        builder.setView(view);
        builder.setPositiveButton(mainActivity.getResources().getText(R.string.generic_ok), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

                String inputText = input.getText().toString();
                if (!inputText.isEmpty())
                {

                    if (domains.getSelectedItem().toString().equals(mainActivity.getResources().getText(R.string.stackoverflow).toString()))
                    {
                        Log.e("IDS", "SO");
                        mainActivity.addIdToSOList(inputText);
                    }
                    else if (domains.getSelectedItem().toString().equals(mainActivity.getResources().getText(R.string.stackexchange).toString()))
                    {
                        Log.e("IDS", "SE");
                        mainActivity.addIdToSEList(inputText);
                    }

                    mainActivity.fragStuff.doFragmentStuff(mainActivity);
                }
                else
                {
                    Toast.makeText(mainActivity.getBaseContext(), "Please enter an ID", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(mainActivity.getResources().getText(R.string.generic_cancel), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        AlertDialog al = builder.create();
        al.show();
    }
}
