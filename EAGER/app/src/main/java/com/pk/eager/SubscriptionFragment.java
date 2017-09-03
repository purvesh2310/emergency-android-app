package com.pk.eager;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pk.eager.ReportObject.Notification;
import com.pk.eager.adapter.ClickListener;
import com.pk.eager.adapter.NotificationRecyclerViewAdapter;
import com.pk.eager.adapter.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 */
public class SubscriptionFragment extends Fragment {

    private static final String USER_NOTIFICATION_REF = "UserNotification";
    private static final String TAG = SubscriptionFragment.class.getSimpleName();
    private FloatingActionButton settingButton;
    private ArrayList<Notification> notificationList = new ArrayList<Notification>();
    private RecyclerView notificationRecyclerView;
    private NotificationRecyclerViewAdapter notificationAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseReference userNotificationRef;
    private ValueEventListener listener;


    public SubscriptionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Notifications");
        return inflater.inflate(R.layout.fragment_subscription, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setButtonListener();

        //recycler view stuff
        notificationRecyclerView = (RecyclerView) getView().findViewById(R.id.subscriptionRecyclerView);
        notificationRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this.getContext());
        notificationRecyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(notificationRecyclerView.getContext(),
                new LinearLayoutManager(getContext()).getOrientation());

        notificationRecyclerView.addItemDecoration(dividerItemDecoration);

        notificationRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), notificationRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Notification notification = notificationList.get(position);
                Intent intent = new Intent(getContext(), ViewNotification.class);
                intent.putExtra("key", notification.getKey());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }
        ));

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                    Notification notification = childSnapshot.getValue(Notification.class);
                    notificationList.add(notification);
                    Log.d(TAG, "body " + notification.getBody());
                }
                Collections.reverse(notificationList);
                notificationAdapter = new NotificationRecyclerViewAdapter(notificationList);
                notificationRecyclerView.setAdapter(notificationAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            userNotificationRef = FirebaseDatabase.getInstance().getReference(USER_NOTIFICATION_REF).child(user.getUid());
            userNotificationRef.addListenerForSingleValueEvent(listener);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    public void setButtonListener(){

        settingButton = (FloatingActionButton) getView().findViewById(R.id.subscription_settingFAB);

        settingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getContext(), NotificationSetting.class);
                startActivity(intent);

            }
        });
    }
}
