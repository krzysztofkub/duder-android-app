package org.duder.ui.chat;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.duder.model.ChatMessage;

public abstract class AbstractChatMessageViewHolder extends RecyclerView.ViewHolder {

    public AbstractChatMessageViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bindMessage(ChatMessage message);
}
