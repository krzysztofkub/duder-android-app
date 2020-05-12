package org.duder.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;

import org.duder.dto.event.EventLoadingMode;

public class PrivateRecyclerViewModel extends EventViewModel {

    private static final EventLoadingMode loadingMode = EventLoadingMode.PRIVATE;

    public PrivateRecyclerViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    public void loadItemsBatch() {
        loadItemsBatch(false, loadingMode);
    }

    @Override
    public void loadItemsOnInit() {
        if (getListAdapter().getItemCount() == 0) {
            loadItemsBatch(false, loadingMode);
        }
    }

    @Override
    public void refreshItems() {
        refreshItems(loadingMode);
    }
}
