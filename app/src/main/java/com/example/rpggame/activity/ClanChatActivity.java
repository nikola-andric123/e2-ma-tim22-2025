package com.example.rpggame.activity;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rpggame.ChatAdapter;
import com.example.rpggame.R;
import com.example.rpggame.domain.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ClanChatActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "clan_channel_msgs";
    private RecyclerView recyclerView;
    private EditText messageEditText;
    private ImageButton sendButton;
    private ChatAdapter adapter;
    private FirebaseFirestore db;

    private DatabaseReference messagesRef;
    private ChildEventListener messagesListener;

    private String clanId;
    private String clanName;
    private String currentUid;
    private String currentUserName;
    private String avatar;

    private List<ChatMessage> messageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clan_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.messagesRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        clanId = getIntent().getStringExtra("clanId");
        clanName = getIntent().getStringExtra("clanName");
        currentUid = FirebaseAuth.getInstance().getUid();
        db = FirebaseFirestore.getInstance();

        toolbar.setTitle(clanName != null ? clanName : "Clan Chat");
        setSupportActionBar(toolbar);

        adapter = new ChatAdapter(currentUid); // pass messages list + uid
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //createNotificationChannel();
        db.collection("users").document(currentUid).get()
                .addOnSuccessListener(user -> {
                    this.currentUserName = user.getString("username");
                    this.avatar = user.getString("avatar");
                });
        // optional: fetch username
        /*FirebaseDatabase.getInstance("https://rpggame-fcbd1-default-rtdb.firebaseio.com/")
                .getReference("users")
                .child(currentUid)
                .child("username")
                .get()
                .addOnSuccessListener(snapshot -> currentUserName = snapshot.getValue(String.class));
*/
        sendButton.setOnClickListener(v -> sendMessage());

        messagesRef = FirebaseDatabase.getInstance("https://rpggame-fcbd1-default-rtdb.firebaseio.com/")
                .getReference("clanChats")
                .child(clanId)
                .child("messages");

        listenForMessages();
    }

    private void sendMessage() {
        String text = messageEditText.getText().toString().trim();
        if (text.isEmpty()) return;

        String key = messagesRef.push().getKey();
        ChatMessage msg = new ChatMessage(
                currentUid,
                currentUserName != null ? currentUserName : "Unknown",
                text,
                System.currentTimeMillis()
        );
        msg.setAvatar(avatar);
        messagesRef.child(key).setValue(msg);
        messageEditText.setText("");
        db.collection("clans").document(clanId).collection("members").get()
                        .addOnSuccessListener(members -> {
                            for(QueryDocumentSnapshot mem : members){
                                if(!mem.getId().toString().equals(currentUid)) {
                                    db.collection("users").document(mem.getId()).get()
                                            .addOnSuccessListener(usr -> {
                                                sendMessageNotification(mem.getId(), usr.get("username").toString(),
                                                        text);
                                            });
                                }
                            }
                        } );


    }

    private void listenForMessages() {
        messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatMessage message = snapshot.getValue(ChatMessage.class);
                if (message != null) {
                    adapter.addItem(message);
                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);


                }
            }

            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void sendMessageNotification(String targetUid, String senderName, String messageText) {
        // Fetch the friend's FCM token from Firestore
        db.collection("users").document(targetUid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("fcmToken")) {
                        String targetToken = documentSnapshot.getString("fcmToken");

                        new Thread(() -> {
                            try {
                                URL url = new URL("http://10.0.2.2:3000/sendRequestAccepted");
                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                conn.setRequestMethod("POST");
                                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                                conn.setRequestProperty("Accept", "application/json");
                                conn.setDoOutput(true);
                                conn.setDoInput(true);

// Build JSON safely
                                JSONObject data = new JSONObject();
                                data.put("type", "CLAN_MESSAGE");
                                data.put("senderName", currentUserName);
                                data.put("clanName", clanName);
                                data.put("clanId", clanId);
                                data.put("senderId", currentUid);

                                JSONObject notification = new JSONObject();
                                notification.put("title", "New message in " + clanName);
                                notification.put("body",   currentUserName + ": " + messageText);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messagesListener != null) {
            messagesRef.removeEventListener(messagesListener);
        }
    }
}
