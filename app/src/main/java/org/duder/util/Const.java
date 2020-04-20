package org.duder.util;

public class Const {
    public static final String TAG = "duder";

    public static final String IP_ADDRESS = "3.20.222.251";

    public static final String WS_ADDRESS = "ws://" + IP_ADDRESS + ":8080/ws/websocket";
    public static final String WS_SEND_MESSAGE_ENDPOINT = "/app/message";
    public static final String TOPIC_PUBLIC = "/topic/public";
    public static final String USER_QUEUE = "/user/queue/reply";

    public static final String REST_ADDRESS = "http://" + IP_ADDRESS + ":8080";
    public static final String GET_MESSAGE_HISTORY_ENDPOINT = "/api/chat/getChatState";
    public static final String REGISTER_USER = "/user/register";
    public static final String LOGIN_USER = "/user/login";
    public static final String EVENTS = "/api/event";
    public static final String HOBBIES = "/api/hobby";
}
