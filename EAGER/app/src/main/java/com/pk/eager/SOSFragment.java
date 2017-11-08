package com.pk.eager;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pk.eager.ReportFragments.Constant;
import com.pk.eager.ReportObject.SOS;

/**
 * Created by Purvesh on 4/7/2017.
 */

public class SOSFragment extends Fragment {

    private static Location location;
    private String phoneNumber;
    static final public String TAG = SOSFragment.class.getSimpleName();


    public SOSFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        location = Dashboard.location;
        getphoneNumber();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_sos, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("SOS");
        if (savedInstanceState == null) {

            final ProgressBar alertProgressBar = (ProgressBar) getActivity().findViewById(R.id.alertProgressBar);
            alertProgressBar.setProgress(0);

            final TextView alertInstructionTextView = (TextView) getActivity().findViewById(R.id.alertInstructions);

            final Button alertButton = (Button) getActivity().findViewById(R.id.sendSOSButton);

            alertButton.setOnTouchListener(new View.OnTouchListener() {

                private Handler mHandler;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {

                        case MotionEvent.ACTION_DOWN:
                            if (mHandler != null) return true;
                            mHandler = new Handler();
                            mHandler.postDelayed(mAction, 200);
                            break;

                        case MotionEvent.ACTION_UP:
                            if (mHandler == null) return true;
                            mHandler.removeCallbacks(mAction);
                            mHandler = null;
                            break;
                    }
                    return false;
                }

                Runnable mAction = new Runnable() {
                    @Override
                    public void run() {

                        alertProgressBar.incrementProgressBy(1);

                        if (alertProgressBar.getProgress() == alertProgressBar.getMax()) {

                            submitSOS();

                            alertButton.setText(getResources().getString(R.string.label_alert_sent));
                            alertButton.setBackground(getActivity().getDrawable(R.drawable.alert_button_success_background));

                            alertInstructionTextView.setText(getResources().getString(R.string.label_alert_notification));

                            alertProgressBar.setVisibility(View.INVISIBLE);
                            alertProgressBar.setProgress(0);
                        }

                        mHandler.postDelayed(this, 200);
                    }
                };

            });
        }
    }

    public void submitSOS(){
        DatabaseReference newChild = FirebaseDatabase.getInstance().getReference("SOS").push();
        newChild.setValue(new SOS(phoneNumber, location.getLatitude(), location.getLongitude()));
    }


    public void getphoneNumber(){
        //get phone number from sharedPreference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        phoneNumber = "";
        phoneNumber = sharedPreferences.getString(Constant.PHONE_NUMBER, phoneNumber);
        if (phoneNumber==null)
            phoneNumber = "";
    }

}