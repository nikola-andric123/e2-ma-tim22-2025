package com.example.rpggame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button dugmeDodaj = findViewById(R.id.dugmeDodajZadatak);
        dugmeDodaj.setOnClickListener(v -> {
            // Kreiramo "Intent" (nameru) da pokrenemo KreirajZadatakActivity
            Intent intent = new Intent(MainActivity.this, KreirajZadatakActivity.class);
            startActivity(intent);
        });
    }
}