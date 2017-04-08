package com.example.farmerboy.chatbbd.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.farmerboy.chatbbd.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    Button btnCont;
    EditText etUserName,etPassWord;

    //khai bao firebase
    //DatabaseReference mData;
     FirebaseAuth mAuthen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuthen = FirebaseAuth.getInstance();
        AnhXa();

        btnCont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DangNhap();
            }
        });
    }



    private void DangNhap(){
        String taikhoan = etUserName.getText().toString();
        String matkhau= etPassWord.getText().toString();

        mAuthen.signInWithEmailAndPassword(taikhoan, matkhau)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()){
                           Toast.makeText(LoginActivity.this,"Đang nhap thanh cong",Toast.LENGTH_SHORT).show();
                       }else{
                           Toast.makeText(LoginActivity.this,"Đang nhap that bai",Toast.LENGTH_SHORT).show();
                       }
                    }
                });
    }

    private void AnhXa() {
        //ánh xạ
        btnCont = (Button) findViewById(R.id.btnCont);
        etUserName = (EditText) findViewById(R.id.etUser);
        etPassWord = (EditText) findViewById(R.id.etPass);

    }

    public void BackWelcom(View view) {
        Intent intent = new Intent(LoginActivity.this,WelcomeActivity.class);
        startActivity(intent);
    }


}
