package org.duder.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import org.duder.R;
import org.duder.websocket.WebSocketService;
import org.duder.websocket.stomp.dto.ConnectionResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private final static String TAG = "LoginActivity";

    private EditText txtLogin;
    private EditText txtPassword;
    private Button btnLogin;
    private Button btnSignUp;
    private Button btnForgotPassword;
    private View viewRoot;
    private PopupWindow busyIndicator;
    private Handler handler;
    private ExecutorService executor;

    private static final int LOGIN_SUCCEEDED = 1;
    private static final int BAD_CREDENTIALS = 0;

    private void initializeFromR() {
        txtLogin = findViewById(R.id.login_text_login);
        txtPassword = findViewById(R.id.login_text_password);
        btnLogin = findViewById(R.id.login_button_login);
        btnSignUp = findViewById(R.id.login_button_sign_up);
        btnForgotPassword = findViewById(R.id.login_button_forgot_password);
        viewRoot = txtLogin.getRootView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.initializeFromR();

        String login = this.getIntent().getStringExtra("login");
        if (login != null) {
            txtLogin.setText(login);
        } else {
            txtLogin.setText("dude");
            txtPassword.setText("duderowsky");
        }

        btnLogin.setOnClickListener(this::onLoginClicked);
        btnSignUp.setOnClickListener(this::onSignUpClicked);
    }

    private void onSignUpClicked(View view) {
        final Intent registrationIntent = new Intent(this, RegistrationActivity.class);
        startActivity(registrationIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler = new LoginHandler();
        executor = Executors.newSingleThreadExecutor();
    }

    private void onLoginClicked(View view) {
        boolean hasErrors = false;
        final String login = txtLogin.getText().toString();
        if (login.trim().isEmpty()) {
            Log.e(TAG, "onLoginClicked - login empty");
            txtLogin.setError("Dude, gimme SOMETHING...");
            hasErrors = true;
        }
        final String password = txtPassword.getText().toString();
        if (password.trim().isEmpty()) {
            Log.e(TAG, "onLoginClicked - password empty");
            txtPassword.setError("No password? Seriously, Dude?");
            hasErrors = true;
        }
        if (hasErrors) {
            return;
        }
        showBusyIndicator();
        executor.submit(this::doLogin);
    }

    private void doLogin() {
        String login = txtLogin.getText().toString();
        String password = txtPassword.getText().toString();
        WebSocketService webSocketService = WebSocketService.getWebSocketService();
        CompletableFuture<ConnectionResponse> futureConnect = webSocketService.connect(login, password);

        futureConnect.thenAccept(response -> {
            final Message message = new Message();
            message.what = response.isBadCredentials() ? BAD_CREDENTIALS : LOGIN_SUCCEEDED;
            handler.sendMessage(message);
        });
    }

    private void showBusyIndicator() {
        final View activityView = getWindow().getDecorView();
        if (busyIndicator == null) {
            final int width = activityView.getWidth() - 20;
            final int height = activityView.getHeight() - 20;
            final Context context = getApplicationContext();
            final LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            final View popupView = inflater.inflate(R.layout.popup_busy, null);
            busyIndicator = new PopupWindow(popupView, width, height);
            busyIndicator.setElevation(10.0f);
        }
        busyIndicator.showAtLocation(activityView, Gravity.CENTER, 0, 0);
    }

    private void hideBusyIndicator() {
        if (busyIndicator != null && busyIndicator.isShowing()) {
            busyIndicator.dismiss();
        }
    }

    private void gotoMainActivity() {
        final String login = txtLogin.getText().toString();
        final Bundle bundle = new Bundle();
        bundle.putString("login", login);
        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideBusyIndicator();
    }

    private class LoginHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i(TAG, "Handler received msg: " + msg.what);
            hideBusyIndicator();
            switch (msg.what) {
                case BAD_CREDENTIALS:
                    Toast.makeText(LoginActivity.this, "Bad credentials", Toast.LENGTH_SHORT).show();
                    break;
                case LOGIN_SUCCEEDED:
                    gotoMainActivity();
                    break;
                default:
                    Log.e(TAG, "LoginHandler received unrecognized code: " + msg.what);
                    break;
            }
        }
    }
}
