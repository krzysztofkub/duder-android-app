package org.duder.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;

import org.duder.dto.event.EventLoadingMode;

public class OwnEventViewModel extends EventViewModel {

    private static final EventLoadingMode loadingMode = EventLoadingMode.OWN;

    public OwnEventViewModel(@NonNull Application application) {
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
