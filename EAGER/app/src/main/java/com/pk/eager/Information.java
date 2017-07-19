package com.pk.eager;

import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
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
import java.util.Iterator;
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

    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_information, container, false);

        db = FirebaseDatabase.getInstance().getReference();
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
                                                Map<String, String> reportData = cmpUtil.parseReportData(cmp);

                                                String title = reportData.get("title");
                                                String info = reportData.get("information");

                                                googleMap.addMarker(new MarkerOptions().position(new LatLng(cmp.latitude,cmp.longitude))
                                                        .title(title).snippet(info)
                                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                                        }
                                    });

                                    // For zooming automatically to the location of the marker
                                    CameraPosition cameraPosition = new CameraPosition.Builder()
                                            .target(currentLocation).zoom(15).build();
                                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                }
                            }
                        });
            }
        });

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
}

