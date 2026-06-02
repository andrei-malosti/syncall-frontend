package com.syncallapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.text.HtmlCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.syncallapp.adapter.TicketAdapter;
import com.syncallapp.dto.EmojiResponse;
import com.syncallapp.dto.PageResponse;
import com.syncallapp.dto.TicketResponse;
import com.syncallapp.network.RetrofitEmoji;
import com.syncallapp.network.RetrofitUser;
import com.syncallapp.service.EmojiApiRoutes;
import com.syncallapp.service.SyncallApiRoutes;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_ticket);
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
            Intent intent = new Intent(TicketActivity.this, TicketDetailsActivity.class);
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

        api.getTickets(currentPage, 20).enqueue(new Callback<PageResponse<TicketResponse>>() {
            @Override
            public void onResponse(Call<PageResponse<TicketResponse>> call, Response<PageResponse<TicketResponse>> response) {
                isLoading = false;

                if (response.isSuccessful() && response.body() != null) {
                    PageResponse<TicketResponse> page = response.body();

                    if(currentPage == 0 && page.getContent().isEmpty()){
                        textEmptyState.setText("Nenhum ticket até o momento ");
                        textEmptyState.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);

                        carregarEmojiExterno();
                    } else{
                        textEmptyState.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                    adapter.addTickets(page.getContent());

                    if (page.getNumber() >= page.getTotalPages() - 1) {
                        isLastPage = true;
                    }
                } else {
                    textEmptyState.setText("Erro ao carregar chamados do servidor ❌");
                    textEmptyState.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<PageResponse<TicketResponse>> call, Throwable t) {
                isLoading = false;
                textEmptyState.setText("Falha de conexão com o servidor 📡");
                textEmptyState.setVisibility(View.VISIBLE);
            }
        });
    }

    private void carregarEmojiExterno() {
        EmojiApiRoutes emojiApi = RetrofitEmoji.getEmojiClient().create(EmojiApiRoutes.class);

        emojiApi.getRandomEmoji().enqueue(new Callback<EmojiResponse>() {
            @Override
            public void onResponse(Call<EmojiResponse> call, Response<EmojiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EmojiResponse emoji = response.body();

                    if (emoji.getHtmlCode() != null && !emoji.getHtmlCode().isEmpty()) {
                        String codigoHtml = emoji.getHtmlCode().get(0);
                        CharSequence emojiVisual = HtmlCompat.fromHtml(codigoHtml, HtmlCompat.FROM_HTML_MODE_LEGACY);

                        String textoVazio = "Nenhum ticket até o momento ";
                        textEmptyState.setText(textoVazio + emojiVisual);

                        android.util.Log.d("API_EMOJI", "Emoji carregado com sucesso: " + emoji.getName());
                    } else {
                        android.util.Log.e("API_EMOJI", "JSON veio sem o campo htmlCode");
                    }
                } else {
                    String erroServidor = "Erro do servidor: " + response.code();
                    android.util.Log.e("API_EMOJI", erroServidor);
                    Toast.makeText(TicketActivity.this, "API Emojis: " + erroServidor, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EmojiResponse> call, Throwable t) {
                android.util.Log.e("API_EMOJI", "Falha crítica na requisição: " + t.getMessage(), t);
                Toast.makeText(TicketActivity.this, "Falha crítica API Emojis: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}