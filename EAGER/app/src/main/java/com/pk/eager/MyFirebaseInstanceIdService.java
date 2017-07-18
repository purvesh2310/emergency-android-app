package com.pk.eager;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by kimpham on 7/17/17.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    public final static String TAG = "InstanceIdService";

    public void onTokenRefresh(){
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, "Refeshed token: " + token);
    }

}
