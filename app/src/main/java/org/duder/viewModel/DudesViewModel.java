package org.duder.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DudesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DudesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dudes fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}