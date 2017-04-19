package com.example.farmerboy.chatbbd.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.farmerboy.chatbbd.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 *  Created by farmerboy on 4/19/2017.
 */
public class NameChangeActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference mDatabase, mConnectionRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText mEtName;
    private Button mBtnCont;
    private ImageButton mBtnHome;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_display_name);
        
        firebaseInitialization();

        mEtName = (EditText) findViewById(R.id.etName);
        mBtnCont = (Button) findViewById(R.id.btnCont);
        mBtnHome = (ImageButton) findViewById(R.id.btnHome);

        mEtName.setText(mAuth.getCurrentUser().getDisplayName());

        mBtnHome.setOnClickListener(this);
        mBtnCont.setOnClickListener(this);
    }

    private void firebaseInitialization() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);
        mConnectionRef = FirebaseDatabase.getInstance().getReference(".info/connected");
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
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btnCont) {
            checkName();
        }
        else if (i == R.id.btnHome) {
            finish();
        }
    }

    private void checkName() {
        String name = mEtName.getText().toString();

        if (name.trim().isEmpty()) {
            Toast.makeText(NameChangeActivity.this, "Vui lòng điền tên hiển thị cần đổi", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(NameChangeActivity.this, "Đang xử lý", Toast.LENGTH_SHORT).show();
        FirebaseUser user = mAuth.getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(NameChangeActivity.this, "Đổi tên hiển thị thành công", Toast.LENGTH_SHORT).show();
                            Log.d("TAG11", "User profile updated.");
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        }
                        else {
                            Toast.makeText(NameChangeActivity.this, "Đổi tên hiển thị thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
