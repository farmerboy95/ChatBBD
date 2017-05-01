package com.example.farmerboy.chatbbd.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.farmerboy.chatbbd.R;
import com.example.farmerboy.chatbbd.activities.ChatActivity;
import com.example.farmerboy.chatbbd.classes.ChatItem;
import com.example.farmerboy.chatbbd.views.CircleImageView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *  Created by farmerboy on 4/30/2017.
 */
public class ChatAdapter extends ArrayAdapter<ChatItem> {

    private List<ChatItem> mChat;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private String mine, friend;
    private DatabaseReference mDatabase;
    private boolean first = false;
    private String uriMine, uriFriend;

    public ChatAdapter(Context context, List<ChatItem> settings, String myPhone, String friendPhone) {
        super(context, 0, settings);
        mContext = context;
        mChat = settings;
        mLayoutInflater = LayoutInflater.from(mContext);
        mine = myPhone;
        friend = friendPhone;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        mDatabase.child("users").child(mine).child("url").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uriMine = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("users").child(friend).child("url").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uriFriend = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query query = mDatabase.child("conversation").child(mine).child(friendPhone).limitToLast(20);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatItem chatItem = dataSnapshot.getValue(ChatItem.class);
                if (!first) {
                    ChatActivity.latestTimeLoaded = chatItem.getTime();
                    first = true;
                }
                if (chatItem.getUid().equals(mine)) chatItem.setIsMine(true);
                else chatItem.setIsMine(false);
                mChat.add(chatItem);
                notifyDataSetChanged();
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
    public int getCount() {
        if (mChat.isEmpty()) return 0;
        return mChat.size();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (mChat.get(position).isMine()) return 0;
        return 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ChatItem chat = mChat.get(position);
        final ViewHolder viewHolder;
        if (convertView == null) {
            if (chat.isMine()) convertView = mLayoutInflater.inflate(R.layout.item_chat_right, parent, false);
            else convertView = mLayoutInflater.inflate(R.layout.item_chat_left, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvMess = (TextView) convertView.findViewById(R.id.tvMess);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
            viewHolder.ivAvaIcon = (CircleImageView) convertView.findViewById(R.id.ivAvaIcon);
            convertView.setTag(viewHolder);
        }
        else viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.tvMess.setText(chat.getContent());
        Date date = new Date(chat.getTime() + 25200000);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String dateFormatted = formatter.format(date);
        viewHolder.tvTime.setText(dateFormatted);
        if (chat.isMine()) {
            if (uriMine.equals("`")) {
                Picasso.with(mContext).load(R.drawable.ic_avatar_pattern).into(viewHolder.ivAvaIcon);
            }
            else {
                Picasso.with(mContext).load(Uri.parse(uriMine)).into(viewHolder.ivAvaIcon);
            }
        }
        else {
            if (uriFriend.equals("`")) {
                Picasso.with(mContext).load(R.drawable.ic_avatar_pattern).into(viewHolder.ivAvaIcon);
            }
            else {
                Picasso.with(mContext).load(Uri.parse(uriFriend)).into(viewHolder.ivAvaIcon);
            }
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView tvMess, tvTime;
        CircleImageView ivAvaIcon;
    }
}
