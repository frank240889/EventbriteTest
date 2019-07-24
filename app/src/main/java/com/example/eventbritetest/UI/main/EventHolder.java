package com.example.eventbritetest.UI.main;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.eventbritetest.R;
import com.example.eventbritetest.UI.BaseHolder;
import com.example.eventbritetest.interfaces.OnItemClick;

public class EventHolder extends BaseHolder {
    Button buttonEventDetails;
    ImageView imageViewEventPreview;
    TextView textViewEventSummary;

    public EventHolder(@NonNull View itemView, OnItemClick onItemClick) {
        super(itemView, onItemClick);
        buttonEventDetails = itemView.findViewById(R.id.event_details);
        buttonEventDetails.setOnClickListener(this);
        imageViewEventPreview = itemView.findViewById(R.id.event_preview);
        textViewEventSummary = itemView.findViewById(R.id.event_summary);
    }
}
