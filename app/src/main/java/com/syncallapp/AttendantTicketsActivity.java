package com.syncallapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.syncallapp.adapter.TicketAdapter;
import com.syncallapp.dto.PageResponse;
import com.syncallapp.dto.TicketResponse;
import com.syncallapp.network.RetrofitUser;
import com.syncallapp.service.SyncallApiRoutes;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendantTicketsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TicketAdapter adapter;
    private SyncallApiRoutes api;

    private int currentPage = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private TextView textEmptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendant_tickets);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        textEmptyState = findViewById(R.id.textEmptyState);
        api = RetrofitUser.getUser(this).create(SyncallApiRoutes.class);
        setupRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        currentPage = 0;
        isLastPage = false;
        isLoading = false;

        if (adapter != null) {
            adapter.clearTickets();
        }

        loadTickets();
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerViewTickets);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new TicketAdapter(selectedTicket -> {
            Intent intent = new Intent(AttendantTicketsActivity.this, TicketDetailsActivity.class);
            intent.putExtra("TICKET_ID", selectedTicket.getId());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && !isLastPage) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            currentPage++;
                            loadTickets();
                        }
                    }
                }
            }
        });
    }

    private void loadTickets() {
        isLoading = true;

        api.getAttendantTickets(currentPage, 20).enqueue(new Callback<PageResponse<TicketResponse>>() {
            @Override
            public void onResponse(Call<PageResponse<TicketResponse>> call, Response<PageResponse<TicketResponse>> response) {
                isLoading = false;

                if (response.isSuccessful() && response.body() != null) {
                    PageResponse<TicketResponse> page = response.body();

                    if(currentPage == 0 && page.getContent().isEmpty()){
                        textEmptyState.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else{
                        textEmptyState.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                    adapter.addTickets(page.getContent());

                    if (page.getNumber() >= page.getTotalPages() - 1) {
                        isLastPage = true;
                    }
                }
            }

            @Override
            public void onFailure(Call<PageResponse<TicketResponse>> call, Throwable t) {
                isLoading = false;
            }
        });
    }
}