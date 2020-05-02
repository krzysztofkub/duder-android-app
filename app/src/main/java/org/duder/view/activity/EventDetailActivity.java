package org.duder.view.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.duder.R;

import static org.duder.view.fragment.EventFragment.EVENT_DESCRIPTION;
import static org.duder.view.fragment.EventFragment.EVENT_IMAGE;
import static org.duder.view.fragment.EventFragment.EVENT_NAME;


public class EventDetailActivity extends BaseActivity {

    private String imageUrl = "";
    private String title = "";
    private String description = "";

    private ImageView image_view;
    private TextView title_text;
    private TextView description_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);
        Bundle extras = this.getIntent().getExtras();
        imageUrl = extras.getString(EVENT_IMAGE, "");
        title = extras.getString(EVENT_NAME, "");
        description = extras.getString(EVENT_DESCRIPTION, "");
        init();
        postponeEnterTransition();
    }

    private void init() {
        image_view = findViewById(R.id.image_view);
        title_text = findViewById(R.id.title_text);
        description_text = findViewById(R.id.description_text);
        loadImage();
        passLayoutValues();
    }

    private void loadImage() {
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.ic_image_24dp)
                .into(image_view, new Callback() {
                    @Override
                    public void onSuccess() {
                        scheduleTransition(image_view);
                    }

                    @Override
                    public void onError(Exception error) {
                        Log.e("HI", "HI", error);
                    }
                });
    }

    private void passLayoutValues() {
        title_text.setText(title);
        description_text.setText(description);
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
