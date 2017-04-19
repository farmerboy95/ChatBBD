package com.example.farmerboy.chatbbd.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.farmerboy.chatbbd.R;

/**
 *  Created by farmerboy on 4/18/2017.
 */
public class ChatListFragment extends Fragment {

    private Context mContext;

    public ChatListFragment() {
    }

    public ChatListFragment(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatlist, null);
        return view;
    }


}
