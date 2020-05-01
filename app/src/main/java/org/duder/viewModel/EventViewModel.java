package org.duder.viewModel;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.facebook.share.Share;
import com.google.gson.Gson;

import org.duder.dto.event.EventPreview;
import org.duder.service.ApiClient;
import org.duder.util.UserSession;
import org.duder.view.adapter.EventListAdapter;
import org.duder.viewModel.state.FragmentState;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;
import static org.duder.util.UserSession.PREF_NAME;
import static org.duder.util.UserSession.TOKEN;

public class EventViewModel extends AbstractViewModel {

    private static final String TAG = EventViewModel.class.getSimpleName();
    private static final int GET_EVENT_NUMBER = 10;

    private SharedPreferences sharedPreferences = getApplication().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
    private MutableLiveData<FragmentState> state = new MutableLiveData<>();
    private EventListAdapter eventListAdapter = new EventListAdapter(new ArrayList<>());
    private int currentPage = 0;

    public EventViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadEventsBatch(boolean clearEventsBefore) {
        state.postValue(FragmentState.loading());
        String token = sharedPreferences.getString(TOKEN, "");
        addSub(
                ApiClient.getApiClient().getEvents(currentPage, GET_EVENT_NUMBER, token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                                    Log.i(TAG, "Fetched " + response.size() + " events");
                                    if (clearEventsBefore) {
                                        eventListAdapter.clearEvents();
                                    }
                                    eventListAdapter.addEvents(response);
                                    state.postValue(FragmentState.success());
                                },
                                error -> {
                                    Log.e(TAG, error.getMessage(), error);
                                    state.postValue(FragmentState.error(error));
                                })
        );
        currentPage++;
    }

    public void fetchAndAddNewEvent(String locationUri) {
        state.postValue(FragmentState.loading());
        String token = sharedPreferences.getString(TOKEN, "");
        addSub(
                ApiClient.getApiClient().getEvent(locationUri, token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                                    Log.i(TAG, "Fetched event " + response.body());
                                    if (response.code() == 200) {
                                        EventPreview event = new Gson().fromJson(response.body().string(), EventPreview.class);
                                        eventListAdapter.addEvent(event);
                                        state.postValue(FragmentState.success());
                                    } else {
                                        Log.e(TAG, "Cant fetch newly created event with location url = " + locationUri);
                                        state.postValue(FragmentState.complete());
                                    }
                                },
                                error -> {
                                    Log.e(TAG, error.getMessage(), error);
                                    state.postValue(FragmentState.error(error));
                                })
        );
    }

    public void refreshEvents() {
        currentPage = 0;
        loadEventsBatch(true);
    }

    public MutableLiveData<FragmentState> getState() {
        return state;
    }

    public EventListAdapter getEventListAdapter() {
        return eventListAdapter;
    }
}