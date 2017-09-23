package com.pk.eager.Notification;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.pk.eager.R;

public class ViewEditSubscriptions extends AppCompatActivity {

    DatabaseReference userSubRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_edit_subscriptions);
    }
}
