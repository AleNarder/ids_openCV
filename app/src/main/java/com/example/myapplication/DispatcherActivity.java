package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DispatcherActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dispatcher);

        Button testButton = findViewById(R.id.test);

        Button openCVButton = findViewById(R.id.openCV);

        Intent testIntent = new Intent(getBaseContext(), FirstTryActivity.class);

        Intent openCVIntent = new Intent (getBaseContext() ,  MainActivity.class);


        testButton.setOnClickListener(v->startActivity(testIntent));
        openCVButton.setOnClickListener(v->startActivity(openCVIntent));


    }
}
