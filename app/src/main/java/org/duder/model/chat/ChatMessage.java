package org.duder.model.chat;

public class ChatMessage {
    private MessageType type;
    private String      content;
    private String      sender;

    public enum MessageType {
        CHAT(0),
        JOIN(1),
        LEAVE(2);

        private int intValue;

        MessageType(int intValue) {
            this.intValue = intValue;
        }

        public int getIntValue() {
            return intValue;
        }

        public static MessageType of(int intValue) {
            for (MessageType mt : MessageType.values()) {
                if (mt.getIntValue() == intValue) {
                    return mt;
                }
            }
            throw new IllegalArgumentException("MessageType for " + intValue + " not found");
        }
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}