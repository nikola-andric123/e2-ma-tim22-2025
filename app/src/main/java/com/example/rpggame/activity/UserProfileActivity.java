package com.example.rpggame.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rpggame.R;
import com.example.rpggame.domain.UserProfile;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfileActivity extends AppCompatActivity {

    UserProfile profileToShow;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    private ImageView userAvatar;
    private TextView usernameText, levelText, powerPointsText, experienceText, coinsText, badgesText;
    private Button changePasswordBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);

        userAvatar = findViewById(R.id.userAvatar);
        usernameText = findViewById(R.id.usernameText);
        levelText = findViewById(R.id.levelText);
        powerPointsText = findViewById(R.id.powerPointsText);
        experienceText = findViewById(R.id.experienceText);
        coinsText = findViewById(R.id.coinsText);
        badgesText = findViewById(R.id.badgesText);
        changePasswordBtn = findViewById(R.id.changePasswordBtn);
        ProgressBar loadingCircle = findViewById(R.id.loadingCircle);
        LinearLayout profileContainer = findViewById(R.id.profileContainer);

        loadingCircle.setVisibility(View.VISIBLE);
        profileContainer.setVisibility(View.GONE);


        changePasswordBtn.setOnClickListener(v -> showChangePasswordDialog());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        String userUID = getIntent().getStringExtra("userId");
        db.collection("users").document(userUID).get()
                 .addOnSuccessListener(doc -> {

                             if (doc.exists()) {
                                 profileToShow = doc.toObject(UserProfile.class);
                                 int avatarId = getAvatarId(profileToShow.getAvatar());
                                 userAvatar.setImageResource(avatarId);
                                 usernameText.setText("Username: " + profileToShow.getUsername());
                                 levelText.setText("Level: " + profileToShow.getLevel());
                                 powerPointsText.setText("Power points: " + profileToShow.getPowerPoints());
                                 experienceText.setText("Experience points: " + profileToShow.getExperiencePoints());
                                 coinsText.setText("Collected coins: " + profileToShow.getCollectedCoins());
                                 badgesText.setText("Badges: " + profileToShow.getNumberOfBadges());
                                 if(!currentUser.getUid().equals(userUID)){
                                     changePasswordBtn.setVisibility(View.GONE);
                                 }
                                 // Hide loading, show profile
                                 loadingCircle.setVisibility(View.GONE);
                                 profileContainer.setVisibility(View.VISIBLE);
                             }
                         });

        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // optional, closes profile so back won’t reopen it
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.userProfile), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showChangePasswordDialog() {
        // Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(this);
        final android.view.View dialogView = inflater.inflate(R.layout.dialog_change_password, null);

        final EditText currentPassword = dialogView.findViewById(R.id.currentPassword);
        final EditText newPassword = dialogView.findViewById(R.id.newPassword);
        final EditText confirmPassword = dialogView.findViewById(R.id.confirmPassword);

        new AlertDialog.Builder(this)
                .setTitle("Change Password")
                .setView(dialogView)
                .setPositiveButton("Change", (dialog, which) -> {
                    String current = currentPassword.getText().toString();
                    String newPass = newPassword.getText().toString();
                    String confirm = confirmPassword.getText().toString();

                    if (!newPass.equals(confirm)) {
                        Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    AuthCredential credential = EmailAuthProvider
                            .getCredential(currentUser.getEmail(), current);
                    currentUser.reauthenticate(credential)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Update password
                                    currentUser.updatePassword(newPass)
                                            .addOnCompleteListener(updateTask -> {
                                                if (updateTask.isSuccessful()) {
                                                    Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(this, "Password update failed: " + updateTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(this, "Re-authentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });

                    Toast.makeText(this, "Password changed successfully (mock)", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private int getAvatarId(String avatarName){
        switch (avatarName){
            case "avatar_1":
                return R.drawable.avatar_1;

            case "avatar_2":
                return R.drawable.avatar_2;

            case "avatar_3":
                return R.drawable.avatar_3;

            case "avatar_4":
                return R.drawable.avatar_4;

            default: return -1;
        }
    }
}