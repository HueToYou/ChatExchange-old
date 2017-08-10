package com.huetoyou.chatexchange.backend.database;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.huetoyou.chatexchange.net.RequestFactory;
import com.huetoyou.chatexchange.ui.activity.main.MainActivity;
import com.huetoyou.chatexchange.ui.misc.Chatroom;

import java.net.URL;
import java.util.ArrayList;

public class DatabaseHelper
{
    /*
     * The database (duh!)
     */
    private HueDatabase hueDatabase = null;

    /*
     * Local caches of the database so that we don't need to query the database
     * every time the user requests one (or all) of the rooms
     */
    private ArrayList<Chatroom> mChatrooms = null;
    private ArrayList<Chatroom> mChatroomsSorted = null;

    /*
     * Constructor
     */
    public DatabaseHelper(Context context)
    {
        hueDatabase = new HueDatabase(context);
    }

    /*
     * This is an internal method for adding the chatroom
     */
    private void addChatroom(Chatroom room)
    {
        /*
         * Get the ID once so we're not calling getId() three times
         */
        int id = room.getId();

        /*
         * Check to see if the room id is already in the database
         */
        if(chatroomObjectsContainsID(id))
        {
            /*
             * If it is, throw an exception; we don't want duplicates in the database :D
             */
            throw new DatabaseException("The user requested that room " + id + " be added to the database, but room " + id + " is already in the database");
        }
        else
        {
            /*
             * It doesn't appear that the ID is already in the database, so
             * just go ahead with the normal adding procedure
             */
            hueDatabase.addChatroom(room);

            /*
             * Update our local cache
             */
            mChatrooms = hueDatabase.getAllChatrooms();
            mChatroomsSorted = sortChatroomsArrayList(mChatrooms);
        }
    }

    /*
     * This is the high-level method for the user
     * to call to add the chatroom
     */
    public Chatroom addChatroomByURL(String url)
    {
        /*
         * The RequestFactory we'll be using to get the html data from the webpage
         */
        RequestFactory requestFactory = new RequestFactory();

        /*
         * This is the listener that we pass as a parameter to the RequestFactory constructor
         */
        RequestFactory.Listener rfl = new RequestFactory.Listener()
        {
            @Override
            public void onSucceeded(URL url, String data)
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
                        fragment = addFragment(chatUrl, name, color, Integer.decode(id));
                        Log.e("RRR", fragment.getArguments().getString("chatUrl", "").concat("HUE"));
                        mainActivity.chatDataBundle.mSEChats.put(Integer.decode(id), fragment);
                        mainActivity.chatDataBundle.mSEChatColors.put(Integer.decode(id), color);
                        mainActivity.chatDataBundle.mSEChatIcons.put(Integer.decode(id), icon);
                        mainActivity.chatDataBundle.mSEChatNames.put(Integer.decode(id), name);
                    }

                    @Override
                    public void onFinish(String name, String url, Drawable icon, Integer color)
                    {
                        addFragmentToList(name, url, icon, color, id);
                        initiateFragment(fragment);
                    }
                });

                mainActivity.mAddList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            @Override
            public void onFailed(String message)
            {
                Toast.makeText(mainActivity, "Failed to load chat ".concat(id).concat(": ").concat(message), Toast.LENGTH_LONG).show();

                Log.e("Couldn't load SE chat ".concat(id), message);

                if (message.toLowerCase().contains("not found"))
                {
                    Log.e("Couldn't load SE chat", "Removing SE ".concat(id).concat(" from list"));
                    mainActivity.removeIdFromSEList(id);
                }
            }
        };

        /*
         * Go grab the HTML data
         */
        requestFactory.get(url, true, rfl);
        return null;
    }

    /*
     * This method will return the cached copy of the database
     */
    public ArrayList<Chatroom> getAllChatrooms()
    {
        return mChatrooms;
    }

    public Chatroom getChatroomByRoomID(int roomID)
    {
        return hueDatabase.getChatroom(roomID);
    }

    public void updateChatroom(Chatroom room)
    {
        /*
         * Get the ID once so we're not calling getId() three times
         */
        int id = room.getId();

        /*
         * Check to see if there is an existing entry for that id
         */
        if(!chatroomObjectsContainsID(id))
        {
            /*
             * Well, that ID is not in the database, so throw an exception
             */
            throw new DatabaseException("The user requested that room " + id + "'s entry in the database be updated, but there is no entry for room " + id + "!");
        }
        else
        {
            /*
             * Everything checks out, there is in fact an entry with that ID
             * in the database, so go ahead and update it.
             */
            hueDatabase.updateChatroom(room);

            /*
             * Update our local caches
             */
            mChatrooms = hueDatabase.getAllChatrooms();
            mChatroomsSorted = sortChatroomsArrayList(mChatrooms);
        }
    }

    public void deleteChatroomByRoomID(int roomID)
    {
        /*
         * Check to see if there is an entry in the database
         * containing the ID we were given
         */
        if(!chatroomObjectsContainsID(roomID))
        {
            /*
             * Well, doesn't look like it. Throw an exception; there's nothing to delete!
             */
            throw new DatabaseException("The requested that room " + roomID + " be removed from the database, but room " + roomID + " is not in the database");
        }
        else
        {
            /*
             * Yup, there is an entry, so go ahead and delete it
             */
            hueDatabase.deleteChatroom(roomID);

            /*
             * Update our local caches
             */
            mChatrooms = hueDatabase.getAllChatrooms();
            mChatroomsSorted = sortChatroomsArrayList(mChatrooms);
        }
    }

    private ArrayList<Chatroom> sortChatroomsArrayList(ArrayList<Chatroom> unsorted)
    {
        ArrayList<Chatroom> sorted = new ArrayList<>();

        for (int i = 0; i < unsorted.size(); i++)
        {
            sorted.add(unsorted.get(i).getRecyclerViewPosition(), unsorted.get(i));
        }

        return sorted;
    }

    /*
     * This method goes through each entry in the ArrayList to determine
     * whether or not and of the rows contains the ID passed in as
     * the method parameter
     */
    private boolean chatroomObjectsContainsID(int id)
    {
        /*
         * Go through each row
         */
        for (int i = 0; i < mChatrooms.size(); i++)
        {
            /*
             * Check whether or not the ID matches
             */
            if(mChatrooms.get(i).getId() == id)
            {
                /*
                 * Yup, we found a match!
                 */
                return true;
            }
        }

        /*
         * Nope, no matches
         */
        return false;
    }
}
