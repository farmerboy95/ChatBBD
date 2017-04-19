package com.example.farmerboy.chatbbd.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.farmerboy.chatbbd.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 *  Created by farmerboy on 4/19/2017.
 */
public class PasswordChangeActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference mDatabase, mConnectionRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText mEtOldPass, mEtNewPass, mEtConfirmPass;
    private Button mBtnCont;
    private ImageButton mBtnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        firebaseInitialization();

        mEtOldPass = (EditText) findViewById(R.id.etOldPass);
        mEtNewPass = (EditText) findViewById(R.id.etNewPass);
        mEtConfirmPass = (EditText) findViewById(R.id.etConfirmPass);
        mBtnCont = (Button) findViewById(R.id.btnCont);
        mBtnHome = (ImageButton) findViewById(R.id.btnHome);

        mBtnHome.setOnClickListener(this);
        mBtnCont.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btnCont) {
            checkPassword();
        }
        else if (i == R.id.btnHome) {
            finish();
        }
    }

    private void checkPassword() {
        String oldPass = mEtOldPass.getText().toString();
        final String newPass = mEtNewPass.getText().toString();
        String conPass = mEtConfirmPass.getText().toString();

        if (newPass.length() < 6) {
            Toast.makeText(PasswordChangeActivity.this, "Mật khẩu mới phải có ít nhất 6 kí tự.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(conPass)) {
            Toast.makeText(PasswordChangeActivity.this, "Mật khẩu và Xác nhận mật khẩu không giống nhau.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(PasswordChangeActivity.this, "Đang xử lý", Toast.LENGTH_SHORT).show();
        final FirebaseUser user = mAuth.getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPass);
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(PasswordChangeActivity.this, "Đổi mật khẩu thành công.", Toast.LENGTH_SHORT).show();
                                        Log.d("TAG11", "Password updated");
                                        finish();
                                    } else {
                                        Toast.makeText(PasswordChangeActivity.this, "Có lỗi xảy ra!\nĐổi mật khẩu thất bại.", Toast.LENGTH_SHORT).show();
                                        Log.d("TAG11", "Error password not updated");
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(PasswordChangeActivity.this, "Mật khẩu cũ sai.", Toast.LENGTH_SHORT).show();
                            Log.d("TAG11", "Error auth failed");
                        }
                    }
                });
    }
}
