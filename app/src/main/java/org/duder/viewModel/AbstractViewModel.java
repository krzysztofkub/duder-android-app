package org.duder.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

abstract class AbstractViewModel extends AndroidViewModel {
    private CompositeDisposable disposables = new CompositeDisposable();

    public AbstractViewModel(@NonNull Application application) {
        super(application);
    }

    synchronized void addSub(Disposable disposable) {
        if (disposables != null) {
            disposables.add(disposable);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (!disposables.isDisposed()) {
            disposables.dispose();
        }
    }
}
