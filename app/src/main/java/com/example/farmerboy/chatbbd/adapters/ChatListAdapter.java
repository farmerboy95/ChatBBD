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
import com.example.farmerboy.chatbbd.classes.ChatListDetails;
import com.example.farmerboy.chatbbd.views.CircleImageView;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *  Created by farmerboy on 5/1/2017.
 */
public class ChatListAdapter extends ArrayAdapter<ChatListDetails> {
    private List<ChatListDetails> mChatList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public ChatListAdapter(Context context, List<ChatListDetails> settings) {
        super(context, 0, settings);
        mContext = context;
        mChatList = settings;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        if (mChatList.isEmpty()) return 0;
        return mChatList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ChatListDetails chatList = mChatList.get(position);
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_chatlist, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
            viewHolder.ivAvaIcon = (CircleImageView) convertView.findViewById(R.id.ivAvaIcon);
            viewHolder.ivNew = (ImageView) convertView.findViewById(R.id.ivNew);
            viewHolder.viewLine = (View) convertView.findViewById(R.id.viewLine);
            convertView.setTag(viewHolder);
        }
        else viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.tvName.setText(chatList.getName());
        viewHolder.tvContent.setText(chatList.getContent());
        Date date = new Date(chatList.getTime() + 25200000);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy\nHH:mm");
        String dateFormatted = formatter.format(date);
        viewHolder.tvTime.setText(dateFormatted);
        if (chatList.isRead()) viewHolder.ivNew.setVisibility(View.GONE);
        else viewHolder.ivNew.setVisibility(View.VISIBLE);
        Uri photoUri = chatList.getImage();
        if (photoUri != null && !Uri.EMPTY.equals(photoUri)) {
            Picasso.with(mContext).load(photoUri).into(viewHolder.ivAvaIcon);
        }
        else Picasso.with(mContext).load(R.drawable.ic_avatar_pattern).into(viewHolder.ivAvaIcon);
        return convertView;
    }

    private static class ViewHolder {
        TextView tvName, tvContent, tvTime;
        CircleImageView ivAvaIcon;
        ImageView ivNew;
        View viewLine;
    }
}
