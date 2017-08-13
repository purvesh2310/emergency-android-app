package com.pk.eager;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class NotificationSetting extends AppCompatActivity {


    private CheckBox categoryMedical;
    private CheckBox categoryFire;
    private CheckBox categoryPolice;
    private CheckBox categoryTraffic;
    private CheckBox categoryUtility;

    private CheckBox weatherFeed;
    private CheckBox crimeFeed;
    private CheckBox missingPersonFeed;

    private SeekBar seekBar;

    private TextView seekBarProgress;


    public ArrayList<String> categorySelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_filter);

        setTitle("Choose Filter");

        categoryMedical = (CheckBox) findViewById(R.id.checkbox_filter_medical);
        categoryFire = (CheckBox) findViewById(R.id.checkbox_filter_fire);
        categoryPolice = (CheckBox) findViewById(R.id.checkbox_filter_police);
        categoryTraffic = (CheckBox) findViewById(R.id.checkbox_filter_Traffic);
        categoryUtility = (CheckBox) findViewById(R.id.checkbox_filter_utility);

        weatherFeed = (CheckBox) findViewById(R.id.checkbox_filter_weatherFeed);
        crimeFeed = (CheckBox) findViewById(R.id.checkbox_filter_crimeFeed);
        missingPersonFeed = (CheckBox) findViewById(R.id.checkbox_filter_missingPersonFeed);

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

        int distance = seekBar.getProgress();

        Intent returnIntent = new Intent();
        returnIntent.putExtra("distance", distance);
        returnIntent.putStringArrayListExtra("selectedCategory",selectedCategory);

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
