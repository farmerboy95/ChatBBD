package com.example.farmerboy.chatbbd.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.farmerboy.chatbbd.R;
import com.example.farmerboy.chatbbd.activities.AddFriendActivity;
import com.example.farmerboy.chatbbd.activities.ChatActivity;
import com.example.farmerboy.chatbbd.activities.FriendRequestActivity;
import com.example.farmerboy.chatbbd.activities.SearchFriendActivity;
import com.example.farmerboy.chatbbd.adapters.FriendListAdapter;
import com.example.farmerboy.chatbbd.classes.FriendListItem;
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

/**
 *  Created by farmerboy on 4/18/2017.
 */
public class ContactFragment extends Fragment {

    private Context mContext;
    private DatabaseReference mDatabase, mConnectionRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ListView mLvFriends;
    private ImageButton mBtnSearchFriend, mBtnAddFriend;
    private List<FriendListItem> mFriendListItems = new ArrayList<>();
    private FriendListAdapter mFriendListAdapter;
    private ChildEventListener mChildEventListener;
    private Thread thread;

    public ContactFragment() {
    }

    public ContactFragment(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mFriendListItems.add(new FriendListItem("", "Yêu cầu kết bạn", "", null, FriendListAdapter.TYPE_REQUEST));
        mFriendListItems.add(new FriendListItem("", "Danh sách trực tuyến", "", null, FriendListAdapter.TYPE_FIRST_LETTER));
        mFriendListItems.add(new FriendListItem("", "Danh sách bạn bè", "", null, FriendListAdapter.TYPE_FIRST_LETTER));
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
                    // User is signed in
                    Log.d("TAG11", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("TAG11", "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, null);
        mLvFriends = (ListView) view.findViewById(R.id.lvFriends);
        mBtnAddFriend = (ImageButton) view.findViewById(R.id.btnAddFriend);
        mBtnSearchFriend = (ImageButton) view.findViewById(R.id.btnSearchFriend);

        mBtnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AddFriendActivity.class);
                startActivity(intent);
            }
        });

        mBtnSearchFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SearchFriendActivity.class);
                startActivity(intent);
            }
        });

        mFriendListAdapter = new FriendListAdapter(mContext, mFriendListItems);
        mLvFriends.setAdapter(mFriendListAdapter);

        FirebaseUser user = mAuth.getCurrentUser();

        mChildEventListener = mDatabase.child("friends").child(user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final String friendId = dataSnapshot.getKey();
                mDatabase.child("users").child(friendId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String friendName = dataSnapshot.child("name").getValue().toString();
                        String url = dataSnapshot.child("url").getValue().toString();
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
                String friendId = dataSnapshot.getKey();
                for (int i = mFriendListItems.size()-1; i >= 0; i--) {
                    if (mFriendListItems.get(i).getUid().equals(friendId)) {
                        mFriendListItems.remove(i);
                        break;
                    }
                }
                mFriendListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mLvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FriendListItem item = (FriendListItem) parent.getItemAtPosition(position);
                if (position == 0) {
                    Intent intent = new Intent(mContext, FriendRequestActivity.class);
                    startActivity(intent);
                } else if (item.getType() == FriendListAdapter.TYPE_FIRST_LETTER) {

                } else {
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("friendId", mFriendListItems.get(position).getUid());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        mLvFriends.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                FriendListItem item = (FriendListItem) parent.getItemAtPosition(position);
                if (item.getType() != FriendListAdapter.TYPE_FIRST_LETTER && item.getType() != FriendListAdapter.TYPE_REQUEST) {
                    showAlertDialog(position, item);
                }
                return false;
            }
        });


        return view;
    }

    private void showAlertDialog(final int position, final FriendListItem item) {
        // tạo một AlertDialog Builder
        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        builder1.setTitle("Xóa bạn");
        builder1.setMessage("Bạn có muốn xóa bạn " + item.getName() + " không?");

        builder1.setPositiveButton(
                "Có",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mFriendListItems.remove(position);
                        mFriendListAdapter.notifyDataSetChanged();
                        FirebaseUser user = mAuth.getCurrentUser();
                        mDatabase.child("friends").child(user.getUid()).child(item.getUid()).removeValue();
                        mDatabase.child("friends").child(item.getUid()).child(user.getUid()).removeValue();
                        mDatabase.child("conversation").child(user.getUid()).child(item.getUid()).removeValue();
                        mDatabase.child("conversation").child(item.getUid()).child(user.getUid()).removeValue();
                        Toast.makeText(mContext, "Đã xóa bạn", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "Không",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create và show dialog ra màn hình
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        for (int i = mFriendListItems.size()-1; i >= 0 ; i--) {
                            if (mFriendListItems.get(i).getType() == FriendListAdapter.TYPE_FRIENDS_ONLINE) mFriendListItems.remove(i);
                        }
                        ((Activity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mFriendListAdapter.notifyDataSetChanged();
                            }
                        });
                        Query q = mDatabase.child("friends").child(mAuth.getCurrentUser().getUid());
                        q.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    final String key = data.getKey();
                                    mDatabase.child("users").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Long timeOnline = Long.parseLong(dataSnapshot.child("timeOnline").getValue().toString());
                                            String url = dataSnapshot.child("url").getValue().toString();
                                            String friendName = dataSnapshot.child("name").getValue().toString();
                                            if (System.currentTimeMillis() - timeOnline <= 120000) {
                                                if (url.equals("`")) {
                                                    FriendListItem item = new FriendListItem(key, friendName, "", null, FriendListAdapter.TYPE_FRIENDS_ONLINE);
                                                    mFriendListItems.add(2, item);
                                                } else {
                                                    Uri uri = Uri.parse(url);
                                                    FriendListItem item = new FriendListItem(key, friendName, "", uri, FriendListAdapter.TYPE_FRIENDS_ONLINE);
                                                    mFriendListItems.add(2, item);
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

                        sleep(120000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (thread != null) {
            thread = null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChildEventListener != null) mDatabase.removeEventListener(mChildEventListener);
    }
}
