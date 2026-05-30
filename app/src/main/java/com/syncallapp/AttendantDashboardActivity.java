package com.syncallapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AttendantDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendant_dashboard);
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

}