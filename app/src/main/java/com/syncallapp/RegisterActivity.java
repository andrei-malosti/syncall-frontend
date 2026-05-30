package com.syncallapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.syncallapp.dto.AuthResponse;
import com.syncallapp.dto.CompanyRequestDTO;
import com.syncallapp.dto.RegisterRequest;
import com.syncallapp.network.RetrofitUser;
import com.syncallapp.service.SyncallApiRoutes;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private SyncallApiRoutes api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            api = RetrofitUser.getUser(this).create(SyncallApiRoutes.class);
            return insets;
        });
    }

    public void register(View view){

        EditText editUsername = findViewById(R.id.editNomeRegistro);
        EditText editEmail = findViewById(R.id.editEmailRegistro);
        EditText editPassword = findViewById(R.id.editSenhaRegistro);
        EditText editCompanyName = findViewById(R.id.editNomeEmpresa);
        EditText editCnpj = findViewById(R.id.editCnpjEmpresa);

        String username = editUsername.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String companyName = editCompanyName.getText().toString().trim();
        String cnpj = editCnpj.getText().toString().trim();

        CompanyRequestDTO companyRequest = new CompanyRequestDTO(companyName, cnpj);
        RegisterRequest registerRequest = new RegisterRequest(username, email, password, companyRequest);

        api.register(registerRequest).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    String role = extractRole(token);

                    SharedPreferences prefs = getSharedPreferences("SyncallPrefs", Context.MODE_PRIVATE);
                    prefs.edit()
                            .putString("TOKEN_JWT", token)
                            .putString("USER_ROLE", role)
                            .apply();

                    Toast.makeText(RegisterActivity.this, "Registro feito com sucesso!", Toast.LENGTH_SHORT).show();

                    if(role != null && (role.contains("ATTENDANT") || role.contains("MANAGER"))){
                        Intent intent = new Intent(RegisterActivity.this, AttendantDashboardActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    }

                    Intent intent = new Intent(RegisterActivity.this, ClientDashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Falha no Registro (Erro " + response.code() + ")", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Erro de Conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String extractRole(String token) {
        try {
            String[] tokenPieces = token.split("\\.");
            if (tokenPieces.length < 2) return null;

            String payloadBase64 = tokenPieces[1];

            byte[] decodeBytes = Base64.decode(payloadBase64, Base64.URL_SAFE);
            String jsonPayload = new String(decodeBytes, "UTF-8");
            JSONObject jsonObject = new JSONObject(jsonPayload);

            return jsonObject.getString("scope");

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}