package com.pk.eager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ViewNotification extends AppCompatActivity {
    public final String TAG = "ViewNotification";
    private String key = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notification);
    }
}
