package com.example.farmerboy.chatbbd.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.farmerboy.chatbbd.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  Created by farmerboy on 4/8/2017.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText mEtPass, mEtConfirmPass, mEtMail, mEtName;
    private Button mBtnCont;
    private ImageButton mBtnHome;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEtPass = (EditText) findViewById(R.id.etPass);
        mEtConfirmPass = (EditText) findViewById(R.id.etConfirmPass);
        mEtMail = (EditText) findViewById(R.id.etMail);
        mEtName = (EditText) findViewById(R.id.etName);
        mBtnCont = (Button) findViewById(R.id.btnCont);
        mBtnHome = (ImageButton) findViewById(R.id.btnHome);

        mBtnCont.setOnClickListener(this);
        mBtnHome.setOnClickListener(this);

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
            createAccount();
        }
        else if (i == R.id.btnHome) {
            finish();
        }
    }

    private void createAccount() {
        String pass = mEtPass.getText().toString();
        String conPass = mEtConfirmPass.getText().toString();
        final String mail = mEtMail.getText().toString();
        final String name = mEtName.getText().toString();

        if (name.trim().isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Vui lòng điền tên hiển thị.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mail.trim().isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Vui lòng điền email.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!emailValidation(mail)) {
            Toast.makeText(RegisterActivity.this, "Email phải đúng định dạng email.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pass.length() < 6) {
            Toast.makeText(RegisterActivity.this, "Mật khẩu phải có ít nhất 6 kí tự.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(conPass)) {
            Toast.makeText(RegisterActivity.this, "Mật khẩu và Xác nhận mật khẩu không giống nhau.", Toast.LENGTH_SHORT).show();
            return;
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Toast.makeText(RegisterActivity.this, "Đang xử lý", Toast.LENGTH_SHORT).show();

        mAuth.createUserWithEmailAndPassword(mail, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TAG11", "createUserWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Có lỗi xảy ra!\nĐăng kí thất bại.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Đăng ký thành công.\nVui lòng xác nhận tài khoản trong Email.", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            // bỏ name vào tài khoản
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("TAG11", "User profile updated.");
                                            }
                                        }
                                    });
                            // gửi mail xác nhận
                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("TAG11", "Email sent.");
                                    }
                                }
                            });
                            // thêm mail vào database
                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.child("users").child(user.getUid()).child("mail").setValue(mail);
                            finish();
                        }
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                });
    }

    private boolean emailValidation(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }
}
