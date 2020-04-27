package org.duder.viewModel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import org.duder.dto.event.EventPreview;
import org.duder.service.ApiClient;
import org.duder.util.UserSession;
import org.duder.view.adapter.EventPostAdapter;
import org.duder.viewModel.state.FragmentState;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class EventViewModel extends AbstractViewModel {

    private static final String TAG = EventViewModel.class.getSimpleName();
    private static final int GET_EVENT_NUMBER = 10;

    private MutableLiveData<FragmentState> state = new MutableLiveData<>();
    private EventPostAdapter eventPostAdapter = new EventPostAdapter(new ArrayList<>());
    private int currentPage = 0;

    public void loadEventsBatch(boolean clearEventsBefore) {
        state.postValue(FragmentState.loading());
        addSub(
                ApiClient.getApiClient().getEvents(currentPage, GET_EVENT_NUMBER, UserSession.getUserSession().getLoggedAccount().getSessionToken())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                                    Log.i(TAG, "Fetched " + response.size() + " events");
                                    if (clearEventsBefore) {
                                        eventPostAdapter.clearEvents();
                                    }
                                    eventPostAdapter.addEvents(response);
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
        addSub(
                ApiClient.getApiClient().getEvent(locationUri, UserSession.getUserSession().getLoggedAccount().getSessionToken())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                                    Log.i(TAG, "Fetched event " + response.body());
                                    if (response.code() == 200) {
                                        EventPreview event = new Gson().fromJson(response.body().string(), EventPreview.class);
                                        eventPostAdapter.addEvent(event);
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

    public EventPostAdapter getEventPostAdapter() {
        return eventPostAdapter;
    }
}