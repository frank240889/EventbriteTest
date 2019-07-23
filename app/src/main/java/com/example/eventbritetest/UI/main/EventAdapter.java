package com.example.eventbritetest.UI.main;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.eventbritetest.R;
import com.example.eventbritetest.UI.AbstractAdapter;
import com.example.eventbritetest.interfaces.OnItemClick;
import com.example.eventbritetest.model.ui.UIEvent;
import com.example.eventbritetest.utils.GlideApp;

import java.util.List;

public class EventAdapter extends AbstractAdapter<UIEvent,EventHolder> {
    private int mDefaultColor;
    public EventAdapter(OnItemClick onItemClick) {
        super(onItemClick);
    }

    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mDefaultColor = ContextCompat.getColor(parent.getContext(), R.color.primaryDarkColor);
        View view = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_event_holder_2, parent, false);
        return new EventHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull EventHolder holder, int position) {
        UIEvent uiEvent = mDatasource.get(position);
        holder.textViewEventSummary.setText(uiEvent.getDescription());

        if(!uiEvent.getUrlPreview().equals("")) {
            GlideApp.with(holder.imageViewEventPreview.getContext()).
                    asBitmap().
                    load(uiEvent.getUrlPreview()).
                    thumbnail(0.25f).
                    placeholder(R.drawable.ic_placeholder_material_24dp).
                    listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }
                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            if (resource != null) {
                                Palette p = Palette.from(resource).generate();
                                int colorPalette = p.getDominantColor(mDefaultColor);
                                //((CardView)holder.itemView).setCardBackgroundColor(colorPalette);
                                //int colorPalette2 = p.getDominantSwatch().getTitleTextColor();
                                //holder.textViewEventSummary.setTextColor(colorPalette2);
                                uiEvent.setDominantColor(colorPalette);
                            }
                            return false;
                        }
                    }).
                    into(holder.imageViewEventPreview);
        }
    }

    @Override
    public void onChanged(List<UIEvent> uiEvents) {
        mDatasource = uiEvents;
        notifyDataSetChanged();
    }
}
