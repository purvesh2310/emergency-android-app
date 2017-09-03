package com.pk.eager;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by kimpham on 7/17/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    final static String KEY = "key";
    final static String TAG = MyFirebaseMessagingService.class.getSimpleName();
    final static String EAGER = "EAGER";
    DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("UserNotification");


    public void onMessageReceived(RemoteMessage remoteMessage){
        Log.d(TAG, "onMessageReceived Called");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null){
            String type = remoteMessage.getData().get("type");
            if(isTypeSubscribedByUser(type)) {
                //push the notification to user's list of notifications
                DatabaseReference newNode = notificationRef.child(user.getUid()).push();
                newNode.setValue(remoteMessage.getData());

                //issue a notification to user
                sendReportNotification(remoteMessage);
            }
        } else if(remoteMessage.getData().get("notificationType").equals("ChatNotification")){
            Intent intent = new Intent(this, HistoryFragment.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Firebase Push Notification")
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, notificationBuilder.build());
        }
    }

    public boolean isTypeSubscribedByUser(String type){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean defaultValue = false;
        boolean value = sharedPreferences.getBoolean(type, defaultValue);
        Log.d(TAG, "Default value "+value);
        return value;
    }

    public void sendReportNotification(RemoteMessage remoteMessage){
        Log.d(TAG, "Body " + remoteMessage.getData().get("body"));
        Log.d(TAG, "Key " + remoteMessage.getData().get("key"));
        String body = remoteMessage.getData().get("body");
        String key = remoteMessage.getData().get("key");

        String clickAction = "OPEN_VIEW_NOTIFICATION";
        Intent intent = new Intent(clickAction);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(KEY, key);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setContentTitle(EAGER);
        notificationBuilder.setContentText(body);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSmallIcon(R.drawable.ic_notification);
        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }



}
