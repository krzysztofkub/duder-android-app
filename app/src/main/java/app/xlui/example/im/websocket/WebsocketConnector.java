package app.xlui.example.im.websocket;

import android.app.Activity;
import android.widget.Toast;

import app.xlui.example.im.util.Const;
import app.xlui.example.im.util.StompUtils;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class WebsocketConnector {
    public static StompClient getWebsocketConnection(Activity activity) {
        StompClient stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, Const.WS_ADDRESS);
        StompUtils.lifecycle(stompClient);
        Toast.makeText(activity, "Start connecting to server", Toast.LENGTH_SHORT).show();
        return stompClient;
    }
}
