package app.xlui.example.im.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import app.xlui.example.im.R;

public class LoginActivity extends AppCompatActivity {
    private EditText nameText;
    private Button loginButton;

    private void init() {
        nameText = findViewById(R.id.name);
        loginButton = findViewById(R.id.login);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.init();

        loginButton.setOnClickListener(v -> {
            String username = nameText.getText().toString();
            if (TextUtils.isEmpty(username)) {
                nameText.setError("Please provide username");
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putString("username", username);
            Intent i = new Intent(LoginActivity.this, ChatActivity.class);
            i.putExtras(bundle);
            startActivity(i);
            finish();
        });
    }
}
