package com.pk.eager.BaseClass;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBeeMessage;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.pk.eager.R;
import com.pk.eager.ReportObject.CompactReport;
import com.pk.eager.ReportObject.Packet;
import com.pk.eager.XBeeManager;
import com.pk.eager.XBeeManagerApplication;

import java.util.List;

public class BaseXbeeActivity extends AppCompatActivity implements IDataReceiveListener{

    private XBeeManager xbeeManager;
    public final String TAG = BaseXbeeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_xbee);
        xbeeManager = XBeeManagerApplication.getInstance().getXBeeManager();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (xbeeManager.getLocalXBeeDevice() != null && xbeeManager.getLocalXBeeDevice().isOpen()) {
            xbeeManager.subscribeDataPacketListener(this);
        }
    }

    @Override
    public void dataReceived(XBeeMessage xbeeMessage){
        showToastMessage("Receive data from xbee channel");
        String data = new String(xbeeMessage.getData());
        receiveDataFromChannel(data);
    }

    // Receive data over XBE/BLE and upload to Firebase
    public void receiveDataFromChannel(String data){
        if(data.equals("a")){
            Toast.makeText(this, "P2P received", Toast.LENGTH_SHORT);
        }else {

            // On Receiving the data, convert it to object and add the XBEE device id in path to server
            Gson gson = new Gson();
            CompactReport cmpReport = gson.fromJson(data, CompactReport.class);

            List<String> pathToServer = cmpReport.getPathToServer();
            String receiverDeviceAddress = xbeeManager.getLocalXBee64BitAddress().toString();
            pathToServer.add(receiverDeviceAddress);

            Packet newPacket = new Packet(pathToServer, FirebaseInstanceId.getInstance().getToken());

            boolean isConnected = checkInternetConnection();

            if (isConnected) {

                // Saving the path to Firebase without report ID. Need to add report id afterwards
                DatabaseReference path = FirebaseDatabase.getInstance().getReference("path").push();
                //path.setValue(pathToServer);
                path.setValue(newPacket);

                DatabaseReference newChild = FirebaseDatabase.getInstance().getReference("Reports").push();
                newChild.setValue(cmpReport);

            } else {
                data = gson.toJson(cmpReport);
                xbeeBroadcast(data);
            }
        }
    }

    public void xbeeBroadcast(String data){
        final String reportData = data;
        Log.d(TAG, "broadcast");
        Thread sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(xbeeManager.getLocalXBeeDevice().isOpen()) {
                        String DATA_TO_SEND = reportData;
                        byte[] dataToSend = DATA_TO_SEND.getBytes();
                        xbeeManager.broadcastData(dataToSend);
                        Log.d(TAG, "Broadcasting ");
                        showToastMessage("Device open and data sent: " + xbeeManager.getLocalXBeeDevice().toString());
                    }else Log.d(TAG, "xbee not open");
                } catch (XBeeException e) {
                    //showToastMessage("error: " + e.getMessage());
                    Log.d("Xbee exception ", e.toString());
                }
            }
        });
        sendThread.start();
    }

    // To check whether device has an active Internet connection
    public boolean checkInternetConnection(){

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    private void showToastMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(this!=null)
                    Toast.makeText(BaseXbeeActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
