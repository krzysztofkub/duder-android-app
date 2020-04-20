package org.duder.viewModel;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import org.duder.model.event.Event;
import org.duder.service.ApiClient;
import org.duder.util.UserSession;
import org.duder.view.adapter.HobbyCategoriesAdapter;
import org.duder.viewModel.state.FragmentState;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class HobbyViewModel extends AbstractViewModel {
    private static final String TAG = HobbyViewModel.class.getSimpleName();
    private MutableLiveData<FragmentState> state = new MutableLiveData<>();
    private HobbyCategoriesAdapter hobbyAdapter = new HobbyCategoriesAdapter(new ArrayList<>());
    public static List<String> hobbiesSelected = new ArrayList<>();

    public void loadHobbies() {
        state.postValue(FragmentState.loading());
        addSub(ApiClient.getApiClient().getHobbies(UserSession.getUserSession().getAccount().getSessionToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            Log.i(TAG, "Fetched hobbies " + response);
                            updateAdapter(response);
                            state.postValue(FragmentState.complete());
                        },
                        error -> {
                            Log.e(TAG, error.getMessage(), error);
                            state.postValue(FragmentState.error(error));
                        })
        );
    }

    private void updateAdapter(List<String> hobbies) {
        hobbies = hobbies != null ? hobbies : new ArrayList<>();
        hobbyAdapter.setHobbies(hobbies);
        hobbyAdapter.notifyDataSetChanged();
    }

    public HobbyCategoriesAdapter getHobbyAdapter() {
        return hobbyAdapter;
    }

    public MutableLiveData<FragmentState> getState() {
        return state;
    }
}
