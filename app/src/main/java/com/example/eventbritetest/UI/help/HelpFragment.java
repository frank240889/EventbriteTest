package com.example.eventbritetest.UI.help;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventbritetest.R;

public class HelpFragment extends DialogFragment {
    private TextView mTitleTextView;
    private TextView mDescriptionTextView;
    private Button mAcceptButton;
    public HelpFragment() {}

    public static HelpFragment newInstance() {
        return new HelpFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.help_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mTitleTextView = view.findViewById(R.id.title_help_dialog);
        mDescriptionTextView = view.findViewById(R.id.description_help_dialog);
        mAcceptButton = view.findViewById(R.id.ok_button_help_dialog);
        mDescriptionTextView.setText("Se muestran actualmente eventos de las categorias culturales y deportivos que están a un máximo de 15 kilometros de casa.");
        mAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
