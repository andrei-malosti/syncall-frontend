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
import com.syncallapp.dto.CompanyRequestDTO;
import com.syncallapp.dto.RegisterRequest;
import com.syncallapp.network.RetrofitUser;
import com.syncallapp.service.SyncallApiRoutes;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private SyncallApiRoutes api;
    private TextView textErrorName;
    private TextView textErrorEmail;
    private TextView textErrorPassword;
    private TextView textErrorCompanyName;
    private TextView textErrorCompanyCnpj;

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
        textErrorName = findViewById(R.id.textErrorNome);
        textErrorEmail = findViewById(R.id.textErrorEmail);
        textErrorPassword = findViewById(R.id.textErrorSenha);
        textErrorCompanyName = findViewById(R.id.textErrorNomeEmpresa);
        textErrorCompanyCnpj = findViewById(R.id.textErrorCnpj);
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
                                        } else if(campo.equals("name")) {
                                            textErrorName.setText(mensagem);
                                            textErrorName.setVisibility(View.VISIBLE);
                                        } else if (campo.equals("company.name")) {
                                            textErrorCompanyName.setText(mensagem);
                                            textErrorCompanyName.setVisibility(View.VISIBLE);
                                        } else if (campo.equals("company.cnpj")) {
                                            textErrorCompanyCnpj.setText(mensagem);
                                            textErrorCompanyCnpj.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            } else if (jsonObject.has("message") && !jsonObject.isNull("message")) {
                                String mensagemDeErro = jsonObject.getString("message");
                                Toast.makeText(RegisterActivity.this, mensagemDeErro, Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this, "Erro ao processar dados (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        }
                    }
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