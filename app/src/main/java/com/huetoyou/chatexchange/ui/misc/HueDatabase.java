package com.huetoyou.chatexchange.ui.misc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class HueDatabase extends SQLiteOpenHelper
{
    /*
     * Database constants
     */
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "HueDB";
    private static final String TABLE_CHATS = "chats";

    /*
     * Chats table column names constants
     */
    private static final String KEY_ROOM_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_URL = "url";

    private static final String[] COLUMNS = {KEY_ROOM_ID, KEY_NAME, KEY_URL};

    /*
     * Constructor
     */
    public HueDatabase(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        /*
         * SQL command to create the "chats" table
         */
        String CREATE_CHAT_TABLE = "CREATE TABLE " + TABLE_CHATS + " ( " +
                KEY_ROOM_ID + " INTEGER PRIMARY KEY, " +
                KEY_NAME + " TEXT, " +
                KEY_URL + " TEXT )";

        /*
         * Execute the command
         */
        db.execSQL(CREATE_CHAT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        /*
         * Drop the older table if it exists
         */
        db.execSQL("DROP TABLE IF EXISTS chats");

        /*
         * Create a new, blank, table
         */
        this.onCreate(db);
    }

    /*
     * Method to add a new chatroom to the database
     */
    public void addChatroom(Chatroom chatroom)
    {
        /*
         * Get a reference to a writable database
         */
        SQLiteDatabase db = this.getWritableDatabase();

        /*
         * Create a ContentValues object to add key "column"/value
         */
        ContentValues values = new ContentValues();
        values.put(KEY_ROOM_ID, chatroom.getId());
        values.put(KEY_NAME, chatroom.getName());
        values.put(KEY_URL, chatroom.getUrl());

        /*
         * Insert the ContentValues object into the database
         */
        db.insert(TABLE_CHATS,null, values);

        /*
         * Close the database since we're finished
         */
        db.close();
    }

    /*
     * Method to get a single chatroom from the database
     */
    public Chatroom getChatroom(int chatroomID)
    {

        /*
         * Get a reference to the readable database
         */
        SQLiteDatabase db = this.getReadableDatabase();

        /*
         * Build the query
         */
        Cursor cursor =
                db.query(TABLE_CHATS, COLUMNS," id = ?", new String[] { String.valueOf(chatroomID) },
                        null,
                        null,
                        null,
                        null);

        /*
         * If we got results, get the first one
         */
        if (cursor != null)
        {
            cursor.moveToFirst();
        }

        /*
         * Build the chatroom object
         */
        Chatroom chatroom = new Chatroom(

                cursor.getString(1),                    // Name
                Integer.parseInt(cursor.getString(0)),  // ID
                cursor.getString(2));                   // URL

        /*
         * Alright, we've got everything, now return it
         */
        return chatroom;
    }

    /*
     * Method to return an ArrayList containing all of the chatrooms that were retrieved from the database
     */
    public ArrayList<Chatroom> getAllChatrooms()
    {
        ArrayList<Chatroom> chats = new ArrayList<>();

        /*
         * Query to grab all the rows in the "chats" table
         */
        String query = "SELECT  * FROM " + TABLE_CHATS;

        /*
         * Get a writable reference to the database
         */
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        /*
         * Go over each row, build a Chatroom object, and add it to the ArrayList
         */
        Chatroom currentChat = null;
        if (cursor.moveToFirst())
        {
            do
            {
                currentChat = new Chatroom(

                        cursor.getString(1),                    // Name
                        Integer.parseInt(cursor.getString(0)),  // ID
                        cursor.getString(2));                   // URL

                /*
                 * Okay, we just built a chatroom object,
                 * now add it to the ArrayList
                 */
                chats.add(currentChat);

            } while (cursor.moveToNext());
        }

        /*
         * Alright, we've gone through all the rows, now return the ArrayList
         * containing all of the chatrooms objects
         */
        return chats;
    }

    /*
     * Method to update an existing chatroom in the database
     * I don't really think we will ever need to do this,
     * but this method is here just in case :D
     */
    public int updateChatrooms(Chatroom chatroom)
    {
        /*
         * Get a writable reference to the database
         */
        SQLiteDatabase db = this.getWritableDatabase();

        /*
         * Create a ContentValues object to add key "column"/value
         */
        ContentValues values = new ContentValues();
        values.put(KEY_ROOM_ID, chatroom.getId());
        values.put(KEY_NAME, chatroom.getName());
        values.put(KEY_URL, chatroom.getUrl());

        /*
         * Mmmkay, time to update the row
         */
        int i = db.update(TABLE_CHATS,
                values,
                KEY_ROOM_ID + " = ?",
                new String[] { String.valueOf(chatroom.getId()) } );

        /*
         * Ok, we're all done, close the database
         */
        db.close();

        return i;
    }

    /*
     * Method to delete a single chatroom from the database
     */
    public void deleteChatroom(int chatroomID)
    {

        /*
         * Get a reference to the writable database
         */
        SQLiteDatabase db = this.getWritableDatabase();

        /*
         * Send the delete command
         */
        db.delete(TABLE_CHATS,KEY_ROOM_ID + " = ?", new String[] { String.valueOf(chatroomID) } );

        /*
         * Close the database since we're finished
         */
        db.close();
    }
}