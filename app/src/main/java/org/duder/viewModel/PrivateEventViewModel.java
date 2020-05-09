package org.duder.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;

import org.duder.dto.event.EventLoadingMode;

public class PrivateEventViewModel extends EventViewModel {

    private static final EventLoadingMode loadingMode = EventLoadingMode.PRIVATE;

    public PrivateEventViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    public void loadEventsBatch() {
        loadEventsBatch(false, loadingMode);
    }

    @Override
    public void loadEventsOnInit() {
        if (getEventListAdapter().getItemCount() == 0) {
            loadEventsBatch(false, loadingMode);
        }
    }

    @Override
    public void refreshEvents() {
        refreshEvents(loadingMode);
    }
}
