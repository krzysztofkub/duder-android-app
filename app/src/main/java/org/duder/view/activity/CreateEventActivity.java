package org.duder.view.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import org.duder.R;
import org.duder.model.event.Event;
import org.duder.viewModel.CreateEventViewModel;
import org.duder.viewModel.state.FragmentState;

import java.util.Calendar;

import static org.duder.util.Const.CREATED_EVENT_URI;

public class CreateEventActivity extends BaseActivity {

    private static final String TAG = CreateEventActivity.class.getSimpleName();

    private TextView txtDate;
    private TextView txtTime;
    private TextView txtName;
    private TextView txtDesc;
    private RecyclerView hobbies;
    private ProgressBar progressBar;
    private RelativeLayout createEventForm;

    private CreateEventViewModel createEventViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        init();
        createEventViewModel.loadHobbies();
    }

    private void init() {
        initViewModel();
        initLayout();
        initSubscriptions();
        initListeners();
    }

    private void initViewModel() {
        createEventViewModel = ViewModelProviders.of(this).get(CreateEventViewModel.class);
    }

    private void initLayout() {
        txtDate = findViewById(R.id.in_date);
        txtTime = findViewById(R.id.in_time);
        txtName = findViewById(R.id.event_name);
        txtDesc = findViewById(R.id.event_description);
        progressBar = findViewById(R.id.progress_spinner);
        hobbies = findViewById(R.id.hobby_list);
        createEventForm = findViewById(R.id.layout_create_event_form);
        createEventForm.setVisibility(View.GONE);
        setTitle("Create Event");
    }

    private void initSubscriptions() {
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.CENTER);
        hobbies.setLayoutManager(layoutManager);
        hobbies.setAdapter(createEventViewModel.getHobbyAdapter());

        createEventViewModel.getState().observe(this, this::update);
    }

    private void initListeners() {
        txtDate.setOnClickListener(v -> onDateClicked());
        txtTime.setOnClickListener(v -> onTimeClicked());
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(txtDesc, InputMethodManager.SHOW_IMPLICIT);
    }

    private void update(FragmentState state) {
        switch (state.getStatus()) {
            case LOADING:
                progressBar.setVisibility(View.VISIBLE);
                break;
            case COMPLETE: //fetched hobbies list
                progressBar.setVisibility(View.GONE);
                createEventForm.setVisibility(View.VISIBLE);
                break;
            case SUCCESS: //created event
                progressBar.setVisibility(View.GONE);
                createEventForm.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Created event", Toast.LENGTH_LONG).show();
                Intent data = new Intent();
                data.putExtra(CREATED_EVENT_URI, (String) state.getData());
                setResult(RESULT_OK, data);
                finish();
                break;
            case ERROR:
                Log.e(TAG, state.getError().getMessage());
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_save:
                onCreateEventClicked();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_nav_menu_with_save, menu);
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
            txtName.setError("Give it a name Bro?");
            hasErrors = true;
        }
        String desc = txtDesc.getText().toString();
        if (desc.trim().isEmpty()) {
            txtDesc.setError("WHAT YOU WANT TO DO?");
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
        if (createEventViewModel.getHobbyAdapter().getSelectedHobbies().size() == 0) {
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
        Event event = new Event(name, desc, createEventViewModel.getHobbyAdapter().getSelectedHobbies(), calendar.getTimeInMillis());
        createEventViewModel.createEvent(event);
    }
}
