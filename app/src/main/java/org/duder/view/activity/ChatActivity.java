package org.duder.view.activity;

import android.os.Bundle;
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

import org.duder.R;
import org.duder.service.ApiClient;
import org.duder.model.chat.ChatMessage;
import org.duder.view.adapter.ChatMessageRecyclerViewAdapter;
import org.duder.util.Const;
import org.duder.util.UserSession;
import org.duder.websocket.WebSocketService;
import org.duder.websocket.dto.StompMessage;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class ChatActivity extends BaseActivity {
    private String login;

    // region View Controls
    private TextView tvChatTitle;
    private RecyclerView rvChatMessages;
    private EditText etChatMessage;
    private Button btnChatSend;
    // endregion

    private ChatMessageRecyclerViewAdapter msgAdapter;
    private WebSocketService webSocketService;
    private ApiClient apiClient;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initialize();
        webSocketService = WebSocketService.getWebSocketService();
        webSocketService.subscribeToChat(publicMessageConsumer, privateMessageConsumer);
        apiClient = ApiClient.getApiClient();
        printChatMessagesHistory();
        btnChatSend.setOnClickListener(v -> sendMessage());
    }

    private void initialize() {
        UserSession userSession = UserSession.getUserSession();
        login = userSession.getAccount().getLogin();
        tvChatTitle = findViewById(R.id.tvChatTitle);
        rvChatMessages = findViewById(R.id.rvChatMessages);
        etChatMessage = findViewById(R.id.etChatMessage);
        btnChatSend = findViewById(R.id.btnChatSend);

        tvChatTitle.setText("Public");
        Toast.makeText(getApplicationContext(), "Joined as " + login, Toast.LENGTH_SHORT).show();

        msgAdapter = new ChatMessageRecyclerViewAdapter(this, login);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvChatMessages.setLayoutManager(layoutManager);
        rvChatMessages.setAdapter(msgAdapter);
    }

    private void printChatMessagesHistory() {
        UserSession userSession = UserSession.getUserSession();
        addSub(apiClient.getChatState(userSession.getAccount().getSessionToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(messages -> {
                    messages.forEach(msgAdapter::addMessage);
                    rvChatMessages.scrollToPosition(msgAdapter.getItemCount() - 1);
                })
        );
    }

    private void sendMessage() {
        String message = etChatMessage.getText().toString();
        if (TextUtils.isEmpty(message)) {
            return;
        }
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(ChatMessage.MessageType.CHAT);
        chatMessage.setSender(login);
        chatMessage.setContent(message);

        webSocketService.sendMessage(chatMessage);
        etChatMessage.setText("");
    }
}
