package com.example.eventbritetest.UI.settings;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventbritetest.R;
import com.example.eventbritetest.UI.BaseRoundedBottomSheetDialogFragment;
import com.example.eventbritetest.interfaces.OnItemClick;
import com.google.android.material.snackbar.Snackbar;

public class SettingsFragment extends BaseRoundedBottomSheetDialogFragment<SettingsViewModel> {
    public interface SettingsListener {
        void onSettingsChange();
    }

    private SettingsListener mSettingsListener;
    private RecyclerView mRecyclerViewUnits;
    private OptionUnitAdapter mOptionUnitAdapter;
    private EditText mEditTextCurrentRange;
    private Button mSaveSettingsButton;

    public SettingsFragment() {}

    public static SettingsFragment newInstance(){
        return new SettingsFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getParentFragment() instanceof SettingsListener) {
            mSettingsListener = (SettingsListener) getParentFragment();
        }
        else if(context instanceof SettingsListener) {
            mSettingsListener = (SettingsListener) context;
        }
        else {
            throw new IllegalArgumentException("Host must implement " + SettingsListener.class.getName() +
                    "interface.");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOptionUnitAdapter = new OptionUnitAdapter(new OnItemClick() {
            @Override
            public void onItemClick(int position, View view) {
                if(view.getId() == R.id.option_unit_radio_button ||
                view.getId() == R.id.edit_text_unit) {
                    mViewModel.checkUnit(position);
                }

            }
        });

        mViewModel.getLiveCurrentRange().observe(this, this::onRangeRead);
        mViewModel.getUnitList().observe(this, mOptionUnitAdapter);
        mViewModel.getMessage().observe(this, this::onMessage);
        mViewModel.hasChangeAny().observe(this, this::onSettingsChange);
        mViewModel.getLoadingState().observe(this, this::onLoading);
    }

    @Override
    protected void onLoading(Boolean isLoading) {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_fragment,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mRecyclerViewUnits = view.findViewById(R.id.unit_list);
        mEditTextCurrentRange = view.findViewById(R.id.edit_text_unit);
        mSaveSettingsButton = view.findViewById(R.id.save_setting_button);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mRecyclerViewUnits.getContext());
        ((LinearLayoutManager) layoutManager).setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerViewUnits.setLayoutManager(layoutManager);
        mRecyclerViewUnits.setAdapter(mOptionUnitAdapter);
        mSaveSettingsButton.setOnClickListener(v -> mViewModel.saveChanges());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSettingsListener = null;
    }

    @Override
    public SettingsViewModel getViewModel() {
        return ViewModelProviders.of(this, androidViewModelFactory).get(SettingsViewModel.class);
    }

    private void onSettingsChange(Boolean changed) {
        if(mSettingsListener != null) {
            if(changed) {
                mSettingsListener.onSettingsChange();
                dismiss();
            }
        }
    }

    private void onMessage(Integer resourceId) {
        Snackbar.make(mRecyclerViewUnits, resourceId, Snackbar.LENGTH_SHORT).show();
    }

    private void onRangeRead(String value) {
        mEditTextCurrentRange.setText(value);
        mEditTextCurrentRange.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mViewModel.setRange(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
