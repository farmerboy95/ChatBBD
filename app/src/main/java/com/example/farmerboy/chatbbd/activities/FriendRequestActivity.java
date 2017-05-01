package com.example.farmerboy.chatbbd.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.farmerboy.chatbbd.R;
import com.example.farmerboy.chatbbd.adapters.FriendRequestAdapter;
import com.example.farmerboy.chatbbd.classes.FriendListItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 *  Created by farmerboy on 4/25/2017.
 */
public class FriendRequestActivity extends AppCompatActivity {

    private DatabaseReference mDatabase, mConnectionRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ImageButton mBtnHome;
    private ListView mLvRequest;
    private List<FriendListItem> mRequestList = new ArrayList<>();
    private FriendRequestAdapter mFriendRequestAdapter;
    private ChildEventListener mChildEventListener;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_pending);

        firebaseInitialization();

        mBtnHome = (ImageButton) findViewById(R.id.btnHome);
        mLvRequest = (ListView) findViewById(R.id.lvRequest);

        mBtnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mFriendRequestAdapter = new FriendRequestAdapter(FriendRequestActivity.this, mRequestList, mAuth);
        mLvRequest.setAdapter(mFriendRequestAdapter);
        user = mAuth.getCurrentUser();


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
        mChildEventListener = mDatabase.child("friend-request-pending").child(user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final String key = dataSnapshot.getKey();
                mDatabase.child("users").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        String url = dataSnapshot.child("url").getValue().toString();
                        if (url.equals("`")) {
                            FriendListItem item = new FriendListItem(key, name, "", null, 0);
                            mRequestList.add(item);
                        }
                        else {
                            Uri uri = Uri.parse(url);
                            FriendListItem item = new FriendListItem(key, name, "", uri, 0);
                            mRequestList.add(item);
                        }
                        mFriendRequestAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        if (mChildEventListener != null) mDatabase.removeEventListener(mChildEventListener);
    }
}
