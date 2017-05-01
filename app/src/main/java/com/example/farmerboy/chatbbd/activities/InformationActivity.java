package com.example.farmerboy.chatbbd.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.farmerboy.chatbbd.R;
import com.example.farmerboy.chatbbd.views.CircleImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 *  Created by farmerboy on 4/18/2017.
 */
public class InformationActivity extends AppCompatActivity {

    private DatabaseReference mDatabase, mConnectionRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText mEtName, mEtEmail;
    private ImageButton mBtnHome;
    private CircleImageView mIvAvaIcon;
    private InformationActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        activity = this;

        mEtName = (EditText) findViewById(R.id.etName);
        mEtEmail = (EditText) findViewById(R.id.etEmail);
        mBtnHome = (ImageButton) findViewById(R.id.btnHome);
        mIvAvaIcon = (CircleImageView) findViewById(R.id.ivAvaIcon);

        mBtnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firebaseInitialization();

        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            String friendId = mBundle.getString("friendId");
            mDatabase.child("users").child(friendId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mEtName.setText(dataSnapshot.child("name").getValue().toString());
                    mEtEmail.setText(dataSnapshot.child("mail").getValue().toString());
                    String s = dataSnapshot.child("url").getValue().toString();
                    Uri photoUri = null;
                    if (!s.equals("`")) photoUri = Uri.parse(s);
                    if (photoUri != null && !Uri.EMPTY.equals(photoUri)) {
                        Picasso.with(activity).load(photoUri).into(mIvAvaIcon);
                    } else Picasso.with(activity).load(R.drawable.ic_avatar_pattern).into(mIvAvaIcon);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
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
}
