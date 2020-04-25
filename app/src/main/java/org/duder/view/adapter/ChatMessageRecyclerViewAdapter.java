package org.duder.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.duder.R;
import org.duder.model.chat.ChatMessage;
import org.duder.view.holder.AbstractChatMessageViewHolder;
import org.duder.view.holder.ChatEventMessageViewHolder;
import org.duder.view.holder.ChatMessageViewHolder;

import java.util.LinkedList;
import java.util.List;


public class ChatMessageRecyclerViewAdapter extends RecyclerView.Adapter<AbstractChatMessageViewHolder> {

    private List<ChatMessage> messages;
    private LayoutInflater inflater;
    private String username;

    // Username - to check later whether show message on the left or right
    public ChatMessageRecyclerViewAdapter(Context context, String username) {
        this.inflater = LayoutInflater.from(context);
        this.messages = new LinkedList<>();
        this.username = username;
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        return messages
                .get(position)
                .getType()
                .getIntValue();
    }

    @NonNull
    @Override
    public AbstractChatMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ChatMessage.MessageType.CHAT.getIntValue()) {
            View view = inflater.inflate(R.layout.activity_chat_message, parent, false);
            return new ChatMessageViewHolder(view, username);
        } else {
            View view = inflater.inflate(R.layout.activity_chat_event, parent, false);
            return new ChatEventMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AbstractChatMessageViewHolder viewHolder, int position) {
        ChatMessage message = messages.get(position);
        viewHolder.bindMessage(message);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return messages.size();
    }

}
