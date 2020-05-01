package org.duder.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;

import org.duder.R;
import org.duder.dto.user.LoggedAccount;
import org.duder.dto.user.LoginResponse;
import org.duder.service.ApiClient;
import org.duder.util.UserSession;
import org.duder.websocket.WebSocketService;
import org.duder.websocket.stomp.dto.ConnectionResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static org.duder.util.UserSession.*;

public class LoginActivity extends BaseActivity {

    private final static String TAG = "LoginActivity";

    private EditText txtLogin;
    private EditText txtPassword;
    private Button btnLogin;
    private Button btnSignUp;
    private Button btnForgotPassword;
    private View viewRoot;
    private PopupWindow busyIndicator;
    private Handler handler;

    private LoggedAccount account;
    private ExecutorService executor;
    private LoginButton fbLoginButton;
    private CallbackManager callbackManager;

    private ApiClient apiClient = ApiClient.getApiClient();
    private SharedPreferences sharedPreferences;

    private static final int LOGIN_SUCCEEDED = 1;
    private static final int BAD_CREDENTIALS = 0;

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

        registerFacebookCallback();

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        if (UserSession.isLoggedIn(sharedPreferences)) {
            doLoginToWebsocket();
        }
    }

    private void initializeFromR() {
        txtLogin = findViewById(R.id.login_text_login);
        txtPassword = findViewById(R.id.login_text_password);
        btnLogin = findViewById(R.id.login_button_login);
        btnSignUp = findViewById(R.id.login_button_sign_up);
        btnForgotPassword = findViewById(R.id.login_button_forgot_password);
        fbLoginButton = findViewById(R.id.login_button_facebook);
        viewRoot = txtLogin.getRootView();
    }

    private void registerFacebookCallback() {
        fbLoginButton.setPermissions("email");
        callbackManager = CallbackManager.Factory.create();
        fbLoginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Single<Response<ResponseBody>> loginUserWithFb = apiClient.loginUserWithFb(loginResult.getAccessToken().getToken());
                        UserSession.saveProfileImageUrl(loginResult.getAccessToken().getUserId(), sharedPreferences);
                        account = new LoggedAccount();
                        doLoginToRest(loginUserWithFb);
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        System.out.println("cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        System.out.println("error");
                    }
                });
    }

    private void onSignUpClicked(View view) {
        final Intent registrationIntent = new Intent(this, RegistrationActivity.class);
        startActivity(registrationIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler = new LoginHandler();
        executor = Executors.newFixedThreadPool(2);
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
        account = LoggedAccount.builder()
                .login(txtLogin.getText().toString())
                .password(txtPassword.getText().toString())
                .build();
        Single<Response<ResponseBody>> loginRequest = apiClient.loginUser(account.getLogin(), account.getPassword());
        executor.submit(() -> doLoginToRest(loginRequest));
    }

    private void doLoginToRest(Single<Response<ResponseBody>> loginRequest) {
        addSub(loginRequest
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    switch (response.code()) {
                        case 200:
                            LoginResponse accountFromResponse = new Gson().fromJson(response.body().string(), LoginResponse.class);
                            account.setNickname(accountFromResponse.getNickname());
                            account.setSessionToken(accountFromResponse.getSessionToken());
                            storeUserSession(account, getSharedPreferences(PREF_NAME, MODE_PRIVATE));
                            doLoginToWebsocket();
                            break;
                        case 422:
                            hideBusyIndicator();
                            Toast.makeText(this, "Bad credentials", Toast.LENGTH_SHORT).show();
                            txtLogin.setError("Baaad login or password, try again DUuuuude");
                            break;
                        default:
                            hideBusyIndicator();
                            Toast.makeText(this, "Unknown response", Toast.LENGTH_SHORT).show();
                            break;
                    }
                })
        );
    }

    private void doLoginToWebsocket() {
        WebSocketService webSocketService = WebSocketService.getWebSocketService();
        String token = getSharedPreferences(PREF_NAME, MODE_PRIVATE).getString(TOKEN, "");
        CompletableFuture<ConnectionResponse> futureConnect = webSocketService.connect(token);
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
        final Intent intent = new Intent(this, MainActivity.class);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
