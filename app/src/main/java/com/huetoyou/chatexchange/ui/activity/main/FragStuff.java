package com.huetoyou.chatexchange.ui.activity.main;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.net.RequestFactory;
import com.huetoyou.chatexchange.ui.frags.ChatFragment;
import com.huetoyou.chatexchange.ui.frags.HomeFragment;
import com.huetoyou.chatexchange.ui.misc.ChatroomRecyclerObject;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.net.URL;

public class FragStuff
{
    private MainActivityUtils mainActivityUtils;
    FragStuff(MainActivityUtils mainActivityUtils)
    {
        this.mainActivityUtils = mainActivityUtils;
    }
    /*
     * Setup fragments
     */

    /**
     * Instantiate fragments and add them to {@link MainActivity#mChatroomSlidingMenu}
     */
    void doFragmentStuff(final MainActivity mainActivity)
    {
//        mainActivity.chatDataBundle.resetArrays(false, mainActivity.mEditor);
        mainActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mainActivity.findViewById(R.id.loading_progress).setVisibility(View.VISIBLE);
            }
        });
//        Looper.prepare();
        Log.e("IDS", mainActivity.chatDataBundle.mSEChatIDs.toString().concat(mainActivity.chatDataBundle.mSOChatIDs.toString()));

        for (String s : mainActivity.chatDataBundle.mSEChatIDs)
        {
            Log.e("ID", s);
            final String chatUrl = "https://chat.stackexchange.com/rooms/".concat(s);

            if (mainActivity.getSupportFragmentManager().findFragmentByTag(chatUrl) == null) //TODO: Make sure fragments get removed on chat removal, otherwise users won't be able to re-add chats
            {
                final String id = s;
                mainActivity.mRequestFactory.get(chatUrl, true, new RequestFactory.Listener()
                {
                    @Override
                    public void onSucceeded(final URL url, String data)
                    {
                        mainActivity.chatDataBundle.mSEChatUrls.put(Integer.decode(id), chatUrl);
                        mainActivity.mAddList = mainActivityUtils.new AddList(mainActivity, mainActivity.mSharedPrefs, data, id, chatUrl, new MainActivity.AddListListener()
                        {

                            private Fragment fragment;

                            @Override
                            public void onStart()
                            {

                            }

                            @Override
                            public void onProgress(String name, Drawable icon, Integer color)
                            {
                                fragment = addFragment(mainActivity, chatUrl, name, color, Integer.decode(id));
                                Log.e("RRR", fragment.getArguments().getString("chatUrl", "").concat("HUE"));
                                mainActivity.chatDataBundle.mSEChats.put(Integer.decode(id), fragment);
                                mainActivity.chatDataBundle.mSEChatColors.put(Integer.decode(id), color);
                                mainActivity.chatDataBundle.mSEChatIcons.put(Integer.decode(id), icon);
                                mainActivity.chatDataBundle.mSEChatNames.put(Integer.decode(id), name);
                            }

                            @Override
                            public void onFinish(String name, String url, Drawable icon, Integer color)
                            {
                                addFragmentToList(mainActivity, name, url, icon, color, id);
                                initiateFragment(mainActivity, fragment);
                            }
                        });

                        mainActivity.mAddList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }

                    @Override
                    public void onFailed(String message)
                    {
                        Toast.makeText(mainActivity, "Failed to load chat ".concat(id).concat(": ").concat(message), Toast.LENGTH_LONG).show();

                        Log.e("Couldn't load SE chat ".concat(id), message);

                        if (message.toLowerCase().contains("not found")) {
                            Log.e("Couldn't load SE chat", "Removing SE ".concat(id).concat(" from list"));
                            mainActivity.removeIdFromSEList(id);
                        }
                    }
                });
            }
        }

        for (String s : mainActivity.chatDataBundle.mSOChatIDs)
        {
            final String chatUrl = "https://chat.stackoverflow.com/rooms/".concat(s);

            if (mainActivity.getSupportFragmentManager().findFragmentByTag(chatUrl) == null) //TODO: Make sure fragments get removed on chat removal, otherwise users won't be able to re-add chats
            {
                final String id = s;
                mainActivity.mRequestFactory.get(chatUrl, true, new RequestFactory.Listener()
                {
                    @Override
                    public void onSucceeded(final URL url, String data)
                    {
                        mainActivity.chatDataBundle.mSOChatUrls.put(Integer.decode(id), chatUrl);
                        MainActivityUtils.AddList addList = mainActivity.mAddList.newInstance(mainActivity, mainActivity.mSharedPrefs, data, id, chatUrl, new MainActivity.AddListListener()
                        {

                            private Fragment fragment;

                            @Override
                            public void onStart()
                            {
                            }

                            @Override
                            public void onProgress(String name, Drawable icon, Integer color)
                            {
                                fragment = addFragment(mainActivity, chatUrl, name, color, Integer.decode(id));
                                mainActivity.chatDataBundle.mSOChats.put(Integer.decode(id), fragment);
                                mainActivity.chatDataBundle.mSOChatColors.put(Integer.decode(id), color);
                                mainActivity.chatDataBundle.mSOChatIcons.put(Integer.decode(id), icon);
                                mainActivity.chatDataBundle.mSOChatNames.put(Integer.decode(id), name);
                            }

                            @Override
                            public void onFinish(String name, String url, Drawable icon, Integer color)
                            {
                                addFragmentToList(mainActivity, name, url, icon, color, id);
                                initiateFragment(mainActivity, fragment);
                            }
                        });

                        addList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }

                    @Override
                    public void onFailed(String message)
                    {
                        Toast.makeText(mainActivity, "Failed to load chat ".concat(id), Toast.LENGTH_SHORT).show();

                        Log.e("Couldn't load SO chat ".concat(id), message);

                        if (message.toLowerCase().contains("not found")) {
                            Log.e("Couldn't load SO chat", "Removing SE ".concat(id).concat(" from list"));
                            mainActivity.removeIdFromSOList(id);
                        }
                    }
                });
            }
        }

        if (mainActivity.chatDataBundle.mSEChatIDs.size() == 0 && mainActivity.chatDataBundle.mSOChatIDs.size() == 0)
        {
            removeAllFragmentsFromList(mainActivity);
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
                    if (mainActivity.mWrappedAdapter.getItemCount() < mainActivity.chatDataBundle.mSEChatIDs.size() + mainActivity.chatDataBundle.mSOChatIDs.size())
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

    /**
     * Open a chat using its tag
     *
     * @param tag the chat's fragment tag (should be its URL)
     */

    public void setFragmentByTag(MainActivity activity, String tag)
    {
        Log.e("TAG", tag);
        if (activity.mFragmentManager.getFragments() != null)
        {
            for (Fragment fragment : activity.mFragmentManager.getFragments())
            {
                if (fragment != null && !fragment.isDetached())
                {
                    activity.mFragmentManager.beginTransaction().detach(fragment).commit();
                }
            }
            Fragment fragToAttach = activity.mFragmentManager.findFragmentByTag(tag);

            if (fragToAttach != null)
            {

                if (tag.equals("home"))
                {
                    activity.mFragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).attach(fragToAttach).commit();
                    activity.mCurrentUsers_SlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                    ((HomeFragment) fragToAttach).hueTest();
                }
                else
                {
                    if (activity.mFragmentManager.findFragmentByTag("home").isDetached())
                    {
                        activity.mFragmentManager.beginTransaction().attach(fragToAttach).commit();
                    }
                    else
                    {
                        activity.mFragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).attach(fragToAttach).commit();
                    }
                    MainActivity.mCurrentUsers_SlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
                }
            }
            else
            {
                Log.e("TAG", tag);
            }

        }
    }

    /**
     * Open a chat by the specified ID
     *
     * @param id     the ID of the desired chat
     * @param domain the domain of the desired chat ("exchange" or "overflow")
     */

    void setFragmentByChatId(MainActivity mainActivity, String id, String domain)
    {
        Log.e("SETID", id.concat(domain));

        if (domain.contains("exchange"))
        {
            if (mainActivity.chatDataBundle.mSEChatUrls.get(Integer.decode(id)) != null)
            {
                setFragmentByTag(mainActivity, mainActivity.chatDataBundle.mSEChatUrls.get(Integer.decode(id)));
            }
            else
            {
                Toast.makeText(mainActivity, "Chat not added", Toast.LENGTH_SHORT).show();
            }
        }
        else if (domain.contains("overflow"))
        {
            if (mainActivity.chatDataBundle.mSOChatUrls.get(Integer.decode(id)) != null)
            {
                setFragmentByTag(mainActivity, mainActivity.chatDataBundle.mSOChatUrls.get(Integer.decode(id)));
            }
            else
            {
                Toast.makeText(mainActivity, "Chat not added", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
     * Fragment Stuffs
     */

    /**
     * Add specified fragment to the {@link FragmentManager}
     *
     * @param fragment Fragment to be added
     */

    private void initiateFragment(MainActivity mainActivity, Fragment fragment) {
        try
        {
            String tag = fragment.getArguments().getString("chatUrl");
            if (mainActivity.mFragmentManager.findFragmentByTag(tag) == null)
            {
                mainActivity.mFragmentManager.beginTransaction().add(R.id.content_main, fragment, tag).detach(fragment).commit();
            }

            if ((mainActivity.mCurrentFragment == null || mainActivity.mCurrentFragment.equals("home")) && mainActivity.mFragmentManager.findFragmentByTag("home") == null)
            {
                mainActivity.mFragmentManager.beginTransaction().add(R.id.content_main, new HomeFragment(), "home").commit();
            }

            mainActivity.mFragmentManager.executePendingTransactions();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Add fragment info to the RecyclerView list
     * @param name Chat name
     * @param url Chat URL
     * @param icon Chat favicon
     * @param color Chat color
     */

    private void addFragmentToList(MainActivity mainActivity, String name, String url, Drawable icon, Integer color, String id) {
        Log.e("ADD", "ADD");
        int identifier;

        if (url.contains("overflow")) identifier = -Integer.decode(id);
        else identifier = Integer.decode(id);

        mainActivity.mWrappedAdapter.addItem(new ChatroomRecyclerObject(
                mainActivity.mWrappedAdapter.getItemCount(),
                name,
                url,
                icon,
                color,
                0,
                identifier
        ));
    }

    /**
     * Might be useful for a batch removal later, but right now, it just enables removal of the only chat added
     */

    void removeAllFragmentsFromList(MainActivity mainActivity)
    {
        if (mainActivity.chatroomsList != null)
        {
//            mAdapter = new RecyclerAdapter(this, mItemClickedListener);
//            chatroomsList.setAdapter(mAdapter);
            for (int i = 0; i < mainActivity.mWrappedAdapter.getItemCount(); i++) {
                mainActivity.mWrappedAdapter.removeItem(i);
            }
        }
        mainActivity.chatDataBundle.resetArrays(true, mainActivity.mEditor);
    }

    /**
     * Instantiate/create the appropriate chat fragment, if necessary
     *
     * @param url   URL of chat
     * @param name  Name of chat
     * @param color Accent color of chat
     * @param id    ID of chat
     * @return the created Fragment
     */
    private Fragment addFragment(MainActivity mainActivity, String url, String name, Integer color, Integer id)
    {
        Fragment fragment;
        if (mainActivity.mFragmentManager.findFragmentByTag(url) != null)
        {
            fragment = mainActivity.mFragmentManager.findFragmentByTag(url);
        }
        else
        {
            fragment = new ChatFragment();
            Bundle args = new Bundle();
            args.putString("chatTitle", name);
            args.putString("chatUrl", url);
            args.putInt("chatColor", color);
            args.putInt("chatId", id);

            fragment.setArguments(args);
        }

        return fragment;
    }
}
