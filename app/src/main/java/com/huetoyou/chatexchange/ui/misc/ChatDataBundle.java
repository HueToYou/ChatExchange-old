package com.huetoyou.chatexchange.ui.misc;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.util.SparseIntArray;

import java.util.HashSet;
import java.util.Set;

public class ChatDataBundle
{
    public SparseArray<Fragment> mSOChats = new SparseArray<>();
    public SparseArray<Fragment> mSEChats = new SparseArray<>();

    public SparseIntArray mSOChatColors = new SparseIntArray();
    public SparseIntArray mSEChatColors = new SparseIntArray();

    public SparseArray<String> mSOChatNames = new SparseArray<>();
    public SparseArray<String> mSEChatNames = new SparseArray<>();

    public SparseArray<String> mSOChatUrls = new SparseArray<>();
    public SparseArray<String> mSEChatUrls = new SparseArray<>();

    public SparseArray<Drawable> mSOChatIcons = new SparseArray<>();
    public SparseArray<Drawable> mSEChatIcons = new SparseArray<>();

    public Set<String> mSOChatIDs = new HashSet<>(0);
    public Set<String> mSEChatIDs = new HashSet<>(0);
}