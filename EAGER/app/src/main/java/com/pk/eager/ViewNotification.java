package com.pk.eager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.pk.eager.ReportObject.CompactReport;
import com.pk.eager.util.CompactReportUtil;

import org.w3c.dom.Text;

import java.util.Map;


public class ViewNotification extends AppCompatActivity implements OnMapReadyCallback{
    public final String TAG = "ViewNotification";
    public final String REPORT = "report";
    public String reportKey;
    private CompactReport report;
    private FirebaseDatabase db;
    private CompactReportUtil cmpUtils;

    private TextView titleTextView;
    private TextView informationTextView;
    private TextView locationTextView;

    private double longitude;
    private double latitude;
    private SupportMapFragment mapFragment;
    LatLng location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notification);

        db = FirebaseDatabase.getInstance();
        cmpUtils = new CompactReportUtil();

        titleTextView = (TextView) findViewById(R.id.viewNotificationReportTitle);
        informationTextView = (TextView) findViewById(R.id.viewNotificationReportInformation);
        locationTextView = (TextView) findViewById(R.id.viewNotificationReportLocation);

        if (getIntent().hasExtra(REPORT)) {
            report = getIntent().getParcelableExtra(REPORT);
        } else {
            if (getIntent() != null && getIntent().getExtras() != null)
                reportKey = getIntent().getExtras().getString("key");

            if (reportKey != null) {
                Query notifiedReportQuery = db.getReference().child("Reports").orderByKey().equalTo(reportKey);
                notifiedReportQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        report = dataSnapshot.getValue(CompactReport.class);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }

        if(report!=null) {

            Map<String, String> map = cmpUtils.parseReportData(report);

            String title = map.get("title");
            String information = map.get("information");
            String location = map.get("location");

            latitude = Double.parseDouble(location.split(",")[0]);
            longitude = Double.parseDouble(location.split(",")[1]);

            Log.d(TAG, "Title " + map.get("title"));
            Log.d(TAG, "Information " + map.get("information"));
            Log.d(TAG, "Location " + map.get("location"));

            if(title!=null){
                titleTextView.setText(title);
            }
            if(information!=null){
                informationTextView.append(information);
            }
            if(location!=null){
                locationTextView.append(location);
            }
        }

        location = new LatLng(latitude, longitude);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.viewReportDetail_mapfragment);
        mapFragment.getMapAsync(this);

        setTitle("Incident Information");
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, location.toString());
        googleMap.addMarker(new MarkerOptions().position(location).title("Marker in Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,16));
    }
}
