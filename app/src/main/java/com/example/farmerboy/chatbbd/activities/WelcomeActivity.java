package com.example.farmerboy.chatbbd.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.farmerboy.chatbbd.R;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnLogin;

    //khai bao firebase
    //DatabaseReference mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mBtnLogin = (Button) findViewById(R.id.btnLogin);
        mBtnLogin.setOnClickListener(this);

        //khởi tạo Firebase
        //mData = FirebaseDatabase.getInstance().getReference();
        //mData la note gốc => khi gọi mData là gọi tới note gốc chatbbd
        /*
        // kiem tra xem da luu thanh công k
        mData.child("HoaiBao").setValue("Lap trinh android", new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError == null){
                    Toast.makeText(WelcomeActivity.this,"Lưu thành công",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(WelcomeActivity.this,"Lưu thất bại",Toast.LENGTH_SHORT).show();
                }
            }
        });*/


    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.btnLogin) {
            //Toast.makeText(WelcomeActivity.this, "Sai cmnr", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(WelcomeActivity.this,LoginActivity.class);
            startActivity(intent);

        }


    }



}
