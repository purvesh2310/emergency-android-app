package com.pk.eager;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IncidentFilterActivity extends AppCompatActivity {

    private CheckBox categoryMedical;
    private CheckBox categoryFire;
    private CheckBox categoryPolice;
    private CheckBox categoryTraffic;
    private CheckBox categoryUtility;

    private CheckBox weatherFeed;
    private CheckBox crimeFeed;
    private CheckBox missingPersonFeed;

    private RadioGroup locationPreferenceRadioGroup;

    private EditText specificLocation;

    private SeekBar seekBar;

    private TextView seekBarProgress;


    public ArrayList<String> categorySelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_filter);

        String callerFragment = getIntent().getStringExtra("caller");

        setTitle("Choose Filter");

        categoryMedical = (CheckBox) findViewById(R.id.checkbox_filter_medical);
        categoryFire = (CheckBox) findViewById(R.id.checkbox_filter_fire);
        categoryPolice = (CheckBox) findViewById(R.id.checkbox_filter_police);
        categoryTraffic = (CheckBox) findViewById(R.id.checkbox_filter_Traffic);
        categoryUtility = (CheckBox) findViewById(R.id.checkbox_filter_utility);

        locationPreferenceRadioGroup = (RadioGroup) findViewById (R.id.radioLocationPreference);
        specificLocation = (EditText) findViewById(R.id.specificLocation);

        locationPreferenceRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radioSpecificLocation){
                    specificLocation.setVisibility(View.VISIBLE);
                }else{
                    specificLocation.setVisibility(View.GONE);
                }
            }
        });

        weatherFeed = (CheckBox) findViewById(R.id.checkbox_filter_weatherFeed);
        crimeFeed = (CheckBox) findViewById(R.id.checkbox_filter_crimeFeed);
        missingPersonFeed = (CheckBox) findViewById(R.id.checkbox_filter_missingPersonFeed);

        if(callerFragment != null && callerFragment.equals("MAP")){
            weatherFeed.setVisibility(View.GONE);
            missingPersonFeed.setVisibility(View.GONE);
        }

        seekBarProgress = (TextView) findViewById(R.id.seekBarProgress);
        seekBarProgress.setText("0 mile(s)");

        seekBar = (SeekBar) findViewById(R.id.distanceSeekBar);
        seekBar.setMax(50);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarProgress.setText(String.valueOf(progress) + " mile(s)");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void applyFilter(View view){

        ArrayList<String> selectedCategory = getCategorySelected();
        int selectedLocationPreference = locationPreferenceRadioGroup.getCheckedRadioButtonId();
        LatLng specifiedLocationCoordinates = null;

        if(selectedLocationPreference == R.id.radioCurrentLocation){

        }else{

            String addressText = specificLocation.getText().toString();

            Geocoder gc = new Geocoder(this);
            try {
                if (gc.isPresent()) {

                    List<Address> list = gc.getFromLocationName(addressText, 1);

                    double lat;
                    double lng;

                    if(list.size()>0) {
                        Address address = list.get(0);
                        lat = address.getLatitude();
                        lng = address.getLongitude();
                        specifiedLocationCoordinates = new LatLng(lat, lng);
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }

        }

        int distance = seekBar.getProgress();

        Intent returnIntent = new Intent();
        returnIntent.putExtra("distance", distance);
        returnIntent.putStringArrayListExtra("selectedCategory",selectedCategory);

        Bundle args = new Bundle();
        args.putParcelable("longLat_dataProvider", specifiedLocationCoordinates);

        returnIntent.putExtras(args);

        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    public ArrayList<String> getCategorySelected(){

        categorySelected = new ArrayList<>();

        if(categoryMedical.isChecked()){
            categorySelected.add("Medical");
        }
        if(categoryFire.isChecked()){
            categorySelected.add("Fire");
        }
        if(categoryPolice.isChecked()){
            categorySelected.add("Police");
        }
        if(categoryTraffic.isChecked()){
            categorySelected.add("Traffic");
        }
        if(categoryUtility.isChecked()){
            categorySelected.add("Utility");
        }
        if(weatherFeed.isChecked()){
            categorySelected.add("Weather");
        }
        if(crimeFeed.isChecked()){
            categorySelected.add("Crime");
        }
        if(missingPersonFeed.isChecked()){
            categorySelected.add("Missing");
        }

        return categorySelected;

    }
}
