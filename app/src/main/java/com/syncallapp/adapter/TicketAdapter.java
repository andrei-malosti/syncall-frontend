package com.syncallapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.syncallapp.R;
import com.syncallapp.dto.TicketResponse;

import java.util.ArrayList;
import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {
    private List<TicketResponse> tickets = new ArrayList<>();
    private OnTicketClickListener listener;

    public interface OnTicketClickListener {
        void onTicketClick(TicketResponse ticket);
    }

    public TicketAdapter(OnTicketClickListener listener) {
        this.listener = listener;
    }

    public void addTickets(List<TicketResponse> newTickets) {
        int startPosition = this.tickets.size();
        this.tickets.addAll(newTickets);
        notifyItemRangeInserted(startPosition, newTickets.size());
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        TicketResponse ticket = tickets.get(position);

        holder.textClientName.setText("Usuario: " + ticket.getClientName());
        holder.textDescription.setText(ticket.getDescription());
        holder.textStatus.setText(ticket.getTicketStatus().toString());

        holder.itemView.setOnClickListener(v -> {
            listener.onTicketClick(ticket);
        });
    }
    public void clearTickets() {
        this.tickets.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView textClientName;
        TextView textDescription;
        TextView textStatus;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            textClientName = itemView.findViewById(R.id.textClientName);
            textDescription = itemView.findViewById(R.id.textDescription);
            textStatus = itemView.findViewById(R.id.textTicketStatus);
        }
    }
}
