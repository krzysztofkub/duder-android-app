package org.duder.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;

import org.duder.R;
import org.duder.viewModel.DudersViewModel;

public class DuderFragment extends BaseFragment {

    private DudersViewModel dudersViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dudersViewModel = ViewModelProviders.of(this).get(DudersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_duders, container, false);
        final TextView textView = root.findViewById(R.id.text_duders);
        dudersViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }
}
