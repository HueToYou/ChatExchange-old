package com.huetoyou.chatexchange;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.huetoyou.chatexchange.ui.misc.Chatroom;

public class DBtest extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbtest);

        /*Chatroom chat = new Chatroom("Ask Ubuntu General Room", 201, "https://chat.stackexchange.com/rooms/201");

        HueDatabase database = new HueDatabase(this);

        database.addChatroom(chat);
        database.deleteChatroom(1);

        Toast.makeText(this, String.valueOf(database.getChatroom(1).getId()),
                Toast.LENGTH_LONG).show();*/


    }
}
