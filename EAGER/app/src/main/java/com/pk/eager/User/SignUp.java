package com.pk.eager.User;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pk.eager.Dashboard;
import com.pk.eager.R;

public class SignUp extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int GG_SIGN_IN = 9001;
    private static final int FB_SIGN_IN = 64206;
    private static final String TAG = "SignUp";
    private FirebaseAuth auth;
    private GoogleApiClient googleApiClient;
    private CallbackManager mCallbackManager;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;
    private boolean firstTimeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("Users");
        firstTimeUser = true;

        configureGoogleAuth();
        configureFacebookAuth();
    }


    //Configure google for authentication - onCreate
    public void configureGoogleAuth(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this/* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SignInButton ggLoginButton = (SignInButton) findViewById(R.id.signUp_google_login_button);
        ggLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignInWithGoogle();
            }
        });
    }

    //configure facebook for authentication - onCreate
    public void configureFacebookAuth(){
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton fbloginButton = (LoginButton) findViewById(R.id.signUp_facebook_login_button);
        fbloginButton.setReadPermissions("email", "public_profile");
        fbloginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                Toast.makeText(getApplicationContext(), "Facebook Login cancelled", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                Toast.makeText(getApplicationContext(), "Error Logging in with Facebook", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //email sign in
    public void onCreateAccountWithEmailAndPasswordButton(View view){
        auth = FirebaseAuth.getInstance();
        final String email = ((EditText)findViewById(R.id.signUp_email_editText)).getText().toString();
        String password = ((EditText)findViewById(R.id.signUp_password_editText)).getText().toString();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "create user with email and pw complete");
                if(task.isSuccessful()){
                    String uid = auth.getCurrentUser().getUid();
                    verifyAndAddUser(uid, email);
                }else{
                    Toast.makeText(SignUp.this, "Sign Up failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //google sign in
    public void onSignInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, GG_SIGN_IN);
    }

    //call to google auth when login success
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            verifyAndAddUser(user.getUid(), user.getEmail());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //call to facebook auth when login success
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            verifyAndAddUser(user.getUid(), user.getEmail());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignUp.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    //On result returns for google and facebook
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GG_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                Toast toast = Toast.makeText(this.getApplicationContext(), "Signed In", Toast.LENGTH_SHORT);
            } else {
                Toast toast = Toast.makeText(this.getApplicationContext(), "Google Sign-In Failed", Toast.LENGTH_SHORT);
                toast.show();
            }
        }else if(requestCode == FB_SIGN_IN){
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    //Google Api client on connection fail
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    //--------Database related functions---------//

    public void addUser(String uid, String email){
        User newUser = new User(email);
        usersRef.child(uid).setValue(newUser);
    }

    private void verifyAndAddUser(final String uid, final String email){
        DatabaseReference ref = usersRef.child(uid);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()==null)
                    addUser(uid, email);
                gotoDashBoard();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




    //----intents to other activity-----//

    public void gotoDashBoard(){
        Intent intent = new Intent(this, Dashboard.class);
        startActivity(intent);
    }

    public void onSwitchToLoginButton(View view){
        Intent intent = new Intent(this, SignIn.class);
        startActivity(intent);
    }



}
