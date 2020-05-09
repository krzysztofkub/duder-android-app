package org.duder.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import org.duder.R;
import org.duder.viewModel.DudesViewModel;

public class DudesFragment extends BaseFragment {

    private DudesViewModel dudesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dudesViewModel = ViewModelProviders.of(this).get(DudesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dudes, container, false);
        final TextView textView = root.findViewById(R.id.text_dudes);
        dudesViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }
}
