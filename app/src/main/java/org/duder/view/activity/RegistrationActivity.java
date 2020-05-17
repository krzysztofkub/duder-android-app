package org.duder.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import org.duder.R;
import org.duder.databinding.ActivityRegistrationBinding;
import org.duder.dto.user.RegisterAccount;
import org.duder.service.ApiClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RegistrationActivity extends BaseActivity {
    private static final String TAG = "RegistrationActivity";
    ActivityRegistrationBinding binding;
    private ApiClient apiClient;

    private void initializeFromR() {
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration);
        apiClient = ApiClient.getApiClient();
        this.initializeFromR();
        binding.regBtn.setOnClickListener(this::onRegistrationClicked);
    }

    private void onRegistrationClicked(View view) {
        boolean hasErrors = false;
        final String login = binding.loginTxt.getText().toString();
        if (login.trim().isEmpty()) {
            binding.loginTxt.setError("Dude, gimme SOMETHING...");
            hasErrors = true;
        }
        final String nickname = binding.nicknameTxt.getText().toString();
        if (login.trim().isEmpty()) {
            binding.nicknameTxt.setError("Dude, gimme SOMETHING...");
            hasErrors = true;
        }
        final String password = binding.passwordTxt.getText().toString();
        if (password.trim().isEmpty()) {
            binding.passwordTxt.setError("No password? Seriously, Dude?");
            hasErrors = true;
        }
        final String confirmPassword = binding.confirmPasswordTxt.getText().toString();
        if (confirmPassword.trim().isEmpty()) {
            binding.confirmPasswordTxt.setError("Maybe confirm password?");
            hasErrors = true;
        }
        if (!confirmPassword.equals(password)) {
            binding.passwordTxt.setError("Oi, mate, those two must match");
            binding.confirmPasswordTxt.setError("Oi, mate, those two must match");
            hasErrors = true;
        }
        if (hasErrors) {
            return;
        }

        RegisterAccount account = new RegisterAccount(login, nickname, password);

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
                            binding.loginTxt.setError("There is already duder with that login");
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
