package com.pk.eager;

import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.pk.eager.BaseClass.BaseXbeeActivity;
import com.pk.eager.ReportObject.CompactReport;
import com.pk.eager.util.CompactReportUtil;

import java.util.Map;


public class ViewNotification extends BaseXbeeActivity implements OnMapReadyCallback{
    public final String TAG = "ViewNotification";
    public final String REPORT = "report";
    public String reportKey;
    private CompactReport report;
    private FirebaseDatabase db;
    private CompactReportUtil cmpUtils;
    private final String SPLIT = "~";

    private TextView titleTextView;
    private TextView informationTextView;
    private ImageView incidentTypeLogo;
    private TextView dateTextView;
    private TextView distanceTextView;
    private TextView sourceTextView;

    private double longitude;
    private double latitude;
    private SupportMapFragment mapFragment;
    LatLng mapLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notification);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = FirebaseDatabase.getInstance();

        cmpUtils = new CompactReportUtil();

        //get views
        titleTextView = (TextView) findViewById(R.id.viewNotificationReportTitle);
        informationTextView = (TextView) findViewById(R.id.viewNotificationReportInformation);
        incidentTypeLogo = (ImageView) findViewById(R.id.viewNotificationTypeLogo);
        dateTextView = (TextView) findViewById(R.id.viewNotificationDateTextView);
        sourceTextView = (TextView) findViewById(R.id.viewNotificationSourceTextView);
        distanceTextView = (TextView) findViewById(R.id.viewNotificationLocationTextView);

        if (getIntent().hasExtra(REPORT)) {
            //case when ViewNotification was triggered by an intent from another activity
            report = getIntent().getParcelableExtra(REPORT);
            setUpReportDetail(report);
        } else {
            //case ViewNotification was triggered by clickAction when user taps on notification
            if (getIntent() != null && getIntent().getExtras() != null)
                reportKey = getIntent().getExtras().getString("key");

            if (reportKey != null) {
                Query notifiedReportQuery = db.getReference().child("Reports").orderByKey().equalTo(reportKey);

                notifiedReportQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        report = dataSnapshot.getValue(CompactReport.class);
                        setUpReportDetail(report);
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
    }

    public void setUpReportDetail(CompactReport report){
        CompactReportUtil cmpUtil = new CompactReportUtil();

        Map<String, String> map = cmpUtil.parseReportData(report,"info");

        String location = map.get("location");
        String title = "";

        //set date
        String date = map.get("date");
        dateTextView.setText(date);

        //set distance
        if(!report.type.equals("feed-weather") && !report.type.equals("feed-missing")) {
            //get distance
            latitude = Double.parseDouble(location.split(",")[0]);
            longitude = Double.parseDouble(location.split(",")[1]);

            Location location1 = new Location("");

            location1.setLongitude(longitude);
            location1.setLatitude(latitude);

            double distanceInMile = cmpUtil.distanceBetweenPoints(location1, Dashboard.location);

            String roundDistance = String.format("%.2f", distanceInMile);
            roundDistance = roundDistance + " mi";

            distanceTextView.setText(roundDistance);

            //set up map
            mapLocation = new LatLng(latitude, longitude);
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.viewReportDetail_mapfragment);
            mapFragment.getMapAsync(this);
        }

        if(!report.type.equals("Report")){

            //case report is type crime/missing/weather
            title = map.get("title");
            String information = map.get("information");

            Log.i("CRIME", information);

            String source = map.get("author");

            titleTextView.setText(title);
            informationTextView.setText(information);
            sourceTextView.setText(source);
        }else{
            //case report is user's report
            String[] titles = map.get("title").split(SPLIT);
            String[] informations = map.get("information").split(SPLIT);
            title = titles[0];

            titleTextView.setText(titles[0]);
            informationTextView.setText(informations[0]);
            sourceTextView.setText("User's Report");

            LinearLayout layout = (LinearLayout) findViewById(R.id.viewNotificationAllInfo);

            for(int i = 1; i < titles.length; i++){
                TextView extraTitle = new TextView(this);
                TextView extraDetail = new TextView(this);

                setTextViewAttribute(extraTitle, true);
                setTextViewAttribute(extraDetail, false);

                extraTitle.setText(titles[i]);
                extraDetail.setText(informations[i]);

                layout.addView(extraTitle);
                layout.addView(extraDetail);
            }
        }

        setTitle("Incident Information");

        switch(title){
            case "Medical":
                incidentTypeLogo.setImageResource(R.drawable.hospital);
                break;
            case "Fire":
                incidentTypeLogo.setImageResource(R.drawable.flame);
                break;
            case "Police":
                incidentTypeLogo.setImageResource(R.drawable.siren);
                break;
            case "Traffic":
                incidentTypeLogo.setImageResource(R.drawable.cone);
                break;
            case "Utility":
                incidentTypeLogo.setImageResource(R.drawable.repairing);
                break;
            case "Weather":
            case "Missing":
            case "Crime":
                incidentTypeLogo.setImageResource(R.drawable.rss);
                break;
        }
    }




    public void setLocation(Double lat, Double longitude){
        mapLocation = new LatLng(lat, longitude);
    }

    public void setTextViewAttribute(TextView textView, boolean title){
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setTextSize(16);
        textView.setTextColor(Color.parseColor("#31343a"));

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int dpValue = 8;
        int px = dpValue * metrics.densityDpi/160;
        textView.setPadding(px,0,0,0);

        if(title){
            textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, mapLocation.toString());
        googleMap.addMarker(new MarkerOptions().position(mapLocation).title("Marker in Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapLocation,16));
    }
}
