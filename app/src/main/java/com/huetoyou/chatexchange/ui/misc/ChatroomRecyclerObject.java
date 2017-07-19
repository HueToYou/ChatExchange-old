package com.huetoyou.chatexchange.ui.misc;

import android.graphics.drawable.Drawable;

public class ChatroomRecyclerObject
{
    private int position;
    private String name;
    private String url;
    private Drawable icon;
    private Integer color;
    private boolean isPinned;
    private int mViewType;
    private long mId;

    public ChatroomRecyclerObject(int position, String name, String url, Drawable icon, Integer color, int viewType, long id)
    {
        this.position = position;
        this.name = name;
        this.url = url;
        this.icon = icon;
        this.color = color;
        this.mViewType = viewType;
        this.mId = id;
    }

    public int getPosition()
    {
        return position;
    }

    public String getName()
    {
        return name;
    }

    public String getUrl()
    {
        return url;
    }

    public Drawable getIcon()
    {
        return icon;
    }

    public Integer getColor()
    {
        return color;
    }

    public void setIsPinned(boolean pinned)
    {
        isPinned = pinned;
    }

    public boolean isPinned()
    {
        return isPinned;
    }

    public int getViewType()
    {
        return mViewType;
    }

    public long getId()
    {
        return mId;
    }

    public void setPosition(int position)
    {
        this.position = position;
    }
}
