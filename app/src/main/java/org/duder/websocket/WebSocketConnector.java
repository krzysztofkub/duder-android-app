package org.duder.websocket;

import org.duder.util.Const;
import org.duder.util.StompUtils;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class WebSocketConnector {
    public static StompClient getWebSocketConnection() {
        StompClient stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, Const.WS_ADDRESS);
        StompUtils.lifecycle(stompClient);
        return stompClient;
    }
}
