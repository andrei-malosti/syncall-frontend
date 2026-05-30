package com.syncallapp;

import android.os.Bundle;
import android.view.View;
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
import retrofit2.Retrofit;

public class TicketDetailsActivity extends AppCompatActivity {

    private SyncallApiRoutes api;

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
        Long ticketId = getIntent().getLongExtra("TICKET_ID", -1L);
        if(ticketId != -1L){
            getTicketById(ticketId);
        }else {
            Toast.makeText(this, "Erro: Ticket não encontrado", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void getTicketById(Long id){
        TextView viewDescription = findViewById(R.id.textViewDescription);
        TextView viewDate = findViewById(R.id.textViewDate);
        TextView viewClientName = findViewById(R.id.textViewClientName);
        TextView viewStatus = findViewById(R.id.textViewStatus);


        api.getTicket(id).enqueue(new Callback<TicketResponse>() {
            @Override
            public void onResponse(Call<TicketResponse> call, Response<TicketResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    TicketResponse ticket = response.body();
                    viewDescription.setText(ticket.getDescription());
                    viewDate.setText(ticket.getCreatedAt());
                    viewClientName.setText(ticket.getClientName());
                    viewStatus.setText("Status do chamado = " + ticket.getTicketStatus().toString());
                }
            }

            @Override
            public void onFailure(Call<TicketResponse> call, Throwable t) {
                Toast.makeText(TicketDetailsActivity.this, "Falha de conexão", Toast.LENGTH_LONG).show();
            }
        });
    }

}