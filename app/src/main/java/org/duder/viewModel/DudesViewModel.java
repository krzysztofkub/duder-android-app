package org.duder.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import org.duder.model.DudeInvitation;
import org.duder.model.DudeItem;
import org.duder.service.ApiClient;
import org.duder.view.adapter.DudeListAdapter;
import org.duder.viewModel.state.FragmentState;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DudesViewModel extends RecyclerViewModel {

    private static final int DUDES_BATCH_SIZE = 25;
    private static final String TAG = DudesViewModel.class.getSimpleName();
    private DudeListAdapter dudeListAdapter = new DudeListAdapter(getApplication().getApplicationContext(), new ArrayList<>());
    protected MutableLiveData<DudeInvitation> dudeInvitation = new MutableLiveData<>();

    public DudesViewModel(@NonNull Application application) {
        super(application);
    }

    void loadItemsBatch(boolean clearEventsBefore) {
        state.postValue(FragmentState.loading());
        addSub(
                ApiClient.getApiClient().getDudes(currentPage, DUDES_BATCH_SIZE, token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            Log.i(TAG, "Fetched " + response.size() + " dudes");
                            if (clearEventsBefore) {
                                dudeListAdapter.clearItems();
                            }
                            dudeListAdapter.addDudes(response);
                            state.postValue(FragmentState.success());
                        }, error -> {
                            Log.e(TAG, error.getMessage(), error);
                            state.postValue(FragmentState.error(error));
                        }));
        currentPage++;
    }

    @Override
    public void loadItemsBatch() {
        state.postValue(FragmentState.loading());
        loadItemsBatch(false);
    }

    @Override
    public void loadItemsOnInit() {
        if (getListAdapter().getItemCount() == 0) {
            loadItemsBatch(false);
        }
    }

    @Override
    public void refreshItems() {
        currentPage = 0;
        dudeListAdapter.clearItems();
        loadItemsBatch(true);
    }

    @Override
    public RecyclerView.Adapter getListAdapter() {
        return dudeListAdapter;
    }

    public void inviteDude(DudeItem dudeItem) {
        state.postValue(FragmentState.loading());
        addSub(
                ApiClient.getApiClient().inviteDude(dudeItem.getDude().getId().toString(), token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            Log.i(TAG, "Invite response " + response);
                            DudeInvitation invitation = new DudeInvitation(response, dudeItem.getmInviteFriendBtn());
                            dudeInvitation.postValue(invitation);
                            state.postValue(FragmentState.success());
                        }, error -> {
                            Log.e(TAG, error.getMessage(), error);
                            state.postValue(FragmentState.error(error));
                        })
        );
    }

    public MutableLiveData<DudeInvitation> getDudeInvitation() {
        return dudeInvitation;
    }
}