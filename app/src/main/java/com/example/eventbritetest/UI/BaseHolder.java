package com.example.eventbritetest.UI;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventbritetest.interfaces.OnItemClick;

import java.lang.ref.WeakReference;

public abstract class BaseHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    protected WeakReference<OnItemClick> mOnItemClick;
    public BaseHolder(@NonNull View itemView) {
        super(itemView);
    }
    public BaseHolder(@NonNull View itemView, OnItemClick onItemClick) {
        this(itemView);
        mOnItemClick = new WeakReference<>(onItemClick);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(mOnItemClick != null && mOnItemClick.get() != null)
            mOnItemClick.get().onItemClick(getLayoutPosition(), v);
    }
}
