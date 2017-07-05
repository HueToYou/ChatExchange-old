package com.huetoyou.chatexchange.ui.misc;

import android.graphics.drawable.Drawable;

class ChatroomRecyclerObject
{
    private int position;
    private String name;
    private String url;
    private Drawable icon;
    private Integer color;

    ChatroomRecyclerObject(int position, String name, String url, Drawable icon, Integer color)
    {
        this.position = position;
        this.name = name;
        this.url = url;
        this. icon = icon;
        this.color = color;
    }

    int getPosition()
    {
        return position;
    }

    String getName()
    {
        return name;
    }

    String getUrl()
    {
        return url;
    }

    Drawable getIcon()
    {
        return icon;
    }

    Integer getColor()
    {
        return color;
    }
}
