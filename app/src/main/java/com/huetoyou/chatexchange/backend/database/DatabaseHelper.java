package com.huetoyou.chatexchange.backend.database;

import android.content.Context;

import com.huetoyou.chatexchange.ui.misc.Chatroom;

import java.util.ArrayList;

public class DatabaseHelper
{
    private HueDatabase hueDatabase = null;
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
        int id = room.getId();

        if(chatroomObjectsContainsID(id))
        {
            throw new DatabaseException("The user requested that room " + id + " be added to the database, but room " + id + " is already in the database");
        }
        else
        {
            hueDatabase.addChatroom(room);
            mChatrooms = hueDatabase.getAllChatrooms();
            mChatroomsSorted = sortChatroomsArrayList(hueDatabase.getAllChatrooms());
        }
    }

    /*
     * This is the high-level method for the user
     * to call to add the chatroom
     */
    public void addChatroomByURL(String url)
    {

    }

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
        hueDatabase.updateChatroom(room);
        mChatrooms = hueDatabase.getAllChatrooms();
        mChatroomsSorted = sortChatroomsArrayList(hueDatabase.getAllChatrooms());
    }

    public void deleteChatroomByRoomID(int roomID)
    {
        if(!chatroomObjectsContainsID(roomID))
        {
            throw new DatabaseException("The requested that room " + roomID + " be removed from the database, but room " + roomID + " is not in the database");
        }
        else
        {
            hueDatabase.deleteChatroom(roomID);
            mChatrooms = hueDatabase.getAllChatrooms();
            mChatroomsSorted = sortChatroomsArrayList(hueDatabase.getAllChatrooms());
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

    private boolean chatroomObjectsContainsID(int id)
    {
        for (int i = 0; i < mChatrooms.size(); i++)
        {
            if(mChatrooms.get(i).getId() == id)
            {
                return true;
            }
        }

        return false;
    }
}
