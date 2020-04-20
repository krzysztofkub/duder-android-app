package org.duder.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.duder.R;
import org.duder.view.activity.CreateEventActivity;
import org.duder.view.adapter.listener.LazyLoadRecyclerViewListener;
import org.duder.viewModel.EventViewModel;
import org.duder.viewModel.state.FragmentState;

public class EventFragment extends BaseFragment {

    private static final String TAG = EventFragment.class.getSimpleName();

    private EventViewModel viewModel;
    private ProgressBar progressBar;
    private RecyclerView eventsList;
    private FloatingActionButton addEventButton;
    private LazyLoadRecyclerViewListener lazyListener;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_events, container, false);
        progressBar = root.findViewById(R.id.progress_spinner);
        eventsList = root.findViewById(R.id.events_list);
        addEventButton = root.findViewById(R.id.btn_add_event);
        init();
        viewModel.loadMoreEvents();
        return root;
    }

    private void init() {
        initViewModel();
        initLayout();
        initSubscriptions();
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(EventViewModel.class);
    }

    private void initLayout() {
        setProgressBarColor();
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        eventsList.setLayoutManager(layoutManager);
        eventsList.setAdapter(viewModel.getEventPostAdapter());

        //Setup infinite scrolling
        lazyListener = new LazyLoadRecyclerViewListener(layoutManager) {
            @Override
            public void onLoadMore() {
                viewModel.loadMoreEvents();
            }
        };
        eventsList.addOnScrollListener(lazyListener);
        addEventButton.setOnClickListener(v -> {
            final Intent registrationIntent = new Intent(getContext(), CreateEventActivity.class);
            startActivity(registrationIntent);
        });
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
        viewModel.getState().observe(this, this::update);
    }

    private void update(FragmentState state) {
        switch (state.getStatus()) {
            case LOADING:
                progressBar.setVisibility(View.VISIBLE);
                break;
            case COMPLETE:
                progressBar.setVisibility(View.GONE);
                break;
            case SUCCESS:
                if (eventsList.getAdapter().getItemCount() == 0) {
                    Toast.makeText(mContext, R.string.no_events, Toast.LENGTH_LONG).show();
                }
                lazyListener.setWasOnBottom(false);
                progressBar.setVisibility(View.GONE);
                break;
            case ERROR:
                Log.e(TAG, "Something went wrong", state.getError());
        }
    }
}
