package com.huetoyou.chatexchange.ui.misc;

public class Chatroom
{
    private String name;
    private String url;
    private int id;

    public Chatroom(String name, int id, String url)
    {
        this.name = name;
        this.id = id;
        this.url = url;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }
}
