package org.duder.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.duder.service.ApiClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TokenValidator extends AbstractViewModel {

    private MutableLiveData<Boolean> tokenValidation = new MutableLiveData<>();

    public TokenValidator(@NonNull Application application) {
        super(application);
    }

    public void validateToken(String accessToken) {
        addSub(ApiClient.getApiClient()
                .validate(accessToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isValid -> tokenValidation.postValue(isValid),
                        error -> {
                            tokenValidation.postValue(false);
                            Log.e("TokenValidator", "error during token validation", error);
                        })
        );
    }

    public LiveData<Boolean> getTokenValidation() {
        return tokenValidation;
    }
}
