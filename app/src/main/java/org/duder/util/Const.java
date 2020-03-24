package org.duder.util;

public class Const {
	public static final String TAG = "duder";

	public static final String IP_ADDRESS = "18.219.45.180";

	public static final String WS_ADDRESS = "ws://" + IP_ADDRESS + ":8080/ws/websocket";
	public static final String WS_SEND_MESSAGE_ENDPOINT = "/app/sendMessage";
	public static final String TOPIC_PUBLIC = "/topic/public";

	public static final String REST_ADDRESS = "http://" + IP_ADDRESS + ":8080";
	public static final String GET_MESSAGE_HISTORY_ENDPOINT = "/getChatState";
}
