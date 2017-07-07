package com.huetoyou.chatexchange.ui.misc;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class HueRecyclerViewSwipeHelperHue extends ItemTouchHelper.SimpleCallback
{
    private RecyclerAdapter mHueAdapter;

    public HueRecyclerViewSwipeHelperHue(RecyclerAdapter hueAdapter)
    {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.mHueAdapter = hueAdapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target)
    {
        //TODO: Not implemented here
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
    {
        final ChatroomRecyclerObject hueObject = mHueAdapter.getItemAt(viewHolder.getAdapterPosition());
        mHueAdapter.removeItem(viewHolder.getAdapterPosition());

        /*try
        {
            Thread.sleep(500);
            mHueAdapter.addItem(hueObject);
            ((RecyclerAdapter.MyViewHolder)mHueAdapter.getViewHolderAt(hueObject.getPosition())).performLongClick();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }*/
    }
}