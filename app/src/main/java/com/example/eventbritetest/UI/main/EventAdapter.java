package com.example.eventbritetest.UI.main;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.DiffUtil;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.eventbritetest.R;
import com.example.eventbritetest.UI.BaseAdapter;
import com.example.eventbritetest.interfaces.OnItemClick;
import com.example.eventbritetest.model.ui.UIEvent;
import com.example.eventbritetest.utils.GlideApp;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class EventAdapter extends BaseAdapter<UIEvent,EventHolder> {
    private Deque<List<UIEvent>> mPendingUpdates = new ArrayDeque<>();

    public EventAdapter(OnItemClick onItemClick) {
        super(onItemClick);
        mDatasource = new ArrayList<>();
    }

    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_event_holder_2, parent, false);
        return new EventHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull EventHolder holder, int position, @NonNull List<Object> payloads) {
        if(payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        }
        else {
            UIEvent uiEvent = mDatasource.get(position);
            Bundle bundle = (Bundle) payloads.get(0);
                String eventName = bundle.getString("event_name");
                holder.textViewEventSummary.setText(eventName);
                String urlLogo = bundle.getString("url_logo");
                if(!urlLogo.equals("")) {
                    loadLogo(uiEvent, holder);
                }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull EventHolder holder, int position) {
        render(holder, position);
    }

    private void render(EventHolder holder, int position) {
        UIEvent uiEvent = mDatasource.get(position);
        holder.textViewEventSummary.setText(uiEvent.getName().isEmpty() ?
                holder.itemView.getContext().getString(R.string.title_not_available) :
                uiEvent.getName());

        if(!uiEvent.getUrlPreview().equals("")) {
            loadLogo(uiEvent, holder);
        }
    }

    private void loadLogo(UIEvent uiEvent, EventHolder holder) {
        GlideApp.with(holder.imageViewEventPreview.getContext()).
                asBitmap().
                load(uiEvent.getUrlPreview()).
                thumbnail(0.25f).
                placeholder(R.drawable.ic_placeholder_material_24dp).
                listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e,
                                                Object model, Target<Bitmap> target,
                                                boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model,
                                                   Target<Bitmap> target,
                                                   DataSource dataSource,
                                                   boolean isFirstResource) {
                        if (resource != null) {
                            Palette p = Palette.from(resource).generate();
                            int colorPalette = p.getDominantColor(ContextCompat.getColor(holder.itemView.getContext(),
                                    R.color.colorPrimary));
                            uiEvent.setDominantColor(colorPalette);
                        }
                        else {
                            uiEvent.setDominantColor(R.color.colorPrimary);
                        }
                        return false;
                    }
                }).
                into(holder.imageViewEventPreview);
    }

    @Override
    public void onChanged(List<UIEvent> uiEvents) {
        if(uiEvents == null || uiEvents.isEmpty() || getItemCount() == 0) {
            mDatasource = uiEvents;
            notifyDataSetChanged();
        }
        else {
            /*if(mPendingUpdates.size() > 1)
                return;*/

            mPendingUpdates.push(uiEvents);
            DiffUtil.DiffResult diffResult = DiffUtil.
                    calculateDiff(new EventDiff(uiEvents, mDatasource),false);
            diffResult.dispatchUpdatesTo(this);
            mDatasource.clear();
            mDatasource.addAll(uiEvents);
            if(mPendingUpdates.size() > 0)
                mPendingUpdates.remove();
        }
    }
}
