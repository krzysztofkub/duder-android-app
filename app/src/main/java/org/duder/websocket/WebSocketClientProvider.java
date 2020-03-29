package org.duder.websocket;

import org.duder.util.Const;
import org.duder.util.StompUtils;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class WebSocketClientProvider {

    private static StompClient stompClient;

    public static StompClient getWebSocketClient() {
        if (stompClient == null) {
            stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, Const.WS_ADDRESS);
            StompUtils.lifecycle(stompClient);
        }
        return stompClient;
    }
}
