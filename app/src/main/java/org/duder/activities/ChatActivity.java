package org.duder.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.duder.R;
import org.duder.model.ChatMessage;
import org.duder.util.Const;
import org.duder.util.messages.ChatMessageRecyclerViewAdapter;
import org.duder.websocket.WebSocketService;
import org.duder.websocket.dto.StompMessage;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.functions.Consumer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ChatActivity extends AppCompatActivity {
    private String username;

    // region View Controls
    private TextView tvChatTitle;
    private RecyclerView rvChatMessages;
    private EditText etChatMessage;
    private Button btnChatSend;
    // endregion

    private ChatMessageRecyclerViewAdapter msgAdapter;
    private WebSocketService webSocketService;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private Gson gson = new GsonBuilder().create();

    // region StompMessage consumers
    Consumer<StompMessage> publicMessageConsumer = topicMessage -> {
        Log.d(Const.TAG, "Received " + topicMessage.getPayload());
        msgAdapter.addMessage(gson.fromJson(topicMessage.getPayload(), ChatMessage.class));
        rvChatMessages.scrollToPosition(msgAdapter.getItemCount() - 1);
    };

    Consumer<StompMessage> privateMessageConsumer = topicMessage -> {
        Log.d(Const.TAG, "Received " + topicMessage.getPayload());
        msgAdapter.addMessage(gson.fromJson(topicMessage.getPayload(), ChatMessage.class));
        rvChatMessages.scrollToPosition(msgAdapter.getItemCount() - 1);
    };
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initialize(this.getIntent().getExtras());
        webSocketService = WebSocketService.getWebSocketService();
        webSocketService.subscribeToChat(publicMessageConsumer, privateMessageConsumer);
        printChatMessagesHistory();
        btnChatSend.setOnClickListener(v -> sendMessage());
    }

    private void initialize(Bundle bundle) {
        username = bundle.getString("username");
        tvChatTitle = findViewById(R.id.tvChatTitle);
        rvChatMessages = findViewById(R.id.rvChatMessages);
        etChatMessage = findViewById(R.id.etChatMessage);
        btnChatSend = findViewById(R.id.btnChatSend);

        tvChatTitle.setText("think of something");
        Toast.makeText(getApplicationContext(), "Joined as " + username, Toast.LENGTH_SHORT).show();

        msgAdapter = new ChatMessageRecyclerViewAdapter(this, username);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvChatMessages.setLayoutManager(layoutManager);
        rvChatMessages.setAdapter(msgAdapter);
    }

    private void printChatMessagesHistory() {
        List<ChatMessage> chatMessages = new ArrayList<>();

        //Android doesn't allow for rest api calls on main thread
        Thread thread = new Thread(() -> {
            Request request = new Request.Builder()
                    .url(Const.REST_ADDRESS + Const.GET_MESSAGE_HISTORY_ENDPOINT)
                    .build();
            try (Response response = okHttpClient.newCall(request).execute()) {
                Type type = new TypeToken<List<ChatMessage>>() {
                }.getType();
                chatMessages.addAll(gson.fromJson(response.body().string(), type));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        chatMessages.forEach(msgAdapter::addMessage);
        rvChatMessages.scrollToPosition(msgAdapter.getItemCount() - 1);
    }

    private void sendMessage() {
        String message = etChatMessage.getText().toString();
        if (TextUtils.isEmpty(message)) {
            return;
        }
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(ChatMessage.MessageType.CHAT);
        chatMessage.setSender(username);
        chatMessage.setContent(message);

        webSocketService.sendMessage(chatMessage);
        etChatMessage.setText("");
    }
}
