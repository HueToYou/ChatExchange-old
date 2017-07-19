package com.huetoyou.chatexchange.ui.misc;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Zacha on 7/17/2017.
 */

public class CustomRecyclerView extends RecyclerView
{
    public CustomRecyclerView(Context context)
    {
        super(context);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected Parcelable onSaveInstanceState()
    {
        super.onSaveInstanceState();
        return null;
    }
}
