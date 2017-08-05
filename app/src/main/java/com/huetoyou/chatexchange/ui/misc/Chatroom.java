package com.huetoyou.chatexchange.ui.misc;

import android.graphics.drawable.Drawable;

public class Chatroom
{
    private String name;
    private String url;
    private int id;
    private int recyclerViewPosition;
    private Integer color;
    private boolean isPinned;
    private int viewType;
    private Drawable icon;

    public Chatroom(int id, String name, String url, Drawable icon, Integer color, int viewType, int recyclerViewPosition)
    {
        this.name = name;
        this.id = id;
        this.url = url;
        this.icon = icon;
        this.color = color;
        this.viewType = viewType;
        this.recyclerViewPosition = recyclerViewPosition;
    }

    public String getName()
    {
        return name;
    }

    public int getId()
    {
        return id;
    }

    public String getUrl()
    {
        return url;
    }

    public int getRecyclerViewPosition()
    {
        return recyclerViewPosition;
    }

    public Integer getColor()
    {
        return color;
    }

    public boolean isPinned()
    {
        return isPinned;
    }

    public void setPinned(boolean pinned)
    {
        isPinned = pinned;
    }

    public int getViewType()
    {
        return viewType;
    }

    public Drawable getIcon()
    {
        return icon;
    }
}
