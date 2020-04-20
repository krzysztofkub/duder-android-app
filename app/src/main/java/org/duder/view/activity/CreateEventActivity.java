package org.duder.view.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.duder.R;
import org.duder.model.event.Event;
import org.duder.service.ApiClient;
import org.duder.util.UserSession;
import org.duder.viewModel.HobbyViewModel;
import org.duder.viewModel.state.FragmentState;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CreateEventActivity extends BaseActivity {

    private static final String TAG = CreateEventActivity.class.getSimpleName();

    private Button btnDatePicker;
    private Button btnTimePicker;
    private Button btnCreateEvent;
    private TextView txtDate;
    private TextView txtTime;
    private TextView txtName;

    private HobbyViewModel viewModel;
    private RecyclerView hobbies;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        initLayout();
        init();
        viewModel.loadHobbies();
    }

    private void init() {
        initViewModel();
        initLayout();
        initSubscriptions();
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(HobbyViewModel.class);
    }

    private void initLayout() {
        btnDatePicker = findViewById(R.id.btn_date);
        btnTimePicker = findViewById(R.id.btn_time);
        btnCreateEvent = findViewById(R.id.event_create_button);
        txtDate = findViewById(R.id.in_date);
        txtTime = findViewById(R.id.in_time);
        txtName = findViewById(R.id.event_name);
        progressBar = findViewById(R.id.progress_spinner);

        hobbies = findViewById(R.id.hobby_list);
    }

    private void initSubscriptions() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        hobbies.setLayoutManager(layoutManager);
        hobbies.setAdapter(viewModel.getHobbyAdapter());

        btnDatePicker.setOnClickListener(v -> onDateClicked());
        btnTimePicker.setOnClickListener(v -> onTimeClicked());
        btnCreateEvent.setOnClickListener(v -> onCreateEventClicked());

        viewModel.getState().observe(this, this::update);
    }

    private void update(FragmentState state) {
        switch (state.getStatus()) {
            case LOADING:
                progressBar.setVisibility(View.VISIBLE);
                break;
            case COMPLETE: //fetched hobbies list
                progressBar.setVisibility(View.GONE);
                break;
            case SUCCESS: //created event
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Created event", Toast.LENGTH_LONG).show();
                finish();
                break;
            case ERROR:
                Log.e(TAG, state.getError().getMessage());
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }

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
                    txtDate.setText(text);
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
                    txtTime.setText(text);
                }, mHour, mMinute, true);
        timePickerDialog.show();
    }

    private void onCreateEventClicked() {
        boolean hasErrors = false;
        String name = txtName.getText().toString();
        if (name.trim().isEmpty()) {
            txtName.setError("WHAT YOU WANT TO DO?");
            hasErrors = true;
        }
        String date = txtDate.getText().toString();
        if (date.trim().isEmpty()) {
            txtDate.setError("dude... when?");
            hasErrors = true;
        }
        String time = txtTime.getText().toString();
        if (time.trim().isEmpty()) {
            txtTime.setError("what time?");
            hasErrors = true;
        }
        if (HobbyViewModel.hobbiesSelected.size() == 0) {
            Toast.makeText(this, "Please pick a category", Toast.LENGTH_LONG).show();
            hasErrors = true;
        }
        if (hasErrors) {
            return;
        }
        String[] dateParts = date.split("-");
        String[] timeParts = time.split(":");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(dateParts[2]), Integer.parseInt(dateParts[1]) - 1, Integer.parseInt(dateParts[0]), Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]));
        Event event = new Event(name, HobbyViewModel.hobbiesSelected, calendar.getTimeInMillis());
        viewModel.createEvent(event);
    }
}
