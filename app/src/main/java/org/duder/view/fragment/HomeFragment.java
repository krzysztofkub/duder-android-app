package org.duder.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import org.duder.R;
import org.duder.util.UserSession;
import org.duder.viewModel.HomeViewModel;

import static android.content.Context.MODE_PRIVATE;
import static org.duder.util.UserSession.PREF_NAME;

public class HomeFragment extends BaseFragment {

    private HomeViewModel homeViewModel;
    private CircularImageView profileImage;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        profileImage = root.findViewById(R.id.profile_image);
        setProfileImage();
        return root;
    }

    private void setProfileImage() {
        String imageUrl = getActivity().getSharedPreferences(PREF_NAME, MODE_PRIVATE).getString(UserSession.IMAGE_URL, "");
        if (!imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(profileImage);
        }
    }
}
