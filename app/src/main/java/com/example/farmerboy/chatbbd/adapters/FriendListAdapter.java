package com.example.farmerboy.chatbbd.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.farmerboy.chatbbd.R;
import com.example.farmerboy.chatbbd.classes.FriendListItem;
import com.example.farmerboy.chatbbd.views.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 *  Created by farmerboy on 7/8/2016.
 */
public class FriendListAdapter extends ArrayAdapter<FriendListItem> {

    public static final int TYPE_FRIENDS_NORM = 0;
    public static final int TYPE_FRIENDS_ONLINE = 1;
    public static final int TYPE_FIRST_LETTER = 2;
    public static final int TYPE_REQUEST = 3;

    private List<FriendListItem> mFriendListItems;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public FriendListAdapter(Context context, List<FriendListItem> friendListItems) {
        super(context, 0, friendListItems);
        mContext = context;
        mFriendListItems = friendListItems;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        if (mFriendListItems.isEmpty()) return 0;
        return mFriendListItems.size();
    }

    @Override
    public int getViewTypeCount() {
        return 5;
    }

    @Override
    public int getItemViewType(int position) {
        return mFriendListItems.get(position).getType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FriendListItem item = mFriendListItems.get(position);
        ViewHolder viewHolder;
        int listViewItemType = getItemViewType(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            if (listViewItemType == TYPE_FRIENDS_NORM) {
                convertView = mLayoutInflater.inflate(R.layout.item_friend_normal, parent, false);
                viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
                viewHolder.ivAvaIcon = (CircleImageView) convertView.findViewById(R.id.ivAvaIcon);
            }
            else if (listViewItemType == TYPE_FRIENDS_ONLINE) {
                convertView = mLayoutInflater.inflate(R.layout.item_friend_online, parent, false);
                viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
                viewHolder.ivAvaIcon = (CircleImageView) convertView.findViewById(R.id.ivAvaIcon);
            }
            else if (listViewItemType == TYPE_FIRST_LETTER) {
                convertView = mLayoutInflater.inflate(R.layout.item_abc, parent, false);
                viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            }
            else if (listViewItemType == TYPE_REQUEST) {
                convertView = mLayoutInflater.inflate(R.layout.item_friend_hint, parent, false);
                viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            }
            convertView.setTag(viewHolder);
        }
        else viewHolder = (ViewHolder) convertView.getTag();
        if (listViewItemType == TYPE_FRIENDS_NORM) {
            viewHolder.tvName.setText(item.getName());
            Uri photoUri = item.getImage();
            if (photoUri != null && !Uri.EMPTY.equals(photoUri)) {
                Picasso.with(mContext).load(photoUri).into(viewHolder.ivAvaIcon);
            }
            else Picasso.with(mContext).load(R.drawable.ic_avatar_pattern).into(viewHolder.ivAvaIcon);
        }
        else if (listViewItemType == TYPE_FRIENDS_ONLINE) {
            viewHolder.tvName.setText(item.getName());
            Uri photoUri = item.getImage();
            if (photoUri != null && !Uri.EMPTY.equals(photoUri)) {
                Picasso.with(mContext).load(photoUri).into(viewHolder.ivAvaIcon);
            }
            else Picasso.with(mContext).load(R.drawable.ic_avatar_pattern).into(viewHolder.ivAvaIcon);
        }
        else if (listViewItemType == TYPE_FIRST_LETTER) {
            viewHolder.tvName.setText(item.getName());
        }
        else if (listViewItemType == TYPE_REQUEST) {
            viewHolder.tvName.setText(item.getName());
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView tvName;
        CircleImageView ivAvaIcon;
    }
}
