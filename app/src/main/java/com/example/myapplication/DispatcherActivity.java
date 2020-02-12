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

        Button prova1Button = findViewById(R.id.prova1);
        Button prova2Button = findViewById(R.id.prova2);

        if (!OpenCVLoader.initDebug()) {
            Log.e("AndroidIngSwOpenCV", "Unable to load OpenCV");
        } else {
            Log.d("AndroidIngSwOpenCV", "OpenCV loaded");
        }

        Intent prova1Intent   = new Intent(getBaseContext(), FirstTryActivity.class);
        Intent prova2Intent   = new Intent(getBaseContext(), SecondTryActivity.class);

        prova1Button.setOnClickListener(v->startActivity(prova1Intent));
        prova2Button.setOnClickListener(v->startActivity(prova2Intent));

    }
}
