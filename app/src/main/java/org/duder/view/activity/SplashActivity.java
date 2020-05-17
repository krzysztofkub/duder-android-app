package org.duder.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.facebook.login.LoginManager;

import org.duder.util.UserSession;
import org.duder.viewModel.TokenValidator;
import org.duder.websocket.WebSocketService;
import org.duder.websocket.dto.ConnectionResponse;

import java.util.concurrent.CompletableFuture;

import static org.duder.util.UserSession.PREF_NAME;
import static org.duder.util.UserSession.TOKEN;

public class SplashActivity extends BaseActivity {

    private TokenValidator viewModel;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        if (UserSession.isLoggedIn(sharedPreferences)) {
            initListeners();
            viewModel.validateToken(sharedPreferences.getString(TOKEN, ""));
        } else {
            gotoActivity(LoginActivity.class);
        }

    }

    private void init() {
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        viewModel = ViewModelProviders.of(this).get(TokenValidator.class);
    }

    private void initListeners() {
        viewModel.getTokenValidation().observe(this, this::handleTokenValidityResult);
    }

    private void handleTokenValidityResult(boolean isValid) {
        if (isValid) {
            CompletableFuture<ConnectionResponse> websocketConn = doLoginToWebsocket();
            websocketConn.thenAccept(r -> gotoActivity(MainActivity.class));

        } else {
            UserSession.logOut(sharedPreferences, LoginManager.getInstance());
            gotoActivity(LoginActivity.class);
        }
    }

    private CompletableFuture<ConnectionResponse> doLoginToWebsocket() {
        WebSocketService webSocketService = WebSocketService.getWebSocketService();
        String token = getSharedPreferences(PREF_NAME, MODE_PRIVATE).getString(TOKEN, "");
        return webSocketService.connect(token);
    }

    private void gotoActivity(Class<? extends BaseActivity> activityClass) {
        final Intent intent = new Intent(this, activityClass);
        startActivity(intent);
        finish();
    }
}
