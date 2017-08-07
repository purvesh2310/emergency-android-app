package com.pk.eager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pk.eager.ReportFragments.IncidentType;
import com.pk.eager.ReportObject.CompactReport;
import com.pk.eager.adapter.ClickListener;
import com.pk.eager.adapter.InformationRecyclerViewAdapter;
import com.pk.eager.adapter.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class InformationListView extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String REPORT = "report";

    private DatabaseReference db;
    List<CompactReport> reportList;
    RecyclerView reportRecyclerView;
    private FusedLocationProviderClient mFusedLocationClient;
    public static final int PERMISSIONS_REQUEST_LOCATION = 99;

    LatLng currentLocation;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public InformationListView() {

    }

    public static InformationListView newInstance(String param1, String param2) {
        InformationListView fragment = new InformationListView();
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

        /**change**/
        db = FirebaseDatabase.getInstance().getReference().child("Reports");
        //db = FirebaseDatabase.getInstance().getReference().child("Reports2");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        return inflater.inflate(R.layout.fragment_information_list_view, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Nearby Incidents");
        reportRecyclerView = (RecyclerView)view.findViewById(R.id.informationListView);

        reportList = new ArrayList<>();

        FloatingActionButton newReportFab = (FloatingActionButton) view.findViewById(R.id.reportIncidentFAB);
        newReportFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

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
        /*
        FloatingActionButton viewMapFab = (FloatingActionButton) view.findViewById(R.id .viewMapFAB);
        viewMapFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Fragment fragment = new Information();
                FragmentTransaction ft = getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrame, fragment)
                        .addToBackStack("information");

                ft.commit();

            }
        });*/

        checkPermission();

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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void checkPermission(){

        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        fetchDataFromFirebase();
                    }
                }
            });

        } else{
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION );
        }
    }

    public void fetchDataFromFirebase(){

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        reportRecyclerView.setLayoutManager(llm);

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    CompactReport cmp = noteDataSnapshot.getValue(CompactReport.class);
                    reportList.add(cmp);
                }

                Log.d("MyTAG",String.valueOf(currentLocation.latitude));
                InformationRecyclerViewAdapter adapter = new InformationRecyclerViewAdapter(getContext(), reportList, currentLocation);
                reportRecyclerView.setAdapter(adapter);
                reportRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), reportRecyclerView, new ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        CompactReport report = reportList.get(position);
                        Intent intent = new Intent(getContext(), ViewNotification.class);
                        intent.putExtra(REPORT, report);


                        startActivity(intent);
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }
                ));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try{
                        checkPermission();
                    }catch (SecurityException e){
                        Log.e("EAGER",e.getMessage());
                    }
                }
                return;
            }
        }

    }
}
