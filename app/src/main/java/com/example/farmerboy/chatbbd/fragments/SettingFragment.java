package com.example.farmerboy.chatbbd.fragments;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farmerboy.chatbbd.R;
import com.example.farmerboy.chatbbd.activities.AvatarChangeActivity;
import com.example.farmerboy.chatbbd.activities.InformationActivity;
import com.example.farmerboy.chatbbd.activities.NameChangeActivity;
import com.example.farmerboy.chatbbd.activities.PasswordChangeActivity;
import com.example.farmerboy.chatbbd.activities.WelcomeActivity;
import com.example.farmerboy.chatbbd.adapters.SettingAdapter;
import com.example.farmerboy.chatbbd.classes.ListViewGeneralItem;
import com.example.farmerboy.chatbbd.views.CircleImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 *  Created by farmerboy on 4/18/2017.
 */
public class SettingFragment extends Fragment {

    private ListView mLvSetting;
    private Context mContext;
    private List<ListViewGeneralItem> mListLvSetting = new ArrayList<>();
    private SettingAdapter mSettingAdapter;
    private TextView tvName;
    private DatabaseReference mDatabase, mConnectionRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CircleImageView mIvAvaIcon;
    private Dialog dialog;

    public SettingFragment() {
    }

    public SettingFragment(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListLvSetting.add(new ListViewGeneralItem(R.drawable.ic_change_name, "Đổi tên hiển thị"));
        mListLvSetting.add(new ListViewGeneralItem(R.drawable.ic_change_avatar, "Đổi hình đại diện"));
        mListLvSetting.add(new ListViewGeneralItem(R.drawable.ic_change_password, "Đổi mật khẩu"));
        mListLvSetting.add(new ListViewGeneralItem(R.drawable.ic_logout, "Đăng xuất"));
        mSettingAdapter = new SettingAdapter(mContext, mListLvSetting);
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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, null);
        mLvSetting = (ListView) view.findViewById(R.id.lvSetting);

        // lấy header
        LayoutInflater inflater1 = LayoutInflater.from(mContext);
        ViewGroup header = (ViewGroup) inflater1.inflate(R.layout.item_header_listview, mLvSetting, false);

        tvName = (TextView) header.findViewById(R.id.tvName);
        mIvAvaIcon = (CircleImageView) header.findViewById(R.id.ivAvaIcon);

        // lấy thông tin người dùng cho vào header
        final FirebaseUser user = mAuth.getCurrentUser();
        Uri photoUri = user.getPhotoUrl();
        tvName.setText(user.getDisplayName());

        if (photoUri != null && !Uri.EMPTY.equals(photoUri)) {
            Picasso.with(mContext).load(photoUri).into(mIvAvaIcon);
        }
        else Picasso.with(mContext).load(R.drawable.ic_avatar_pattern).into(mIvAvaIcon);

        // set header và adapter
        mLvSetting.addHeaderView(header, null, true);
        mLvSetting.setAdapter(mSettingAdapter);

        // làm việc với các item cài đặt
        mLvSetting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // nhấn vào header
                        Intent intentInfo = new Intent(mContext, InformationActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("friendId", user.getUid());
                        intentInfo.putExtras(bundle);
                        startActivity(intentInfo);
                        break;
                    case 1:
                        // đổi tên hiển thị
                        Intent intentName = new Intent(mContext, NameChangeActivity.class);
                        startActivityForResult(intentName, 1);
                        break;
                    case 2:
                        // đổi avatar
                        Intent intentAvaChange = new Intent(mContext, AvatarChangeActivity.class);
                        startActivityForResult(intentAvaChange, 2);
                        break;
                    case 3:
                        // đổi password
                        Intent intentPassChange = new Intent(mContext, PasswordChangeActivity.class);
                        startActivity(intentPassChange);
                        break;
                    case 4:
                        // logout
                        FirebaseAuth.getInstance().signOut();
                        Intent returnIntent = new Intent();
                        Toast.makeText(mContext, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                        getActivity().setResult(1001, returnIntent);
                        getActivity().finish();
                        break;
                }

            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                tvName.setText(user.getDisplayName());
            }
        }
        else if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Uri photoUri = user.getPhotoUrl();
                if (photoUri != null && !Uri.EMPTY.equals(photoUri)) {
                    Picasso.with(mContext).load(photoUri).into(mIvAvaIcon);
                }
                else Picasso.with(mContext).load(R.drawable.ic_avatar_pattern).into(mIvAvaIcon);
            }
        }
    }
}
