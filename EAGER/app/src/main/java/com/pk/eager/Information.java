package com.pk.eager;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pk.eager.ReportObject.CompactReport;
import com.pk.eager.util.CompactReportUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;


/**
 * Created by Purvesh on 4/7/2017.
 */

public class Information extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    private DatabaseReference db;
    LatLng currentLocation;
    List<CompactReport> reportList;

    private MenuItem mSearchAction;
    private MenuItem mFilterAction;

    private boolean isFilterApplied = false;

    private FusedLocationProviderClient mFusedLocationClient;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_information, container, false);

        /**change**/
        db = FirebaseDatabase.getInstance().getReference().child("Reports");
        //db = FirebaseDatabase.getInstance().getReference().child("Reports2");

        final CompactReportUtil cmpUtil = new CompactReportUtil();

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {

                        String title = marker.getTitle();
                        String info = marker.getSnippet();
                        LatLng reportLocation = marker.getPosition();

                        double distanceInMile = cmpUtil.distanceBetweenPoints(currentLocation, reportLocation);
                        String roundDistance = String.format("%.2f", distanceInMile);
                        roundDistance = roundDistance + " miles far";

                        ((Dashboard)getActivity()).showEditDialog(title, info, roundDistance);
                    }
                });


                if (ContextCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    // For showing a move to my location button
                    googleMap.setMyLocationEnabled(true);

                }

                reportList = new ArrayList<>();

                mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {

                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    currentLocation = new LatLng(location.getLatitude(),location.getLongitude());

                                    db.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                                                CompactReport cmp = noteDataSnapshot.getValue(CompactReport.class);
                                                reportList.add(cmp);
                                            }
                                            addReportsToMapView(reportList);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                                        }
                                    });




                                    // For zooming automatically to the location of the marker
                                    CameraPosition cameraPosition = new CameraPosition.Builder()
                                            .target(currentLocation).zoom(10).build();
                                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                }
                            }
                        });
            }
        });

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search);
        mFilterAction = menu.findItem(R.id.action_filter);

        mSearchAction.setVisible(false);

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_filter:
                handleMenuFilter();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void handleMenuFilter(){

        if(isFilterApplied){
            mFilterAction.setIcon(getResources().getDrawable(R.drawable.filter));
            isFilterApplied = false;
            googleMap.clear();
            addReportsToMapView(reportList);
        }else {
            Intent intent = new Intent(getContext(), IncidentFilterActivity.class);
            intent.putExtra("caller","MAP");
            startActivityForResult(intent, 1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {

                isFilterApplied = true;

                int distance = data.getIntExtra("distance",0);
                ArrayList<String> categoryList = data.getStringArrayListExtra("selectedCategory");
                LatLng ll = data.getParcelableExtra("longLat_dataProvider");

                if(categoryList.size()>0 && distance!=0){
                    combineFilter(categoryList, distance, ll);
                }else if(categoryList.size()>0){
                    filterByCategory(categoryList);
                }else{
                    filterByDistance(distance, ll);
                }

                mSearchAction.setVisible(false);
                mFilterAction.setIcon(getResources().getDrawable(R.drawable.close));
            }
        }

    }

    public void addReportsToMapView(List<CompactReport> reportList){

        CompactReportUtil cmpUtil = new CompactReportUtil();
        float markerColorType;

        for(CompactReport cmpReport: reportList){

            Map<String, String> reportData = cmpUtil.parseReportData(cmpReport,"info");

            String title = reportData.get("title");
            String info = reportData.get("information");

            if (cmpReport.type.equals("Report")){
                markerColorType = BitmapDescriptorFactory.HUE_RED;
            } else{
                markerColorType = BitmapDescriptorFactory.HUE_ORANGE;
            }

            googleMap.addMarker(new MarkerOptions().position(new LatLng(cmpReport.latitude,cmpReport.longitude))
                    .title(title).snippet(info)
                    .icon(BitmapDescriptorFactory.defaultMarker(markerColorType)));

        }
    }

    public void filterByCategory(List<String> categoryList){

        List<CompactReport> temp = new ArrayList();
        CompactReportUtil cmpUtil = new CompactReportUtil();

        for(CompactReport cmpReport: reportList){

            Map<String, String> reportData = cmpUtil.parseReportData(cmpReport,"list");
            String reportTitle = reportData.get("title");

            for (String category : categoryList) {

                if (reportTitle.toLowerCase().equals(category.toLowerCase())) {
                    temp.add(cmpReport);
                }
            }

        }

        googleMap.clear();
        addReportsToMapView(temp);
    }

    public void filterByDistance(double queryDistance, LatLng specifiedLocation){

        List<CompactReport> temp = new ArrayList();
        CompactReportUtil cmpUtil = new CompactReportUtil();
        double distanceInMile;

        for(CompactReport cmpReport: reportList){
            LatLng reportLocation = new LatLng(cmpReport.latitude,cmpReport.longitude);

            if(specifiedLocation != null){
                distanceInMile = cmpUtil.distanceBetweenPoints(specifiedLocation,reportLocation);
            }else{
                distanceInMile = cmpUtil.distanceBetweenPoints(currentLocation,reportLocation);
            }

            if (distanceInMile <= queryDistance){
                temp.add(cmpReport);
            }
        }

        googleMap.clear();
        addReportsToMapView(temp);
    }

    public void combineFilter(List<String> categoryList, double queryDistance, LatLng specifiedLocation) {

        List<CompactReport> temp = new ArrayList();
        CompactReportUtil cmpUtil = new CompactReportUtil();
        double distanceInMile;

        for (CompactReport cmpReport : reportList) {

            Map<String, String> reportData = cmpUtil.parseReportData(cmpReport,"list");
            String reportTitle = reportData.get("title");

            LatLng reportLocation = new LatLng(cmpReport.latitude,cmpReport.longitude);

            if(specifiedLocation != null){
                distanceInMile = cmpUtil.distanceBetweenPoints(specifiedLocation,reportLocation);
            }else{
                distanceInMile = cmpUtil.distanceBetweenPoints(currentLocation,reportLocation);
            }

            for (String category : categoryList) {
                if ((reportTitle.toLowerCase().equals(category.toLowerCase())) && distanceInMile <= queryDistance) {
                    temp.add(cmpReport);
                }
            }
        }
        googleMap.clear();
        addReportsToMapView(temp);
    }
}

