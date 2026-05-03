package com.miapp.dndcompanion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class NotasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas);

        configurarMenu();

        // Botón guardar nota
        findViewById(R.id.btnGuardarNota).setOnClickListener(v ->
                Toast.makeText(this, "✦ Nota guardada", Toast.LENGTH_SHORT).show()
        );
    }

    private void configurarMenu() {
        findViewById(R.id.menuInicio).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        findViewById(R.id.menuInventario).setOnClickListener(v -> {
            startActivity(new Intent(this, InventarioActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        findViewById(R.id.menuNotas).setOnClickListener(v -> { /* ya estamos aquí */ });

        findViewById(R.id.menuAjustes).setOnClickListener(v -> {
            // Placeholder
        });
    }
}