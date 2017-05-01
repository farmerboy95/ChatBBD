package com.example.farmerboy.chatbbd.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.example.farmerboy.chatbbd.R;
import com.example.farmerboy.chatbbd.fragments.ChatListFragment;
import com.example.farmerboy.chatbbd.fragments.ContactFragment;
import com.example.farmerboy.chatbbd.fragments.SettingFragment;
import com.example.farmerboy.chatbbd.services.NewFriendRequestsService;
import com.example.farmerboy.chatbbd.services.NewMessagesService;
import com.example.farmerboy.chatbbd.services.UpdateOnlineTimeService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase, mConnectionRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseInitialization();
        setupTabs();
    }

    private void setupTabs() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
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
                    Intent intent = new Intent(MainActivity.this, UpdateOnlineTimeService.class);
                    startService(intent);
                    Intent mIntent = new Intent(MainActivity.this, NewMessagesService.class);
                    startService(mIntent);
                    Intent rIntent = new Intent(MainActivity.this, NewFriendRequestsService.class);
                    startService(rIntent);
                } else {

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

    private void setupTabIcons() {
        View view1 = getLayoutInflater().inflate(R.layout.customtab, null);
        view1.findViewById(R.id.icon).setBackgroundResource(R.drawable.ic_chat);
        tabLayout.getTabAt(0).setCustomView(view1);
        View view2 = getLayoutInflater().inflate(R.layout.customtab, null);
        view2.findViewById(R.id.icon).setBackgroundResource(R.drawable.ic_people);
        tabLayout.getTabAt(1).setCustomView(view2);
        View view3 = getLayoutInflater().inflate(R.layout.customtab, null);
        view3.findViewById(R.id.icon).setBackgroundResource(R.drawable.ic_list);
        tabLayout.getTabAt(2).setCustomView(view3);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ChatListFragment(this), "ONE");
        adapter.addFrag(new ContactFragment(this), "TWO");
        adapter.addFrag(new SettingFragment(this), "THREE");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(MainActivity.this, UpdateOnlineTimeService.class);
        stopService(intent);
    }
}
