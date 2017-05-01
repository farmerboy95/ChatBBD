package com.example.farmerboy.chatbbd.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farmerboy.chatbbd.R;
import com.example.farmerboy.chatbbd.classes.FriendListItem;
import com.example.farmerboy.chatbbd.views.CircleImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 *  Created by farmerboy on 4/25/2017.
 */
public class FriendRequestAdapter extends ArrayAdapter<FriendListItem> {
    private List<FriendListItem> mFriendListItems;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    public FriendRequestAdapter(Context context, List<FriendListItem> friendListItems, FirebaseAuth auth) {
        super(context, 0, friendListItems);
        mContext = context;
        mFriendListItems = friendListItems;
        mLayoutInflater = LayoutInflater.from(mContext);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = auth;
    }

    @Override
    public int getCount() {
        if (mFriendListItems.isEmpty()) return 0;
        return mFriendListItems.size();
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return mFriendListItems.get(position).getType();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final FriendListItem item = mFriendListItems.get(position);
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.item_request, parent, false);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.ivAvaIcon = (CircleImageView) convertView.findViewById(R.id.ivAvaIcon);
            viewHolder.btnAccept = (Button) convertView.findViewById(R.id.btnAccept);
            viewHolder.btnDelete = (Button) convertView.findViewById(R.id.btnDelete);
            convertView.setTag(viewHolder);
        }
        else viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.tvName.setText(item.getName());
        Uri photoUri = item.getImage();
        if (photoUri != null && !Uri.EMPTY.equals(photoUri)) {
            Picasso.with(mContext).load(photoUri).into(viewHolder.ivAvaIcon);
        }
        else Picasso.with(mContext).load(R.drawable.ic_avatar_pattern).into(viewHolder.ivAvaIcon);
        viewHolder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = item.getUid();
                String myId = mAuth.getCurrentUser().getUid();
                mDatabase.child("friend-request-pending").child(myId).child(id).removeValue();
                mDatabase.child("friends").child(myId).child(id).setValue(true);
                mDatabase.child("friends").child(id).child(myId).setValue(true);
                Toast.makeText(mContext, "Bạn và " + item.getName() + " đã trở thành bạn bè", Toast.LENGTH_SHORT).show();
                mFriendListItems.remove(position);
                notifyDataSetChanged();
            }
        });
        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = item.getUid();
                String myId = mAuth.getCurrentUser().getUid();
                mDatabase.child("friend-request-pending").child(myId).child(id).removeValue();
                Toast.makeText(mContext, "Đã xóa yêu cầu kết bạn này", Toast.LENGTH_SHORT).show();
                mFriendListItems.remove(position);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        TextView tvName;
        CircleImageView ivAvaIcon;
        Button btnAccept, btnDelete;
    }
}
