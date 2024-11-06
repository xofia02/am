package com.example.am;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Botão para abrir a LocationManagerActivity
        Button btnOpenLocationManager = findViewById(R.id.Parte1);
        btnOpenLocationManager.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LocationManagerActivity.class);
            startActivity(intent);
        });

        // Botão para abrir a MapsActivity
        Button btnOpenMapsActivity = findViewById(R.id.map_button); // Usando o ID "Parte2"
        btnOpenMapsActivity.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);
        });
    }
}