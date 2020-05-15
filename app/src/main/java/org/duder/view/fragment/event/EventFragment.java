package org.duder.view.fragment.event;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.duder.R;
import org.duder.dto.event.EventLoadingMode;
import org.duder.view.activity.CreateEventActivity;
import org.duder.view.activity.EventDetailActivity;
import org.duder.view.adapter.EventListAdapter;
import org.duder.view.fragment.RecyclerFragment;
import org.duder.viewModel.EventViewModel;
import org.duder.viewModel.OwnRecyclerViewModel;
import org.duder.viewModel.PrivateRecyclerViewModel;
import org.duder.viewModel.PublicRecyclerViewModel;
import org.duder.viewModel.state.FragmentState;

import io.reactivex.android.schedulers.AndroidSchedulers;

import static org.duder.util.Const.CREATED_EVENT_URI;

public class EventFragment extends RecyclerFragment {

    public static final String EVENT_NAME = "EVENT_NAME";
    public static final String EVENT_DESCRIPTION = "EVENT_DESCRIPTION";
    public static final String EVENT_IMAGE = "EVENT_IMAGE";
    private static final String TAG = EventFragment.class.getSimpleName();
    private static final int CREATE_EVENT_REQUEST = 1;
    private final EventLoadingMode loadingMode;
    private FloatingActionButton addEventButton;


    public EventFragment(EventLoadingMode loadingMode) {
        this.loadingMode = loadingMode;
    }

    @Override
    public View onCreateRecyclerView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_events, container, false);
        progressBar = root.findViewById(R.id.progress_spinner);
        list = root.findViewById(R.id.events_list);
        addEventButton = root.findViewById(R.id.btn_add_event);
        swipeLayout = root.findViewById(R.id.swipe_layout);

        init();
        viewModel.loadItemsOnInit();
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
                viewModel = ViewModelProviders.of(getActivity()).get(OwnRecyclerViewModel.class);
                break;
            case PRIVATE:
                viewModel = ViewModelProviders.of(getActivity()).get(PrivateRecyclerViewModel.class);
                break;
            case PUBLIC:
                viewModel = ViewModelProviders.of(getActivity()).get(PublicRecyclerViewModel.class);
                break;
        }
    }

    private void initLayout() {
        if (loadingMode == EventLoadingMode.OWN) {
            addEventButton.show();
        }
    }

    private void initListeners() {
        addEventButton.setOnClickListener(v -> {
            final Intent intent = new Intent(mContext, CreateEventActivity.class);
            startActivityForResult(intent, CREATE_EVENT_REQUEST);
        });

        attachClickListenerToImageAdapter();
    }

    private void attachClickListenerToImageAdapter() {
        addSub(((EventListAdapter) viewModel.getListAdapter())
                .getClickStream()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(e -> {
                            Intent intent = new Intent(getActivity(), EventDetailActivity.class);
                            intent.putExtra(EVENT_NAME, e.getEventPreview().getName());
                            intent.putExtra(EVENT_DESCRIPTION, e.getEventPreview().getDescription());
                            String imageUrl = "https://miro.medium.com/max/1200/1*mk1-6aYaf_Bes1E3Imhc0A.jpeg";
                            if (e.getEventPreview().getImageUrl() != null) {
                                imageUrl = e.getEventPreview().getImageUrl();
                            }
                            intent.putExtra(EVENT_IMAGE, imageUrl);
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                                    e.getImageView(), ViewCompat.getTransitionName(e.getImageView()));
                            startActivity(intent, options.toBundle());
                        },
                        e -> Log.e(TAG, "Error", e))
        );
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
                if (list.getAdapter().getItemCount() == 0) {
                    Toast.makeText(mContext, R.string.no_events, Toast.LENGTH_SHORT).show();
                }
                finishLoading();
                break;
            case ERROR:
                Log.e(TAG, "Something went wrong", state.getError());
                finishLoading();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_EVENT_REQUEST && resultCode == Activity.RESULT_OK) {
            String locationUri = data.getStringExtra(CREATED_EVENT_URI);
            ((EventViewModel) viewModel).loadEvent(locationUri);
        }
    }
}
