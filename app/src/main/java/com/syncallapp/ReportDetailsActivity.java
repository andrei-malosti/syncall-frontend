package com.syncallapp; // Confirme se o pacote está correto

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportDetailsActivity extends AppCompatActivity {
    private TextView textViewCreatedAt;
    private TextView textViewConcludedAt;
    private TextView textViewDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textViewCreatedAt = findViewById(R.id.textViewCreatedAt);
        textViewConcludedAt = findViewById(R.id.textViewConcludedAt);
        textViewDescription = findViewById(R.id.textViewDescriptionReport);


        Intent intent = getIntent();
        if (intent != null) {
            String description = intent.getStringExtra("REPORT_DESC");
            String createdAt = intent.getStringExtra("REPORT_CREATED");
            String concludedAt = intent.getStringExtra("REPORT_CONCLUDED");

            textViewDescription.setText(description != null ? description : "Sem descrição");
            textViewCreatedAt.setText(formatarData(createdAt));
            textViewConcludedAt.setText(formatarData(concludedAt));
        }
    }

    private String formatarData(String dataIso) {
        if (dataIso == null ) {
            return "Data indisponível";
        }
        if(dataIso.isEmpty()){
            return "Chamado não foi concluido ainda";
        }

        try {
            LocalDateTime data = LocalDateTime.parse(dataIso);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return data.format(formatter);
        } catch (Exception e) {
            e.printStackTrace();
            return dataIso;
        }
    }
}