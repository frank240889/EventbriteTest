package com.example.eventbritetest.UI.settings;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.palette.graphics.Palette;

import com.example.eventbritetest.R;
import com.example.eventbritetest.UI.BaseAdapter;
import com.example.eventbritetest.interfaces.OnItemClick;
import com.example.eventbritetest.network.DistanceUnit;

import java.util.List;

public class OptionUnitAdapter extends BaseAdapter<DistanceUnit.Unit, ItemOptionUnitHolder> {

    public OptionUnitAdapter(OnItemClick onItemClick) {
        super(onItemClick);
    }

    @NonNull
    @Override
    public ItemOptionUnitHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_option_unit_holder,parent, false);

        return new ItemOptionUnitHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemOptionUnitHolder holder, int position) {
        DistanceUnit.Unit unit = mDatasource.get(position);
        holder.unitTextView.setText(unit.getValue());
        holder.radioButtonOption.setChecked(unit.isChecked());
    }

    @Override
    public void onChanged(List<DistanceUnit.Unit> units) {
        mDatasource = units;
        notifyDataSetChanged();
    }

    public void createPaletteAsync(Bitmap bitmap) {
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette p) {
                // Use generated instance
            }
        });
    }

}
