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

public class AttendantRegisterActivity extends AppCompatActivity {

    private SyncallApiRoutes api;

    private TextView textErrorName;

    private TextView textErrorEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendant_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            textErrorName = findViewById(R.id.textAttendantErrorName);
            textErrorEmail = findViewById(R.id.textAttendantErrorEmail);

            api = RetrofitUser.getUser(this).create(SyncallApiRoutes.class);
            return insets;
        });
    }

    public void attendantRegister(View view){
        EditText editAttendantName = findViewById(R.id.editAttendantRegisterName);
        EditText editAttendantEmail = findViewById(R.id.editAttendantRegisterEmail);

        textErrorEmail.setVisibility(View.GONE);
        textErrorName.setVisibility(View.GONE);

        String attendantName = editAttendantName.getText().toString().trim();
        String attendantEmail = editAttendantEmail.getText().toString().trim();

        UserRegisterRequest registerRequest = new UserRegisterRequest(attendantEmail, attendantName);

        api.attendantRegister(registerRequest).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    Toast.makeText(AttendantRegisterActivity.this, "Atendente cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                    editAttendantEmail.setText("");
                    editAttendantName.setText("");
                } else {
                    if(response.errorBody() != null){
                        try {
                            String jsonError = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(jsonError);
                            if(jsonObject.has("fields") && !jsonObject.isNull("fields")){
                                JSONArray fieldsArray = jsonObject.getJSONArray("fields");

                                for(int i = 0; i < fieldsArray.length() ; i++) {
                                    String fieldError = fieldsArray.getString(i);
                                    String[] pieces = fieldError.split(":");

                                    if (pieces.length == 2) {
                                        String field = pieces[0];
                                        String message = pieces[1];
                                        if (field.equals("email")) {
                                            textErrorEmail.setText(message);
                                            textErrorEmail.setVisibility(View.VISIBLE);
                                        } else if (field.equals("name")) {
                                            textErrorName.setText(message);
                                            textErrorName.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            } else if(jsonObject.has("message") && !jsonObject.isNull("message")){
                                String errorMessage = jsonObject.getString("message");
                                Toast.makeText(AttendantRegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(AttendantRegisterActivity.this, "Erro ao processar dados (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(AttendantRegisterActivity.this, "Erro de Conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}