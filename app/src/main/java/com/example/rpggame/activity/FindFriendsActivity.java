package com.example.rpggame.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.rpggame.domain.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindFriendsActivity extends AppCompatActivity {

    private EditText searchUsername;
    private RecyclerView searchResultsRecyclerView;
    private FriendsAdapter searchAdapter;
    private List<Friend> searchResults = new ArrayList<>();
    //private UserProfile currentUserProfile;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String currentUserUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserUid = mAuth.getCurrentUser().getUid();

        searchUsername = findViewById(R.id.searchUsername);
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchAdapter = new FriendsAdapter(this, searchResults);
        searchResultsRecyclerView.setAdapter(searchAdapter);

        searchUsername.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (!query.isEmpty()) {
                    searchUsers(query);
                } else {
                    searchResults.clear();
                    searchAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void searchUsers(String query) {
        db.collection("users")
                .whereGreaterThanOrEqualTo("username", query)
                .whereLessThanOrEqualTo("username", query + "\uf8ff")
                .get()
                .addOnSuccessListener(snapshots -> {
                    searchResults.clear();
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        if (!doc.getId().equals(currentUserUid)) { // don't show self
                            String uid = doc.getId();
                            String username = doc.getString("username");
                            String profileUrl = doc.getString("profileImageUrl");
                            int level = doc.getLong("level") != null ? doc.getLong("level").intValue() : 0;

                            searchResults.add(new Friend(uid, username, profileUrl, level));

                        }
                    }
                    searchAdapter.notifyDataSetChanged();
                    searchAdapter.setShowAddButton(true, this::addFriend);



                });
    }

    // Called when "Add Friend" button is clicked
    private void addFriend(Friend friend) {


        Map<String, Object> request = new HashMap<>();
        request.put("fromUid", currentUserUid);
        //request.put("fromUsername", currentUserProfile.getUsername()); // assuming you have this
        request.put("status", "pending");
        request.put("timestamp", FieldValue.serverTimestamp());

        db.collection("users")
                .document(friend.getUid())
                .collection("friendRequests")
                .document(currentUserUid) // request ID = sender
                .set(request)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Friend request sent!", Toast.LENGTH_SHORT).show()

                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
        db.collection("users")
                .document(currentUserUid)
                .collection("sentRequests")
                .document(friend.getUid()) // sent request ID = receiver
                .set(request)

                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
