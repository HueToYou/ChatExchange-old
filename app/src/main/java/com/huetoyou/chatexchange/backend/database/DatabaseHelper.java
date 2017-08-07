package com.huetoyou.chatexchange.backend.database;

import android.content.Context;
import com.huetoyou.chatexchange.ui.misc.Chatroom;
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
