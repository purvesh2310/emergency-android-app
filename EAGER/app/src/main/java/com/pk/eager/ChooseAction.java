package com.pk.eager;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChooseAction.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChooseAction#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChooseAction extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static String phoneNumber;

    private static final int PHONE_PERMISSION = 0;
    private static final String TAG = "ChooseAction";



    ImageButton informationButton;
    ImageButton reportButton;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ChooseAction() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChooseAction.
     */
    // TODO: Rename and change types and number of parameters
    public static ChooseAction newInstance(String param1, String param2) {
        ChooseAction fragment = new ChooseAction();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public void getPhonePermission(){
        if (ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, PHONE_PERMISSION);
            Log.d(TAG, "Permission requested");
        }else{
            getPhoneNumberFromDevice();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PHONE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //read phone number
                    getPhoneNumberFromDevice();
                    Log.d(TAG, "Phone number"+ phoneNumber);
                } else {
                    Log.d(TAG, "Permission denied");
                    final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.setTitle("Phone Permission Required");
                    alertDialog.setMessage("Phone permission is needed to submit a report");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(ChooseAction.this.getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, PHONE_PERMISSION);
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel reporting", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();


                }
                return;
            }


        }
    }

    public void getPhoneNumberFromDevice(){
        TelephonyManager tMgr = (TelephonyManager)this.getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        phoneNumber = tMgr.getDeviceId();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose_action, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Choose Action");

        // To set onClick Listener on the Info and Report button
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

        informationButton = (ImageButton) this.getView().findViewById(R.id.infoActionButton);
        informationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Fragment fragment = new Information();
                FragmentTransaction ft = getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrame, fragment)
                        .addToBackStack("information");

                ft.commit();

            }
        });

        reportButton = (ImageButton) this.getView().findViewById(R.id.reportActionButton);
        reportButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {

                getPhonePermission();

                if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
                    TelephonyManager mngr = (TelephonyManager)getContext().getSystemService(Context.TELEPHONY_SERVICE);
                    getPhoneNumberFromDevice();
                    Log.d(TAG, "Phone Number "+ phoneNumber);
                }else{
                    Log.d(TAG,"Permission not granted, retry");
                }


                Fragment fragment = Dashboard.incidentType;
                if(fragment == null) {
                    Dashboard.incidentType = new IncidentType();
                    fragment = Dashboard.incidentType;
                }

                FragmentTransaction ft = getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrame, fragment)
                        .addToBackStack("chooseAction");

                ft.commit();
            }
        });
    }
}
