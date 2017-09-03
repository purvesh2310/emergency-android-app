package com.pk.eager.User;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.pk.eager.ReportFragments.IncidentType;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Account.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Account#newInstance} factory method to
 * create an instance of this fragment.
 */
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
        setupUI(currentUser);
        setButtonListener();
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IncidentType.OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
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
                signOut.setVisibility(View.GONE);
                signIn.setVisibility(View.VISIBLE);
                textView.setText("Please sign in or create an account");
            }
        });
    }

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(getActivity(), "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    public void setupUI(FirebaseUser currentUser){
        signIn = (Button)getView().findViewById(R.id.account_signup_Button);
        signOut = (Button)getView().findViewById(R.id.account_signout_Button);
        textView = (TextView)getView().findViewById(R.id.account_name_textview);
        if(currentUser != null){
            signIn.setVisibility(View.GONE);
            signOut.setVisibility(View.VISIBLE);
        }else{
            textView.setText("Please sign in or create an account.");
            signIn.setVisibility(View.VISIBLE);
            signOut.setVisibility(View.GONE);
        }
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
        TextView name = (TextView) getView().findViewById(R.id.account_name_textview);
        TextView email = (TextView) getView().findViewById(R.id.account_email_textview);

        //right now user's information isnt needed
        Log.d(TAG, "user email " + user.email);

        name.setText(currentUser.getDisplayName().toString());
        email.setText(currentUser.getEmail().toString());

    }
}
