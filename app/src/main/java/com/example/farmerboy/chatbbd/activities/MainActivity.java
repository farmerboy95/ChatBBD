package com.example.farmerboy.chatbbd.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.farmerboy.chatbbd.R;

import java.io.Serializable;


public class MainActivity extends AppCompatActivity  implements Serializable{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
