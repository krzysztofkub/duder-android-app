package org.duder.view.fragment.dudes;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import org.duder.R;
import org.duder.view.fragment.RecyclerFragment;
import org.duder.viewModel.DudesViewModel;
import org.duder.viewModel.state.FragmentState;

public class DudesFragment extends RecyclerFragment {

    private static final String TAG = DudesFragment.class.getSimpleName();

    @Override
    public View onCreateRecyclerView(@NonNull LayoutInflater inflater,
                                     ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dudes, container, false);
        progressBar = root.findViewById(R.id.progress_spinner);
        list = root.findViewById(R.id.dudes_list);
        swipeLayout = root.findViewById(R.id.swipe_layout);
        init();
        viewModel.loadItemsOnInit();
        return root;
    }

    private void init() {
        initViewModel();
        initSubscriptions();
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(DudesViewModel.class);
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
}
