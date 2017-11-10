package com.pk.eager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pk.eager.ReportFragments.Constant;
import com.pk.eager.ReportFragments.IncidentType;
import com.pk.eager.ReportObject.CompactReport;
import com.pk.eager.adapter.ClickListener;
import com.pk.eager.adapter.InformationRecyclerViewAdapter;
import com.pk.eager.adapter.RecyclerTouchListener;
import com.pk.eager.util.CompactReportUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class InformationListView extends Fragment {

    private static final String REPORT = "report";

    private DatabaseReference db;
    List<CompactReport> reportList;
    RecyclerView reportRecyclerView;
    LatLng currentLocation;
    public String phoneNumber;
    private MenuItem mSearchAction;
    private MenuItem mFilterAction;
    private boolean isSearchOpened = false;
    private boolean isFilterApplied = false;
    private EditText searchBar;
    InformationRecyclerViewAdapter adapter;
    CompactReportUtil cmpUtil = new CompactReportUtil();
    final private SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy");


    private FusedLocationProviderClient mFusedLocationClient;

    public static final int PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int PERMISSIONS_REQUEST_PHONE = 0;

    private OnFragmentInteractionListener mListener;

    public InformationListView() {

    }

    public static InformationListView newInstance(String param1, String param2) {
        InformationListView fragment = new InformationListView();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        db = FirebaseDatabase.getInstance().getReference().child("Reports");
        setHasOptionsMenu(true);

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        return inflater.inflate(R.layout.fragment_information_list_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Nearby Incidents");
        reportRecyclerView = (RecyclerView) view.findViewById(R.id.informationListView);

        FloatingActionButton newReportFab = (FloatingActionButton) view.findViewById(R.id.reportIncidentFAB);
        newReportFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Fragment fragment = Dashboard.incidentType;
                if (fragment == null) {
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

        checkPermission();
        getPhonePermission();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void checkPermission() {

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

        } else {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);
        }
    }

    public void fetchDataFromFirebase() {

        reportList = new ArrayList<>();
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        reportRecyclerView.setLayoutManager(llm);

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    CompactReport cmp = noteDataSnapshot.getValue(CompactReport.class);
                    reportList.add(cmp);
                }

                Collections.sort(reportList, new Comparator<CompactReport>() {
                    @Override
                    public int compare(CompactReport o1, CompactReport o2) {
                        Map<String,String> map1 = cmpUtil.parseReportData(o1, "info");
                        Map<String,String> map2 = cmpUtil.parseReportData(o2, "info");

                        String[] coors1 = map1.get("location").split(",");
                        String[] coors2 = map2.get("location").split(",");

                        Location location1 = new Location("");
                        Location location2 = new Location("");

                        double distanceInMile1 = 0;
                        double distanceInMile2 = 0;

                        if(coors1.length == 2 && coors2.length == 2) {

                            location1.setLongitude(Double.parseDouble(coors1[1]));
                            location1.setLatitude(Double.parseDouble(coors1[0]));


                            location2.setLongitude(Double.parseDouble(coors2[1]));
                            location2.setLatitude(Double.parseDouble(coors2[0]));

                            distanceInMile1 = cmpUtil.distanceBetweenPoints(location1, Dashboard.location);
                            distanceInMile2 = cmpUtil.distanceBetweenPoints(location1, Dashboard.location);
                        }

                        Date date1 = null;
                        Date date2 = null;

                        try {
                            date1 = dateFormat.parse(map1.get("date"));
                            date2 = dateFormat.parse(map2.get("date"));
                        }catch (Exception ex){

                        }

                        if(date1!=null && date2!=null) {
                            if (date1.equals(date2)) {
                                if (distanceInMile1 == distanceInMile2)
                                    return -1;
                                return Double.compare(distanceInMile1, distanceInMile2);
                            } else return date1.compareTo(date2);
                        }else return -1;
                    }
                });

                adapter = new InformationRecyclerViewAdapter(getContext(), reportList, currentLocation);
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
                    try {
                        checkPermission();
                    } catch (SecurityException e) {
                        Log.e("EAGER", e.getMessage());
                    }
                }
                return;
            }
            case PERMISSIONS_REQUEST_PHONE: {

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
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_REQUEST_PHONE);
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

    public void getPhonePermission() {
        if (ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{android.Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_REQUEST_PHONE);
            Log.d(TAG, "Permission requested");
        } else {
            getPhoneNumberFromDevice();
        }
    }

    public void getPhoneNumberFromDevice(){
        TelephonyManager tMgr = (TelephonyManager)this.getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        phoneNumber = tMgr.getLine1Number();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.PHONE_NUMBER, phoneNumber);
        editor.commit();
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
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                handleMenuSearch();
                return true;
            case R.id.action_filter:
                handleMenuFilter();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void handleMenuSearch(){
        ActionBar action = ((AppCompatActivity)getActivity()).getSupportActionBar(); //get the actionbar

        if(isSearchOpened){ //test if the search is open

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar

            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);

            mSearchAction.setIcon(getResources().getDrawable(R.drawable.magnifying_glass));

            fetchDataFromFirebase();

            isSearchOpened = false;
        } else { //open the search entry

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.search_bar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            searchBar = (EditText)action.getCustomView().findViewById(R.id.searchBar); //the text editor

            //this is a listener to do a search when the user clicks on search button
            searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);

                        String searchQuery = searchBar.getText().toString();
                        adapter.filterByQuery(searchQuery);
                        return true;
                    }
                    return false;
                }
            });


            searchBar.requestFocus();

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT);

            //add the close icon
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.close));

            isSearchOpened = true;
        }
    }

    public void handleMenuFilter(){

        if(isFilterApplied){
            mSearchAction.setVisible(true);
            mFilterAction.setIcon(getResources().getDrawable(R.drawable.filter));
            isFilterApplied = false;
            fetchDataFromFirebase();
        }else {
            Intent intent = new Intent(getContext(), IncidentFilterActivity.class);
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
                    adapter.combineFilter(categoryList,distance,ll);
                }else if(categoryList.size()>0){
                    adapter.filterByCategory(categoryList);
                }else{
                    adapter.filterByDistance(distance,ll);
                }

                mSearchAction.setVisible(false);
                mFilterAction.setIcon(getResources().getDrawable(R.drawable.close));
            }
        }

    }
}
