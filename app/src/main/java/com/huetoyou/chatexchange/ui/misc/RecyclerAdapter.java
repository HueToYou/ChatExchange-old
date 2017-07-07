package com.huetoyou.chatexchange.ui.misc;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDefault;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;
import com.huetoyou.chatexchange.R;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>
        implements SwipeableItemAdapter<RecyclerAdapter.MyViewHolder>
{
    private Activity mContext;
    private OnItemClicked onItemClicked;

    private ArrayList<MyViewHolder> mVHs = new ArrayList<>();
    private ArrayList<ChatroomRecyclerObject> mChatroomObjects = new ArrayList<>();

    private RecyclerViewSwipeManager mSwipeManager;

    public RecyclerAdapter(Activity activity, OnItemClicked onItemClicked, RecyclerViewSwipeManager swipeManager)
    {
        this.mContext = activity;
        this.onItemClicked = onItemClicked;
        this.mSwipeManager = swipeManager;

        // SwipeableItemAdapter requires stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true);
    }

    @Override
    public int getItemCount()
    {
        return mChatroomObjects.size();
    }

    @Override
    public long getItemId(int position)
    {
        return mChatroomObjects.get(position).getId();
    }

    @Override
    public int getItemViewType(int position)
    {
        return mChatroomObjects.get(position).getViewType();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        ChatroomRecyclerObject item = mChatroomObjects.get(position);

        //mViewHolder = holder;
        holder.setClickListener();
//        holder.setOnLongClickListener(position);
        holder.setCloseClickListener();
        holder.setText();
        holder.setImage();
        mVHs.add(position, holder);

//        holder.mContainer.setOnClickListener(mSwipeableViewContainerOnClickListener);
//        holder.mCloseChat.setOnClickListener(mUnderSwipeableViewButtonOnClickListener);

        holder.setMaxLeftSwipeAmount(-0.25f);
        holder.setMaxRightSwipeAmount(0);
        holder.setSwipeItemHorizontalSlideAmount(item.isPinned() ? -0.25f : 0);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatroom_list_item, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(mView);
        return myViewHolder;
    }

    @Override
    public int onGetSwipeReactionType(MyViewHolder holder, int position, int x, int y)
    {
        return Swipeable.REACTION_CAN_SWIPE_LEFT;
    }

    @Override
    public void onSetSwipeBackground(MyViewHolder holder, int position, int type)
    {}

    @Override
    public SwipeResultAction onSwipeItem(MyViewHolder holder, int position, int result)
    {
        Log.d("SWIPED", "onSwipeItem(position = " + position + ", result = " + result + ")");

        switch (result) {
            // swipe left --- pin
            case Swipeable.RESULT_SWIPED_LEFT:
                holder.revealCloseButton();
                return new SwipeLeftResultAction(this, position);
            // other --- do nothing
            case Swipeable.RESULT_SWIPED_RIGHT:
            case Swipeable.RESULT_CANCELED:
            default:
                holder.hideCloseButton();
                if (position != RecyclerView.NO_POSITION) {
                    return new UnpinResultAction(this, position);
                } else {
                    return null;
                }
        }
    }

    public void addItem(ChatroomRecyclerObject hueObject)
    {
        for (int i = 0; i < mChatroomObjects.size(); i++) {
            if (mChatroomObjects.get(i).getId() == hueObject.getId()) {
                return;
            }
        }

        int position = hueObject.getPosition();
        mChatroomObjects.add(position, hueObject);
        notifyItemInserted(position);
    }

    public ChatroomRecyclerObject getItemAt(int position)
    {
        return mChatroomObjects.get(position);
    }

    public RecyclerViewSwipeManager getSwipeManager() {
        return mSwipeManager;
    }

    public MyViewHolder getViewHolderAt(int position)
    {
        return mVHs.get(position);
    }

    //Move an item at fromPosition to toPosition and notify changes.
    public void moveItem(int fromPosition, int toPosition)
    {
        final ChatroomRecyclerObject object = mChatroomObjects.remove(fromPosition);
        mChatroomObjects.add(toPosition, object);

        notifyItemMoved(fromPosition, toPosition);
    }

    //Remove an item at position and notify changes.
    public ChatroomRecyclerObject removeItem(int position)
    {
        if (mChatroomObjects.get(position) != null)
        {
            final ChatroomRecyclerObject item = mChatroomObjects.remove(position);
            mVHs.remove(position);
            notifyItemRemoved(position);
            return item;
        }

        return null;
    }

    //Remove an item at position and notify changes.
    public void removeItemWithSnackbar(Activity activity, final int position, final SnackbarListener listener)
    {
        getSwipeManager().performFakeSwipe(mVHs.get(position), 1);

        if (mChatroomObjects.get(position) != null)
        {
            final ChatroomRecyclerObject huehuehue = removeItem(position);

            if (huehuehue != null)
            {

                String chatroomName = huehuehue.getName();
                final View parentLayout = activity.findViewById(android.R.id.content);
                Snackbar snackbar = Snackbar
                        .make(parentLayout, "Deleted " + chatroomName, Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                Snackbar hue = Snackbar.make(parentLayout, "Chatroom restored!", Snackbar.LENGTH_SHORT);
                                hue.show();
                                addItem(huehuehue);
                                listener.onUndo();
                            }
                        });

                snackbar.show();
                snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>()
                {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event)
                    {
                        switch (event) {
                            case DISMISS_EVENT_TIMEOUT:
                                listener.onUndoExpire(huehuehue.getUrl());
                                break;
                        }
                    }
                });
            }
        }
    }

    public interface OnItemClicked
    {
        void onClick(View view, int position);

        void onCloseClick(View view, int position);
    }

    public interface SnackbarListener {
        void onUndo();
        void onUndoExpire(String url);
    }

    private interface Swipeable extends SwipeableItemConstants
    {
    }

    public class MyViewHolder extends AbstractSwipeableItemViewHolder
            implements SwipeableItemViewHolder
    {
        // TODO: whatever views you need to bind
        TextView mTextView;
        ImageView mImageView;
        ImageView mCloseChat;
        View mItem;

        FrameLayout mContainer;
        RelativeLayout mBehind;

        private final AnimatorSet mCloseButtonRevealSet = new AnimatorSet();
        private final AnimatorSet mCloseButtonHideSet = new AnimatorSet();
        private final AnimatorListenerAdapter mRevealListener = new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation)
            {
                mCloseChat.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        };
        private final AnimatorListenerAdapter mHideListener = new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                mCloseChat.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation)
            {
                super.onAnimationStart(animation);
            }
        };

        MyViewHolder(View v)
        {
            super(v); // done this way instead of view tagging
            mItem = v;
            mTextView = v.findViewById(R.id.chatroomName);
            mImageView = v.findViewById(R.id.chatroomImg);
            mCloseChat = v.findViewById(R.id.close_chat_img);

            mBehind = v.findViewById(R.id.behind_views);
            mContainer = v.findViewById(R.id.chat_item_container);

            mCloseChat.setScaleX(0f);
            mCloseChat.setScaleY(0f);

            ObjectAnimator revealAnimatorX = ObjectAnimator.ofFloat(
                    mCloseChat,
                    "scaleX",
                    0f,
                    1.0f
            );

            ObjectAnimator revealAnimatorY = ObjectAnimator.ofFloat(
                    mCloseChat,
                    "scaleY",
                    0f,
                    1.0f
            );

            mCloseButtonRevealSet.play(revealAnimatorX);
            mCloseButtonRevealSet.play(revealAnimatorY);
            mCloseButtonRevealSet.setInterpolator(new OvershootInterpolator());
            mCloseButtonRevealSet.setDuration(mContext.getResources().getInteger(R.integer.animation_duration_ms) - 200);
            mCloseButtonRevealSet.addListener(mRevealListener);

            ObjectAnimator hideAnimatorX = ObjectAnimator.ofFloat(
                    mCloseChat,
                    "scaleX",
                    1.0f,
                    0f
            );

            ObjectAnimator hideAnimatorY = ObjectAnimator.ofFloat(
                    mCloseChat,
                    "scaleY",
                    1.0f,
                    0f
            );

            mCloseButtonHideSet.play(hideAnimatorX);
            mCloseButtonHideSet.play(hideAnimatorY);
            mCloseButtonHideSet.setInterpolator(new AnticipateInterpolator());
            mCloseButtonHideSet.setDuration(mContext.getResources().getInteger(R.integer.animation_duration_ms) - 200);
            mCloseButtonHideSet.addListener(mHideListener);
        }

        @Override
        public View getSwipeableContainerView()
        {
            return mContainer;
        }

        public View getItem()
        {
            return mItem;
        }

        public ImageView getCloseChatButton()
        {
            return mCloseChat;
        }

        public void setText()
        {
            if (mChatroomObjects.size() > getAdapterPosition())
            {
                mTextView.setText(mChatroomObjects.get(getAdapterPosition()).getName());
            }
        }

        public void setImage()
        {
            if (mChatroomObjects.size() > getAdapterPosition())
            {
                mImageView.setImageDrawable(mChatroomObjects.get(getAdapterPosition()).getIcon());
            }
        }

        public void setClickListener()
        {
            mItem.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Log.e("CLICKED", getAdapterPosition() + "");

                    if (mCloseChat.getScaleX() == 1.0f)
                    {
                        mCloseButtonRevealSet.cancel();
                        mCloseButtonHideSet.start();
                        getSwipeManager().performFakeSwipe(mVHs.get(getAdapterPosition()), 1);
                        //mCloseChat.setVisibility(View.INVISIBLE);
                        Log.e("CLOSE", "HIDING");
                    }
                    else if (onItemClicked != null)
                    {
                        Log.e("SENDING", "CLICKTERFACE");
                        onItemClicked.onClick(view, getAdapterPosition());
                    }
                }
            });
        }

        public void revealCloseButton() {
            mCloseButtonHideSet.cancel();
            mCloseButtonRevealSet.start();
        }

        public void hideCloseButton() {
            mCloseButtonRevealSet.cancel();
            mCloseButtonHideSet.start();
        }

        public void setCloseClickListener()
        {
            mCloseChat.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (onItemClicked != null)
                    {
                        onItemClicked.onCloseClick(mCloseChat, getAdapterPosition());
                    }
                }
            });
        }
    }

    private static class SwipeLeftResultAction extends SwipeResultActionMoveToSwipedDirection
    {
        private RecyclerAdapter mAdapter;
        private final int mPosition;
        private boolean mSetPinned;

        SwipeLeftResultAction(RecyclerAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            ChatroomRecyclerObject item = mAdapter.mChatroomObjects.get(mPosition);

            if (!item.isPinned()) {
                item.setIsPinned(true);
                mAdapter.notifyItemChanged(mPosition);
                mSetPinned = true;
            }
        }

        @Override
        protected void onSlideAnimationEnd() {
            super.onSlideAnimationEnd();
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }

    private static class UnpinResultAction extends SwipeResultActionDefault
    {
        private RecyclerAdapter mAdapter;
        private final int mPosition;

        UnpinResultAction(RecyclerAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            ChatroomRecyclerObject item = mAdapter.mChatroomObjects.get(mPosition);
            if (item.isPinned()) {
                item.setIsPinned(false);
                mAdapter.notifyItemChanged(mPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }
}
