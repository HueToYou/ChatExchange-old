package com.huetoyou.chatexchange.ui.activity.main;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.net.RequestFactory;

import java.net.URL;

public class FragStuff
{

    /*
     * Setup fragments
     */

    /**
     * Instantiate fragments and add them to {@link MainActivity#mChatroomSlidingMenu}
     */
    static void doFragmentStuff(final MainActivity mainActivity)
    {
        mainActivity.resetArrays(false);
        mainActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mainActivity.findViewById(R.id.loading_progress).setVisibility(View.VISIBLE);
            }
        });
//        Looper.prepare();
        Log.e("IDS", mainActivity.mSEChatIDs.toString().concat(mainActivity.mSOChatIDs.toString()));

        for (String s : mainActivity.mSEChatIDs)
        {
            Log.e("ID", s);
            final String chatUrl = "https://chat.stackexchange.com/rooms/".concat(s);
            final String id = s;
            mainActivity.mRequestFactory.get(chatUrl, true, new RequestFactory.Listener()
            {
                @Override
                public void onSucceeded(final URL url, String data)
                {
                    mainActivity.mSEChatUrls.put(Integer.decode(id), chatUrl);
                    mainActivity.mAddList = MainActivityUtils.AddList.newInstance(mainActivity, mainActivity.mSharedPrefs, data, id, chatUrl, new MainActivity.AddListListener()
                    {

                        private Fragment fragment;

                        @Override
                        public void onStart()
                        {

                        }

                        @Override
                        public void onProgress(String name, Drawable icon, Integer color)
                        {
                            fragment = mainActivity.addFragment(chatUrl, name, color, Integer.decode(id));
                            Log.e("RRR", fragment.getArguments().getString("chatUrl", "").concat("HUE"));
                            mainActivity.mSEChats.put(Integer.decode(id), fragment);
                            mainActivity.mSEChatColors.put(Integer.decode(id), color);
                            mainActivity.mSEChatIcons.put(Integer.decode(id), icon);
                            mainActivity.mSEChatNames.put(Integer.decode(id), name);
                        }

                        @Override
                        public void onFinish(String name, String url, Drawable icon, Integer color)
                        {
                            mainActivity.addFragmentToList(name, url, icon, color, id);
                            mainActivity.initiateFragment(fragment);
                        }
                    });

                    mainActivity.mAddList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }

                @Override
                public void onFailed(String message)
                {
                    Toast.makeText(mainActivity, "Failed to load chat ".concat(id).concat(": ").concat(message), Toast.LENGTH_LONG).show();

                    mainActivity.removeIdFromSEList(id);
                    Log.e("Couldn't load SE chat ".concat(id), message);
                }
            });
        }

        for (String s : mainActivity.mSOChatIDs)
        {
            final String chatUrl = "https://chat.stackoverflow.com/rooms/".concat(s);
            final String id = s;
            mainActivity.mRequestFactory.get(chatUrl, true, new RequestFactory.Listener()
            {
                @Override
                public void onSucceeded(final URL url, String data)
                {
                    mainActivity.mSOChatUrls.put(Integer.decode(id), chatUrl);
                    MainActivityUtils.AddList addList = MainActivityUtils.AddList.newInstance(mainActivity, mainActivity.mSharedPrefs, data, id, chatUrl, new MainActivity.AddListListener()
                    {

                        private Fragment fragment;

                        @Override
                        public void onStart()
                        {
                        }

                        @Override
                        public void onProgress(String name, Drawable icon, Integer color)
                        {
                            fragment = mainActivity.addFragment(chatUrl, name, color, Integer.decode(id));
                            mainActivity.mSOChats.put(Integer.decode(id), fragment);
                            mainActivity.mSOChatColors.put(Integer.decode(id), color);
                            mainActivity.mSOChatIcons.put(Integer.decode(id), icon);
                            mainActivity.mSOChatNames.put(Integer.decode(id), name);
                        }

                        @Override
                        public void onFinish(String name, String url, Drawable icon, Integer color)
                        {
                            mainActivity.addFragmentToList(name, url, icon, color, id);
                            mainActivity.initiateFragment(fragment);
                        }
                    });

                    addList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }

                @Override
                public void onFailed(String message)
                {
                    Toast.makeText(mainActivity, "Failed to load chat ".concat(id), Toast.LENGTH_SHORT).show();
                    mainActivity.removeIdFromSOList(id);
                    Log.e("Couldn't load SO chat ".concat(id), message);
                }
            });
        }

        if (mainActivity.mSEChatIDs.size() == 0 && mainActivity.mSOChatIDs.size() == 0)
        {
            mainActivity.removeAllFragmentsFromList();
            mainActivity.findViewById(R.id.loading_progress).setVisibility(View.GONE);
        }

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(350);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                while (true)
                {
                    if (mainActivity.chatroomsList == null)
                    {
                        continue;
                    }
                    if (mainActivity.mWrappedAdapter.getItemCount() < mainActivity.mSEChatIDs.size() + mainActivity.mSOChatIDs.size())
                    {
                        continue;
                    }
                    break;
                }

                mainActivity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mainActivity.findViewById(R.id.loading_progress).setVisibility(View.GONE);
                        Log.e("VIS", "GONE");
                    }
                });
            }
        }).start();
    }
}
