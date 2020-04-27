package org.duder.viewModel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import org.duder.dto.event.CreateEvent;
import org.duder.service.ApiClient;
import org.duder.util.UserSession;
import org.duder.view.adapter.HobbyCategoriesAdapter;
import org.duder.viewModel.state.FragmentState;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Headers;

public class CreateEventViewModel extends AbstractViewModel {
    private static final String TAG = CreateEventViewModel.class.getSimpleName();
    private MutableLiveData<FragmentState> state = new MutableLiveData<>();
    private HobbyCategoriesAdapter hobbyAdapter = new HobbyCategoriesAdapter(new ArrayList<>());

    public void loadHobbies() {
        state.postValue(FragmentState.loading());
        addSub(ApiClient.getApiClient().getHobbies(UserSession.getUserSession().getLoggedAccount().getSessionToken())
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

    public void createEvent(CreateEvent event) {
        addSub(ApiClient.getApiClient().createEvent(event, UserSession.getUserSession().getLoggedAccount().getSessionToken())
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(r -> state.postValue(FragmentState.loading()))
                .subscribe(response -> {
                    if (response.isSuccessful()) {
                        Headers headers = response.headers();
                        state.postValue(FragmentState.success(headers.get("Location")));
                    } else {
                        Log.e(TAG, response.message());
                        state.postValue(FragmentState.error(new RuntimeException("Something went wrong: " + response.errorBody().string())));
                    }

                }, error -> {
                    Log.e(TAG, error.getMessage());
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
