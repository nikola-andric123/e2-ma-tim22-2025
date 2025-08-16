package com.example.rpggame;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.rpggame.R;
import com.example.rpggame.activity.LoginActivity;
import com.example.rpggame.domain.UserProfile;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfileFragment extends Fragment {

    private UserProfile profileToShow;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private ImageView userAvatar;
    private TextView usernameText, levelText, powerPointsText, experienceText, coinsText, badgesText;
    private Button changePasswordBtn;
    private Button logoutBtn;

    public UserProfileFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate fragment layout instead of setContentView()
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userAvatar = view.findViewById(R.id.userAvatar);
        usernameText = view.findViewById(R.id.usernameText);
        levelText = view.findViewById(R.id.levelText);
        powerPointsText = view.findViewById(R.id.powerPointsText);
        experienceText = view.findViewById(R.id.experienceText);
        coinsText = view.findViewById(R.id.coinsText);
        badgesText = view.findViewById(R.id.badgesText);
        changePasswordBtn = view.findViewById(R.id.changePasswordBtn);
        logoutBtn = view.findViewById(R.id.logout);
        ProgressBar loadingCircle = view.findViewById(R.id.loadingCircle);
        LinearLayout profileContainer = view.findViewById(R.id.profileContainer);

        loadingCircle.setVisibility(View.VISIBLE);
        profileContainer.setVisibility(View.GONE);

        changePasswordBtn.setOnClickListener(v -> showChangePasswordDialog());
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  // use "v" instead of "view"
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Retrieve userId from arguments instead of getIntent()
        String userUID = requireArguments().getString("userId");

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
                        if (!currentUser.getUid().equals(userUID)) {
                            changePasswordBtn.setVisibility(View.GONE);
                        }
                        loadingCircle.setVisibility(View.GONE);
                        profileContainer.setVisibility(View.VISIBLE);
                    }
                });

        ImageView backArrow = view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            requireActivity().onBackPressed(); // simple back navigation
        });
    }

    private void showChangePasswordDialog() {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        final View dialogView = inflater.inflate(R.layout.dialog_change_password, null);

        final EditText currentPassword = dialogView.findViewById(R.id.currentPassword);
        final EditText newPassword = dialogView.findViewById(R.id.newPassword);
        final EditText confirmPassword = dialogView.findViewById(R.id.confirmPassword);

        new AlertDialog.Builder(requireContext())
                .setTitle("Change Password")
                .setView(dialogView)
                .setPositiveButton("Change", (dialog, which) -> {
                    String current = currentPassword.getText().toString();
                    String newPass = newPassword.getText().toString();
                    String confirm = confirmPassword.getText().toString();

                    if (!newPass.equals(confirm)) {
                        Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    AuthCredential credential = EmailAuthProvider
                            .getCredential(currentUser.getEmail(), current);
                    currentUser.reauthenticate(credential)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    currentUser.updatePassword(newPass)
                                            .addOnCompleteListener(updateTask -> {
                                                if (updateTask.isSuccessful()) {
                                                    Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(requireContext(), "Password update failed: " + updateTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(requireContext(), "Re-authentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private int getAvatarId(String avatarName) {
        switch (avatarName) {
            case "avatar_1": return R.drawable.avatar_1;
            case "avatar_2": return R.drawable.avatar_2;
            case "avatar_3": return R.drawable.avatar_3;
            case "avatar_4": return R.drawable.avatar_4;
            default: return -1;
        }
    }
}
