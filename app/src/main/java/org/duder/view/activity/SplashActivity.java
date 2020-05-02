package org.duder.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.duder.util.UserSession;
import org.duder.websocket.WebSocketService;
import org.duder.websocket.dto.ConnectionResponse;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static org.duder.util.UserSession.PREF_NAME;
import static org.duder.util.UserSession.TOKEN;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        if (UserSession.isLoggedIn(sharedPreferences)) {
            doLoginToWebsocket((r) -> gotoMainActivity());
        } else {
            final Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private void doLoginToWebsocket(Consumer<ConnectionResponse> afterConnection) {
        WebSocketService webSocketService = WebSocketService.getWebSocketService();
        String token = getSharedPreferences(PREF_NAME, MODE_PRIVATE).getString(TOKEN, "");
        CompletableFuture<ConnectionResponse> futureConnect = webSocketService.connect(token);
        futureConnect.thenAccept(afterConnection);
    }

    private void gotoMainActivity() {
        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
