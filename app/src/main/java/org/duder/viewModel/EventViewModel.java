package org.duder.viewModel;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import org.duder.service.ApiClient;
import org.duder.model.event.Event;
import org.duder.util.UserSession;
import org.duder.view.adapter.EventPostAdapter;
import org.duder.viewModel.state.FragmentState;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class EventViewModel extends AbstractViewModel {

    private static final String TAG = EventViewModel.class.getSimpleName();
    private static final int GET_EVENT_NUMBER = 10;

    private MutableLiveData<FragmentState> state = new MutableLiveData<>();
    private EventPostAdapter eventPostAdapter = new EventPostAdapter(new ArrayList<>());
    private int currentPage = 0;

    public void loadMoreEvents() {
        state.postValue(FragmentState.loading());
        addSub(
                ApiClient.getApiClient().getEvents(currentPage, GET_EVENT_NUMBER, UserSession.getUserSession().getAccount().getSessionToken())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                                    Log.i(TAG, "Fetched " + response.size() + " events");
                                    updateAdapter(response);
                                    state.postValue(FragmentState.success());
                                },
                                error -> {
                                    Log.e(TAG, error.getMessage(), error);
                                    state.postValue(FragmentState.error(error));
                                })
        );
        currentPage++;
    }

    private void updateAdapter(List<Event> data) {
        data = data != null ? data : new ArrayList<>();
        eventPostAdapter.getEvents().addAll(data);
        eventPostAdapter.notifyDataSetChanged();
    }

    public void createEvent(Event event) {
        addSub(ApiClient.getApiClient().createEvent(event, UserSession.getUserSession().getAccount().getSessionToken())
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(r -> state.postValue(FragmentState.loading()))
                .subscribe(response -> {
                    if (response.isSuccessful()) {
                        state.postValue(FragmentState.success());
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

    public MutableLiveData<FragmentState> getState() {
        return state;
    }

    public EventPostAdapter getEventPostAdapter() {
        return eventPostAdapter;
    }
}