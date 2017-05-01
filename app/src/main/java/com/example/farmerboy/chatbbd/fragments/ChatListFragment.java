package com.example.farmerboy.chatbbd.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.farmerboy.chatbbd.R;
import com.example.farmerboy.chatbbd.activities.ChatActivity;
import com.example.farmerboy.chatbbd.adapters.ChatListAdapter;
import com.example.farmerboy.chatbbd.classes.ChatListDetails;
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
public class ChatListFragment extends Fragment {

    private Context mContext;
    private ListView mLvChatList;
    private List<ChatListDetails> mListLvChatList = new ArrayList<>();
    private ChatListAdapter mChatListAdapter;
    private DatabaseReference mDatabase, mConnectionRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ChildEventListener mChildEventListener;

    public ChatListFragment() {
    }

    public ChatListFragment(Context context) {
        mContext = context;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mChildEventListener = mDatabase.child("conversation").child(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final String friendId = dataSnapshot.getKey();
                mDatabase.child("users").child(friendId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String friendName = dataSnapshot.child("name").getValue().toString();
                        String s = dataSnapshot.child("url").getValue().toString();
                        Uri uri = null;
                        if (!s.equals("`")) uri = Uri.parse(s);
                        Log.i("TAG11", friendName);
                        Query query = mDatabase.child("conversation").child(mAuth.getCurrentUser().getUid()).child(friendId).limitToLast(1);
                        final Uri finalUri = uri;
                        query.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                String content = dataSnapshot.child("content").getValue().toString();
                                String sender = dataSnapshot.child("uid").getValue().toString();
                                if (sender.equals(mAuth.getCurrentUser().getUid())) content = "Bạn: " + content;
                                final long time = (long) dataSnapshot.child("time").getValue();
                                final String finalContent = content;
                                mDatabase.child("read-message").child(mAuth.getCurrentUser().getUid()).child(friendId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Boolean h = (boolean) dataSnapshot.getValue();
                                        Log.i("TAG11", h.toString());
                                        ChatListDetails item = new ChatListDetails(friendName, finalContent, time, finalUri, h, friendId);
                                        for (ChatListDetails i : mListLvChatList)
                                            if (i.getId().equals(friendId)) {
                                                mListLvChatList.remove(i);
                                                break;
                                            }
                                        int r = binarySearch(mListLvChatList.size(), time);
                                        mListLvChatList.add(r, item);
                                        mChatListAdapter.notifyDataSetChanged();
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
                for (int i = mListLvChatList.size()-1; i >= 0; i--) {
                    if (mListLvChatList.get(i).getId().equals(friendId)) mListLvChatList.remove(i);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private int binarySearch(int size, long time) {
        int fi = 0;
        int la = size-1;
        while (fi <= la) {
            int mid = (fi + la) / 2;
            if (mListLvChatList.get(mid).getTime() > time) fi = mid+1;
            else la = mid-1;
        }
        return fi;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatlist, null);
        mLvChatList = (ListView) view.findViewById(R.id.lvChatList);
        mChatListAdapter = new ChatListAdapter(mContext, mListLvChatList);
        mLvChatList.setAdapter(mChatListAdapter);

        mLvChatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("friendId", mListLvChatList.get(position).getId());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mChildEventListener != null) mDatabase.removeEventListener(mChildEventListener);
    }
}
