package org.duder.view.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import org.duder.R;
import org.duder.databinding.ActivityCreateEventBinding;
import org.duder.model.Event;
import org.duder.util.FileUtils;
import org.duder.viewModel.CreateEventViewModel;
import org.duder.viewModel.state.FragmentState;

import java.io.IOException;
import java.util.Calendar;

import static org.duder.util.BusyIndicator.hideBusyIndicator;
import static org.duder.util.BusyIndicator.showBusyIndicator;
import static org.duder.util.Const.CREATED_EVENT_URI;

public class CreateEventActivity extends BaseActivity {

    private static final String TAG = CreateEventActivity.class.getSimpleName();
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private ActivityCreateEventBinding binding;
    private PopupWindow busyIndicator;
    private Bitmap eventImage;
    private CreateEventViewModel createEventViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_event);
        init();
        createEventViewModel.loadHobbies();
        setTitle("Create Event");
    }

    private void init() {
        initViewModel();
        initSubscriptions();
        initListeners();
    }

    private void initViewModel() {
        createEventViewModel = ViewModelProviders.of(this).get(CreateEventViewModel.class);
    }

    private void initSubscriptions() {
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.CENTER);
        binding.hobbies.setLayoutManager(layoutManager);
        binding.hobbies.setAdapter(createEventViewModel.getHobbyAdapter());
        createEventViewModel.getState().observe(this, this::update);
    }

    private void initListeners() {
        binding.dateTxt.setOnClickListener(v -> onDateClicked());
        binding.timeTxt.setOnClickListener(v -> onTimeClicked());
        binding.createEventBtn.setOnClickListener(v -> onCreateEventClicked());
        binding.addImageView.setOnClickListener(v -> onAddImageClicked());
    }

    private void update(FragmentState state) {
        switch (state.getStatus()) {
            case LOADING:
                busyIndicator = showBusyIndicator(this, busyIndicator);
                break;
            case COMPLETE: //fetched hobbies list
                hideBusyIndicator(busyIndicator);
                break;
            case SUCCESS: //created event
                hideBusyIndicator(busyIndicator);
                Toast.makeText(this, "Created event", Toast.LENGTH_LONG).show();
                Intent data = new Intent();
                data.putExtra(CREATED_EVENT_URI, (String) state.getData());
                setResult(RESULT_OK, data);
                finish();
                break;
            case ERROR:
                hideBusyIndicator(busyIndicator);
                Log.e(TAG, state.getError().getMessage());
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
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

    private void onDateClicked() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    String text = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                    binding.dateTxt.setText(text);
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void onTimeClicked() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    String text = hourOfDay + ":" + minute;
                    binding.timeTxt.setText(text);
                }, mHour, mMinute, true);
        timePickerDialog.show();
    }

    private void onCreateEventClicked() {
        boolean hasErrors = false;
        String name = binding.eventName.getText().toString();
        if (name.trim().isEmpty()) {
            binding.eventName.setError("Give it a name Bro?");
            hasErrors = true;
        }
        String desc = binding.eventDescription.getText().toString();
        if (desc.trim().isEmpty()) {
            binding.eventDescription.setError("WHAT YOU WANT TO DO?");
            hasErrors = true;
        }
        String date = binding.dateTxt.getText().toString();
        if (date.trim().isEmpty()) {
            binding.dateTxt.setError("dude... when?");
            hasErrors = true;
        }
        String time = binding.timeTxt.getText().toString();
        if (time.trim().isEmpty()) {
            binding.timeTxt.setError("what time?");
            hasErrors = true;
        }
        if (createEventViewModel.getHobbyAdapter().getSelectedHobbies().isEmpty()) {
            Toast.makeText(this, "Please pick a category", Toast.LENGTH_LONG).show();
            hasErrors = true;
        }
        if (hasErrors) {
            return;
        }
        Event event = new Event(
                name, desc, date, time,
                binding.privateCheckbox.isChecked(),
                FileUtils.mapBitmapToFile(this, eventImage)
        );
        createEventViewModel.createEvent(event);
    }

    private void onAddImageClicked() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permissions, PERMISSION_CODE);
        } else {
            pickImageFromGallery();
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery();
                } else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK
                && requestCode == IMAGE_PICK_CODE
                && data != null) {
            try {
                eventImage = FileUtils.resizeImage(this, data.getData());
                binding.addImageView.setImageBitmap(eventImage);
            } catch (IOException e) {
                Log.e(TAG, "Error while adding image!", e);
                Toast.makeText(this, "Error while adding image!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
