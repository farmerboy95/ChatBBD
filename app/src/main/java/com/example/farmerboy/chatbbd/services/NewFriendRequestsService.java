package com.example.farmerboy.chatbbd.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.farmerboy.chatbbd.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 *  Created by farmerboy on 5/1/2017.
 */
public class NewFriendRequestsService extends Service {

    private DatabaseReference mDatabase, mConnectionRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String uid;
    private ChildEventListener mChildEventListener;
    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        firebaseInitialization();
        uid = mAuth.getCurrentUser().getUid();
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
                    mDatabase.child("friend-request-pending").child(uid).removeEventListener(mChildEventListener);
                    stopSelf();
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mAuth.getCurrentUser() != null) {
            mChildEventListener = mDatabase.child("friend-request-pending").child(uid).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    ActivityManager am = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
                    ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                    if (cn.toString().equals("ComponentInfo{com.example.farmerboy.chatbbd/com.example.farmerboy.chatbbd.activities.FriendRequestActivity}")) {
                        return;
                    }
                    String friendId = dataSnapshot.getKey();
                    mDatabase.child("users").child(friendId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String name = dataSnapshot.child("name").getValue().toString();
                            //Intent intent = new Intent(mContext, MainActivity.class);
                            //PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
                            Notification n = new Notification.Builder(mContext)
                                    .setContentText("Lời mời kết bạn mới từ " + name)
                                    .setContentTitle("ChatBBD")
                                    .setSmallIcon(R.drawable.logo)
                                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.logo))
                                    //.setContentIntent(pIntent)
                                    .setAutoCancel(true).build();


                                NotificationManager notificationManager =
                                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                                notificationManager.notify(0, n);
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
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
