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
import com.example.eventbritetest.UI.settings.SettingsFragment;
import com.example.eventbritetest.persistence.sharedpreferences.SharedPref;
import com.example.eventbritetest.utils.LocationLiveData;
import com.example.eventbritetest.utils.Permission;
import com.example.eventbritetest.utils.RoundedSnackbar;
import com.example.eventbritetest.utils.SnackBar;
import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

import static com.example.eventbritetest.utils.SnackBar.Action.NONE;
import static com.example.eventbritetest.utils.SnackBar.Action.REQUEST_LOCATION_PERMISSION;

public class MainFragment extends BaseFragment<MainViewModel> implements SettingsFragment.SettingsListener {

    private EventAdapter mEventAdapter;
    private RecyclerView mRecyclerView;
    private Toolbar mToolbar;
    private ActionBar mActionBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //private TextView mGeneralMessage;
    private FrameLayout mBlockingInteractionLayout;
    private boolean mLoadingMore = false;

    @Inject
    LocationLiveData locationLiveData;

    @Inject
    SharedPref sharedPref;

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
        mViewModel.observeMessageState().observe(this, this::onSnackbarMessage);
        mViewModel.observeEvents().observe(this, uiEvents -> {
            mEventAdapter.onChanged(uiEvents);
            String title = String.format(getString(R.string.title_main_toolbar), uiEvents.size());
            mActionBar.setTitle(title);
        });
        setHasOptionsMenu(true);
        fetchOrRequestPermissions();
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
        //mGeneralMessage = view.findViewById(R.id.text_view_general_message);
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
                    if (totalItemCount <= (lastVisibleItem + 1) && !mLoadingMore) {
                        mViewModel.fetchEvents(null, true);
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
        return super.onOptionsItemSelected(item);
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
        mBlockingInteractionLayout.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        if (isLoading) {
            mToolbar.setTitle(R.string.searching_near_events);
        }
    }

    private void onLocationChanged(Location location) {
        mViewModel.fetchEvents(location, false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == Permission.LOCATION.ordinal()) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //mGeneralMessage.setVisibility(View.GONE);
                locationLiveData.observe(this, this::onLocationChanged);
            }
            else {
                //mGeneralMessage.setVisibility(View.VISIBLE);
                mSwipeRefreshLayout.setRefreshing(false);
                onSnackbarMessage(SnackBar.create(R.string.location_error,R.string.retry, REQUEST_LOCATION_PERMISSION));
            }
        }
    }

    @Override
    protected void onSnackbarMessage(SnackBar snackBar) {
        if(snackBar.getAction() == NONE) {
            RoundedSnackbar.make(mRecyclerView, snackBar.getMessageResourceId(), Snackbar.LENGTH_SHORT).show();
        }
        else {
            createSnackbarWithAction(snackBar);
        }
    }

    @Override
    protected void createSnackbarWithAction(SnackBar snackBar) {
        Snackbar snackbar = RoundedSnackbar.make(mRecyclerView, snackBar.getMessageResourceId(), Snackbar.LENGTH_INDEFINITE);
        View.OnClickListener onClickListener = getOnClickListener(snackBar);
        snackbar.setAction(snackBar.getActionResourceId(), onClickListener);
        snackbar.show();
    }

    @Override
    protected View.OnClickListener getOnClickListener(SnackBar snackBar) {

        switch (snackBar.getAction()) {
            case REQUEST_FETCH_EVENTS:
                return v -> locationLiveData.getLocation();
            case REQUEST_LOCATION_PERMISSION:
                return v -> MainFragment.this.requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Permission.LOCATION.ordinal()
                );
            case REQUEST_MORE_EVENTS:
                return v -> mViewModel.fetchEvents(null, true);
        }
        return null;
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
            mRecyclerView.smoothScrollToPosition(0);
            locationLiveData.getLocation();
        }
    }
}
