package org.duder.view.fragment.dudes;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.button.MaterialButton;

import org.duder.R;
import org.duder.model.DudeInvitation;
import org.duder.model.DudeItem;
import org.duder.util.InviteButtonUtil;
import org.duder.view.adapter.DudeListAdapter;
import org.duder.view.fragment.RecyclerFragment;
import org.duder.viewModel.DudesViewModel;
import org.duder.viewModel.state.FragmentState;

import io.reactivex.android.schedulers.AndroidSchedulers;

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
        viewModel.getState().observe(getViewLifecycleOwner(), this::updateState);
        ((DudesViewModel) viewModel).getDudeInvitation().observe(getViewLifecycleOwner(), this::updateDude);
        attachListenerToAddDudeBtn();
    }

    private void attachListenerToAddDudeBtn() {
        addSub(
                ((DudeListAdapter) viewModel.getListAdapter())
                        .getClickStream()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::accept,
                                e -> Log.e(TAG, "Error", e))
        );
    }

    private void updateState(FragmentState state) {
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

    private void updateDude(DudeInvitation dudeInvitation) {
        MaterialButton button = (MaterialButton) dudeInvitation.getmInvFriendBtn();
        InviteButtonUtil.setInviteButtonProperties(mContext, dudeInvitation.getFriendshipStatus(), button);
        switch (dudeInvitation.getFriendshipStatus()) {
            case INVITATION_SENT:
                Toast.makeText(mContext, R.string.invitationSent, Toast.LENGTH_SHORT).show();
                break;
            case FRIENDS:
                Toast.makeText(mContext, R.string.dudeAdded, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void accept(DudeItem dudeItem) {
        ((DudesViewModel) viewModel).inviteDude(dudeItem);
    }
}
