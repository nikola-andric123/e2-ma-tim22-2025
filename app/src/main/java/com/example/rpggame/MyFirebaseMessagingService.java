package com.example.rpggame;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.rpggame.activity.ClanChatActivity;
import com.example.rpggame.activity.ClanLeaderActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";
    private static final String CHANNEL_ID = "fcm_channel";
    private final List<String> recentMessages = new ArrayList<>();


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if ("CLAN_INVITE".equals(remoteMessage.getData().get("type"))) {
            String clanId = remoteMessage.getData().get("clanId");
            String senderId = remoteMessage.getData().get("senderId");

            // Create channel if needed
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        "clan_channel",
                        "Clan Notifications",
                        NotificationManager.IMPORTANCE_HIGH
                );
                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel);
            }

            showClanInviteNotification(clanId, senderId);
        } else if("CLAN_INVITE_ACCEPTED".equals(remoteMessage.getData().get("type"))){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        "clan_channel",
                        "Clan Notifications",
                        NotificationManager.IMPORTANCE_HIGH
                );
                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel);
            }
            if (remoteMessage.getNotification() != null) {
                String title = remoteMessage.getNotification().getTitle();
                String body = remoteMessage.getNotification().getBody();
                String clanId = remoteMessage.getData().get("clanId");
                String clanName = remoteMessage.getData().get("clanName");
                Log.d("FCM", "Notification: title=" + title + " body=" + body);

                // Show the notification manually
                showNotification(title, body, "clan_channel", clanId, clanName);
            }
            // Create channel if needed

        } else if("CLAN_MESSAGE".equals(remoteMessage.getData().get("type"))){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        "clan_message",
                        "Clan Message",
                        NotificationManager.IMPORTANCE_HIGH
                );
                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel);
            }
            if (remoteMessage.getNotification() != null) {
                String title = remoteMessage.getNotification().getTitle();
                String body = remoteMessage.getNotification().getBody();
                String clanId = remoteMessage.getData().get("clanId");
                String clanName = remoteMessage.getData().get("clanName");
                // Show the notification manually
                showNotification(title, body, "clan_message", clanId, clanName);
            }
        }
    }

    private void showNotification(String title, String body, String channelId, String clanId, String clanName) {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "clan_channel",
                    "Clan Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }*/
           Intent intent = channelId.equals("clan_message") ? new Intent(this, ClanChatActivity.class) : new Intent(this, ClanLeaderActivity.class);
           intent.putExtra("clanId", clanId);
           intent.putExtra("clanName", clanName);

           PendingIntent pi = PendingIntent.getActivity(
                   this,
                   0,
                   intent,
                   PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
           );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.notification_bell)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setGroup(channelId); // 🔑 assign to group

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        int messageId = (int) System.currentTimeMillis();
        NotificationManagerCompat.from(this).notify(messageId, builder.build());

        recentMessages.add(title + ": " + body);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle()
                .setSummaryText("Clan messages");
        // only show last 5 messages in the summary
        int start = Math.max(0, recentMessages.size() - 5);
        for (int i = start; i < recentMessages.size(); i++) {
            inboxStyle.addLine(recentMessages.get(i));
        }

        Notification summary = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.notification_bell)
                .setContentTitle(title)
                .setContentText("You have new messages")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setStyle(new NotificationCompat.InboxStyle()
                        .setSummaryText("Clan messages"))
                .setContentIntent(pi)               // ✅ tap opens chat
                .setAutoCancel(true)
                .setGroup(channelId)
                .setGroupSummary(true) // 🔑 marks this as the summary notification
                .build();

        NotificationManagerCompat.from(this).notify(0, summary);
    }

    private void showClanInviteNotification(String clanId, String senderId) {
        Context context = getApplicationContext();

        // Accept intent
        Intent acceptIntent = new Intent(context, ClanInviteReceiver.class);
        acceptIntent.setAction("ACCEPT_CLAN");
        acceptIntent.putExtra("clanId", clanId);
        acceptIntent.putExtra("senderId", senderId);
        Log.d("DATA", "SenderId: " + senderId + " clanId: " + clanId);
        PendingIntent acceptPendingIntent = PendingIntent.getBroadcast(
                context, 0, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Reject intent
        Intent rejectIntent = new Intent(context, ClanInviteReceiver.class);
        rejectIntent.setAction("REJECT_CLAN");
        rejectIntent.putExtra("clanId", clanId);
        PendingIntent rejectPendingIntent = PendingIntent.getBroadcast(
                context, 1, rejectIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "clan_channel")
                .setSmallIcon(R.drawable.notification_bell)
                .setContentTitle("Clan Invitation")
                .setContentText("You were invited to a clan!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true) // stays until handled
                .addAction(R.drawable.back_arrow, "Accept", acceptPendingIntent)
                .addAction(R.drawable.back_arrow, "Reject", rejectPendingIntent);



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        NotificationManagerCompat.from(context).notify(1001, builder.build());
    }


    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
    }



    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("FCM Message")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "FCM Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }
}