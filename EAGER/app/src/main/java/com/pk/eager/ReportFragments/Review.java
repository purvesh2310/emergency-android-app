package com.pk.eager.ReportFragments;


import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeMessage;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.pk.eager.BluetoothChatService;
import com.pk.eager.BluetoothDeviceListActivity;
import com.pk.eager.Dashboard;
import com.pk.eager.LocationUtils.GeoConstant;
import com.pk.eager.LocationUtils.GeocodeIntentService;
import com.pk.eager.R;
import com.pk.eager.ReportObject.CompactReport;
import com.pk.eager.ReportObject.IncidentReport;
import com.pk.eager.ReportObject.Packet;
import com.pk.eager.ReportObject.Utils;
import com.pk.eager.XBeeManager;
import com.pk.eager.XBeeManagerApplication;
import com.pk.eager.db.handler.DatabaseHandler;
import com.pk.eager.db.model.Report;
import com.pk.eager.util.CompactReportUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.google.android.gms.internal.zzagz.runOnUiThread;

public class Review extends Fragment implements IDataReceiveListener {

    private static final String REPORT = "report";
    private IncidentReport incidentReport;
    private static final String TAG = "Review";
    private DatabaseReference db;
    private AddressResultReceiver resultReceiver;
    private Location location;
    private String phoneNumber;
    Button submit;

    private XBeeManager xbeeManager;

    private ImageButton internetButton;
    private ImageButton xbeeButton;
    private ImageButton bluetoothButton;

    private LinearLayout internetLayout;
    private LinearLayout xbeeLayout;

    private int sendingMethod = 0;
    private int receivingMethod = 0;

    private TextView selectMethodTextView;

    private static int REQUEST_ENABLE_BT = 100;
    private static int REQUEST_SELECT_BT = 101;

    // Message types sent from the BluetoothMessageService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothMessageService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    private BluetoothAdapter mBluetoothAdapter;
    private String mConnectedDeviceName = null;
    private StringBuffer mOutStringBuffer;
    private BluetoothChatService mChatService = null;

    public Review() {
        // Required empty public constructor
    }

    public static Review newInstance(IncidentReport report) {
        Review fragment = new Review();
        Bundle args = new Bundle();
        args.putParcelable(REPORT, report);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            incidentReport = getArguments().getParcelable(REPORT);
        }else {
            incidentReport = new IncidentReport();
        }

        setHasOptionsMenu(true);

        incidentReport = Dashboard.incidentReport;
        db = FirebaseDatabase.getInstance().getReference("Reports");

