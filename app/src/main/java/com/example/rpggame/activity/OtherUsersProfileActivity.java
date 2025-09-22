package com.example.rpggame.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rpggame.OtherUsersProfileFragment;
import com.example.rpggame.R;

public class OtherUsersProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_users_profile);

        if (savedInstanceState == null) {
            String userId = getIntent().getStringExtra("userId");

            OtherUsersProfileFragment fragment = new OtherUsersProfileFragment();
            Bundle args = new Bundle();
            args.putString("userId", userId);
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

}
