/**
 * Dupplicate of Account.java, this is an activity instead of a fragment
 */
/*
package com.pk.eager.User;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pk.eager.R;

public class UserAccount extends AppCompatActivity {
    private TextView textView;
    private Button signIn;
    private Button signOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        signIn = (Button)findViewById(R.id.userAccount_signup_Button);
        signOut = (Button)findViewById(R.id.userAccount_signout_Button);
        textView = (TextView)findViewById(R.id.userAccount_textview);
        if(currentUser != null){
            signIn.setVisibility(View.GONE);
            signOut.setVisibility(View.VISIBLE);
        }else{
            textView.setText("Please sign in or create an account.");
            signIn.setVisibility(View.VISIBLE);
            signOut.setVisibility(View.GONE);

        }
    }

    public void onSignInButtonClick(View view){
        Intent intent = new Intent(getApplicationContext(), SignUp.class);
        startActivity(intent);
    }

    public void onSignOutButtonClick(View view){
        FirebaseAuth.getInstance().signOut();
        signOut.setVisibility(View.GONE);
        signIn.setVisibility(View.VISIBLE);
    }
}
*/