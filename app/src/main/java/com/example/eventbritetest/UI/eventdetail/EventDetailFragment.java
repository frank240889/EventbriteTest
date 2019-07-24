package com.example.eventbritetest.UI.eventdetail;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.eventbritetest.R;
import com.example.eventbritetest.UI.RoundedBottomSheetDialogFragment;
import com.example.eventbritetest.network.EventbriteApiService;
import com.example.eventbritetest.utils.GlideApp;
import com.example.eventbritetest.utils.SnackBar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

public class EventDetailFragment extends RoundedBottomSheetDialogFragment<EventDetailViewModel> {

    private static final String DOMINANT_COLOR = "dominant_color";
    private TextView mTextViewTitle;
    private TextView mTextViewOrganizer;
    private TextView mTextViewDescription;
    private TextView mTextViewDate;
    private TextView mTextViewAddress;
    private TextView mTextViewUrl;
    private ImageView mImageViewLogo;
    private ProgressBar mLoadingProgressBar;
    private FrameLayout mLoadingLayout;
    private int mDominantColor;

    public EventDetailFragment() {}

    public static EventDetailFragment newInstance(String idEvent, int color) {
        EventDetailFragment eventDetailFragment = new EventDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EventbriteApiService.EVENT_ID, idEvent);
        bundle.putInt(DOMINANT_COLOR, color);
        eventDetailFragment.setArguments(bundle);
        return eventDetailFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDominantColor = getArguments().getInt(DOMINANT_COLOR) == 0 ?
                R.color.colorPrimary : getArguments().getInt(DOMINANT_COLOR);

        mViewModel.getLoadingState().observe(this, this::onLoading);
        mViewModel.getTitle().observe(this, this::onTitleFetched);
        mViewModel.getOrganizer().observe(this, this::onOrganizerFetched);
        mViewModel.getLogo().observe(this, this::onLogoUrlFetched);
        mViewModel.getDescription().observe(this, this::onDescriptionFetched);
        mViewModel.getDate().observe(this, this::onDateFetched);
        mViewModel.getAddress().observe(this, this::onAddressFetched);
        mViewModel.getUrl().observe(this, this::onUrlFetched);
        mViewModel.getOnSnackbarMessage().observe(this, this::onSnackbarMessage);
    }

    @Override
    protected void onLoading(Boolean isLoading) {
        mLoadingLayout.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.event_detail_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mTextViewTitle = view.findViewById(R.id.event_title);
        mTextViewTitle.setTextColor(mDominantColor);
        mTextViewOrganizer = view.findViewById(R.id.event_organizer);
        mTextViewOrganizer.setTextColor(mDominantColor);
        mTextViewDescription = view.findViewById(R.id.event_description);
        mTextViewDate = view.findViewById(R.id.event_date);
        mTextViewAddress = view.findViewById(R.id.event_address);
        mImageViewLogo = view.findViewById(R.id.event_image);
        mLoadingProgressBar = view.findViewById(R.id.loading_progress);
        mLoadingLayout = view.findViewById(R.id.loading_layout);
        mLoadingProgressBar.setIndeterminateTintList(ColorStateList.valueOf(mDominantColor));
        ViewCompat.requestApplyInsets(view);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setNavigationBarColor(mDominantColor);
        dialog.setOnShowListener(d -> {
            FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            mViewModel.fetchEvent(EventDetailFragment.this.getArguments().getString(EventbriteApiService.EVENT_ID));
        });
        return dialog;

    }

    @Override
    public EventDetailViewModel getViewModel() {
        return ViewModelProviders.of(this, androidViewModelFactory).get(EventDetailViewModel.class);
    }

    private void onTitleFetched(String s) {
        mTextViewTitle.setText(s);
    }

    private void onOrganizerFetched(String s) {
        mTextViewOrganizer.setText(s);
    }

    private void onLogoUrlFetched(String value) {
        GlideApp.with(this).
                load(value).
                fitCenter().
                transition(DrawableTransitionOptions.withCrossFade(150)).
                placeholder(R.drawable.ic_placeholder_material_24dp).
                into(mImageViewLogo);
    }

    private void onDescriptionFetched(String s) {
        mTextViewDescription.setText(s);
    }

    private void onDateFetched(String s) {
        mTextViewDate.setText(s);
    }

    private void onAddressFetched(String s) {
        mTextViewAddress.setText(s);
    }

    private void onUrlFetched(String s) {
        //mTextViewUrl.setText(s);
    }

    private void onSnackbarMessage(SnackBar snackBar) {
        if(snackBar.getAction() == SnackBar.Action.REQUEST_EVENT_DETAIL) {
            Snackbar snackbar = Snackbar.make(((View)mLoadingLayout.getParent()), R.string.error_fetching_event, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(snackBar.getActionResourceId(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewModel.fetchEvent(EventDetailFragment.this.getArguments().getString(EventbriteApiService.EVENT_ID));
                }
            });
            snackbar.show();
        }
    }
}
