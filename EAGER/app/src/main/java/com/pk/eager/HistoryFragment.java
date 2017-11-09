package com.pk.eager;


import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.pk.eager.BaseClass.BaseXBeeFragment;
import com.pk.eager.adapter.HistoryRecyclerViewAdapter;
import com.pk.eager.db.handler.DatabaseHandler;
import com.pk.eager.db.model.Report;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends BaseXBeeFragment {

    RecyclerView historyRecyclerView;
    LatLng currentLocation;
    private FusedLocationProviderClient mFusedLocationClient;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("History");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        historyRecyclerView = (RecyclerView) getActivity().findViewById(R.id.historyListView);
                        LinearLayoutManager llm = new LinearLayoutManager(getContext());
                        historyRecyclerView.setLayoutManager(llm);

                        DatabaseHandler db = new DatabaseHandler(getContext());
                        List<Report> reports = db.getAllReports();

                        HistoryRecyclerViewAdapter adapter = new HistoryRecyclerViewAdapter(getContext(), reports, currentLocation);
                        historyRecyclerView.setAdapter(adapter);
                    }
                }
            });
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }
}
