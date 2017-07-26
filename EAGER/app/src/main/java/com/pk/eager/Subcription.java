package com.pk.eager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

public class Subcription extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcription);
        setTitle("Notification Subscription");
    }


    public void subscriptionAddButtonClick(View view){
        String topic;
        EditText editText = (EditText) findViewById(R.id.subscriptionEditText);
        topic = editText.getText().toString();
        FirebaseMessaging.getInstance().subscribeToTopic("95138");
        TextView textView = (TextView)findViewById(R.id.subscriptionTextView);
        textView.setText(textView.getText().toString()+"\n"+topic);
    }

}
