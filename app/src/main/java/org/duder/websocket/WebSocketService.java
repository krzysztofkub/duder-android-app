package org.duder.websocket;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.duder.dto.chat.ChatMessage;
import org.duder.util.Const;
import org.duder.websocket.dto.StompHeader;
import org.duder.websocket.dto.StompMessage;
import org.duder.websocket.stomp.dto.ConnectionResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static org.duder.util.Const.TAG;

public class WebSocketService {

    private StompClient stompClient;
    private Gson gson = new GsonBuilder().create();
    private static WebSocketService webSocketService;

    private WebSocketService() {
        stompClient = initialize();
    }

    synchronized public static WebSocketService getWebSocketService() {
        if (webSocketService == null) {
            webSocketService = new WebSocketService();
        }
        return webSocketService;
    }

    private StompClient initialize() {
        if (stompClient == null) {
            stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, Const.WS_ADDRESS);
            Disposable subscribe = stompClient.lifecycle().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(lifecycleEvent -> {
                        switch (lifecycleEvent.getType()) {
                            case OPENED:
                                Log.d(TAG, "Stomp connection opened");
                                break;
                            case ERROR:
                                Log.e(TAG, "Error", lifecycleEvent.getException());
                                System.out.println("error");
                                break;
                            case CLOSED:
                                Log.e(TAG, "Closed");
                                break;
                        }
                    });
        }
        return stompClient;
    }

    public CompletableFuture<ConnectionResponse> connect(String sessionToken) {
        final StompHeader loginHeader = new StompHeader("Authorization", sessionToken);
        List<StompHeader> headers = new ArrayList<>();
        headers.add(loginHeader);

        return stompClient.connect(headers);
    }

    public void subscribeToChat(Consumer<StompMessage> onPublicMessage, Consumer<StompMessage> onPrivateMessage) {
        Disposable dispTopic = stompClient.topic(Const.TOPIC_PUBLIC)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onPublicMessage);

        Disposable dispUserReply = stompClient.topic(Const.USER_QUEUE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onPrivateMessage);
    }

    public void sendMessage(ChatMessage chatMessage) {
        stompClient.send(Const.WS_SEND_MESSAGE_ENDPOINT, gson.toJson(chatMessage)).subscribe();
    }
}



