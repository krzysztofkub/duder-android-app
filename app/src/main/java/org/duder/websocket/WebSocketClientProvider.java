package org.duder.websocket;

import android.util.Log;

import org.duder.util.Const;
import org.duder.websocket.stomp.Stomp;
import org.duder.websocket.stomp.StompClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static org.duder.util.Const.TAG;

public class WebSocketClientProvider {

    private static StompClient stompClient;

    public static StompClient getWebSocketClient() {
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
}



