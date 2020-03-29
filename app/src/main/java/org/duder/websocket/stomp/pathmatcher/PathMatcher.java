package org.duder.websocket.stomp.pathmatcher;


import org.duder.websocket.stomp.dto.StompMessage;

public interface PathMatcher {

    boolean matches(String path, StompMessage msg);
}
