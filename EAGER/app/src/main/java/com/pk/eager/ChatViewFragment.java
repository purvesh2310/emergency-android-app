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
import com.pk.eager.adapter.ChatRecyclerViewAdapter;
import com.pk.eager.db.handler.DatabaseHandler;
import com.pk.eager.db.model.Report;

import java.util.List;

public class ChatViewFragment extends Fragment {

    RecyclerView chatRecyclerView;
    LatLng currentLocation;
    private FusedLocationProviderClient mFusedLocationClient;

    public ChatViewFragment() {
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

        getActivity().setTitle("Select A Report");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        return inflater.inflate(R.layout.fragment_chat_view, container, false);
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

                        chatRecyclerView = (RecyclerView) getActivity().findViewById(R.id.chatListView);
                        LinearLayoutManager llm = new LinearLayoutManager(getContext());
                        chatRecyclerView.setLayoutManager(llm);

                        DatabaseHandler db = new DatabaseHandler(getContext());
                        List<Report> reports = db.getAllReports();

                        ChatRecyclerViewAdapter adapter = new ChatRecyclerViewAdapter(getContext(), reports, currentLocation);
                        chatRecyclerView.setAdapter(adapter);
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
