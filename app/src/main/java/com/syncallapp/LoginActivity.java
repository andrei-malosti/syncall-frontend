package com.syncallapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.syncallapp.dto.AuthResponse;
import com.syncallapp.dto.LoginRequest;
import com.syncallapp.network.RetrofitUser;
import com.syncallapp.service.SyncallApiRoutes;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;

public class LoginActivity extends AppCompatActivity {

    private SyncallApiRoutes api;

    private TextView textErrorEmail;
    private TextView textErrorPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        textErrorEmail = findViewById(R.id.textErrorEmailLogin);
        textErrorPassword = findViewById(R.id.textErrorSenhaLogin);
        api = RetrofitUser.getUser(LoginActivity.this).create(SyncallApiRoutes.class);
    }

    public void registerPage(View view){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void login(View view){

        EditText editEmail = findViewById(R.id.editEmail);
        EditText editSenha = findViewById(R.id.editSenha);

        String email = editEmail.getText().toString().trim();
        String password = editSenha.getText().toString().trim();

        textErrorEmail.setVisibility(View.GONE);
        textErrorPassword.setVisibility(View.VISIBLE);

        LoginRequest loginRequest = new LoginRequest(email, password);

        api.login(loginRequest).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, retrofit2.Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    String role = extractRole(token);
                    Long userId = extractUserId(token);

                    SharedPreferences prefs = getSharedPreferences("SyncallPrefs", Context.MODE_PRIVATE);
                    prefs.edit()
                            .putString("TOKEN_JWT", token)
                            .putString("USER_ROLE", role)
                            .putLong("USER_ID", userId)
                            .apply();

                    Toast.makeText(LoginActivity.this, "Login feito com sucesso!", Toast.LENGTH_SHORT).show();

                    if (role != null && (role.contains("ATTENDANT") || role.contains("MANAGER"))) {
                        Intent intent = new Intent(LoginActivity.this, AttendantDashboardActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    }

                    Intent intent = new Intent(LoginActivity.this, ClientDashboardActivity.class);
                    startActivity(intent);
                    finish();
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

                                    if (partes.length == 2) {
                                        String campo = partes[0].trim();
                                        String mensagem = partes[1].trim();

                                        if (campo.equals("email")) {
                                            textErrorEmail.setText(mensagem);
                                            textErrorEmail.setVisibility(View.VISIBLE);
                                        } else if (campo.equals("password")) {
                                            textErrorPassword.setText(mensagem);
                                            textErrorPassword.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            } else if (jsonObject.has("message") && !jsonObject.isNull("message")) {
                                String mensagemDeErro = jsonObject.getString("message");
                                Toast.makeText(LoginActivity.this, mensagemDeErro, Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Erro ao processar dados (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Erro de Conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
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

    private Long extractUserId(String token) {
        try {
            String[] tokenPieces = token.split("\\.");
            if (tokenPieces.length < 2) return null;

            String payloadBase64 = tokenPieces[1];

            byte[] decodeBytes = Base64.decode(payloadBase64, Base64.URL_SAFE);
            String jsonPayload = new String(decodeBytes, "UTF-8");
            JSONObject jsonObject = new JSONObject(jsonPayload);

            return Long.valueOf(jsonObject.getString("userId"));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}