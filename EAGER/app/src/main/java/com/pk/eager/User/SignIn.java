package com.pk.eager.User;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.pk.eager.Dashboard;
import com.pk.eager.R;

public class SignIn extends AppCompatActivity {

    private FirebaseAuth auth;
    private static final String TAG = "SignIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
    }

    public void signInWithEmailAndPassword(View view){
        auth = FirebaseAuth.getInstance();
        String email = ((EditText)findViewById(R.id.signIn_email_edittext)).getText().toString();
        String password = ((EditText)findViewById(R.id.signIn_password_edittext)).getText().toString();
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "sign in complete");

                if(task.isSuccessful()){
                    loggedIn();
                }else{
                    Toast.makeText(SignIn.this, "Sign In failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void loggedIn(){
        Intent intent = new Intent(this, Dashboard.class);
        startActivity(intent);
    }

    public void switchToSignup(View view){
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }





}
