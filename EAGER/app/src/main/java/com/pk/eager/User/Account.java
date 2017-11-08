package com.pk.eager.User;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pk.eager.R;


public class Account extends Fragment implements GoogleApiClient.OnConnectionFailedListener{

    private OnFragmentInteractionListener mListener;
    final String TAG = "Account";

    private TextView textView;
    private Button signIn;
    private Button signOut;
    private GoogleApiClient googleApiClient;
    private DatabaseReference userRef;
    private FirebaseUser currentUser;
    private User user;
    private LinearLayout signedInView;
    private LinearLayout unsignedView;

    public Account() {
        // Required empty public constructor
    }


    public static Account newInstance(String param1, String param2) {
        Account fragment = new Account();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle("Account Options");

        configureGoogle();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser!=null) {
            userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        user = dataSnapshot.getValue(User.class);
                        setUpUserInfoInUI(user);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        signedInView = (LinearLayout) view.findViewById(R.id.signedInContentView);
        unsignedView = (LinearLayout) view.findViewById(R.id.unsignedContentView);

        if(currentUser != null){
            signedInView.setVisibility(View.VISIBLE);
            unsignedView.setVisibility(View.GONE);
        } else {
            signedInView.setVisibility(View.GONE);
            unsignedView.setVisibility(View.VISIBLE);
        }

        setButtonListener();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }

    @Override
    public void onPause() {
        super.onPause();

        googleApiClient.stopAutoManage(getActivity());
        googleApiClient.disconnect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    public void setButtonListener(){
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), SignUp.class);
                startActivity(intent);
            }
        });

        signOut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Signed out");

                FirebaseAuth.getInstance().signOut();
                Auth.GoogleSignInApi.signOut(googleApiClient);
                LoginManager.getInstance().logOut();

                signedInView.setVisibility(View.GONE);
                unsignedView.setVisibility(View.VISIBLE);
            }
        });
    }

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(getActivity(), "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    public void configureGoogle(){
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        if(googleApiClient==null) {
            googleApiClient = new GoogleApiClient.Builder(this.getActivity())
                    .enableAutoManage(this.getActivity() /* FragmentActivity */, this/* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }
    }

    public void setUpUserInfoInUI(User user) {

        TextView name = (TextView) getView().findViewById(R.id.account_username_textview);
        TextView email = (TextView) getView().findViewById(R.id.account_email_textview);

        //right now user's information isnt needed
        Log.d(TAG, "user email " + user.email);

        if(currentUser.getDisplayName() != null){
            name.setText(currentUser.getDisplayName().toString());
        } else {
            name.setVisibility(View.GONE);
        }

        email.setText(currentUser.getEmail().toString());

    }
}
