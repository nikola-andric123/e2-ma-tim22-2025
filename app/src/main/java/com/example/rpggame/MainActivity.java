package com.example.rpggame;

import android.content.Intent;
import android.os.Bundle;
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
            return; // Važno je dodati return da se ostatak koda ne bi izvršio
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

        // DODAT DEO: Listener za test dugme
        Button testBorbaDugme = findViewById(R.id.test_borba_dugme);
        testBorbaDugme.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, BorbaActivity.class);
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
                } else if (itemId == R.id.nav_kategorije) {
                    selectedFragment = new KategorijeFragment();
                } else if (itemId == R.id.nav_profil) {
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
                            .addToBackStack(null) // Omogućava povratak na prethodni fragment
                            .commit();
                }
                else if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                }

                return true;
            };
}