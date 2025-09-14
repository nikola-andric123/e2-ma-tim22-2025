package com.example.rpggame.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
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
import com.example.rpggame.domain.Member;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

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
import java.util.Objects;

public class ClanLeaderActivity extends AppCompatActivity {

    private TextView clanNameText;
    private Button deleteClanButton, showMembersButton, showOthersButton;
    private RecyclerView friendsRecyclerView;
    private FloatingActionButton chatFab;
    private FriendsAdapter friendsAdapter;
    private List<Friend> friendsList = new ArrayList<>();
    private List<String> currentUsersFriendsList = new ArrayList<>();
    private List<Member> clanMembers = new ArrayList<>();
    private String createdClanId;
    private String currentUserUid;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String clanId, clanName, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clan_leader);

        clanNameText = findViewById(R.id.clanNameText);
        deleteClanButton = findViewById(R.id.deleteClanButton);
        showMembersButton = findViewById(R.id.showMembersButton);
        showOthersButton = findViewById(R.id.showOthersButton);
        friendsRecyclerView = findViewById(R.id.friendsRecyclerView);
        chatFab = findViewById(R.id.chatFab);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();
        currentUserUid = auth.getCurrentUser().getUid();

        // Get clan data passed from previous activity
        clanId = getIntent().getStringExtra("clanId");
        clanName = getIntent().getStringExtra("clanName");

        clanNameText.setText(clanName);

        // Setup RecyclerView
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        friendsAdapter = new FriendsAdapter(this, friendsList);
        friendsRecyclerView.setAdapter(friendsAdapter);

        db.collection("users").document(currentUserUid).get().addOnSuccessListener( usr -> {
                    usr.getReference()
                            .collection("friends")
                            .get()
                            .addOnSuccessListener(friendsQuery -> {
                                for (DocumentSnapshot friendDoc : friendsQuery) {
                                   //Friend newFriend = friendDoc.toObject(Friend.class);
                                   currentUsersFriendsList.add(friendDoc.getId());
                                   /*if(Objects.equals(friendDoc.getString("clanId"), clanId)){
                                       clanMembers.add(new Member(friendDoc.getId(),friendDoc.getString("role"),friendDoc.getString("username"),
                                               friendDoc.getString("level"),friendDoc.getString("avatar")));
                                   }*/
                                }
                                loadFriends(false);
                            });
                });
        //loadFriends(false); // default show all

        // Delete clan button
        deleteClanButton.setOnClickListener(v -> showDeleteDialog());

        // Filters
        showMembersButton.setOnClickListener(v -> loadFriends(true));
        showOthersButton.setOnClickListener(v -> loadFriends(false));

        // Chat button
        chatFab.setOnClickListener(v -> {
            Toast.makeText(this, "Open clan chat (not implemented)", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadFriends(boolean onlyMembers) {
        db.collection("users").get().addOnSuccessListener(query -> {
            friendsList.clear();
            for (DocumentSnapshot doc : query) {
                String uid = doc.getId();
                if(!currentUsersFriendsList.contains(uid)) continue;
                String name = doc.getString("username");
                String userClanId = doc.getString("clanId");
                int level = doc.getLong("level") != null ? doc.getLong("level").intValue() : 0;
                String avatar = doc.getString("avatar");

                boolean isMember = clanId.equals(userClanId);
                boolean isInvited = false; // TODO: check invitations collection if you store invites

                if (onlyMembers && isMember) friendsList.add(new Friend(uid, name, avatar, level));
                if (!onlyMembers && !isMember) friendsList.add(new Friend(uid, name, avatar, level));


            }
            friendsAdapter.notifyDataSetChanged();
            if(onlyMembers) {
                friendsAdapter.setAlreadyMemberButton();
                friendsAdapter.setMemberMode(FriendsAdapter.Mode.CLAN_MEMBER);
            } else{
                friendsAdapter.setInviteToClan();
                friendsAdapter.setMode(FriendsAdapter.Mode.INVITE_TO_CLAN, this::sendClanInvite);
            }
        });
    }

    private void sendClanInvite(Friend friend) {
        if (clanId == null) {
            Toast.makeText(this, "Create clan first!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> invite = new HashMap<>();
        invite.put("fromUid", currentUserUid);
        invite.put("status", "pending");
        invite.put("sentAt", FieldValue.serverTimestamp());

        db.collection("clans").document(clanId)
                .collection("invitations").document(friend.getUid())
                .set(invite)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Invitation sent to " + friend.getUsername(), Toast.LENGTH_SHORT).show();

                    // ✅ Trigger backend notification
                    sendInviteNotification(friend.getUid(), clanId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to send invite: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void showDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Clan")
                .setMessage("Are you sure you want to delete this clan? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteClan())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteClan() {
        db.collection("clans").document(clanId).collection("members").get()
                .addOnSuccessListener(membersDoc -> {
                   for(DocumentSnapshot doc : membersDoc){
                       db.collection("users").document(doc.getString("uid")).update("clanId", "");
                   }
                });

        db.collection("users").document(userId).update("clanId", "");
        db.collection("clans").document(clanId).delete()
                .addOnSuccessListener(aVoid -> {
                    deleteClanCompletely(clanId);
                    Toast.makeText(this, "Clan deleted", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to delete clan", Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteClanCompletely(String clanId) {
        DocumentReference clanRef = db.collection("clans").document(clanId);

        // Example: delete "members" subcollection
        clanRef.collection("members").get().addOnSuccessListener(query -> {
            WriteBatch batch = db.batch();
            for (DocumentSnapshot doc : query) {
                batch.delete(doc.getReference());
            }
            batch.commit().addOnSuccessListener(aVoid -> {
                // After deleting subcollection, delete the clan itself
                clanRef.delete().addOnSuccessListener(v -> {
                    Log.d("CLAN", "Members fully deleted");
                });
            });
        });
        clanRef.collection("invitations").get().addOnSuccessListener(query -> {
            WriteBatch batch = db.batch();
            for (DocumentSnapshot doc : query) {
                batch.delete(doc.getReference());
            }
            batch.commit().addOnSuccessListener(aVoid -> {
                // After deleting subcollection, delete the clan itself
                clanRef.delete().addOnSuccessListener(v -> {
                    Log.d("CLAN", "Invitations fully deleted");
                });
            });
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
                                data.put("clanId", clanId);

                                JSONObject notification = new JSONObject();
                                notification.put("title", "Clan Invitation");
                                notification.put("body", "You have been invited to join " + clanName + "!");

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