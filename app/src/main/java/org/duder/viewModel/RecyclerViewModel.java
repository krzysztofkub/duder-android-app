package org.duder.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import org.duder.service.ApiClient;
import org.duder.viewModel.state.FragmentState;

import static android.content.Context.MODE_PRIVATE;
import static org.duder.util.UserSession.PREF_NAME;
import static org.duder.util.UserSession.TOKEN;

public abstract class RecyclerViewModel extends AbstractViewModel {

    protected MutableLiveData<FragmentState> state = new MutableLiveData<>();
    String token = getApplication().getSharedPreferences(PREF_NAME, MODE_PRIVATE).getString(TOKEN, "");
    ApiClient apiClient = ApiClient.getApiClient();
    int currentPage = 0;

    RecyclerViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<FragmentState> getState() {
        return state;
    }

    public abstract void loadItemsBatch();

    public abstract void loadItemsOnInit();

    public abstract void refreshItems();

    public abstract RecyclerView.Adapter getListAdapter();
}