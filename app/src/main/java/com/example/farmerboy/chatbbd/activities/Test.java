package com.example.farmerboy.chatbbd.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.farmerboy.chatbbd.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 *  Created by farmerboy on 3/30/2017.
 */
public class Test extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText mEtUser, mEtPass, mEtConfirmPass, mEtMail, mEtName;
    private Button mBtnCont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEtUser = (EditText) findViewById(R.id.etUser);
        mEtPass = (EditText) findViewById(R.id.etPass);
        mEtConfirmPass = (EditText) findViewById(R.id.etConfirmPass);
        mEtMail = (EditText) findViewById(R.id.etMail);
        mEtName = (EditText) findViewById(R.id.etName);
        mBtnCont = (Button) findViewById(R.id.btnCont);

        mBtnCont.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("TAG11", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("TAG11", "onAuthStateChanged:signed_out");
                }
            }
        };

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();



    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        Log.d("TAG11", "" + i + " " + R.id.btnCont);
        if (i == R.id.btnCont) {
            createAccount();
        }
    }

    private void createAccount() {
        String username = mEtUser.getText().toString();
        String pass = mEtPass.getText().toString();
        String conPass = mEtConfirmPass.getText().toString();
        String mail = mEtMail.getText().toString();
        String name = mEtName.getText().toString();
        Log.d("TAG11", "I'm going in");

        if (pass.equals(conPass)) {
            mAuth.createUserWithEmailAndPassword(mail, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("TAG11", "createUserWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(Test.this, "Sai cmnr", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(Test.this, "Chuẩn cmnr", Toast.LENGTH_SHORT).show();
                            }

                            // [START_EXCLUDE]
                            //hideProgressDialog();
                            // [END_EXCLUDE]
                        }
                    });
        }
    }
}
