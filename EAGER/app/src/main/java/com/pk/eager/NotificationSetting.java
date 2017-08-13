package com.pk.eager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

public class NotificationSetting extends AppCompatActivity {


    private CheckBox categoryMedical;
    private CheckBox categoryFire;
    private CheckBox categoryPolice;
    private CheckBox categoryTraffic;
    private CheckBox categoryUtility;
    private EditText zipcode1;
    private EditText zipcode2;
    private Button apply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_setting);

        setTitle("Notification Setting");

        categoryMedical = (CheckBox) findViewById(R.id.checkbox_filter_medical);
        categoryFire = (CheckBox) findViewById(R.id.checkbox_filter_fire);
        categoryPolice = (CheckBox) findViewById(R.id.checkbox_filter_police);
        categoryTraffic = (CheckBox) findViewById(R.id.checkbox_filter_Traffic);
        categoryUtility = (CheckBox) findViewById(R.id.checkbox_filter_utility);

        readSharedPreference();

        zipcode1 = (EditText) findViewById(R.id.subscription_setting_zipcode1);
        zipcode2 = (EditText) findViewById(R.id.subscription_setting_zipcode2);

        apply = (Button) findViewById(R.id.subscription_setting_apply);
    }

    public void onSubscribeAddClick(View v){
        String zipcode = (zipcode1).getText().toString();
        if(zipcode.length()!=0 && zipcode.length()==5 && zipcode.matches("[0-9]+")) {
            FirebaseMessaging.getInstance().subscribeToTopic(zipcode);
            Toast.makeText(getApplicationContext(), "Subcribed to "+zipcode, Toast.LENGTH_SHORT).show();
            zipcode1.setText("");
        }
        else
            Toast.makeText(getApplicationContext(), "Please enter correct zipcode", Toast.LENGTH_SHORT).show();
    }

    public void onUnSubcribeAddClick(View v){
        String zipcode = (zipcode1).getText().toString();
        if(zipcode.length()!=0 && zipcode.length()==5 && zipcode.matches("[0-9]+")) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(zipcode);
            Toast.makeText(getApplicationContext(), "Unsubcribed from "+zipcode, Toast.LENGTH_SHORT).show();
            zipcode2.setText("");
        }
        else
            Toast.makeText(getApplicationContext(), "Please enter correct zipcode", Toast.LENGTH_SHORT).show();
    }

    public void readSharedPreference(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        setCheckBoxBaseOnPreference(categoryMedical, "Medical", sharedPreferences);
        setCheckBoxBaseOnPreference(categoryFire, "Fire", sharedPreferences);
        setCheckBoxBaseOnPreference(categoryPolice, "Police", sharedPreferences);
        setCheckBoxBaseOnPreference(categoryTraffic, "Traffic", sharedPreferences);
        setCheckBoxBaseOnPreference(categoryUtility, "Utility", sharedPreferences);
    }

    public void setCheckBoxBaseOnPreference(CheckBox box, String val, SharedPreferences sharedPreferences){
        boolean defaultValue = false;
        defaultValue = sharedPreferences.getBoolean(val, defaultValue);
        if(defaultValue)
            box.setChecked(true);
        else box.setChecked(false);
    }




    public void onApplyClick(View v){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(categoryMedical.isChecked()){
            editor.putBoolean("Medical", true);
        }else editor.putBoolean("Medical", false);

        if(categoryFire.isChecked()){
            editor.putBoolean("Fire", true);
        }else editor.putBoolean("Fire", false);

        if(categoryPolice.isChecked()){
            editor.putBoolean("Police", true);
        }else editor.putBoolean("Police", false);

        if(categoryTraffic.isChecked()){
            editor.putBoolean("Traffic", true);
        }else editor.putBoolean("Traffic", false);

        if(categoryUtility.isChecked()){
            editor.putBoolean("Utility", true);
        }else editor.putBoolean("Utility", false);

        editor.commit();
        finish();
    }









}
