package org.duder.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;

import org.duder.R;
import org.duder.view.activity.CreateEventActivity;
import org.duder.view.activity.RegistrationActivity;
import org.duder.viewModel.HomeViewModel;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =  ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final Button button = root.findViewById(R.id.event_button);
        button.setOnClickListener(v -> createEvent());
        return root;
    }

    private void createEvent() {
        final Intent registrationIntent = new Intent(getContext(), CreateEventActivity.class);
        startActivity(registrationIntent);
    }
}
