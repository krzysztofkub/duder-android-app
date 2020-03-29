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
import org.duder.websocket.WebSocketClientProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.LifecycleEvent;
import ua.naiksoftware.stomp.dto.StompHeader;

public class LoginActivity extends AppCompatActivity {

    private final static String TAG = "LoginActivity";

    private EditText        txtLogin;
    private EditText        txtPassword;
    private Button          btnLogin;
    private Button          btnSignUp;
    private Button          btnForgotPassword;
    private View            viewRoot;
    private PopupWindow     busyIndicator;
    private Handler         handler;
    private ExecutorService executor;

    private              Disposable connectDisposable;
    private static final int        LOGIN_SUCCEEDED = 1;
    private static final int        LOGIN_FAILED    = 0;

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
        // Resets launcher theme to base one - this needs to be called FIRST
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.initializeFromR();

        // TODO remove later - Im getting tired of filling the form all the time
        txtLogin.setText("dude");
        txtPassword.setText("duderowsky");
        btnLogin.setOnClickListener(this::onLoginClicked);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler = new LoginHandler();
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectDisposable != null) {
            connectDisposable.dispose();
        }

        // This caused some leaks if not called
        hideBusyIndicator();
    }

    private void onLoginClicked(View view) {
        if (view != btnLogin) {
            Log.e(TAG, "onLoginClicked attached not to login button, but: " + view);
            return;
        }

        // Initial validation - user must provide SOMETHING
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

        if (connectDisposable != null) {
            connectDisposable.dispose();
        }

        showBusyIndicator();

        // We will send stuff to the server, might take some time, show that we are busy doing stuff
        executor.submit(this::doLogin);
    }

    // TODO implement logic, this is called on separate thread
    private void doLogin() {

        final StompClient client = WebSocketClientProvider.getWebSocketClient();
        connectDisposable = client
                .lifecycle()
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    Message message = new Message();
                    if (event.getType() == LifecycleEvent.Type.OPENED) {
                        Log.i(TAG, "Login succeeded");
                        message.what = LOGIN_SUCCEEDED;
                    } else {
                        Log.w(TAG, "Login falied", event.getException());
                        message.what = LOGIN_FAILED;
                    }
                    handler.sendMessage(message);
                });

        final String login = txtLogin.getText().toString();
        final String password = txtPassword.getText().toString();

        final StompHeader loginHeader = new StompHeader("login", login);
        final StompHeader passwordHeader = new StompHeader("password", password);
        List<StompHeader> headers = new ArrayList<>();
        headers.add(loginHeader);
        headers.add(passwordHeader);

        client.connect(headers);
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

    private void gotoChat() {
        Log.i(TAG, "gotoChat");

        final String login = txtLogin.getText().toString();
        final Bundle bundle = new Bundle();
        bundle.putString("username", login); //sic!

        final Intent chatIntent = new Intent(LoginActivity.this, ChatActivity.class);
        chatIntent.putExtras(bundle);

        Log.i(TAG, "gotoChat - starting activity");
        startActivity(chatIntent);
        finish();
    }


    private class LoginHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i(TAG, "Handler received msg: " + msg.what);
            hideBusyIndicator();
            switch (msg.what) {
                case LOGIN_FAILED:
                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    break;
                case LOGIN_SUCCEEDED:
                    gotoChat();
                    break;
                default:
                    Log.e(TAG, "LoginHandler received unrecognized code: " + msg.what);
                    break;
            }
        }
    }
}
