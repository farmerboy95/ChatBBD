package com.example.farmerboy.chatbbd.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.TimeZone;

/**
 *  Created by farmerboy on 4/25/2017.
 */
public class UpdateOnlineTimeService extends Service {

    private Handler handler;
    private DatabaseReference mDatabase, mConnectionRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private long date;

    @Override
    public void onCreate() {
        super.onCreate();
        firebaseInitialization();
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
                } else {
                    stopSelf();
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                date = System.currentTimeMillis();
                if (mAuth.getCurrentUser() != null) {
                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("timeOnline").setValue(date);
                }
                handler.postDelayed(this, 120000);
            }
        }, 0);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
