package com.example.rpggame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ClanInviteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String clanId = intent.getStringExtra("clanId");
        String senderId = intent.getStringExtra("senderId");
        //String targetToken = intent.getStringExtra("targetToken");
        String action = intent.getAction();
        String uid = FirebaseAuth.getInstance().getUid();
        AtomicReference<String> memberUsername = new AtomicReference<>("");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if ("ACCEPT_CLAN".equals(action)) {
            // Add to members
            Map<String, Object> memberData = new HashMap<>();
            memberData.put("uid", uid);
            memberData.put("role", "member");
            memberData.put("joinedAt", FieldValue.serverTimestamp());

            db.collection("users").document(uid).get()
                            .addOnSuccessListener(usr -> {
                                memberUsername.set(usr.getString("username"));
                                if(!usr.getString("clanId").isEmpty()) {
                                    db.collection("clans").document(usr.getString("clanId")).get()
                                                    .addOnSuccessListener(clan -> {
                                                        if(clan.getString("leaderId").equals(uid)){
                                                            db.collection("clans").document(usr.getString("clanId")).collection("members").get()
                                                                    .addOnSuccessListener(membersDoc -> {
                                                                        for(DocumentSnapshot doc : membersDoc){
                                                                            db.collection("users").document(doc.getString("uid")).update("clanId", "");
                                                                        }
                                                                        db.collection("clans").document(usr.getString("clanId")).collection("members")
                                                                                .document(uid).delete()
                                                                                .addOnSuccessListener(docc->{
                                                                                    db.collection("users").document(uid).update("clanId", clanId);
                                                                                }); //Removes user from previous clan
                                                                    });
                                                            deleteClanCompletely(usr.getString("clanId"));

                                                        }else{
                                                            db.collection("clans").document(usr.getString("clanId")).collection("members")
                                                                    .document(uid).delete()
                                                                    .addOnSuccessListener(docc->{
                                                                        db.collection("users").document(uid).update("clanId", clanId);
                                                                    }); //Removes user from previous clan
                                                        }
                                                    });


                                } else{
                                    db.collection("users").document(uid).update("clanId", clanId);

                                }
                                db.collection("clans").document(clanId)
                                        .collection("members").document(uid)
                                        .set(memberData);
                                db.collection("clans").document(clanId)
                                        .collection("invitations").document(uid)
                                        .update("status", "accepted");
                                Log.d("CLAN RECEIVER", "SenderId: " + senderId + "memberUsername: " + memberUsername.toString());
                                sendAcceptedNotification(senderId, memberUsername.toString());

                            });

        } else if ("REJECT_CLAN".equals(action)) {
            // Just mark rejected
            db.collection("clans").document(clanId)
                    .collection("invitations").document(uid)
                    .update("status", "rejected");
        } else if("CLAN_INVITE_ACCEPTED".equals(action)) {

        }

        // Remove notification
        NotificationManagerCompat.from(context).cancel(1001);
    }
    private void deleteClanCompletely(String clanId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
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

    private void sendAcceptedNotification(String targetUid, String memberUsername) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                                data.put("type", "CLAN_INVITE_ACCEPTED");
                                //data.put("clanId", clanId);

                                JSONObject notification = new JSONObject();
                                notification.put("title", memberUsername + " Accepted your invitation.");
                                notification.put("body", memberUsername + " has became clan member!");

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
