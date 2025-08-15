package com.example.rpggame;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        // Postavi početni fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ListaZadatakaFragment()).commit();
        }

        // Deo za Floating Action Button
        FloatingActionButton fab = findViewById(R.id.fab_dodaj_zadatak);
        fab.setOnClickListener(view -> {
            // Pokrećemo KreirajZadatakActivity bez ikakvih dodatnih podataka,
            // što znači da je u pitanju "Create Mode" (režim kreiranja)
            Intent intent = new Intent(MainActivity.this, KreirajZadatakActivity.class);
            startActivity(intent);
        });
    }

    private final BottomNavigationView.OnItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

                int itemId = item.getItemId();
                if (itemId == R.id.nav_lista) {
                    selectedFragment = new ListaZadatakaFragment();
                } else if (itemId == R.id.nav_kalendar) {
                    selectedFragment = new KalendarFragment();
                } else if (itemId == R.id.nav_profil) {
                    // TODO: Kreirati i postaviti ProfilFragment
                    // selectedFragment = new ProfilFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                }

                return true;
            };
}