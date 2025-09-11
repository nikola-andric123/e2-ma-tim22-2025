package com.example.rpggame;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rpggame.R;
import com.example.rpggame.activity.FindFriendsActivity;
import com.example.rpggame.activity.FriendRequestsActivity;
import com.example.rpggame.activity.LoginActivity;
import com.example.rpggame.activity.UserProfileActivity;
import com.example.rpggame.domain.Friend;
import com.example.rpggame.domain.UserProfile;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class UserProfileFragment extends Fragment {

    private UserProfile profileToShow;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ImageView userAvatar, titleImage;
    private TextView usernameText, levelText, powerPointsText, experienceText, coinsText, badgesText;
    private Button changePasswordBtn;
    private Button logoutBtn;
    private RecyclerView friendsRecyclerView;
    private String userUID;
    private FriendsAdapter friendsAdapter;
    private List<Friend> friendsList = new ArrayList<>();

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
        titleImage = view.findViewById(R.id.userTitle);
        //changePasswordBtn = view.findViewById(R.id.changePasswordBtn);
        //logoutBtn = view.findViewById(R.id.logout);
        ProgressBar loadingCircle = view.findViewById(R.id.loadingCircle);
        LinearLayout profileContainer = view.findViewById(R.id.profileContainer);

        loadingCircle.setVisibility(View.VISIBLE);
        profileContainer.setVisibility(View.GONE);

        //changePasswordBtn.setOnClickListener(v -> showChangePasswordDialog());
        /*logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  // use "v" instead of "view"
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });*/

        friendsRecyclerView = view.findViewById(R.id.friendsRecyclerView);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        friendsAdapter = new FriendsAdapter(getContext(), friendsList);
        friendsRecyclerView.setAdapter(friendsAdapter);
        db = FirebaseFirestore.getInstance();



        view.findViewById(R.id.btnFindFriends).setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), FindFriendsActivity.class));
        });

        view.findViewById(R.id.btnFriendRequests).setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), FriendRequestsActivity.class));
        });

        ImageView menuIcon = view.findViewById(R.id.menuIcon);

        menuIcon.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(getContext(), v);
            popup.getMenuInflater().inflate(R.menu.user_profile_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();

                if (id == R.id.action_logout) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                    return true;
                } else if (id == R.id.action_change_password) {
                    showChangePasswordDialog();
                    //Toast.makeText(getContext(), "Change Password clicked", Toast.LENGTH_SHORT).show();
                    return true;
                }

                return false;
            });

            popup.show();
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Retrieve userId from arguments instead of getIntent()
        userUID = requireArguments().getString("userId");
        ProgressBar xpProgressBar = view.findViewById(R.id.xpProgressBar);
        TextView xpLabel = view.findViewById(R.id.xpLabel);
        db.collection("users").document(userUID).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        profileToShow = doc.toObject(UserProfile.class);
                        int avatarId = getAvatarId(profileToShow.getAvatar());
                        userAvatar.setImageResource(avatarId);
                        usernameText.setText("Username: " + profileToShow.getUsername());
                        levelText.setText("Level: " + profileToShow.getLevel() + " - " + profileToShow.getTitle());
                        powerPointsText.setText("Power points: " + profileToShow.getPowerPoints());
                        experienceText.setText("Experience points: " + profileToShow.getExperiencePoints());
                        coinsText.setText("Collected coins: " + profileToShow.getCollectedCoins());
                        badgesText.setText("Badges: " + profileToShow.getNumberOfBadges());
                        if (!currentUser.getUid().equals(userUID)) {
                            changePasswordBtn.setVisibility(View.GONE);
                        }
                        loadingCircle.setVisibility(View.GONE);
                        profileContainer.setVisibility(View.VISIBLE);
                        calculateXPbar(xpProgressBar, xpLabel);
                        switch (profileToShow.getLevel()){
                            case 0:
                                titleImage.setImageResource(R.drawable.level0);
                                break;
                            case 1:
                                titleImage.setImageResource(R.drawable.level1);
                                break;
                            case 2:
                                titleImage.setImageResource(R.drawable.level2);
                                break;
                            case 3:
                                titleImage.setImageResource(R.drawable.level3);
                                break;
                            default:
                                titleImage.setImageResource(R.drawable.level3);
                                break;
                        }
                    }
                });

        loadFriends();
        ImageView backArrow = view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            requireActivity().onBackPressed(); // simple back navigation
        });




    }

    private void loadFriends() {
        db.collection("users").document(userUID).collection("friends")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Error loading friends");
                        return;
                    }

                    friendsList.clear();
                    if (value != null) {
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Friend friend = doc.toObject(Friend.class);
                            if (friend != null) {
                                friendsList.add(friend);
                            }
                        }
                        friendsAdapter.notifyDataSetChanged();
                    }
                });
    }
    private void calculateXPbar(ProgressBar xpProgressBar, TextView xpLabel){


        // Example values (replace with values from Firestore)
        int currentXP = profileToShow.getExperiencePoints();       // user XP
        int currentLevel = 0;
        int requiredXP = 200; // XP needed for next level
        while(currentLevel < profileToShow.getLevel()){
            currentLevel ++;
            requiredXP += (int) (Math.ceil((5.0/2.0) * (double) requiredXP/100.0) * 100);
        }


        // Calculate percentage
        int progressPercent = (int) ((currentXP * 100.0f) / requiredXP);

        // Update UI
        xpProgressBar.setProgress(progressPercent);
        xpLabel.setText("XP: " + currentXP + " / " + requiredXP);
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
