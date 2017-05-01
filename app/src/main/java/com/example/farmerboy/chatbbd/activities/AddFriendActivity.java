package com.example.farmerboy.chatbbd.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.farmerboy.chatbbd.R;
import com.google.android.gms.internal.zzbmn;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  Created by farmerboy on 4/21/2017.
 */
public class AddFriendActivity extends AppCompatActivity {

    private final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private Button mBtnCont;
    private ImageButton mBtnHome;
    private EditText mEtEmail;
    private DatabaseReference mDatabase, mConnectionRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        firebaseInitialization();

        mEtEmail = (EditText) findViewById(R.id.etEmail);
        mBtnCont = (Button) findViewById(R.id.btnCont);
        mBtnHome = (ImageButton) findViewById(R.id.btnHome);

        mBtnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBtnCont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = mEtEmail.getText().toString();
                final String myUid = mAuth.getCurrentUser().getUid();

                if (mail.trim().isEmpty()) {
                    Toast.makeText(AddFriendActivity.this, "Vui lòng điền email.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!emailValidation(mail)) {
                    Toast.makeText(AddFriendActivity.this, "Email phải đúng định dạng email.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mail.equals(mAuth.getCurrentUser().getEmail())) {
                    Toast.makeText(AddFriendActivity.this, "Bạn không thể kết bạn với chính mình.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(AddFriendActivity.this, "Đang xử lý", Toast.LENGTH_SHORT).show();

                mDatabase.child("users").orderByChild("mail").equalTo(mail).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Object obj = dataSnapshot.getValue();
                        if (obj == null) {
                            Toast.makeText(AddFriendActivity.this, "Email này chưa được đăng ký", Toast.LENGTH_LONG).show();
                        }
                        else {
                            final String friendUid = filter(dataSnapshot.getValue().toString());
                            mDatabase.child("friends").child(myUid).child(friendUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Object object = dataSnapshot.getValue();
                                    if (object != null) {
                                        Toast.makeText(AddFriendActivity.this, "Hai bạn hiện đang là bạn bè", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    mDatabase.child("friend-request-pending").child(friendUid).child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Object object = dataSnapshot.getValue();
                                            if (object != null) {
                                                Toast.makeText(AddFriendActivity.this, "Bạn đã gửi yêu cầu kết bạn đến người này", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            mDatabase.child("friend-request-pending").child(myUid).child(friendUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    Object object = dataSnapshot.getValue();
                                                    if (object != null) {
                                                        Toast.makeText(AddFriendActivity.this, "Người này đã gửi yêu cầu kết bạn cho bạn\nVui lòng chấp nhận ở phần Yêu cầu kết bạn", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                    mDatabase.child("friend-request-pending").child(friendUid).child(myUid).setValue(true);
                                                    Toast.makeText(AddFriendActivity.this, "Gửi yêu cầu thành công\nĐang chờ chấp nhận", Toast.LENGTH_LONG).show();
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

    private String filter(String s) {
        String res = "";
        for (int i = 1; i < s.length(); i++)
            if (s.charAt(i) == '=') break;
            else res += s.charAt(i);
        return res;
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

    private boolean emailValidation(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }
}
