package com.example.rpggame.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rpggame.FriendsAdapter;
import com.example.rpggame.R;
import com.example.rpggame.domain.Friend;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateClanActivity extends AppCompatActivity {

    private EditText clanNameInput;
    private Button createClanBtn;
    private RecyclerView friendsRecyclerView;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String currentUserUid;
    private List<Friend> friendsList = new ArrayList<>();
    private FriendsAdapter friendsAdapter;
    String clanName;
    private String createdClanId; // store clanId after creation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_clan);

        clanNameInput = findViewById(R.id.clanNameInput);
        createClanBtn = findViewById(R.id.createClanBtn);
        friendsRecyclerView = findViewById(R.id.friendsRecyclerView);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserUid = mAuth.getCurrentUser().getUid();

        // Setup RecyclerView
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        friendsAdapter = new FriendsAdapter(this, friendsList);
        friendsRecyclerView.setAdapter(friendsAdapter);

        // Load friends
        loadFriends();

        // Create Clan button
        createClanBtn.setOnClickListener(v -> createClan());
    }

    private void loadFriends() {
        db.collection("users").document(currentUserUid).collection("friends")
                .get()
                .addOnSuccessListener(snapshots -> {
                    friendsList.clear();
                    for (DocumentSnapshot doc : snapshots) {
                        String uid = doc.getId();
                        String username = doc.getString("username");
                        String profileUrl = doc.getString("profileImageUrl");
                        int level = doc.getLong("level") != null ? doc.getLong("level").intValue() : 0;

                        friendsList.add(new Friend(uid, username, profileUrl, level));
                    }
                    friendsAdapter.notifyDataSetChanged();

                    // Setup "Invite" button instead of "Add"
                    friendsAdapter.setShowAddButton(true, this::sendClanInvite);
                    friendsAdapter.setMode(FriendsAdapter.Mode.INVITE_TO_CLAN, this::sendClanInvite);
                });
    }

    private void createClan() {
        clanName = clanNameInput.getText().toString().trim();
        //Button createClanBtn = findViewById(R.id.createClanBtn);
        createClanBtn.setEnabled(false);
        createClanBtn.setBackgroundColor(Color.GRAY);
        createClanBtn.setTextColor(Color.WHITE);

        if (clanName.isEmpty()) {
            Toast.makeText(this, "Enter clan name", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference clanRef = db.collection("clans").document();
        createdClanId = clanRef.getId();

        Map<String, Object> clanData = new HashMap<>();
        clanData.put("name", clanName);
        clanData.put("leaderId", currentUserUid);
        clanData.put("createdAt", FieldValue.serverTimestamp());

        clanRef.set(clanData)
                .addOnSuccessListener(aVoid -> {
                    // Add current user as leader
                    Map<String, Object> leaderData = new HashMap<>();
                    leaderData.put("uid", currentUserUid);
                    leaderData.put("role", "leader");
                    leaderData.put("joinedAt", FieldValue.serverTimestamp());

                    clanRef.collection("members").document(currentUserUid).set(leaderData);
                    db.collection("users").document(currentUserUid).update("clanId", createdClanId);

                    Toast.makeText(this, "Clan created!", Toast.LENGTH_SHORT).show();
                });
    }

    private void sendClanInvite(Friend friend) {
        if (createdClanId == null) {
            Toast.makeText(this, "Create clan first!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> invite = new HashMap<>();
        invite.put("fromUid", currentUserUid);
        invite.put("status", "pending");
        invite.put("sentAt", FieldValue.serverTimestamp());

        db.collection("clans").document(createdClanId)
                .collection("invitations").document(friend.getUid())
                .set(invite)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Invitation sent to " + friend.getUsername(), Toast.LENGTH_SHORT).show();

                    // ✅ Trigger backend notification
                    sendInviteNotification(friend.getUid(), createdClanId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to send invite: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void sendInviteNotification(String targetUid, String clanName) {
        // Fetch the friend's FCM token from Firestore
        db.collection("users").document(targetUid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("fcmToken")) {
                        String targetToken = documentSnapshot.getString("fcmToken");
                        Toast.makeText(this, "Target Token " + targetToken, Toast.LENGTH_SHORT).show();

                        new Thread(() -> {
                            try {
                                URL url = new URL("http://10.0.2.2:3000/send");
                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                conn.setRequestMethod("POST");
                                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                                conn.setRequestProperty("Accept", "application/json");
                                conn.setDoOutput(true);
                                conn.setDoInput(true);

// Build JSON safely
                                JSONObject data = new JSONObject();
                                data.put("type", "CLAN_INVITE");
                                data.put("clanId", createdClanId);
                                data.put("senderId", currentUserUid);

                                JSONObject notification = new JSONObject();
                                notification.put("title", "Clan Invitation");
                                notification.put("body", "You have been invited to join " + this.clanName + "!");

                                JSONObject body = new JSONObject();
                                body.put("targetToken", targetToken);
                                body.put("data", data);
                                body.put("notification", notification);

                                String jsonString = body.toString();
                                Log.d("FCM_REQUEST", "Sending JSON: " + jsonString);

// Write JSON body
                                try (OutputStream os = conn.getOutputStream()) {
                                    byte[] input = jsonString.getBytes(StandardCharsets.UTF_8);
                                    os.write(input, 0, input.length);
                                    os.flush();
                                }

// Read response
                                int code = conn.getResponseCode();
                                BufferedReader br = new BufferedReader(
                                        new InputStreamReader(
                                                (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream(),
                                                StandardCharsets.UTF_8
                                        )
                                );

                                StringBuilder response = new StringBuilder();
                                String line;
                                while ((line = br.readLine()) != null) {
                                    response.append(line.trim());
                                }
                                Log.d("FCM_REQUEST", "Response code: " + code + " | Response: " + response);

                            } catch (Exception e) {
                                Log.e("FCM_REQUEST", "Error sending notification", e);
                            }
                        }).start();

                    } else {
                        Log.e("FCM_REQUEST", "No FCM token found for user: " + targetUid);
                    }
                })
                .addOnFailureListener(e -> Log.e("FCM_REQUEST", "Failed to fetch user FCM token", e));
    }
}