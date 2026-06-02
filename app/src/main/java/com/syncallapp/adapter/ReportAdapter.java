package com.syncallapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.syncallapp.R;
import com.syncallapp.dto.ReportResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private List<ReportResponse> reportList = new ArrayList<>();
    private final OnReportClickListener listener;

    public interface OnReportClickListener {
        void onReportClick(ReportResponse report);
    }

    public ReportAdapter(List<ReportResponse> reportList, OnReportClickListener listener) {
        if (reportList != null) {
            this.reportList = reportList;
        }
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        ReportResponse report = reportList.get(position);

        holder.textDescription.setText(report.getTicketDescription());

        String dataCriacao = formatarData(report.getTicketCreatedAt());
        holder.textCreatedAt.setText("Aberto em: " + dataCriacao);

        if (report.getTicketConcludedAt() != null && !report.getTicketConcludedAt().isEmpty()) {
            String dataConclusao = formatarData(report.getTicketConcludedAt());
            holder.textConcludedAt.setText("Concluído: " + dataConclusao);
        } else {
            holder.textConcludedAt.setText("Ainda não foi concluído");
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReportClick(report);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public void setReports(List<ReportResponse> reports) {
        this.reportList = reports;
        notifyDataSetChanged();
    }

    private String formatarData(String dataIso) {
        if (dataIso == null || dataIso.isEmpty()) {
            return "Data não disponível";
        }
        try {
            LocalDateTime data = LocalDateTime.parse(dataIso);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return data.format(formatter);
        } catch (Exception e) {
            return dataIso;
        }
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView textDescription;
        TextView textCreatedAt;
        TextView textConcludedAt;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            textDescription = itemView.findViewById(R.id.textReportDescription);
            textCreatedAt = itemView.findViewById(R.id.textReportCreatedAt);
            textConcludedAt = itemView.findViewById(R.id.textReportConcludedAt);
        }
    }
}