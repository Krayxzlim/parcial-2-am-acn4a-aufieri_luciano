package com.miapp.dndcompanion;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class InventarioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);

        configurarMenu();
    }

    private void configurarMenu() {
        findViewById(R.id.menuInicio).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        findViewById(R.id.menuInventario).setOnClickListener(v -> { /* ya estamos aquí */ });

        findViewById(R.id.menuNotas).setOnClickListener(v -> {
            startActivity(new Intent(this, NotasActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        findViewById(R.id.menuAjustes).setOnClickListener(v -> {
            // Placeholder — próxima versión
        });
    }
}