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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  Created by farmerboy on 4/8/2017.
 */
public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ImageButton mBtnHome;
    private Button mBtnCont;
    private EditText mEtMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        mBtnHome = (ImageButton) findViewById(R.id.btnHome);
        mBtnCont = (Button) findViewById(R.id.btnCont);
        mEtMail = (EditText) findViewById(R.id.etMail);

        mBtnCont.setOnClickListener(this);

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
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btnCont) {
            sendPasswordEmail();
        }
        else if (i == R.id.btnHome) {
            finish();
        }
    }

    private void sendPasswordEmail() {
        String mail = mEtMail.getText().toString();

        if (mail.isEmpty()) {
            Toast.makeText(ForgetPasswordActivity.this, "Vui lòng điền email.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!emailValidation(mail)) {
            Toast.makeText(ForgetPasswordActivity.this, "Email phải đúng định dạng email.", Toast.LENGTH_SHORT).show();
            return;
        }
        // đóng băng màn hình không cho bấm
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Toast.makeText(ForgetPasswordActivity.this, "Đang xử lý", Toast.LENGTH_SHORT).show();
        // gửi mail reset mật khẩu
        mAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(ForgetPasswordActivity.this, "Mail đổi mật khẩu đã được gửi đi", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(ForgetPasswordActivity.this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                }
                // mở đóng băng
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    private boolean emailValidation(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }
}
