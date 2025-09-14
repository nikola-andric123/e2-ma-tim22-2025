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

import java.util.HashMap;
import java.util.Map;

public class ClanInviteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String clanId = intent.getStringExtra("clanId");
        String action = intent.getAction();
        String uid = FirebaseAuth.getInstance().getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if ("ACCEPT_CLAN".equals(action)) {
            // Add to members
            Map<String, Object> memberData = new HashMap<>();
            memberData.put("uid", uid);
            memberData.put("role", "member");
            memberData.put("joinedAt", FieldValue.serverTimestamp());

            db.collection("users").document(uid).get()
                            .addOnSuccessListener(usr -> {
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

                            });

        } else if ("REJECT_CLAN".equals(action)) {
            // Just mark rejected
            db.collection("clans").document(clanId)
                    .collection("invitations").document(uid)
                    .update("status", "rejected");
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
}
