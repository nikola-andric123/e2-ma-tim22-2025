package com.example.rpggame;

import android.Manifest;
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

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";
    private static final String CHANNEL_ID = "fcm_channel";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().get("type").equals("CLAN_INVITE")) {
            String clanId = remoteMessage.getData().get("clanId");
            showClanInviteNotification(clanId);
        }
    }

    private void showClanInviteNotification(String clanId) {
        Context context = getApplicationContext();

        // Accept intent
        Intent acceptIntent = new Intent(context, ClanInviteReceiver.class);
        acceptIntent.setAction("ACCEPT_CLAN");
        acceptIntent.putExtra("clanId", clanId);
        PendingIntent acceptPendingIntent = PendingIntent.getBroadcast(
                context, 0, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Reject intent
        Intent rejectIntent = new Intent(context, ClanInviteReceiver.class);
        rejectIntent.setAction("REJECT_CLAN");
        rejectIntent.putExtra("clanId", clanId);
        PendingIntent rejectPendingIntent = PendingIntent.getBroadcast(
                context, 1, rejectIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "clan_channel")
                .setSmallIcon(R.drawable.back_arrow)
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