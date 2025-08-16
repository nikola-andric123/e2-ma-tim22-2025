package com.example.rpggame;

import android.content.Intent;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if(user == null || !user.isEmailVerified()){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ListaZadatakaFragment()).commit();
        }

        FloatingActionButton fab = findViewById(R.id.fab_dodaj_zadatak);
        fab.setOnClickListener(view -> {
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
                } else if (itemId == R.id.nav_kategorije) { // AŽURIRAN DEO
                    selectedFragment = new KategorijeFragment(); // FRAGMENT KOJI ĆEMO NAPRAVITI
                } else if (itemId == R.id.nav_profil) {
                    // TODO: Kreirati i postaviti ProfilFragment
                    selectedFragment = new UserProfileFragment();

                }
                if(selectedFragment != null && itemId == R.id.nav_profil){
                    Bundle bundle = new Bundle();
                    bundle.putString("userId", user.getUid());
                    UserProfileFragment fragment = new UserProfileFragment();
                    fragment.setArguments(bundle);

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
                }
                else if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                }

                return true;
            };
}
