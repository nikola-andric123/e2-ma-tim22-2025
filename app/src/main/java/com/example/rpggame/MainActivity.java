package com.example.rpggame;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.rpggame.activity.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    FloatingActionButton fabDodajZadatak;
    FloatingActionButton fabDodajKategoriju;
    Button dugmeReset;
    ZadatakRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null || !user.isEmailVerified()) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // --- ISPRAVKA: Svi findViewById pozivi idu ovde, na početak ---
        repository = new ZadatakRepository(getApplication());
        dugmeReset = findViewById(R.id.dugme_reset);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        fabDodajZadatak = findViewById(R.id.fab_dodaj_zadatak);
        fabDodajKategoriju = findViewById(R.id.fab_dodaj_kategoriju);
        // --- KRAJ ISPRAVKE ---

        bottomNav.setOnItemSelectedListener(navListener);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ListaZadatakaFragment()).commit();
            // Inicijalno stanje vidljivosti FAB dugmadi
            fabDodajZadatak.setVisibility(View.VISIBLE);
            fabDodajKategoriju.setVisibility(View.GONE);
        }

        fabDodajZadatak.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, KreirajZadatakActivity.class);
            startActivity(intent);
        });

        fabDodajKategoriju.setOnClickListener(view -> {
            Fragment trenutniFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (trenutniFragment instanceof KategorijeFragment) {
                ((KategorijeFragment) trenutniFragment).prikaziDijalogZaDodavanjeKategorije();
            }
        });

        dugmeReset.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Resetovanje podataka")
                    .setMessage("Da li ste sigurni da želite da obrišete sve zadatke i resetujete profil na nivo 0? Ova akcija je nepovratna.")
                    .setPositiveButton("Resetuj", (dialog, which) -> {
                        repository.resetLokalnuBazu();
                        repository.resetUserProfileNaPocetnoStanje(() -> {
                            recreate(); // Ponovo učitaj aktivnost da se sve osveži
                        });
                    })
                    .setNegativeButton("Odustani", null)
                    .show();
        });
    }

    private final BottomNavigationView.OnItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.nav_lista) {
                    selectedFragment = new ListaZadatakaFragment();
                    fabDodajZadatak.setVisibility(View.VISIBLE);
                    fabDodajKategoriju.setVisibility(View.GONE);
                } else if (itemId == R.id.nav_kalendar) {
                    selectedFragment = new KalendarFragment();
                    fabDodajZadatak.setVisibility(View.VISIBLE);
                    fabDodajKategoriju.setVisibility(View.GONE);
                } else if (itemId == R.id.nav_kategorije) {
                    selectedFragment = new KategorijeFragment();
                    fabDodajZadatak.setVisibility(View.GONE);
                    fabDodajKategoriju.setVisibility(View.VISIBLE);
                } else if (itemId == R.id.nav_profil) {
                    selectedFragment = new UserProfileFragment();
                    fabDodajZadatak.setVisibility(View.GONE);
                    fabDodajKategoriju.setVisibility(View.GONE);
                } else if (itemId == R.id.nav_prodavnica) {
                    selectedFragment = new ShopFragment();
                    fabDodajZadatak.setVisibility(View.GONE);
                    fabDodajKategoriju.setVisibility(View.GONE);
                }

                if (selectedFragment != null && itemId == R.id.nav_profil) {
                    Bundle bundle = new Bundle();
                    bundle.putString("userId", user.getUid());
                    UserProfileFragment fragment = new UserProfileFragment();
                    fragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment)
                            .addToBackStack(null).commit();
                } else if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                }

                return true;
            };
}