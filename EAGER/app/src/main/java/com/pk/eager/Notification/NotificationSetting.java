package com.pk.eager.Notification;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pk.eager.LocationUtils.GeoConstant;
import com.pk.eager.LocationUtils.GeocodeIntentService;
import com.pk.eager.R;
import com.pk.eager.ReportObject.Subscription;

public class NotificationSetting extends AppCompatActivity {


    private CheckBox categoryMedical;
    private CheckBox categoryFire;
    private CheckBox categoryPolice;
    private CheckBox categoryTraffic;
    private CheckBox categoryUtility;
    private EditText addressTextField;
    private Spinner distanceSpinner;
    private String subscribeAddress;
    private String subscribeDistance;
    private String TAG = NotificationSetting.class.getSimpleName();
    private AddressResultReceiver resultReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_setting);

        setTitle("Notification Setting");

        categoryMedical = (CheckBox) findViewById(R.id.checkbox_filter_medical);
        categoryFire = (CheckBox) findViewById(R.id.checkbox_filter_fire);
        categoryPolice = (CheckBox) findViewById(R.id.checkbox_filter_police);
        categoryTraffic = (CheckBox) findViewById(R.id.checkbox_filter_Traffic);
        categoryUtility = (CheckBox) findViewById(R.id.checkbox_filter_utility);

        readSharedPreference();

        addressTextField = (EditText) findViewById(R.id.subscription_setting_address);

        //set up distance spinner
        distanceSpinner = (Spinner) findViewById(R.id.subscription_setting_distance_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.subscription_setting_distance, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        distanceSpinner.setAdapter(adapter);
        subscribeDistance = (String) adapter.getItem(0);

        //address translation
        resultReceiver = new AddressResultReceiver(null);
    }

    //Spinner handler
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        subscribeDistance = (String) parent.getItemAtPosition(pos);
        Log.d(TAG, subscribeDistance);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        subscribeDistance = (String) parent.getItemAtPosition(0);
    }
    //Spinner hanlder

    public void getCoordinates(String address){
        Log.d(TAG, "Starting service");
        Intent intent = new Intent(getApplicationContext(), GeocodeIntentService.class);
        intent.putExtra(GeoConstant.RECEIVER, resultReceiver);
        intent.putExtra(GeoConstant.FETCH_TYPE_EXTRA, GeoConstant.ADDRESS);
        intent.putExtra(GeoConstant.LOCATION_NAME_DATA_EXTRA, address);
        Log.d(TAG, address);
        startService(intent);
    }

    public void onSubscribeAddClick(View v) {
        subscribeAddress = (addressTextField).getText().toString();
        getCoordinates(subscribeAddress);

        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != getCurrentFocus())
            imm.hideSoftInputFromWindow(getCurrentFocus()
                    .getApplicationWindowToken(), 0);
        addressTextField.setText("");

        Toast.makeText(getApplicationContext(), "Subscribed to "+subscribeAddress, Toast.LENGTH_SHORT);
        /*
        if (zipcode.length() != 0 && zipcode.length() == 5 && zipcode.matches("[0-9]+")) {
            FirebaseMessaging.getInstance().subscribeToTopic(zipcode);
            Toast.makeText(getApplicationContext(), "Subcribed to " + zipcode, Toast.LENGTH_SHORT).show();
            addressTextField.setText("");
        } else
            Toast.makeText(getApplicationContext(), "Please enter correct zipcode", Toast.LENGTH_SHORT).show();
            */
    }

    public void onViewSubscriptionClick(View v){

    }


    public void readSharedPreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        setCheckBoxBaseOnPreference(categoryMedical, "Medical", sharedPreferences);
        setCheckBoxBaseOnPreference(categoryFire, "Fire", sharedPreferences);
        setCheckBoxBaseOnPreference(categoryPolice, "Police", sharedPreferences);
        setCheckBoxBaseOnPreference(categoryTraffic, "Traffic", sharedPreferences);
        setCheckBoxBaseOnPreference(categoryUtility, "Utility", sharedPreferences);
    }

    public void setCheckBoxBaseOnPreference(CheckBox box, String val, SharedPreferences sharedPreferences) {
        boolean defaultValue = false;
        defaultValue = sharedPreferences.getBoolean(val, defaultValue);
        if (defaultValue)
            box.setChecked(true);
        else box.setChecked(false);
    }

    public void onApplyClick(View v) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (categoryMedical.isChecked()) {
            editor.putBoolean("Medical", true);
        } else editor.putBoolean("Medical", false);

        if (categoryFire.isChecked()) {
            editor.putBoolean("Fire", true);
        } else editor.putBoolean("Fire", false);

        if (categoryPolice.isChecked()) {
            editor.putBoolean("Police", true);
        } else editor.putBoolean("Police", false);

        if (categoryTraffic.isChecked()) {
            editor.putBoolean("Traffic", true);
        } else editor.putBoolean("Traffic", false);

        if (categoryUtility.isChecked()) {
            editor.putBoolean("Utility", true);
        } else editor.putBoolean("Utility", false);

        editor.commit();
        finish();
    }

    class AddressResultReceiver extends android.os.ResultReceiver {
        public AddressResultReceiver(Handler handler) {
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
                        String longitude = address.getLongitude()+"";
                        String latitude = address.getLatitude()+"";
                        final String topic = longitude+"_"+latitude;
                        FirebaseMessaging.getInstance().subscribeToTopic(topic+"_"+distanceSpinner.getSelectedItem());

                        final DatabaseReference subscriptionRef = FirebaseDatabase.getInstance().getReference("AllSubscriptions");
                        final Subscription newSub = new Subscription(subscribeAddress, distanceSpinner.getSelectedItem().toString(), topic);


                        Query topicQuery = subscriptionRef.orderByChild("topic").equalTo(topic);
                        topicQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d(TAG, "here");
                                boolean exist = false;
                                for(DataSnapshot childsnapshot : dataSnapshot.getChildren()) {
                                    exist = true;
                                }

                                if(!exist) {
                                    DatabaseReference newChild = subscriptionRef.push();
                                    newChild.setValue(newSub);

                                    subscribeAddress = "";
                                    Log.d(TAG, "not here");
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if(user!=null) {
                            String uid = user.getUid();
                            DatabaseReference userSubRef = FirebaseDatabase.getInstance().getReference("UserSubscription").child(uid);
                            DatabaseReference newChild = userSubRef.push();
                            newChild.setValue(newSub);
                        }

                    }
                });
            } else {
                Log.d(TAG, "Unable to find longitude latitude");
            }
        }
    }

}