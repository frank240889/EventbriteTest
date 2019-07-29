package com.example.eventbritetest.UI.eventdetail;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.eventbritetest.R;
import com.example.eventbritetest.UI.BaseRoundedBottomSheetDialogFragment;
import com.example.eventbritetest.network.EventbriteApiService;
import com.example.eventbritetest.utils.AndroidUtils;
import com.example.eventbritetest.utils.ColorUtils;
import com.example.eventbritetest.utils.GlideApp;
import com.example.eventbritetest.utils.RoundedSnackbar;
import com.example.eventbritetest.utils.SnackBar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

public class EventDetailFragment extends BaseRoundedBottomSheetDialogFragment<EventDetailViewModel> {

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
    private int mInvertedDominantColor;
    private LinearLayout mContainerTitle;
    private CardView mCardEventDetail;
    private CoordinatorLayout mRootEventDetailContainer;
    private NestedScrollView mNestedScrollView;

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
    public void onAttach(Context context) {
        super.onAttach(context);
        mDominantColor = getArguments().getInt(DOMINANT_COLOR) == 0 ?
                R.color.colorPrimary : getArguments().getInt(DOMINANT_COLOR);

        if(mDominantColor == R.color.colorPrimary) {
            mInvertedDominantColor = R.color.white_overlay;
        }
        else {
            mInvertedDominantColor = ColorUtils.invertColor(mDominantColor);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel.observeLoadingState().observe(this, this::onLoading);
        mViewModel.observeTitle().observe(this, this::onTitleFetched);
        mViewModel.observeOrganizer().observe(this, this::onOrganizerFetched);
        mViewModel.observeLogo().observe(this, this::onLogoUrlFetched);
        mViewModel.observeDescription().observe(this, this::onDescriptionFetched);
        mViewModel.observeDate().observe(this, this::onDateFetched);
        mViewModel.observeAddress().observe(this, this::onAddressFetched);
        mViewModel.observeUrl().observe(this, this::onUrlFetched);
        mViewModel.observeSnackbarMessage().observe(this, this::onSnackbarMessage);
    }

    @Override
    protected void onLoading(Boolean isLoading) {
        //mLoadingLayout.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        mLoadingProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.event_detail_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mTextViewTitle = view.findViewById(R.id.event_title);
        mTextViewTitle.setTextColor(mInvertedDominantColor);
        mTextViewOrganizer = view.findViewById(R.id.event_organizer);
        mTextViewOrganizer.setTextColor(mInvertedDominantColor);
        mTextViewDescription = view.findViewById(R.id.event_description);
        mTextViewDescription.setTextColor(mInvertedDominantColor);
        mTextViewDate = view.findViewById(R.id.event_date);
        mTextViewDate.setTextColor(mInvertedDominantColor);
        mTextViewDate.getCompoundDrawablesRelative()[0].setColorFilter(mInvertedDominantColor, PorterDuff.Mode.SRC_ATOP);
        mTextViewAddress = view.findViewById(R.id.event_address);
        mTextViewAddress.setTextColor(mInvertedDominantColor);
        mTextViewAddress.getCompoundDrawablesRelative()[0].setColorFilter(mInvertedDominantColor, PorterDuff.Mode.SRC_IN);
        mImageViewLogo = view.findViewById(R.id.event_image);
        mLoadingProgressBar = view.findViewById(R.id.loading_progress);
        //mLoadingLayout = view.findViewById(R.id.loading_layout);
        mRootEventDetailContainer = view.findViewById(R.id.event_detail_root_container);
        mContainerTitle = view.findViewById(R.id.event_title_container);
        mNestedScrollView = view.findViewById(R.id.scrollable_container);
        mNestedScrollView.setBackgroundColor(mDominantColor);
        view.findViewById(R.id.event_detail_separator).setBackgroundColor(mInvertedDominantColor);
        mLoadingProgressBar.setIndeterminateTintList(ColorStateList.valueOf(mInvertedDominantColor));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(d -> {
            FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            bottomSheet.setBackground(AndroidUtils.getRoundedDrawable(mDominantColor, getActivity()));
            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheetBehavior.setSkipCollapsed(true);
            dialog.getWindow().setNavigationBarColor(mDominantColor);
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
        mTextViewDescription.setVisibility(View.VISIBLE);
    }

    private void onDateFetched(String s) {
        mTextViewDate.setText(s);
        mTextViewDate.setVisibility(View.VISIBLE);
    }

    private void onAddressFetched(String s) {
        mTextViewAddress.setText(s);
        mTextViewAddress.setVisibility(View.VISIBLE);
    }

    private void onUrlFetched(String s) {
        //mTextViewUrl.setText(s);
    }

    private void onSnackbarMessage(SnackBar snackBar) {
        if(snackBar.getAction() == SnackBar.Action.REQUEST_EVENT_DETAIL) {
            Snackbar snackbar = RoundedSnackbar.make(mContainerTitle,
                    R.string.error_fetching_event, Snackbar.LENGTH_INDEFINITE);

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
