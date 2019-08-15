package com.example.eventbritetest.UI.eventdetail;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
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
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.eventbritetest.R;
import com.example.eventbritetest.UI.BaseRoundedBottomSheetDialogFragment;
import com.example.eventbritetest.network.EventbriteApiService;
import com.example.eventbritetest.utils.ContextUtils;
import com.example.eventbritetest.utils.ErrorState;
import com.example.eventbritetest.utils.GlideApp;
import com.example.eventbritetest.utils.SnackBar;
import com.example.eventbritetest.utils.SnackbarFactory;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

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
    private int mDominantColor;
    private LinearLayout mContainerTitle;
    private View mSeparator;

    @Inject
    ContextUtils contextUtils;

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
                ContextCompat.getColor(context,
                        R.color.colorPrimary) : getArguments().getInt(DOMINANT_COLOR);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel.observeLoaderState().observe(this, this::onLoading);
        mViewModel.observeErrorState().observe(this, this::onError);
        mViewModel.observeTitle().observe(this, this::onTitleFetched);
        mViewModel.observeOrganizer().observe(this, this::onOrganizerFetched);
        mViewModel.observeLogo().observe(this, this::onLogoUrlFetched);
        mViewModel.observeDescription().observe(this, this::onDescriptionFetched);
        mViewModel.observeDate().observe(this, this::onDateFetched);
        mViewModel.observeAddress().observe(this, this::onAddressFetched);
        mViewModel.observeUrl().observe(this, this::onUrlFetched);
        mViewModel.fetchEvent(EventDetailFragment.this.getArguments().getString(EventbriteApiService.EVENT_ID));
    }

    @Override
    protected void onLoading(Boolean isLoading) {
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
        mTextViewOrganizer = view.findViewById(R.id.event_organizer);
        mTextViewDescription = view.findViewById(R.id.event_description);
        mTextViewDate = view.findViewById(R.id.event_date);
        mTextViewAddress = view.findViewById(R.id.event_address);
        mImageViewLogo = view.findViewById(R.id.event_image);
        mLoadingProgressBar = view.findViewById(R.id.loading_progress);
        mContainerTitle = view.findViewById(R.id.event_title_container);
        mSeparator = view.findViewById(R.id.event_detail_separator);
        setColor();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(d -> {
            FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheetBehavior.setSkipCollapsed(true);
            dialog.getWindow().setNavigationBarColor(mDominantColor);
        });
        return dialog;
    }

    private void setColor(){
        mLoadingProgressBar.setIndeterminateTintList(ColorStateList.valueOf(mDominantColor));
        mSeparator.setBackgroundColor(mDominantColor);
        mTextViewDate.getCompoundDrawablesRelative()[0].setColorFilter(mDominantColor, PorterDuff.Mode.SRC_IN);
        mTextViewAddress.getCompoundDrawablesRelative()[0].setColorFilter(mDominantColor, PorterDuff.Mode.SRC_IN);
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
                asBitmap().
                load(value).
                fitCenter().
                placeholder(R.drawable.ic_placeholder_material_24dp).
                apply(new RequestOptions().
                        transform(
                                new RoundedCornersTransformation(
                                        (int) contextUtils.dpToPx(16),
                                        0,
                                        RoundedCornersTransformation.CornerType.ALL))).
                addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        mImageViewLogo.setImageDrawable(getContext().getDrawable(R.drawable.ic_placeholder_material_24dp));
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model,
                                                   Target<Bitmap> target,
                                                   DataSource dataSource,
                                                   boolean isFirstResource) {

                        mImageViewLogo.setImageDrawable(null);

                        if(mDominantColor == ContextCompat.getColor(getContext(),
                                R.color.colorPrimary) && resource != null) {
                            Palette p = Palette.from(resource).generate();
                            mDominantColor = p.getDominantColor(ContextCompat.getColor(getContext(),
                                    R.color.colorPrimary));
                            getDialog().getWindow().setNavigationBarColor(mDominantColor);
                        }
                        return false;
                    }
                }).
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

    private void onError(ErrorState errorState) {
        SnackBar snackBar = SnackBar.create(errorState);
        Snackbar snackbar = SnackbarFactory.create(snackBar, mLoadingProgressBar);
        View.OnClickListener onClickListener = getOnClickListener(snackBar);
        if(onClickListener != null) {
            snackbar.setAction(snackBar.getActionResourceId(), onClickListener);
        }
    }

    private View.OnClickListener getOnClickListener(SnackBar snackBar) {

        switch (snackBar.getAction()) {
            case REQUEST_EVENT_DETAIL:
                return v -> mViewModel.fetchEvent(getArguments().getString(EventbriteApiService.EVENT_ID));
        }
        return null;
    }
}
