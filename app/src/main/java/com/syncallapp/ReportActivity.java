package com.syncallapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.syncallapp.adapter.ReportAdapter;
import com.syncallapp.dto.PageResponse;
import com.syncallapp.dto.ReportResponse;
import com.syncallapp.network.RetrofitUser;
import com.syncallapp.service.SyncallApiRoutes;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportActivity extends AppCompatActivity {

    private RecyclerView recyclerViewReports;
    private TextView textEmptyState;
    private ReportAdapter reportAdapter;
    private List<ReportResponse> reportList = new ArrayList<>();

    private SyncallApiRoutes api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        recyclerViewReports = findViewById(R.id.recyclerViewReports);
        textEmptyState = findViewById(R.id.textEmptyState);

        recyclerViewReports.setLayoutManager(new LinearLayoutManager(this));

        reportAdapter = new ReportAdapter(reportList, new ReportAdapter.OnReportClickListener() {
            @Override
            public void onReportClick(ReportResponse report) {
                Intent intent = new Intent(ReportActivity.this, ReportDetailsActivity.class);

                intent.putExtra("REPORT_ID", report.getId());
                intent.putExtra("REPORT_DESC", report.getTicketDescription());

                if (report.getTicketCreatedAt() != null) {
                    intent.putExtra("REPORT_CREATED", report.getTicketCreatedAt());
                }
                if (report.getTicketConcludedAt() != null) {
                    intent.putExtra("REPORT_CONCLUDED", report.getTicketConcludedAt());
                }

                startActivity(intent);
            }
        });
        api = RetrofitUser.getUser(this).create(SyncallApiRoutes.class);
        recyclerViewReports.setAdapter(reportAdapter);

        carregarRelatorios();
    }

    private void carregarRelatorios() {

        api.getAllReports().enqueue(new Callback<PageResponse<ReportResponse>>() {
            @Override
            public void onResponse(Call<PageResponse<ReportResponse>> call, Response<PageResponse<ReportResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    reportList = response.body().getContent();
                    atualizarInterface();
                } else {
                    Toast.makeText(ReportActivity.this, "Erro ao carregar relatórios", Toast.LENGTH_SHORT).show();
                    atualizarInterface();
                }
            }

            @Override
            public void onFailure(Call<PageResponse<ReportResponse>> call, Throwable t) {
                Toast.makeText(ReportActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private void atualizarInterface() {
        if (reportList == null || reportList.isEmpty()) {
            textEmptyState.setVisibility(View.VISIBLE);
            recyclerViewReports.setVisibility(View.GONE);
        } else {
            textEmptyState.setVisibility(View.GONE);
            recyclerViewReports.setVisibility(View.VISIBLE);
            reportAdapter.setReports(reportList);
        }
    }
}