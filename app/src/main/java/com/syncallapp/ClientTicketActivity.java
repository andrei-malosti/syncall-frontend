package com.syncallapp;

import android.content.Intent;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientTicketActivity extends AppCompatActivity {

    private SyncallApiRoutes api;

    private Long ticketId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_ticket);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        api = RetrofitUser.getUser(this).create(SyncallApiRoutes.class);
        getClientTicket();
        Button buttonOpenChat = findViewById(R.id.buttonVerTicketChat);
        buttonOpenChat.setOnClickListener(v -> {
            Intent intent = new Intent(ClientTicketActivity.this, ChatActivity.class);
            intent.putExtra("TICKET_ID", ticketId);
            startActivity(intent);
        });
    }

    public void getClientTicket(){
        TextView viewDescription = findViewById(R.id.textViewDescriptionTicket);
        TextView viewDate = findViewById(R.id.textViewDateTicket);
        TextView viewClientName = findViewById(R.id.textViewClientTicketName);
        TextView viewStatus = findViewById(R.id.textViewStatusTicket);
        TextView viewAttendantName = findViewById(R.id.textViewAttendantName);
        TextView viewAttendantText = findViewById(R.id.textViewAttendantNameText);


        api.clientTicket().enqueue(new Callback<TicketResponse>() {
            @Override
            public void onResponse(Call<TicketResponse> call, Response<TicketResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    TicketResponse ticket = response.body();
                    ticketId = ticket.getId();
                    viewDescription.setText(ticket.getDescription());
                    viewDate.setText(ticket.getCreatedAt());
                    viewClientName.setText(ticket.getClientName());
                    viewStatus.setText("Status do chamado = " + ticket.getTicketStatus().toString());

                    if(ticket.getAttendantName() != null){
                        viewAttendantText.setVisibility(View.VISIBLE);
                        viewAttendantName.setVisibility(View.VISIBLE);
                        viewAttendantName.setText(ticket.getAttendantName());
                    }
                }
            }

            @Override
            public void onFailure(Call<TicketResponse> call, Throwable t) {
                Toast.makeText(ClientTicketActivity.this, "Falha de conexão", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void openClientChat(View view) {
        Intent intent = new Intent(ClientTicketActivity.this, ChatActivity.class);
        intent.putExtra("TICKET_ID", ticketId);
        startActivity(intent);
    }

}