package com.pk.eager.LocationUtils;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//import android.support.v4.os.ResultReceiver;

/**
 * Created by kimpham on 7/17/17.
 */

public class GeocodeIntentService extends IntentService {
    protected ResultReceiver receiver;
    private static final String TAG = "GeocodeIntentService";

    public GeocodeIntentService(){
        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "service started");
        String errorMsg = "";
        receiver = intent.getParcelableExtra(GeoConstant.RECEIVER);

        if(receiver == null){
            Log.d("TAG", "No receiver found");
            return;
        }

        Location location = intent.getParcelableExtra(GeoConstant.LOCATION_DATA_EXTRA);

        if(location == null){
            errorMsg = "No location data provided";
            Log.d(TAG, errorMsg);
            // deliverResultToReceiver(GeoConstant.FAILURE_RESULT, errorMsg, null);
            // return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses = null;
        int fetchType = intent.getIntExtra(GeoConstant.FETCH_TYPE_EXTRA, 0);
        if(fetchType == GeoConstant.ADDRESS){
            String name = intent.getStringExtra(GeoConstant.LOCATION_NAME_DATA_EXTRA);
            Log.d(TAG, name);
            try {
                addresses = geocoder.getFromLocationName(name, 1);
            } catch (IOException e) {
                errorMsg = "Service not available";
                Log.d(TAG, errorMsg, e);
            }
        }else{
            try{
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                // addresses = geocoder.getFromLocation(37.3351874, -121.8810715, 1);
            }catch(IOException ex){
                errorMsg = "IOException";
                Log.d(TAG, errorMsg, ex);
            }catch(IllegalArgumentException ex2){
                errorMsg = "Illegal argument exception";
                Log.d(TAG, errorMsg, ex2);
            }
        }

        //address not found
        if(addresses == null || addresses.size() == 0) {
            if (errorMsg.isEmpty()) {
                errorMsg = "Address not found";
                Log.d(TAG, errorMsg);
            }
            deliverResultToReceiver(GeoConstant.FAILURE_RESULT, errorMsg, null);
        }else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
                Log.d(TAG, "address fragmemts "+ address.getAddressLine(i));
            }
            Log.d(TAG, "Address found");
            deliverResultToReceiver(GeoConstant.SUCCESS_RESULT, TextUtils.join(System.getProperty("line.separator"), addressFragments), address);
        }

    }


    //Sends a result code and message to receiver
    public void deliverResultToReceiver(int resultCode, String msg, Address address){
        Log.d(TAG, "Delivering result");
        Bundle bundle = new Bundle();
        bundle.putParcelable(GeoConstant.RESULT_DATA, address);
        bundle.putString(GeoConstant.RESULT_DATA_KEY, msg);
        receiver.send(resultCode, bundle);
    }

}
