package com.example.rpggame.activity;

import android.app.AlertDialog;
import android.os.Bundle;
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

import com.example.rpggame.MembersAdapter;
import com.example.rpggame.R;
import com.example.rpggame.domain.Member;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ClanMemberActivity extends AppCompatActivity {

    private TextView clanNameText;
    private Button leaveClanButton;
    private RecyclerView membersRecyclerView;
    private MembersAdapter membersAdapter;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String clanId, clanName, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clan_member);

        clanNameText = findViewById(R.id.clanNameText);
        leaveClanButton = findViewById(R.id.leaveClanButton);
        membersRecyclerView = findViewById(R.id.membersRecyclerView);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();

        // Passed from previous activity
        clanId = getIntent().getStringExtra("clanId");
        clanName = getIntent().getStringExtra("clanName");

        clanNameText.setText(clanName);

        // Setup members list
        membersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        membersAdapter = new MembersAdapter(new ArrayList<>());
        membersRecyclerView.setAdapter(membersAdapter);

        loadMembers();

        leaveClanButton.setOnClickListener(v -> showLeaveDialog());
    }

    private void loadMembers() {
        db.collection("clans").document(clanId).collection("members").get()
                .addOnSuccessListener(query -> {
                    List<Member> members = new ArrayList<>();
                    for (DocumentSnapshot doc : query) {
                        String userId = doc.getString("uid");
                        String role = doc.getString("role");
                        db.collection("users").document(userId).get()
                                        .addOnSuccessListener(usr -> {
                                            String username = usr.getString("username");
                                            String level = usr.getLong("level").toString();
                                            String avatar = usr.getString("avatar");
                                            members.add(new Member(userId,role,username,level,avatar));
                                            membersAdapter.updateList(members);
                                        });

                    }
                    //membersAdapter.updateList(members); //maybe needs to be in onSuccessListener??
                });
    }

    private void showLeaveDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Leave Clan")
                .setMessage("Are you sure you want to leave this clan?")
                .setPositiveButton("Leave", (dialog, which) -> leaveClan())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void leaveClan() {
        db.collection("users").document(userId)
                .update("clanId", "")
                .addOnSuccessListener(aVoid -> {
                    db.collection("clans").document(clanId).collection("members")
                                    .document(userId).delete()
                                    .addOnSuccessListener(res -> {
                                        Toast.makeText(this, "You left the clan", Toast.LENGTH_SHORT).show();
                                        finish(); // Close activity
                                    });

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to leave clan", Toast.LENGTH_SHORT).show();
                });
    }
}
