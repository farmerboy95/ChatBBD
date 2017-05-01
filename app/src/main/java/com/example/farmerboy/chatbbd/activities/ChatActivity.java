package com.example.farmerboy.chatbbd.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.farmerboy.chatbbd.R;
import com.example.farmerboy.chatbbd.adapters.ChatAdapter;
import com.example.farmerboy.chatbbd.classes.ChatItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *  Created by farmerboy on 4/29/2017.
 */
public class ChatActivity extends AppCompatActivity {

    private ImageButton mBtnHome, mBtnSend;
    private TextView mTvName, mTvTime;
    private EditText mEtChat;
    private ListView mLvChat;
    private List<ChatItem> chatItemList;
    private ChatAdapter chatAdapter;
    private Bundle mBundle;
    private String mFriendUid;
    public static long latestTimeLoaded = 0;
    public static int MAX_LOAD_CHAT = 20;
    private ValueEventListener mValueEventListener;
    private int currentScrollState;
    private int currentVisibleItemCount;
    private int currentFirstVisibleItem;
    private boolean done = true;

    private DatabaseReference mDatabase, mConnectionRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mBtnHome = (ImageButton) findViewById(R.id.btnHome);
        mTvName = (TextView) findViewById(R.id.tvName);
        mTvTime = (TextView) findViewById(R.id.tvTime);
        mBtnSend = (ImageButton) findViewById(R.id.btnSend);
        mEtChat = (EditText) findViewById(R.id.etChat);
        mLvChat = (ListView) findViewById(R.id.lvChat);
        chatItemList = new ArrayList<>();

        firebaseInitialization();

        mBtnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mEtChat.getText().toString();
                long time = System.currentTimeMillis();
                ChatItem chatItem = new ChatItem(mAuth.getCurrentUser().getUid(), text, time, true);
                mDatabase.child("conversation").child(mAuth.getCurrentUser().getUid()).child(mFriendUid).push().setValue(chatItem);
                mDatabase.child("conversation").child(mFriendUid).child(mAuth.getCurrentUser().getUid()).push().setValue(chatItem);
                mDatabase.child("read-message").child(mFriendUid).child(mAuth.getCurrentUser().getUid()).setValue(false);
                mEtChat.setText("");
            }
        });

        mBundle = getIntent().getExtras();
        final FirebaseUser user = mAuth.getCurrentUser();
        if (mBundle != null) {
            mFriendUid = mBundle.getString("friendId");

            mTvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ChatActivity.this, InformationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("friendId", mFriendUid);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            mDatabase.child("users").child(mFriendUid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String s = dataSnapshot.getValue().toString();
                    mTvName.setText(s);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mDatabase.child("users").child(mFriendUid).child("timeOnline").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Long time = Long.parseLong(dataSnapshot.getValue().toString());
                    Long now = System.currentTimeMillis();
                    now -= time;
                    if (now <= 120000) mTvTime.setText("Trực tuyến");
                    else {
                        if (TimeUnit.MILLISECONDS.toDays(now) > 0) mTvTime.setText("Truy cập lần cuối " + TimeUnit.MILLISECONDS.toDays(now) + " ngày trước");
                        else if (TimeUnit.MILLISECONDS.toHours(now) > 0) mTvTime.setText("Truy cập lần cuối " + TimeUnit.MILLISECONDS.toHours(now) + " giờ trước");
                        else mTvTime.setText("Truy cập lần cuối " + TimeUnit.MILLISECONDS.toMinutes(now) + " phút trước");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mValueEventListener = mDatabase.child("read-message").child(user.getUid()).child(mFriendUid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mDatabase.child("read-message").child(user.getUid()).child(mFriendUid).setValue(true);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            chatAdapter = new ChatAdapter(this, chatItemList, user.getUid(), mFriendUid);
            mLvChat.setAdapter(chatAdapter);
            mLvChat.setSelection(MAX_LOAD_CHAT - 1);
            mLvChat.setStackFromBottom(false);
            mLvChat.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    currentScrollState = scrollState;
                    if (currentVisibleItemCount > 0 && currentScrollState == SCROLL_STATE_IDLE) {
                        if (!done) return;
                        if (currentFirstVisibleItem == 0) {
                            mLvChat.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
                            // getMessages(); //write what you want to do when you scroll up to the end of listview.
                            final Query query = mDatabase.child("conversation").child(user.getUid()).child(mFriendUid).orderByChild("time").endAt(latestTimeLoaded - 1).limitToLast(MAX_LOAD_CHAT);
                            final boolean[] first = {false};
                            final int[] k = {0};
                            Log.i("TAG11", "fuck");
                            ChildEventListener a = query.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    ChatItem chatItem = dataSnapshot.getValue(ChatItem.class);
                                    if (!first[0]) {
                                        done = false;
                                        Log.i("TAG11", "get the god damn time");
                                        ChatActivity.latestTimeLoaded = chatItem.getTime();
                                        first[0] = true;
                                    }
                                    if (chatItem.getUid().equals(user.getUid()))
                                        chatItem.setIsMine(true);
                                    else chatItem.setIsMine(false);
                                    chatItemList.add(k[0], chatItem);
                                    k[0]++;
                                    chatAdapter.notifyDataSetChanged();
                                    if (k[0] == MAX_LOAD_CHAT) {
                                        done = true;
                                        Log.i("TAG11", "done it");
                                        mLvChat.setSelection(MAX_LOAD_CHAT);
                                    }
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
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    currentFirstVisibleItem = firstVisibleItem;
                    currentVisibleItemCount = visibleItemCount;
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
        if (mValueEventListener != null) {
            mDatabase.child("read-message").child(mAuth.getCurrentUser().getUid()).child(mFriendUid).removeEventListener(mValueEventListener);
        }
    }
}
