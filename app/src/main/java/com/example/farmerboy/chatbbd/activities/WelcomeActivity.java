package com.example.farmerboy.chatbbd.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.farmerboy.chatbbd.R;

/**
 *  Created by farmerboy on 3/30/2017.
 */
public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mBtnLogin = (Button) findViewById(R.id.btnLogin);
        mBtnLogin.setOnClickListener(this);
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
