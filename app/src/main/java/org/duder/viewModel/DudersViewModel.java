package org.duder.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DudersViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DudersViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is duders fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}