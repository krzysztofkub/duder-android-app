package org.duder.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.duder.dto.event.EventLoadingMode;
import org.duder.dto.event.EventPreview;
import org.duder.service.ApiClient;
import org.duder.view.adapter.EventListAdapter;
import org.duder.viewModel.state.FragmentState;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public abstract class EventViewModel extends RecyclerViewModel {

    private static final int EVENT_BATCH_SIZE = 10;
    private static final String TAG = EventViewModel.class.getSimpleName();
    private EventListAdapter eventListAdapter = new EventListAdapter(new ArrayList<>());

    EventViewModel(@NonNull Application application) {
        super(application);
    }

    void loadItemsBatch(boolean clearEventsBefore, EventLoadingMode loadingMode) {
        state.postValue(FragmentState.loading());
        addSub(
                apiClient.getEvents(currentPage, EVENT_BATCH_SIZE, loadingMode, token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            Log.i(TAG, "Fetched " + response.size() + " events");
                            if (clearEventsBefore) {
                                eventListAdapter.clearEvents();
                            }
                            eventListAdapter.addEvents(response);
                            state.postValue(FragmentState.success());
                        }, error -> {
                            Log.e(TAG, error.getMessage(), error);
                            state.postValue(FragmentState.error(error));
                        }));
        currentPage++;
    }

    public void loadEvent(String locationUri) {
        state.postValue(FragmentState.loading());
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

    protected void refreshItems(EventLoadingMode loadingMode) {
        currentPage = 0;
        loadItemsBatch(true, loadingMode);
    }

    @Override
    public RecyclerView.Adapter getListAdapter() {
        return eventListAdapter;
    }
}
