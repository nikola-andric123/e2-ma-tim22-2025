package com.example.rpggame;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rpggame.R;
import com.example.rpggame.activity.ClanLeaderActivity;
import com.example.rpggame.activity.ClanMemberActivity;
import com.example.rpggame.activity.CreateClanActivity;
import com.example.rpggame.activity.FindFriendsActivity;
import com.example.rpggame.activity.FriendRequestsActivity;
import com.example.rpggame.activity.LoginActivity;
import com.example.rpggame.activity.UserProfileActivity;
import com.example.rpggame.domain.Friend;
import com.example.rpggame.domain.UserProfile;
import com.example.rpggame.helper.QRCodeHelper;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OtherUsersProfileFragment extends Fragment {

    private UserProfile profileToShow;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ImageView userAvatar, titleImage;
    private TextView usernameText, levelText, powerPointsText, experienceText, coinsText, badgesText;


    private String userUID;
    private String currentUserUid;



    public OtherUsersProfileFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate fragment layout instead of setContentView()
        return inflater.inflate(R.layout.fragment_other_users_profile, container, false);
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
        ImageView qrImage = view.findViewById(R.id.qrImage);


        ProgressBar loadingCircle = view.findViewById(R.id.loadingCircle);
        LinearLayout profileContainer = view.findViewById(R.id.profileContainer);

        loadingCircle.setVisibility(View.VISIBLE);
        profileContainer.setVisibility(View.GONE);





        db = FirebaseFirestore.getInstance();










        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserUid = mAuth.getCurrentUser().getUid();

        // Retrieve userId from arguments instead of getIntent()
        userUID = requireArguments().getString("userId");
        Bitmap qrCode = QRCodeHelper.generateQRCode(userUID, 512);
        qrImage.setImageBitmap(qrCode);
        ProgressBar xpProgressBar = view.findViewById(R.id.xpProgressBar);
        TextView xpLabel = view.findViewById(R.id.xpLabel);
        db.collection("users").document(userUID)
                .addSnapshotListener((doc, e) -> {
                    if (e != null) {
                        Log.e("UserProfile", "Listen failed", e);
                        return;
                    }
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

                    view.findViewById(R.id.scanQrCode).setOnClickListener(v -> {
                        startQRScanner();

                    });


                });




        ImageView backArrow = view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            requireActivity().onBackPressed(); // simple back navigation
        });




    }

    private void startQRScanner() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan a friend's QR code");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureActivity.class); // use default ZXing activity
        barcodeLauncher.launch(options);
    }

    // Register for scan result
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if(result.getContents() != null) {
                    String scannedUserId = result.getContents();
                    addFriend();
                }
            }
    );
    private void addFriend() {


        Map<String, Object> request = new HashMap<>();
        request.put("fromUid", currentUserUid);
        //request.put("fromUsername", currentUserProfile.getUsername()); // assuming you have this
        request.put("status", "pending");
        request.put("timestamp", FieldValue.serverTimestamp());

        db.collection("users")
                .document(userUID)
                .collection("friendRequests")
                .document(currentUserUid) // request ID = sender
                .set(request)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(requireContext(), "Friend request sent!", Toast.LENGTH_SHORT).show()

                )
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
        db.collection("users")
                .document(currentUserUid)
                .collection("sentRequests")
                .document(userUID) // sent request ID = receiver
                .set(request)

                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
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
