package com.example.rpggame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

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

            db.collection("clans").document(clanId)
                    .collection("members").document(uid)
                    .set(memberData);

            db.collection("users").document(uid).update("clanId", clanId);

        } else if ("REJECT_CLAN".equals(action)) {
            // Just mark rejected
            db.collection("clans").document(clanId)
                    .collection("invitations").document(uid)
                    .update("status", "rejected");
        }

        // Remove notification
        NotificationManagerCompat.from(context).cancel(1001);
    }
}