        resultReceiver = new AddressResultReceiver(null);
        xbeeManager = XBeeManagerApplication.getInstance().getXBeeManager();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (xbeeManager.getLocalXBeeDevice() != null && xbeeManager.getLocalXBeeDevice().isOpen()) {
            xbeeManager.subscribeDataPacketListener(this);
        }
    }

    public void getAddress(){
        Log.d(TAG, "Starting service");
        Intent intent = new Intent(this.getActivity(), GeocodeIntentService.class);
        intent.putExtra(GeoConstant.RECEIVER, resultReceiver);
        intent.putExtra(GeoConstant.FETCH_TYPE_EXTRA, GeoConstant.COORDINATE);
        intent.putExtra(GeoConstant.LOCATION_DATA_EXTRA, location);
        Log.d(TAG, location.getLongitude()+"");
        getActivity().startService(intent);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_review, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Review");

        submit = (Button) this.getView().findViewById(R.id.button_review_submit);

        TextView trap = new TextView(getContext());
        TextView medical = new TextView(getContext());
        TextView fire = new TextView(getContext());
        TextView police = new TextView(getContext());
        TextView utility = new TextView(getContext());
        TextView traffic = new TextView(getContext());

        formatReviewInformationTextView(trap);
        formatReviewInformationTextView(medical);
        formatReviewInformationTextView(fire);
        formatReviewInformationTextView(police);
        formatReviewInformationTextView(utility);
        formatReviewInformationTextView(traffic);

        trap.setText(incidentReport.getReport(Constant.TRAP).toString());
        medical.setText(incidentReport.getReport(Constant.MEDICAL).toString());
        fire.setText(incidentReport.getReport(Constant.FIRE).toString());
        police.setText(incidentReport.getReport(Constant.POLICE).toString());
        utility.setText(incidentReport.getReport(Constant.UTILITY).toString());
        traffic.setText(incidentReport.getReport(Constant.TRAFFIC).toString());

        LinearLayout layout = (LinearLayout) this.getView().findViewById(R.id.view_review);

        if(!trap.getText().toString().isEmpty()) {
            setReviewInformationOnScreen(layout, Constant.TRAP);
        }
        if(!medical.getText().toString().isEmpty()) {
            setReviewInformationOnScreen(layout, Constant.MEDICAL);
        }
        if(!fire.getText().toString().isEmpty()) {
            setReviewInformationOnScreen(layout, Constant.FIRE);
        }
        if(!police.getText().toString().isEmpty()) {
            setReviewInformationOnScreen(layout, Constant.POLICE);
        }
        if(!utility.getText().toString().isEmpty()) {
            setReviewInformationOnScreen(layout, Constant.UTILITY);
        }
        if(!traffic.getText().toString().isEmpty()) {
            setReviewInformationOnScreen(layout, Constant.TRAFFIC);
        }

        selectMethodTextView = (TextView) view.findViewById(R.id.selectMethodMessage);

        internetButton = (ImageButton) view.findViewById(R.id.internetOptionBtn);
        xbeeButton = (ImageButton) view.findViewById(R.id.xbeeOptionBtn);
        bluetoothButton = (ImageButton) view.findViewById(R.id.bluetoothOptionButton);

        internetLayout = (LinearLayout) view.findViewById(R.id.internetLayout);
        xbeeLayout = (LinearLayout) view.findViewById(R.id.xbeeLayout);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter.isEnabled()) {
            mChatService = new BluetoothChatService(getActivity(), mHandler);
            mOutStringBuffer = new StringBuffer("");
        }

        setupSendButtonOptions();
        getphoneNumber();
        setButtonListener();
    }

    public void setReviewInformationOnScreen(LinearLayout layout, int reportType){

        CardView cardView = (CardView) LayoutInflater.from(getContext())
                .inflate(R.layout.row_review_list, null, false);

        TextView reportTitle = (TextView) cardView.findViewById(R.id.reviewTitleTextView);

        String reportCategory = incidentReport.getReport(reportType).getType();
        reportTitle.setText(reportCategory);

        TextView reportInformation = (TextView) cardView.findViewById(R.id.reviewInformationTextView);

        String info = incidentReport.getReport(reportType).toString();
        info = info.replace(reportCategory.toUpperCase()+"\n","");

        reportInformation.setText(info);
        layout.addView(cardView);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }

    public void getphoneNumber(){
        //get phone number from sharedPreference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        phoneNumber = "";
        phoneNumber = sharedPreferences.getString(Constant.PHONE_NUMBER, phoneNumber);
        if (phoneNumber==null)
            phoneNumber = "";
    }

    public void setButtonListener(){

        submit.setEnabled(false);

        submit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                showSubmitConfirmationDialog();
            }
        });

        Button additional = (Button) this.getView().findViewById(R.id.button_review_additional);
        additional.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Fragment fragment = Dashboard.incidentType;
                FragmentTransaction ft = getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrame, fragment)
                        .addToBackStack("review");
                ft.commit();
            }
        });

        internetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                selectMethodTextView.setText("Selected Method: Internet");
                sendingMethod = 1;
                submit.setEnabled(true);
            }
        });

        xbeeButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                selectMethodTextView.setText("Selected Method: XBee");
                sendingMethod = 2;
                submit.setEnabled(true);
            }
        });

        bluetoothButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                selectMethodTextView.setText("Selected Method: Bluetooth");
                sendingMethod = 3;
                configureBluetooth();
                submit.setEnabled(true);
            }
        });

    }

    public void sendP2P(){

        Thread sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                XBee64BitAddress address64 = new XBee64BitAddress("0013A2004125D261");
                RemoteXBeeDevice remote = new RemoteXBeeDevice(xbeeManager.getLocalXBeeDevice(),address64);
                String data = "a";
                byte[] bytedata = data.getBytes();
                try {
                    xbeeManager.sendDataToRemote(bytedata, remote);
                    Log.d(TAG, "sending data " );
                }catch(XBeeException e){
                    Log.d(TAG, e.toString());
                }
            }
        });
        sendThread.start();
    }

    // Formats the TextView to show in Review Screen
    public void formatReviewInformationTextView(TextView textView){

        if (Build.VERSION.SDK_INT < 23) {
            textView.setTextAppearance(getContext(), R.style.question);
        } else {
            textView.setTextAppearance(R.style.question);
        }
    }

    public void sendNotificationToZipCode(String zipcode, String key, String message, String type){
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("notificationRequests");

        Map notification = new HashMap<>();
        notification.put("zipcode", zipcode);
        notification.put("key", key);
        notification.put("message", message);
        notification.put("type", type);
        Log.d(TAG, "Push notification " + key);
        notificationRef.push().setValue(notification);

    }

    public void setupSendButtonOptions(){

        selectMethodTextView.setText("Please select One method");

        boolean isInternetAvailable = checkInternetConnection();

        if(!isInternetAvailable){
            internetLayout.setVisibility(View.GONE);
        }

        if (xbeeManager.getLocalXBeeDevice() == null || !xbeeManager.getLocalXBeeDevice().isOpen()) {
            xbeeLayout.setVisibility(View.GONE);
        }
    }


    class AddressResultReceiver extends android.os.ResultReceiver {
        public AddressResultReceiver(Handler handler){
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, final Bundle resultData) {
            Log.d(TAG, "onReceiveResult");
            if (resultCode == GeoConstant.SUCCESS_RESULT) { //when the thing is done, result is passed back here
                final Address address = resultData.getParcelable(GeoConstant.RESULT_DATA); //this retrieve the address
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {  //this part is where you put whatever you want to do

                        final String locString = address.getLongitude()+"_"+address.getLatitude();

                        DatabaseReference newChild = db.push();
                        final String key = newChild.getKey();
                        String reportType = incidentReport.getFirstType();

                        Log.d(TAG, "type "+ reportType);

                        IncidentReport smallerSize = Utils.compacitize(incidentReport);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                        String timestamp = simpleDateFormat.format(new Date());
                        final CompactReport compact = new CompactReport(smallerSize, location.getLongitude(), location.getLatitude(), phoneNumber, "Report", timestamp);


                        newChild.setValue(compact, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                Dashboard.incidentType = null;
                                Dashboard.incidentReport = new IncidentReport("Bla");
                                saveReportForHistory(compact, key);
                                getActivity().getSupportFragmentManager().
                                        popBackStackImmediate("chooseAction", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                submit.setText("SUBMITTED");
                            }
                        });

                        sendNotificationToZipCode(locString, key, Utils.notificationMessage(compact), reportType);

			            // This is a way to know that which device create the alert, store the information on Firebase (NB)
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        ref.child("ReportOwner").child(key).child("owner").
                                setValue(FirebaseInstanceId.getInstance().getToken());

                    }
                });
            }else{
                Log.d(TAG, "Unable to find longitude latitude");
            }
        }
    }

    // To check whether device has an active Internet connection
    public boolean checkInternetConnection(){

        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    // Display Dialog box for the confirmation of the report submission
    public void showSubmitConfirmationDialog(){
        SubmitDialog dialog = new SubmitDialog();
        dialog.showDialog(getActivity(),"Are you ready to submit the report?");
    }

    // To send the data using XBE/BLE mode of communication
    public void sendDataOverChannel(String data){
        Log.d(TAG,"Sending data over channel");
        xbeeBroadcast(data);
    }

    // Receive data over XBE/BLE and upload to Firebase
    public void receiveDataFromChannel(String data){
        List<String> pathToServer = null;
        Packet newPacket = null;

        Gson gson = new Gson();
        CompactReport cmpReport = gson.fromJson(data, CompactReport.class);

        boolean isConnected = checkInternetConnection();

        if(receivingMethod == 2) {
            // On Receiving the data, convert it to object and add the XBEE device id in path to server
            pathToServer = cmpReport.getPathToServer();
            String receiverDeviceAddress = xbeeManager.getLocalXBee64BitAddress().toString();
            pathToServer.add(receiverDeviceAddress);
        }


        if (isConnected) {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
            String timestamp = simpleDateFormat.format(new Date());
            cmpReport.setTimestamp(timestamp);

            if(receivingMethod == 2) {
                newPacket = new Packet(pathToServer, FirebaseInstanceId.getInstance().getToken());
                // Saving the path to Firebase without report ID. Need to add report id afterwards
                DatabaseReference path = FirebaseDatabase.getInstance().getReference("path").push();
                path.setValue(newPacket);
            }

            DatabaseReference newChild = db.push();
            newChild.setValue(cmpReport, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    Dashboard.incidentType = null;
                    getActivity().getSupportFragmentManager()
                            .popBackStackImmediate("chooseAction", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    submit.setText("SUBMITTED");
                }
            });

        } else {

            if(receivingMethod == 3){

                if (xbeeManager.getLocalXBeeDevice() != null && xbeeManager.getLocalXBeeDevice().isOpen()) {

                    pathToServer = cmpReport.getPathToServer();

                    if(pathToServer == null){
                        pathToServer = new ArrayList<String>();
                    }

                    String receiverDeviceAddress = xbeeManager.getLocalXBee64BitAddress().toString();
                    pathToServer.add(receiverDeviceAddress);
                    cmpReport.setPathToServer(pathToServer);
                }
            }
            data = gson.toJson(cmpReport);
            sendDataOverChannel(data);
        }
    }

    // Saving reports locally for the History
    public void saveReportForHistory(CompactReport cmpReport, String key){

        CompactReportUtil cmpUtil = new CompactReportUtil();
        Map<String, String> reportData = cmpUtil.parseReportData(cmpReport,"info");

        String uid = key;
        String title = reportData.get("title");
        String information = reportData.get("information");
        double latitude = cmpReport.getLatitude();
        double longitude = cmpReport.getLongitude();
        long unixTime = System.currentTimeMillis() / 1000L;

        Report report = new Report(uid, title, information, latitude, longitude, unixTime);

        DatabaseHandler db = new DatabaseHandler(getContext());
        db.addReport(report);
    }

    public void xbeeBroadcast(String data){
        final String reportData = data;
        Log.d(TAG, "broadcast");
        Thread sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, " in xbee broadcast thread");
                    if(xbeeManager.getLocalXBeeDevice().isOpen()) {
                        String DATA_TO_SEND = reportData;
                        byte[] dataToSend = DATA_TO_SEND.getBytes();
                        xbeeManager.broadcastData(dataToSend);
                        Log.d(TAG, "Broadcasting ");
                        showToastMessage("Device open and data sent: " + xbeeManager.getLocalXBeeDevice().toString());
                        showResultDialog("Success","Data successfully sent over XBee.");
                    }else Log.d(TAG, "xbee not open");
                } catch (XBeeException e) {
                    Log.d("Xbee exception ", e.toString());
                    showResultDialog("Error","Something went wrong. Please try again.");
                }
            }
        });
        sendThread.start();
    }
    @Override
    public void dataReceived(XBeeMessage xbeeMessage){
        sendNotification("Offline report submitted to database");
        showToastMessage("Received data from XBee channel");
        String data = new String(xbeeMessage.getData());
        receivingMethod = 2;
        showToastMessage("data received from: "+ xbeeMessage.getDevice().get64BitAddress()+ ", message: "+new String(xbeeMessage.getData()));
        receiveDataFromChannel(data);

    }

    /**
     * Displays the given message.
     *
     * @param message The message to show.
     */
    private void showToastMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(getActivity()!=null)
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void sendNotification(String message){
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this.getContext())
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("EAGER")
                        .setContentText(message);
        NotificationManager notificationManager = (NotificationManager) getActivity()
                                                    .getSystemService(getActivity().NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());
        Log.d(TAG, "Notify");
    }

    class SubmitDialog {

        public void showDialog(Activity activity, String msg) {

            final Dialog dialog = new Dialog(activity);

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_submit_report);

            TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
            text.setText(msg);


            Button dialogNoButton = (Button) dialog.findViewById(R.id.btn_dialog_no);

            Button dialogYesButton = (Button) dialog.findViewById(R.id.btn_dialog_yes);

            dialogNoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialogYesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    switch (sendingMethod){
                        case 1:
                            sendDataOverInternet();
                            break;
                        case 2:
                            sendDataOverXBee();
                            break;
                        case 3:
                            String data =  getReportDataAsJSON();
                            sendDataOverBluetooth(data);
                            break;
                    }
                }
            });

            dialog.show();
        }
    }

    public void sendDataOverInternet(){

        location = Dashboard.location;

        if (location != null) {
            getAddress();
        } else {
            Toast.makeText(getContext(), "Location " + location, Toast.LENGTH_SHORT);
        }
    }

    public void sendDataOverBluetooth(String message){

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);

            showResultDialog("Success","Report successfully sent over Bluetooth.");
        }

    }

    public void sendDataOverXBee(){

        IncidentReport smallerSize = Utils.compacitize(incidentReport);

        location = Dashboard.location;

        CompactReport compact = new CompactReport(smallerSize, location.getLongitude(),
                location.getLatitude(), phoneNumber, "Report", null);

        // Setting XBEE address of the originator device
        String deviceAddress = xbeeManager.getLocalXBee64BitAddress().toString();

        List<String> pathToServer = new ArrayList<String>();
        pathToServer.add(deviceAddress);
        compact.setPathToServer(pathToServer);

        UUID keyUUID = UUID.randomUUID();
        String key = keyUUID.toString();

        saveReportForHistory(compact, key);

        Gson gson = new Gson();
        String data = gson.toJson(compact);
        sendDataOverChannel(data);
    }

    private void configureBluetooth(){

        if (mBluetoothAdapter == null) {
            Toast.makeText(getActivity(), "Bluetooth not supported. Cannot Send Message.", Toast.LENGTH_LONG).show();
        } else {
            // Starting Bluetooth process
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                Intent intent = new Intent(getContext(), BluetoothDeviceListActivity.class);
                startActivityForResult(intent, REQUEST_SELECT_BT);
            }
        }
    }

    public String getReportDataAsJSON(){

        location = Dashboard.location;
        IncidentReport smallerSize = Utils.compacitize(incidentReport);

        CompactReport compact = new CompactReport(smallerSize, location.getLongitude(),
                location.getLatitude(), phoneNumber, "Report", null);

        UUID keyUUID = UUID.randomUUID();
        String key = keyUUID.toString();

        saveReportForHistory(compact, key);

        Gson gson = new Gson();
        String data = gson.toJson(compact);

        return data;

    }

    @Override
    public void onResume() {
        super.onResume();

        if (mChatService != null) {
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                mChatService.start();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();

            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            Log.i("MessageSTATE", "-->CONNECTED");
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            Log.i("MessageSTATE", "-->CONNECTING");
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                            Log.i("MessageSTATE", "-->LISTENING");
                        case BluetoothChatService.STATE_NONE:
                            Log.i("MessageSTATE", "-->NONE");
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Toast.makeText(activity,readMessage, Toast.LENGTH_LONG).show();
                    receivingMethod = 3;
                    receiveDataFromChannel(readMessage);
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_ENABLE_BT && resultCode == getActivity().RESULT_OK){
            // Bluetooth Turned On
            Toast.makeText(getActivity(), "Bluetooth Turned On", Toast.LENGTH_LONG).show();

            mChatService = new BluetoothChatService(getActivity(), mHandler);
            mOutStringBuffer = new StringBuffer("");

            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                mChatService.start();
            }

            Intent intent = new Intent(getContext(), BluetoothDeviceListActivity.class);
            startActivityForResult(intent, REQUEST_SELECT_BT);
        }

        if(requestCode == REQUEST_SELECT_BT && resultCode == getActivity().RESULT_OK){
            String macAddressOfReceiver =  data.getStringExtra("device_address");

            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                mChatService.start();
            }

            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(macAddressOfReceiver);
            mChatService.connect(device, true);
        }
    }

    public void showResultDialog(String title, String message){

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getContext());

        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        getActivity().getSupportFragmentManager()
                                .popBackStackImmediate("chooseAction", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                })
                .show();
    }
}


