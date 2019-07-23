package com.example.eventbritetest.UI;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventbritetest.interfaces.OnItemClick;

import java.util.List;

public abstract class AbstractAdapter<D, VH extends AbstractHolder> extends RecyclerView.Adapter<VH> implements Observer<List<D>> {
    protected List<D> mDatasource;
    protected OnItemClick mListener;
    public AbstractAdapter() {}
    public AbstractAdapter(OnItemClick listener) {
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mDatasource != null ? mDatasource.size() : 0;
    }
}
