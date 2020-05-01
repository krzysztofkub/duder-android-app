package org.duder.view.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

abstract class BaseActivity extends AppCompatActivity {
    private CompositeDisposable disposables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        initRx();
    }

    private void initRx() {
        disposables = new CompositeDisposable();
    }

    synchronized protected void addSub(Disposable disposable) {
        if (disposable == null) return;
        disposables.add(disposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!disposables.isDisposed()) disposables.dispose();
    }
}
