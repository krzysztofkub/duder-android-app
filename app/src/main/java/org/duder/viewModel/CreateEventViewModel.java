package org.duder.viewModel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import org.duder.dto.event.CreateEvent;
import org.duder.model.Event;
import org.duder.service.ApiClient;
import org.duder.util.UserSession;
import org.duder.view.adapter.HobbyCategoriesAdapter;
import org.duder.viewModel.state.FragmentState;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Headers;

import static android.content.Context.*;
import static org.duder.util.UserSession.*;

public class CreateEventViewModel extends AbstractViewModel {
    private static final String TAG = CreateEventViewModel.class.getSimpleName();
    private MutableLiveData<FragmentState> state = new MutableLiveData<>();
    private HobbyCategoriesAdapter hobbyAdapter = new HobbyCategoriesAdapter(new ArrayList<>());
    private SharedPreferences sharedPreferences = getApplication().getSharedPreferences(PREF_NAME, MODE_PRIVATE);

    public CreateEventViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadHobbies() {
        state.postValue(FragmentState.loading());
        String token = sharedPreferences.getString(TOKEN, "");
        addSub(ApiClient.getApiClient().getHobbies(token)
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

    public void createEvent(Event event) {
        CreateEvent createEvent = mapEventToCreateEventDto(event);
        String token = sharedPreferences.getString(TOKEN, "");
        addSub(ApiClient.getApiClient().createEvent(createEvent, token)
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

    private CreateEvent mapEventToCreateEventDto(Event event) {
        String[] dateParts = event.getDate().split("-");
        String[] timeParts = event.getTime().split(":");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(dateParts[2]), Integer.parseInt(dateParts[1]) - 1, Integer.parseInt(dateParts[0]), Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]));
        return CreateEvent
                .builder()
                .name(event.getName())
                .description(event.getDescription())
                .timestamp(calendar.getTimeInMillis())
                .hobbies(hobbyAdapter.getSelectedHobbies())
                .build();
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
