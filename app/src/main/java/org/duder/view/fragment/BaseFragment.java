package org.duder.view.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseFragment extends Fragment {
    protected Context mContext;
    private CompositeDisposable disposables;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
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
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        super.onDestroy();
        if (!disposables.isDisposed()) disposables.dispose();
    }
}