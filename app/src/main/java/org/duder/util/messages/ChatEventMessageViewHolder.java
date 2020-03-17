package org.duder.util.messages;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import org.duder.model.ChatMessage;

import app.xlui.example.im.R;

import static org.duder.model.ChatMessage.MessageType;

public class ChatEventMessageViewHolder extends AbstractChatMessageViewHolder {

    private TextView tvContent;

    public ChatEventMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        tvContent = itemView.findViewById(R.id.tvContent);
    }

    public void bindMessage(ChatMessage message) {
        MessageType type = message.getType();
        String sender = message.getSender();
        switch (type) {
            case CHAT:
                //throw new IllegalArgumentException(getClass().getSimpleName() + " handles either Join or Leave messages");
                break;
            case JOIN:
                tvContent.setText(sender + " joined");
                break;
            case LEAVE:
                tvContent.setText(sender + " left");
                break;
        }
    }

}
