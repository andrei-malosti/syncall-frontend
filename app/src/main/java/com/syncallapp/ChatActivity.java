package com.syncallapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.syncallapp.adapter.ChatAdapter;
import com.syncallapp.dto.ChatResponse;
import com.syncallapp.dto.MessageRequest;
import com.syncallapp.dto.MessageResponse;
import com.syncallapp.dto.PageResponse;
import com.syncallapp.network.RetrofitUser;
import com.syncallapp.service.SyncallApiRoutes;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private SyncallApiRoutes api;
    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private Long currentChatId;

    private final Handler chatHandler = new Handler(Looper.getMainLooper());
    private Runnable chatUpdater;
    private final int REFRESH_RATE = 20000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBarsAndKeyboard = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(
                    systemBarsAndKeyboard.left,
                    systemBarsAndKeyboard.top,
                    systemBarsAndKeyboard.right,
                    systemBarsAndKeyboard.bottom
            );
            return WindowInsetsCompat.CONSUMED;
        });

        api = RetrofitUser.getUser(this).create(SyncallApiRoutes.class);

        SharedPreferences prefs = getSharedPreferences("SyncallPrefs", Context.MODE_PRIVATE);
        Long loggedUserId = prefs.getLong("USER_ID", -1L);

        recyclerView = findViewById(R.id.recyclerViewChat);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ChatAdapter(loggedUserId);
        recyclerView.setAdapter(adapter);

        Long ticketId = getIntent().getLongExtra("TICKET_ID", -1L);
        if(ticketId != -1L) {
            loadChatByTicket(ticketId);
        }

        chatUpdater = new Runnable() {
            @Override
            public void run() {
                if (currentChatId != null) {
                    loadMessages(currentChatId, 0, 20);
                }
                chatHandler.postDelayed(this, REFRESH_RATE);
            }
        };
        chatHandler.post(chatUpdater);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chatUpdater != null) {
            chatHandler.removeCallbacks(chatUpdater);
        }
    }

    private void loadChatByTicket(Long ticketId) {
        api.getTicketChat(ticketId).enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if(response.isSuccessful() && response.body() != null) {
                    currentChatId = response.body().getId();
                    loadMessages(currentChatId, 0, 20);
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "Erro ao carregar o chat.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMessages(Long chatId, int page, int size) {
        api.getTicketMessages(chatId, page, 20).enqueue(new Callback<PageResponse<MessageResponse>>() {
            @Override
            public void onResponse(Call<PageResponse<MessageResponse>> call, Response<PageResponse<MessageResponse>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    adapter.setMessages(response.body().getContent());

                    if(adapter.getItemCount() > 0) {
                        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    }
                }
            }

            @Override
            public void onFailure(Call<PageResponse<MessageResponse>> call, Throwable t) {
                // Isto obriga o Android Studio a cuspir a árvore de erro completa no Logcat
                Log.e("ERRO_CHAT", "Falha ao CARREGAR mensagens: ", t);
                Toast.makeText(ChatActivity.this, "Erro no console!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void submitMessage(View view) {
        EditText editMessage = findViewById(R.id.editTextMessage);
        String messageText = editMessage.getText().toString().trim();

        if (messageText.isEmpty()) {
            return;
        }

        if (currentChatId == null) {
            Toast.makeText(this, "Aguarde o chat carregar...", Toast.LENGTH_SHORT).show();
            return;
        }

        MessageRequest request = new MessageRequest(messageText);

        api.createTicketMessages(currentChatId, request).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    editMessage.setText("");
                    loadMessages(currentChatId, 0, 20);

                } else {
                    Toast.makeText(ChatActivity.this, "Erro ao enviar mensagem (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                // Isto obriga o Android Studio a cuspir a árvore de erro completa no Logcat
                Log.e("ERRO_CHAT", "Falha ao CARREGAR mensagens: ", t);
                Toast.makeText(ChatActivity.this, "Erro no console!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}