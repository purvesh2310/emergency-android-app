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

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kimpham on 7/17/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService implements IDataReceiveListener {

    final static String KEY = "key";
    final static String TAG = MyFirebaseMessagingService.class.getSimpleName();
    final static String EAGER = "EAGER";
    DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("UserNotification");
    private XBeeManager xbeeManager;

    public void onCreate(){
        super.onCreate();
        xbeeManager = XBeeManagerApplication.getInstance().getXBeeManager();
        if (xbeeManager.getLocalXBeeDevice() != null && xbeeManager.getLocalXBeeDevice().isOpen()) {
            xbeeManager.subscribeDataPacketListener(this);
        }
    }

    @Override
    public void dataReceived(XBeeMessage xbeeMessage){
        Log.d(TAG, "data received in firebase msg");
    }


    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived Called");
        Log.d(TAG, remoteMessage.getData().toString());
        Log.d(TAG, remoteMessage.getData().get("msgType"));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (remoteMessage.getData() != null && remoteMessage.getData().get("msgType")==null) {
            if (user != null) {
                String type = remoteMessage.getData().get("type");
                if (isTypeSubscribedByUser(type)) {
                    //push the notification to user's list of notifications
                    DatabaseReference newNode = notificationRef.child(user.getUid()).push();
                    newNode.setValue(remoteMessage.getData());

                    //issue a notification to user
                    sendReportNotification(remoteMessage);
                }
            } else if (remoteMessage.getData().get("notificationType").equals("ChatNotification")) {
                Intent intent = new Intent(this, HistoryFragment.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                        PendingIntent.FLAG_ONE_SHOT);

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
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
        } else if (remoteMessage.getData() != null && remoteMessage.getData().get("msgType")!=null) {
            Log.d(TAG, "Ack msg received " + remoteMessage.getData());


            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
            notificationBuilder.setContentTitle(EAGER);
            notificationBuilder.setContentText("Report sent to database");
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setSmallIcon(R.drawable.ic_notification);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notificationBuilder.build());

            String pathKey = remoteMessage.getData().get("key");
            final String msg = remoteMessage.getData().get("msg");
            //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);


            DatabaseReference pathRef = FirebaseDatabase.getInstance().getReference("path").child(pathKey);
            final List<String> pathList = new ArrayList<String>();

            pathRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        pathList.add(snapshot.getValue().toString());
                    }

                    Log.d(TAG, pathList.toString());

                    String data = "Message sent "+pathList.toString();
                    if(pathList.size() >=1) {
                        XBee64BitAddress start = new XBee64BitAddress(pathList.get(0));
                        RemoteXBeeDevice remote = new RemoteXBeeDevice(xbeeManager.getLocalXBeeDevice(), start);
                        try {
                            Log.d(TAG, "send message back to device");
                            xbeeManager.sendDataToRemote(data.getBytes(), remote);
                        } catch (XBeeException ex) {
                            Log.d(TAG, ex.toString());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



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
