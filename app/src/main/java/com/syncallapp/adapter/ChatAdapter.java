package com.syncallapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.syncallapp.R;
import com.syncallapp.dto.MessageResponse;
import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private List<MessageResponse> messages = new ArrayList<>();
    private Long loggedUserId;

    public ChatAdapter(Long loggedUserId) {
        this.loggedUserId = loggedUserId;
    }

    public void setMessages(List<MessageResponse> newMessages) {
        this.messages = newMessages;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        MessageResponse message = messages.get(position);

        if (loggedUserId.equals(message.getUserId())) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageResponse message = messages.get(position);

        if (holder.getItemViewType() == VIEW_TYPE_SENT) {
            SentMessageViewHolder sentHolder = (SentMessageViewHolder) holder;
            sentHolder.textMessage.setText(message.getContent());

            sentHolder.textTime.setText(message.getSendAt());
        } else {
            ReceivedMessageViewHolder receivedHolder = (ReceivedMessageViewHolder) holder;
            receivedHolder.textMessage.setText(message.getContent());

            receivedHolder.textTime.setText(String.valueOf(message.getSendAt()));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage, textTime;
        SentMessageViewHolder(View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessageSent);
            textTime = itemView.findViewById(R.id.textTimeSent);
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage, textTime;
        ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessageReceived);
            textTime = itemView.findViewById(R.id.textTimeReceived);
        }
    }
}