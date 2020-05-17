package org.duder.view.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.databinding.DataBindingUtil;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.duder.R;
import org.duder.databinding.ActivityEventDetailBinding;

import static org.duder.view.fragment.event.EventFragment.EVENT_DESCRIPTION;
import static org.duder.view.fragment.event.EventFragment.EVENT_IMAGE;
import static org.duder.view.fragment.event.EventFragment.EVENT_NAME;


public class EventDetailActivity extends BaseActivity {

    ActivityEventDetailBinding binding;
    private String imageUrl;
    private String title;
    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_detail);
        Bundle extras = this.getIntent().getExtras();
        if (extras == null) {
            finish();
            return;
        }
        imageUrl = extras.getString(EVENT_IMAGE, "");
        title = extras.getString(EVENT_NAME, "");
        description = extras.getString(EVENT_DESCRIPTION, "");
        init();
        postponeEnterTransition();
    }

    private void init() {
        binding.titleText.setText(title);
        binding.descriptionText.setText(description);
        if (!imageUrl.isEmpty()) {
            loadImage();
        }
    }

    private void loadImage() {
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.ic_image_24dp)
                .into(binding.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        scheduleTransition(binding.imageView);
                    }

                    @Override
                    public void onError(Exception error) {
                        Log.e("HI", "HI", error);
                    }
                });
    }

    private void scheduleTransition(View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                startPostponedEnterTransition();
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
