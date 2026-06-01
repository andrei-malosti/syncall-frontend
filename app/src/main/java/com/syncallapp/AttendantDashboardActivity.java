package com.syncallapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

public class AttendantDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendant_dashboard);

        Button buttonReport = findViewById(R.id.buttonReport);
        Button buttonUsers = findViewById(R.id.buttonUsuarios);
        View viewSepartor = findViewById(R.id.viewSeparator);

        SharedPreferences prefs = getSharedPreferences("SyncallPrefs", Context.MODE_PRIVATE);
        String userRole = prefs.getString("USER_ROLE", "");

        if(userRole == null || !userRole.contains("MANAGER")){
            buttonReport.setVisibility(View.GONE);
            buttonUsers.setVisibility(View.GONE);
            viewSepartor.setVisibility(View.GONE);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void findTicketsPage(View view){
        Intent intent = new Intent(AttendantDashboardActivity.this, TicketActivity.class);
        startActivity(intent);
    }

    public void findReportsPage(View view){
        Intent intent = new Intent(AttendantDashboardActivity.this, ReportActivity.class);
        startActivity(intent);
    }

    public void managePage(View view){
        Intent intent = new Intent(AttendantDashboardActivity.this, ManagerActivity.class);
        startActivity(intent);
    }

    public void myTicketsPage(View view){
        Intent intent = new Intent(AttendantDashboardActivity.this, AttendantTicketsActivity.class);
        startActivity(intent);
    }

}