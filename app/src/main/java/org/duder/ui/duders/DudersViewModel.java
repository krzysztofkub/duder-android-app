package org.duder.ui.duders;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

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