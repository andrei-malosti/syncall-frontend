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

import com.syncallapp.dto.UserRegisterRequest;
import com.syncallapp.dto.UserResponse;
import com.syncallapp.network.RetrofitUser;
import com.syncallapp.service.SyncallApiRoutes;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientRegisterActivity extends AppCompatActivity {

    private SyncallApiRoutes api;
    private TextView textErrorName;
    private TextView textErrorEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_register);

        textErrorName = findViewById(R.id.textErrorName);
        textErrorEmail = findViewById(R.id.textErrorEmail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        api = RetrofitUser.getUser(this).create(SyncallApiRoutes.class);
    }

    public void clientRegister(View view){
        EditText editClientName = findViewById(R.id.editClientRegisterName);
        EditText editClientEmail = findViewById(R.id.editClientRegisterEmail);

        textErrorName.setVisibility(View.GONE);
        textErrorEmail.setVisibility(View.GONE);

        String clientName = editClientName.getText().toString().trim();
        String clientEmail = editClientEmail.getText().toString().trim();

        UserRegisterRequest registerRequest = new UserRegisterRequest(clientEmail, clientName);

        api.clientRegister(registerRequest).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ClientRegisterActivity.this, "Cliente cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                    editClientEmail.setText("");
                    editClientName.setText("");
                } else {
                    if (response.errorBody() != null) {
                        try {
                            String errorJson = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(errorJson);

                            if (jsonObject.has("fields") && !jsonObject.isNull("fields")) {
                                JSONArray fieldsArray = jsonObject.getJSONArray("fields");

                                for (int i = 0; i < fieldsArray.length(); i++) {
                                    String fieldError = fieldsArray.getString(i);

                                    String[] partes = fieldError.split(":");

                                    if(partes.length == 2) {
                                        String campo = partes[0].trim();
                                        String mensagem = partes[1].trim();

                                        if (campo.equals("name")) {
                                            textErrorName.setText(mensagem);
                                            textErrorName.setVisibility(View.VISIBLE);
                                        } else if (campo.equals("email")) {
                                            textErrorEmail.setText(mensagem);
                                            textErrorEmail.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            }
                            else if (jsonObject.has("message") && !jsonObject.isNull("message")) {
                                String mensagemDeErro = jsonObject.getString("message");
                                Toast.makeText(ClientRegisterActivity.this, mensagemDeErro, Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(ClientRegisterActivity.this, "Erro ao processar dados (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(ClientRegisterActivity.this, "Erro de Conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}