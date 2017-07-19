package com.pk.eager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pk.eager.LocationUtils.GeoConstant;
import com.pk.eager.LocationUtils.GeocodeIntentService;
import com.pk.eager.ReportObject.CompactReport;
import com.pk.eager.ReportObject.IncidentReport;
import com.pk.eager.ReportObject.Utils;

import java.util.HashMap;
import java.util.Map;

import static com.google.android.gms.internal.zzagz.runOnUiThread;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Review.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Review#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Review extends Fragment {

    private static final String REPORT = "report";
    private IncidentReport incidentReport;
    private static final String TAG = "Review";
    private DatabaseReference db;
    private AddressResultReceiver resultReceiver;
    private OnFragmentInteractionListener mListener;
    private Location location;

    public Review() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment Review.
     */
    // TODO: Rename and change types and number of parameters
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
        }else incidentReport = new IncidentReport();
        incidentReport = Dashboard.incidentReport;
        db = FirebaseDatabase.getInstance().getReference("Reports");
        Location location = Dashboard.location;
        resultReceiver = new AddressResultReceiver(null);


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
            layout.addView(trap);
            layout.addView(getHorizontalSeparatorView());
        }
        if(!medical.getText().toString().isEmpty()) {
            layout.addView(medical);
            layout.addView(getHorizontalSeparatorView());
        }
        if(!fire.getText().toString().isEmpty()) {
            layout.addView(fire);
            layout.addView(getHorizontalSeparatorView());
        }
        if(!police.getText().toString().isEmpty()) {
            layout.addView(police);
            layout.addView(getHorizontalSeparatorView());
        }
        if(!utility.getText().toString().isEmpty()) {
            layout.addView(utility);
            layout.addView(getHorizontalSeparatorView());
        }
        if(!traffic.getText().toString().isEmpty()) {
            layout.addView(traffic);
            layout.addView(getHorizontalSeparatorView());
        }
        setButtonListener();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



    public void setButtonListener(){
        Button submit = (Button) this.getView().findViewById(R.id.button_review_submit);
        submit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
                dialog.setTitle("Submit Report");
                dialog.setMessage("Submit the report?");
                dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                double longitude = 200, latitude = 200;
                                location = Dashboard.location;
                                Log.d(TAG, "Button pressed");
                                if(location!=null) {
                                    Log.d(TAG, "Location not null");
                                    getAddress();
                                }else{
                                    //error message
                                }

                            }
                        });
                dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dialog.show();
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


    }

    // Formats the TextView to show in Review Screen
    public void formatReviewInformationTextView(TextView textView){

        if (Build.VERSION.SDK_INT < 23) {
            textView.setTextAppearance(getContext(), R.style.question);
        } else {
            textView.setTextAppearance(R.style.question);
        }
    }

    public View getHorizontalSeparatorView(){

        View view = new View(getContext());
        view.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,3
        ));
        view.setBackgroundColor(Color.parseColor("#c0c0c0"));

        return view;
    }

    public void sendNotificationToZipCode(String zipcode, String key){
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("notificationRequests");

        Map notification = new HashMap<>();
        notification.put("zipcode", zipcode);
        notification.put("message", key);
        Log.d(TAG, "Push notification " + key);
        notificationRef.push().setValue(notification);

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
                        Log.d(TAG, "In thead");
                        final String zipcode = address.getPostalCode();
                        DatabaseReference newChild = db.push();
                        final String key = newChild.getKey();
                        IncidentReport smallerSize = Utils.compacitize(incidentReport);
                        Log.d(TAG, "Smaller report " + smallerSize.toString());
                        CompactReport compact = new CompactReport(smallerSize, location.getLongitude(), location.getLatitude(), "4089299999");


                        newChild.setValue(compact, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                Dashboard.incidentType = null;
                                Dashboard.incidentReport = new IncidentReport("Bla");

                                getActivity().getSupportFragmentManager().popBackStackImmediate("chooseAction", FragmentManager.POP_BACK_STACK_INCLUSIVE);

                            }
                        });
                        sendNotificationToZipCode(zipcode, compact.getCompactReports().toString());

                    }
                });
            }else{
                Log.d(TAG, "Unable to find longitude latitude");
            }
        }
    }
}
