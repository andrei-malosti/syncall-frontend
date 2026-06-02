package com.syncallapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.syncallapp.dto.TicketResponse;
import com.syncallapp.network.RetrofitUser;
import com.syncallapp.service.SyncallApiRoutes;

// Imports necessários para formatar a data
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TicketDetailsActivity extends AppCompatActivity {

    private SyncallApiRoutes api;

    private Long ticketId;

    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ticket_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        api = RetrofitUser.getUser(this).create(SyncallApiRoutes.class);
        ticketId = getIntent().getLongExtra("TICKET_ID", -1L);
        if(ticketId != -1L){
            getTicketById(ticketId);
        }else {
            Toast.makeText(this, "Erro: Ticket não encontrado", Toast.LENGTH_SHORT).show();
            finish();
        }
        Button buttonOpenChat = findViewById(R.id.buttonVerChat);
        buttonOpenChat.setOnClickListener(v -> {
            Intent intent = new Intent(TicketDetailsActivity.this, ChatActivity.class);
            intent.putExtra("TICKET_ID", ticketId);
            startActivity(intent);
        });

        SharedPreferences prefs = getSharedPreferences("SyncallPrefs", Context.MODE_PRIVATE);

        userRole = prefs.getString("USER_ROLE", "");
    }

    public void getTicketById(Long id){
        TextView viewDescription = findViewById(R.id.textViewDescription);
        TextView viewDate = findViewById(R.id.textViewDate);
        TextView viewClientName = findViewById(R.id.textViewClientName);
        TextView viewStatus = findViewById(R.id.textViewStatus);
        Button buttonAssignTicket = findViewById(R.id.buttonAtenderChamado);
        Button buttonOpenChat = findViewById(R.id.buttonVerChat);

        api.getTicket(id).enqueue(new Callback<TicketResponse>() {
            @Override
            public void onResponse(Call<TicketResponse> call, Response<TicketResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    TicketResponse ticket = response.body();

                    String status = ticket.getTicketStatus().toString();

                    if(userRole.contains("ATTENDANT") || userRole.contains("MANAGER")){
                        if(ticket.getTicketStatus().toString().equals("OPEN"))
                            buttonAssignTicket.setVisibility(View.VISIBLE);
                    }
                    if(ticket.getTicketStatus().toString().equals("IN_PROGRESS")) {
                        buttonOpenChat.setVisibility(View.VISIBLE);
                    }

                    if(status.equals("IN_PROGRESS")) {
                        status = "EM ANDAMENTO";
                    } else if(status.equals("OPEN")){
                        status = "CHAMADO ABERTO";
                    }

                    viewDescription.setText(ticket.getDescription());

                    viewDate.setText(formatarData(ticket.getCreatedAt()));

                    viewClientName.setText(ticket.getClientName());
                    viewStatus.setText("Status do chamado = " + status);
                }
            }

            @Override
            public void onFailure(Call<TicketResponse> call, Throwable t) {
                Toast.makeText(TicketDetailsActivity.this, "Falha de conexão", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void assignTicket(View view){
        api.assignTicket(ticketId).enqueue(new Callback<TicketResponse>() {
            @Override
            public void onResponse(Call<TicketResponse> call, Response<TicketResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    Toast.makeText(TicketDetailsActivity.this, "Chamado atribuido com sucesso!", Toast.LENGTH_SHORT);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<TicketResponse> call, Throwable t) {
                Toast.makeText(TicketDetailsActivity.this, "Falha de conexão", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void openChat(View view) {
        Intent intent = new Intent(TicketDetailsActivity.this, ChatActivity.class);
        intent.putExtra("TICKET_ID", ticketId);
        startActivity(intent);
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
}