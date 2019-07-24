package com.example.eventbritetest.UI.settings;

import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.eventbritetest.R;
import com.example.eventbritetest.UI.BaseHolder;
import com.example.eventbritetest.interfaces.OnItemClick;

public class ItemOptionUnitHolder extends BaseHolder {
    RadioButton radioButtonOption;
    TextView unitTextView;
    public ItemOptionUnitHolder(@NonNull View itemView, OnItemClick onItemClick) {
        super(itemView, onItemClick);
        radioButtonOption = itemView.findViewById(R.id.option_unit_radio_button);
        unitTextView = itemView.findViewById(R.id.option_unit_text_view);
        radioButtonOption.setOnClickListener(this);
        unitTextView.setOnClickListener(this);
    }
}
