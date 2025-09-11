package com.example.rpggame.activity;

import android.os.Bundle;
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
                });
    }

    private void createClan() {
        String clanName = clanNameInput.getText().toString().trim();
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
                });
    }
}