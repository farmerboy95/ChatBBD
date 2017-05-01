package com.example.farmerboy.chatbbd.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.farmerboy.chatbbd.R;
import com.example.farmerboy.chatbbd.adapters.FriendListAdapter;
import com.example.farmerboy.chatbbd.classes.FriendListItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 *  Created by farmerboy on 5/1/2017.
 */
public class SearchFriendActivity extends AppCompatActivity {

    private DatabaseReference mDatabase, mConnectionRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ImageButton mBtnHome, mBtnSearch;
    private ListView mLvFriends;
    private EditText mEtSearch;
    private List<FriendListItem> mFriendListItems = new ArrayList<>();
    private FriendListAdapter mFriendListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);

        firebaseInitialization();

        mBtnHome = (ImageButton) findViewById(R.id.btnHome);
        mBtnSearch = (ImageButton) findViewById(R.id.btnSearch);
        mEtSearch = (EditText) findViewById(R.id.etSearch);
        mLvFriends = (ListView) findViewById(R.id.lvFriends);

        mBtnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mFriendListAdapter = new FriendListAdapter(this, mFriendListItems);
        mLvFriends.setAdapter(mFriendListAdapter);

        mLvFriends.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchFriendActivity.this, InformationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("friendId", mFriendListItems.get(position).getUid());
                intent.putExtras(bundle);
                startActivity(intent);
                return false;
            }
        });

        mLvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchFriendActivity.this, ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("friendId", mFriendListItems.get(position).getUid());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String s = mEtSearch.getText().toString();
                if (s.trim().isEmpty()) {
                    Toast.makeText(SearchFriendActivity.this, "Nhập tên bạn cần tìm", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(SearchFriendActivity.this, "Đang tìm kiếm", Toast.LENGTH_SHORT).show();
                FirebaseUser user = mAuth.getCurrentUser();

                mDatabase.child("friends").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (final DataSnapshot data : dataSnapshot.getChildren()) {
                            final String friendId = data.getKey();
                            mDatabase.child("users").child(friendId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String friendName = dataSnapshot.child("name").getValue().toString();
                                    String url = dataSnapshot.child("url").getValue().toString();
                                    if (friendName.toLowerCase().contains(s.toLowerCase())) {
                                        if (url.equals("`")) {
                                            FriendListItem item = new FriendListItem(friendId, friendName, "", null, FriendListAdapter.TYPE_FRIENDS_NORM);
                                            mFriendListItems.add(item);
                                        } else {
                                            Uri uri = Uri.parse(url);
                                            FriendListItem item = new FriendListItem(friendId, friendName, "", uri, FriendListAdapter.TYPE_FRIENDS_NORM);
                                            mFriendListItems.add(item);
                                        }
                                        mFriendListAdapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
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
