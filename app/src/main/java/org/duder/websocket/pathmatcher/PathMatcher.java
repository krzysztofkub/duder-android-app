package org.duder.websocket.pathmatcher;


import org.duder.websocket.dto.StompMessage;

public interface PathMatcher {

    boolean matches(String path, StompMessage msg);
}
