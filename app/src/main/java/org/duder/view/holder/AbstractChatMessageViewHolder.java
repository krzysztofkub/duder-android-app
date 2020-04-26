package org.duder.view.holder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ord.duder.dto.chat.ChatMessage;

public abstract class AbstractChatMessageViewHolder extends RecyclerView.ViewHolder {

    public AbstractChatMessageViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bindMessage(ChatMessage message);
}
