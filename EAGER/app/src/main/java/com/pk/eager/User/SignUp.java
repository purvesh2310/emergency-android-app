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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pk.eager.Dashboard;
import com.pk.eager.R;

public class SignUp extends AppCompatActivity {

    private FirebaseAuth auth;
    private static final String TAG = "SignUp";
    private FirebaseDatabase database;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("Users");
    }

    public void onCreateAccountWithEmailAndPasswordButton(View view){
        auth = FirebaseAuth.getInstance();
        String email = ((EditText)findViewById(R.id.signUp_email_editText)).getText().toString();
        String password = ((EditText)findViewById(R.id.signUp_password_editText)).getText().toString();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "create user with email and pw complete");
                if(task.isSuccessful()){
                    addUser(auth.getCurrentUser().getUid(), auth.getCurrentUser().getEmail());
                    accountCreated();
                }else{
                    Toast.makeText(SignUp.this, "Sign Up failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void addUser(String uid, String email){
        User newUser = new User(email);
        usersRef.child(uid).setValue(newUser);
    }

    public void accountCreated(){
        Intent intent = new Intent(this, Dashboard.class);
        startActivity(intent);
    }

    public void onSwitchToLoginButton(View view){
        Intent intent = new Intent(this, SignIn.class);
        startActivity(intent);
    }

}
