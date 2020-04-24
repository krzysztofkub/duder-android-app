package org.duder.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.duder.R;
import org.duder.model.user.Account;
import org.duder.service.ApiClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RegistrationActivity extends BaseActivity {

    private final static String TAG = "RegistrationActivity";

    private EditText txtLogin;
    private EditText txtNickname;
    private EditText txtPassword;
    private EditText txtConfirmPassword;
    private Button btnRegistration;
    private View viewRoot;

    private ApiClient apiClient;

    private void initializeFromR() {
        txtLogin = findViewById(R.id.registration_text_login);
        txtNickname = findViewById(R.id.registration_text_nickname);
        txtPassword = findViewById(R.id.registration_text_password);
        txtConfirmPassword = findViewById(R.id.registration_text_confirm_password);
        btnRegistration = findViewById(R.id.registration_button_registration);
        viewRoot = txtLogin.getRootView();
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        apiClient = ApiClient.getApiClient();

        this.initializeFromR();

        btnRegistration.setOnClickListener(this::onRegistrationClicked);
    }

    private void onRegistrationClicked(View view) {
        boolean hasErrors = false;
        final String login = txtLogin.getText().toString();
        if (login.trim().isEmpty()) {
            txtLogin.setError("Dude, gimme SOMETHING...");
            hasErrors = true;
        }
        final String nickname = txtNickname.getText().toString();
        if (login.trim().isEmpty()) {
            txtNickname.setError("Dude, gimme SOMETHING...");
            hasErrors = true;
        }
        final String password = txtPassword.getText().toString();
        if (password.trim().isEmpty()) {
            txtPassword.setError("No password? Seriously, Dude?");
            hasErrors = true;
        }
        final String confirmPassword = txtConfirmPassword.getText().toString();
        if (confirmPassword.trim().isEmpty()) {
            txtConfirmPassword.setError("Maybe confirm password?");
            hasErrors = true;
        }
        if (!confirmPassword.equals(password)) {
            txtPassword.setError("Oi, mate, those two must match");
            txtConfirmPassword.setError("Oi, mate, those two must match");
            hasErrors = true;
        }
        if (hasErrors) {
            return;
        }

        Account account = new Account(login, nickname, password);

        addSub(apiClient.registerUser(account)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    switch (response.code()) {
                        case 201:
                            final Intent intent = new Intent(this, LoginActivity.class);
                            intent.putExtra("login", login);
                            startActivity(intent);
                            Toast.makeText(this, "User registered", Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                        case 409:
                            Toast.makeText(this, "This login already exists", Toast.LENGTH_SHORT).show();
                            txtLogin.setError("There is already duder with that login");
                            break;
                        default:
                            Toast.makeText(this, "Unknown response", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }, error -> {
                    Toast.makeText(this, "Internet error", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "error during rest call", error);
                })
        );
    }
}
