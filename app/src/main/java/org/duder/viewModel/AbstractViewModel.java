package org.duder.viewModel;

import androidx.lifecycle.ViewModel;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

abstract class AbstractViewModel extends ViewModel {
    private CompositeDisposable disposables = new CompositeDisposable();

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
