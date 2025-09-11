package com.example.rpggame.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rpggame.FriendRequestsAdapter;
import com.example.rpggame.R;
import com.example.rpggame.domain.Friend;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendRequestsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FriendRequestsAdapter adapter;
    private List<Friend> requestList = new ArrayList<>();

    private FirebaseFirestore db;
    private String currentUserUid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);

        recyclerView = findViewById(R.id.friendRequestsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        adapter = new FriendRequestsAdapter(this, requestList, new FriendRequestsAdapter.OnRequestActionListener() {
            @Override
            public void onAccept(Friend friend) {
                acceptRequest(friend);
            }

            @Override
            public void onReject(Friend friend) {
                rejectRequest(friend);
            }
        });

        recyclerView.setAdapter(adapter);

        loadRequests();
    }

    private void loadRequests() {
        db.collection("users").document(currentUserUid)
                .collection("friendRequests")
                .get()
                .addOnSuccessListener(query -> {
                    requestList.clear();
                    for (DocumentSnapshot doc : query) {
                        String fromUid = doc.getId();
                        // Fetch sender’s profile to display
                        db.collection("users").document(fromUid).get()
                                .addOnSuccessListener(userDoc -> {
                                    if (userDoc.exists()) {
                                        String username = userDoc.getString("username");
                                        String profileUrl = userDoc.getString("profileImageUrl");
                                        int level = userDoc.getLong("level") != null ? userDoc.getLong("level").intValue() : 0;

                                        requestList.add(new Friend(fromUid, username, profileUrl, level));
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                    }
                });
    }

    private void acceptRequest(Friend friend) {
        // 1. Add each user to each other's friends collection
        Map<String, Object> friendData = new HashMap<>();
        friendData.put("uid", friend.getUid());
        friendData.put("username", friend.getUsername());
        friendData.put("profileImageUrl", friend.getProfileImageUrl());
        friendData.put("level", friend.getLevel());

        db.collection("users").document(currentUserUid)
                .collection("friends").document(friend.getUid())
                .set(friendData);

        // add current user to friend's collection
        db.collection("users").document(friend.getUid())
                .collection("friends").document(currentUserUid)
                .set(friendData); // you could fetch current user profile for better info

        // 2. Remove the request
        db.collection("users").document(currentUserUid)
                .collection("friendRequests").document(friend.getUid())
                .delete();

        Toast.makeText(this, "Friend request accepted!", Toast.LENGTH_SHORT).show();
    }

    private void rejectRequest(Friend friend) {
        db.collection("users").document(currentUserUid)
                .collection("friendRequests").document(friend.getUid())
                .delete();

        Toast.makeText(this, "Friend request rejected", Toast.LENGTH_SHORT).show();
    }
}