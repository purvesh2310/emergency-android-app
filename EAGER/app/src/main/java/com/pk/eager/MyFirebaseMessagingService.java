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
import android.widget.Toast;

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
import com.pk.eager.ReportObject.Packet;

import java.util.List;

import static com.google.android.gms.internal.zzagz.runOnUiThread;

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

    public void onMessageReceived(RemoteMessage remoteMessage){
        Log.d(TAG, "onMessageReceived Called");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        /*
        debugging
         */

        Log.d(TAG, "remote message " + remoteMessage.getData().toString());
        Log.d(TAG, "notification type " + remoteMessage.getData().get("notificationType"));

        /*
        debugging
         */
        if(remoteMessage!=null && user!=null && remoteMessage.getData().get("notificationType")==null){
            showToastMessage("here1");
            showToastMessage("notification type " + remoteMessage.getData().get("notificationType"));
            Log.d(TAG, "notification type " + remoteMessage.getData().get("notificationType"));
            String type = remoteMessage.getData().get("type");
            if(isTypeSubscribedByUser(type)) {
                //push the notification to user's list of notifications
                DatabaseReference newNode = notificationRef.child(user.getUid()).push();
                newNode.setValue(remoteMessage.getData());

                //issue a notification to user
                Log.d(TAG, "Issue a notification");
                sendReportNotification(remoteMessage);
            }
        } else if(remoteMessage!=null && remoteMessage.getData().get("notificationType").equals("ChatNotification")){
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
        } else if(remoteMessage!=null && remoteMessage.getData().get("notificationType").equals("Ack")){
            Log.d(TAG, "Xbee confirmation received");

            showToastMessage("here2");
            String key = remoteMessage.getData().get("key");
            final String msg = remoteMessage.getData().get("msg");


            ValueEventListener packetListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Packet packet = dataSnapshot.getValue(Packet.class);
                    Log.d(TAG, dataSnapshot.getValue().toString());

                    List<String> path = packet.getPath();

                    if(path.size()>0){
                        String xbeeToAddress = path.get(path.size()-2);

                        path.remove(path.size()-1);
                        path.remove(path.size()-1);

                        String data = msg + path;


                        XBee64BitAddress toAddress = new XBee64BitAddress(xbeeToAddress);
                        RemoteXBeeDevice remote = new RemoteXBeeDevice(xbeeManager.getLocalXBeeDevice(), toAddress);
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
            };

            DatabaseReference packetRef = FirebaseDatabase.getInstance().getReference("path").child(key);
            packetRef.addValueEventListener(packetListener);


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
        Log.d(TAG, "send report notification");
        String body = remoteMessage.getData().get("body");
        String key = remoteMessage.getData().get("key");

        String clickAction = "OPEN_VIEW_NOTIFICATION";
        Intent intent = new Intent(clickAction);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(KEY, key);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("EAGER")
                        .setContentText(body);

        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(001, notificationBuilder.build());
        Log.d(TAG, "notified");
    }

    private void showToastMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(getApplicationContext()!=null)
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

}
