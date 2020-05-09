package org.duder.view.fragment.event;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.duder.R;
import org.duder.dto.event.EventLoadingMode;
import org.duder.view.activity.CreateEventActivity;
import org.duder.view.activity.EventDetailActivity;
import org.duder.view.adapter.listener.LazyLoadRecyclerViewListener;
import org.duder.view.fragment.BaseFragment;
import org.duder.viewModel.EventViewModel;
import org.duder.viewModel.OwnEventViewModel;
import org.duder.viewModel.PrivateEventViewModel;
import org.duder.viewModel.PublicEventViewModel;
import org.duder.viewModel.state.FragmentState;

import io.reactivex.android.schedulers.AndroidSchedulers;

import static org.duder.util.Const.CREATED_EVENT_URI;

public class EventViewFragment extends BaseFragment {

    private static final String TAG = EventViewFragment.class.getSimpleName();
    private static final int CREATE_EVENT_REQUEST = 1;
    public static final String EVENT_NAME = "EVENT_NAME";
    public static final String EVENT_DESCRIPTION = "EVENT_DESCRIPTION";
    public static final String EVENT_IMAGE = "EVENT_IMAGE";

    private EventViewModel viewModel;
    private ProgressBar progressBar;
    private RecyclerView eventsList;
    private FloatingActionButton addEventButton;
    private LazyLoadRecyclerViewListener lazyListener;
    private SwipeRefreshLayout swipeLayout;
    private final EventLoadingMode loadingMode;

    public EventViewFragment(EventLoadingMode loadingMode) {
        this.loadingMode = loadingMode;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_events_public, container, false);
        progressBar = root.findViewById(R.id.progress_spinner);
        eventsList = root.findViewById(R.id.events_list);
        addEventButton = root.findViewById(R.id.btn_add_event);
        swipeLayout = root.findViewById(R.id.swipe_layout);

        init();
        viewModel.loadEventsOnInit();
        return root;
    }

    private void init() {
        initViewModel();
        initLayout();
        initListeners();
        initSubscriptions();
    }

    private void initViewModel() {
        switch (loadingMode) {
            case OWN:
                viewModel = ViewModelProviders.of(getActivity()).get(OwnEventViewModel.class);
                break;
            case PRIVATE:
                viewModel = ViewModelProviders.of(getActivity()).get(PrivateEventViewModel.class);
                break;
            case PUBLIC:
                viewModel = ViewModelProviders.of(getActivity()).get(PublicEventViewModel.class);
                break;
        }
    }

    private void initLayout() {
        setProgressBarColor();
        swipeLayout.setColorSchemeResources(R.color.primary);
        if (loadingMode == EventLoadingMode.OWN) {
            addEventButton.show();
        }
    }

    private void initListeners() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        eventsList.setLayoutManager(layoutManager);
        eventsList.setAdapter(viewModel.getEventListAdapter());
        //Setup infinite scrolling
        lazyListener = new LazyLoadRecyclerViewListener(layoutManager) {
            @Override
            public void onLoadMore() {
                viewModel.loadEventsBatch();
            }
        };
        eventsList.addOnScrollListener(lazyListener);

        swipeLayout.setOnRefreshListener(() -> {
            viewModel.refreshEvents();
        });

        addEventButton.setOnClickListener(v -> {
            final Intent intent = new Intent(mContext, CreateEventActivity.class);
            startActivityForResult(intent, CREATE_EVENT_REQUEST);
        });

        attachClickListenerToImageAdapter();
    }

    private void attachClickListenerToImageAdapter() {
        addSub(viewModel.getEventListAdapter()
                .getClickStream()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((e) -> {
                            Intent intent = new Intent(getActivity(), EventDetailActivity.class);
                            intent.putExtra(EVENT_NAME, e.getEventPreview().getName());
                            intent.putExtra(EVENT_DESCRIPTION, e.getEventPreview().getDescription());
                            intent.putExtra(EVENT_IMAGE, "https://miro.medium.com/max/1200/1*mk1-6aYaf_Bes1E3Imhc0A.jpeg");
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                                    e.getImageView(), ViewCompat.getTransitionName(e.getImageView()));
                            startActivity(intent, options.toBundle());
                        },
                        (e) -> Log.e(TAG, "Error", e))
        );
    }

    private void setProgressBarColor() {
        Drawable indeterminateDrawable = progressBar.getIndeterminateDrawable();
        if (indeterminateDrawable != null) {
            indeterminateDrawable.setColorFilter(ContextCompat.getColor(mContext, R.color.secondary_text), PorterDuff.Mode.SRC_IN);
        }
        Drawable progressDrawable = progressBar.getProgressDrawable();
        if (progressDrawable != null) {
            progressDrawable.setColorFilter(ContextCompat.getColor(mContext, R.color.secondary_text), PorterDuff.Mode.SRC_IN);
        }
    }

    private void initSubscriptions() {
        viewModel.getState().observe(getViewLifecycleOwner(), this::update);
    }

    private void update(FragmentState state) {
        switch (state.getStatus()) {
            case LOADING:
                if (!swipeLayout.isRefreshing()) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                break;
            case COMPLETE:
                progressBar.setVisibility(View.GONE);
                break;
            case SUCCESS:
                if (eventsList.getAdapter().getItemCount() == 0) {
                    Toast.makeText(mContext, R.string.no_events, Toast.LENGTH_LONG).show();
                }
                finishLoading();
                break;
            case ERROR:
                Log.e(TAG, "Something went wrong", state.getError());
                finishLoading();
                break;
        }
    }

    private void finishLoading() {
        lazyListener.setLoading(false);
        progressBar.setVisibility(View.GONE);
        swipeLayout.setRefreshing(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_EVENT_REQUEST && resultCode == Activity.RESULT_OK) {
            String locationUri = data.getStringExtra(CREATED_EVENT_URI);
            viewModel.loadEvent(locationUri);
        }
    }
}
