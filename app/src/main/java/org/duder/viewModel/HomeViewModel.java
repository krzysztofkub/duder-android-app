package org.duder.viewModel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.facebook.AccessToken;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
        init();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void init() {
        AccessToken token = AccessToken.getCurrentAccessToken();
        if (token!=null) {
            System.out.println(token);
        }
    }
}