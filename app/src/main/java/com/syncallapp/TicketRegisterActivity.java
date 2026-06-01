package com.syncallapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.syncallapp.dto.TicketRegister;
import com.syncallapp.dto.TicketResponse;
import com.syncallapp.network.RetrofitUser;
import com.syncallapp.service.SyncallApiRoutes;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketRegisterActivity extends AppCompatActivity {

    private SyncallApiRoutes api;

    private TextView textErrorProblem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ticket_register);

        textErrorProblem = findViewById(R.id.textErrorProblem);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        api = RetrofitUser.getUser(this).create(SyncallApiRoutes.class);
    }

    public void createTicket(View view){

        EditText editTicketRegister = findViewById(R.id.editProblem);

        String ticket = editTicketRegister.getText().toString();
        TicketRegister ticketRegister = new TicketRegister(ticket);

        textErrorProblem.setVisibility(View.GONE);

        api.createTicket(ticketRegister).enqueue(new Callback<TicketResponse>() {
            @Override
            public void onResponse(Call<TicketResponse> call, Response<TicketResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    Toast.makeText(TicketRegisterActivity.this, "Chamado criado com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    if(response.errorBody() != null){
                        try {
                            String errorJson = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(errorJson);

                            if(jsonObject.has("fields") && !jsonObject.isNull("fields")){
                                JSONArray fieldsArray = jsonObject.getJSONArray("fields");

                                for(int i = 0; i < fieldsArray.length(); i++){
                                    String fieldError = fieldsArray.getString(i);
                                    String[] fieldPieces = fieldError.split(":");

                                    if(fieldPieces.length == 2){
                                        String field = fieldPieces[0].trim();
                                        String message = fieldPieces[1].trim();

                                        if(field.equals("description")){
                                            textErrorProblem.setText(message);
                                            textErrorProblem.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            } else if (jsonObject.has("message") && !jsonObject.isNull("message")) {
                                String mensagemDeErro = jsonObject.getString("message");
                                Toast.makeText(TicketRegisterActivity.this, mensagemDeErro, Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(TicketRegisterActivity.this, "Erro ao processar dados (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<TicketResponse> call, Throwable t) {
                Toast.makeText(TicketRegisterActivity.this, "Erro de Conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}