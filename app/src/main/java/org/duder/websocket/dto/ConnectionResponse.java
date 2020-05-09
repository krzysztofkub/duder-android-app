package org.duder.websocket.dto;

public class ConnectionResponse {
    private boolean isBadCredentials;

    public boolean isBadCredentials() {
        return isBadCredentials;
    }

    public void setBadCredentials(boolean badCredentials) {
        isBadCredentials = badCredentials;
    }
}
