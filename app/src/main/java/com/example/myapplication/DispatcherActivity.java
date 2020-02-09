package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.OpenCVLoader;

public class DispatcherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatcher);

        Button testButton = findViewById(R.id.test);
        Button openCVButton = findViewById(R.id.openCV);
        Button nearbyButton = findViewById(R.id.nearby);

        if (!OpenCVLoader.initDebug()) {
            Log.e("AndroidIngSwOpenCV", "Unable to load OpenCV");
        } else {
            Log.d("AndroidIngSwOpenCV", "OpenCV loaded");
        }

        Intent testIntent   = new Intent(getBaseContext(), FirstTryActivity.class);
        Intent openCVIntent = new Intent(getBaseContext(), MainActivity.class);
        //Intent nearbyIntent = new Intent(getBaseContext(), Nearby.class);

        testButton.setOnClickListener(v->startActivity(testIntent));
        openCVButton.setOnClickListener(v->startActivity(openCVIntent));
        //nearbyButton.setOnClickListener(v->startActivity(nearbyIntent));

    }
}
