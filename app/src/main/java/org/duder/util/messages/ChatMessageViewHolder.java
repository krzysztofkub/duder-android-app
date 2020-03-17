package org.duder.util.messages;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.duder.model.ChatMessage;
import org.duder.model.ChatMessage.MessageType;

import app.xlui.example.im.R;

public class ChatMessageViewHolder extends AbstractChatMessageViewHolder {
    TextView     tvContent;
    LinearLayout linLayMsgMain;
    String       currentUser;

    public ChatMessageViewHolder(@NonNull View itemView, String currentUser) {
        super(itemView);
        tvContent = itemView.findViewById(R.id.tvContent);
        linLayMsgMain = itemView.findViewById(R.id.linLayMsgMain);
        this.currentUser = currentUser;
    }

    public void bindMessage(ChatMessage message) {
        MessageType type = message.getType();
        String sender = message.getSender();
        String content = message.getContent();

        switch (type) {
            case CHAT:
                tvContent.setText(content);
                setStyle(sender);
            case JOIN:
            case LEAVE:
                //throw new IllegalArgumentException(getClass().getSimpleName() + " handles only Chat messages");
                break;
        }
    }

    private void setStyle(String username) {
        // from "me"
        if (username.equals(currentUser)) {
            linLayMsgMain.setGravity(Gravity.RIGHT);
            tvContent.setTextColor(Color.rgb(255, 255, 255));
            tvContent.setBackgroundColor(Color.parseColor("#3333ff"));
        }
        // from anybody else
        else {
            linLayMsgMain.setGravity(Gravity.LEFT);
            tvContent.setTextColor(Color.parseColor("#505050"));
            tvContent.setBackgroundColor(Color.parseColor("#eeeeee"));
        }
    }
}