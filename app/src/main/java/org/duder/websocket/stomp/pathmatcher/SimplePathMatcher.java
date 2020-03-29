package org.duder.websocket.stomp.pathmatcher;

import org.duder.websocket.stomp.dto.StompHeader;
import org.duder.websocket.stomp.dto.StompMessage;

public class SimplePathMatcher implements PathMatcher {

    @Override
    public boolean matches(String path, StompMessage msg) {
        String dest = msg.findHeader(StompHeader.DESTINATION);
        if (dest == null) return false;
        else return path.equals(dest);
    }
}
