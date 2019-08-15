package com.example.eventbritetest.UI.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.eventbritetest.R;
import com.example.eventbritetest.UI.BaseFragment;
import com.example.eventbritetest.UI.eventdetail.EventDetailFragment;
import com.example.eventbritetest.UI.help.HelpFragment;
import com.example.eventbritetest.UI.settings.SettingsFragment;
import com.example.eventbritetest.utils.ErrorState;
import com.example.eventbritetest.utils.LocationLiveData;
import com.example.eventbritetest.utils.Permission;
import com.example.eventbritetest.utils.SnackBar;
import com.example.eventbritetest.utils.SnackbarFactory;
import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

public class MainFragment extends BaseFragment<MainViewModel> implements SettingsFragment.SettingsListener {

    private EventAdapter mEventAdapter;
    private RecyclerView mRecyclerView;
    private Toolbar mToolbar;
    private ActionBar mActionBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FrameLayout mBlockingInteractionLayout;
    private boolean mLoadingMore = false;
    @Inject
    LocationLiveData locationLiveData;

    public MainFragment(){}

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventAdapter = new EventAdapter((position, view) -> {
            if(view.getId() == R.id.event_details) {

                EventDetailFragment eventDetailFragment =
                        EventDetailFragment.newInstance(mViewModel.getEvent(position).getId(),
                                mViewModel.getEvent(position).getDominantColor());
                eventDetailFragment.
                        show(MainFragment.this.getChildFragmentManager(), EventDetailFragment.class.getName());
            }
        });

        mViewModel.observeLoaderState().observe(this, this::onLoading);
        mViewModel.observeErrorState().observe(this, this::onError);
        mViewModel.observeEvents().observe(this, mEventAdapter);
        setHasOptionsMenu(true);
        fetchOrRequestPermissions();
    }

    private void onError(ErrorState errorState) {
        SnackBar snackBar = SnackBar.create(errorState);
        Snackbar snackbar = SnackbarFactory.create(snackBar, mRecyclerView);
        View.OnClickListener onClickListener = getOnClickListener(snackBar);
        if(onClickListener != null) {
            snackbar.setAction(snackBar.getActionResourceId(), onClickListener);
        }
        snackbar.show();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mRecyclerView = view.findViewById(R.id.list_events);
        mToolbar = view.findViewById(R.id.main_toolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_events);
        mBlockingInteractionLayout = view.findViewById(R.id.blocking_layout);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        mRecyclerView.setAdapter(mEventAdapter);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(getActivity(), R.color.dark));
        mSwipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.primaryDarkColor),
                ContextCompat.getColor(getActivity(), R.color.colorAccentSecondary)
        );

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0) {
                    int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                    int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                    if (!mLoadingMore && totalItemCount <= (lastVisibleItem +1) && totalItemCount > 1) {
                        mViewModel.fetchMoreEvents();
                    }
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(MainFragment.this::fetchOrRequestPermissions);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity)getActivity()).setSupportActionBar(mToolbar);
        mActionBar = ((MainActivity)getActivity()).getSupportActionBar();
        if(mActionBar != null)
            mActionBar.setTitle(R.string.near_events);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.settings_item){
            showSettings();
        }
        else if(id == R.id.help_item) {
            showHelp();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showHelp() {
        HelpFragment helpFragment = HelpFragment.newInstance();
        helpFragment.show(getChildFragmentManager(), HelpFragment.class.getName());
    }

    private void showSettings() {
        SettingsFragment settingsFragment = SettingsFragment.newInstance();
        settingsFragment.show(getChildFragmentManager(), SettingsFragment.class.getName());
    }

    private void fetchOrRequestPermissions() {
        boolean hasLocationPermission = ContextCompat.
                checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if(!hasLocationPermission) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Permission.LOCATION.ordinal());
        }
        else {
            locationLiveData.observe(this, this::onLocationChanged);
        }
    }

    @Override
    public MainViewModel getViewModel() {
        return ViewModelProviders.of(this, androidViewModelFactory).get(MainViewModel.class);
    }

    @Override
    protected void onLoading(Boolean isLoading) {
        mLoadingMore = isLoading;
        mSwipeRefreshLayout.setRefreshing(isLoading);
        if (isLoading) {
            mToolbar.setTitle(R.string.searching_near_events);
        }
        else {
            mToolbar.setTitle(R.string.near_events);
        }
    }

    private void onLocationChanged(Location location) {
        mViewModel.fetchEvents(location);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == Permission.LOCATION.ordinal()) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationLiveData.observe(this, this::onLocationChanged);
            }
            else {
                mSwipeRefreshLayout.setRefreshing(false);
                onError(ErrorState.create(ErrorState.Error.LOCATION));
            }
        }
    }

    @Override
    public void onSettingsChange() {
        reload();
    }

    private void reload() {
        boolean hasLocationPermission = ContextCompat.
                checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if(!hasLocationPermission) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Permission.LOCATION.ordinal());
        }
        else {
            mRecyclerView.scrollToPosition(0);
            locationLiveData.getLocation();
        }
    }

    private View.OnClickListener getOnClickListener(SnackBar snackBar) {
        switch (snackBar.getAction()) {
            case REQUEST_FETCH_EVENTS:
                return v -> locationLiveData.getLocation();
            case REQUEST_LOCATION:
            case REQUEST_LOCATION_PERMISSION:
                return v -> fetchOrRequestPermissions();
            case REQUEST_MORE_EVENTS:
                return v -> mViewModel.fetchMoreEvents();
        }
        return null;
    }
}
