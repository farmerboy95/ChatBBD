package com.example.farmerboy.chatbbd.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farmerboy.chatbbd.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  Created by farmerboy on 4/8/2017.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText mEtUser, mEtPass;
    private TextView mTvForgetPass;
    private Button mBtnCont;
    private ImageButton mBtnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEtUser = (EditText) findViewById(R.id.etUser);
        mEtPass = (EditText) findViewById(R.id.etPass);
        mBtnCont = (Button) findViewById(R.id.btnCont);
        mTvForgetPass = (TextView) findViewById(R.id.tvForgetPass);
        mBtnHome = (ImageButton) findViewById(R.id.btnHome);

        mBtnCont.setOnClickListener(this);
        mTvForgetPass.setOnClickListener(this);
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
            checkLogin();
        }
        else if (i == R.id.tvForgetPass) {
            Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
            startActivity(intent);
        }
        else if (i == R.id.btnHome) {
            finish();
        }
    }

    private void checkLogin() {
        String mail = mEtUser.getText().toString();
        String password = mEtPass.getText().toString();

        if (mail.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Vui lòng điền email.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!emailValidation(mail)) {
            Toast.makeText(LoginActivity.this, "Email phải đúng định dạng email.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(LoginActivity.this, "Mật khẩu phải có ít nhất 6 kí tự.", Toast.LENGTH_SHORT).show();
            return;
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Toast.makeText(LoginActivity.this, "Đang xử lý", Toast.LENGTH_SHORT).show();
        mAuth.signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.d("TAG11", "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
                        } else {
                            // nếu đã xác nhận mail thì login xong, không thì không cho
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user.isEmailVerified()) {
                                Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(LoginActivity.this, "Vui lòng xác nhận tài khoản trong Email của bạn.", Toast.LENGTH_SHORT).show();
                            }
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
