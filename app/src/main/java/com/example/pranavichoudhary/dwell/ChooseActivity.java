package com.example.pranavichoudhary.dwell;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.OnMapReadyCallback;

public class ChooseActivity extends AppCompatActivity {

    Button pothole;
    Button noise;
    Button luminance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        tb.setSubtitle("Location Alert");
    }

    public void onPotholeClicked(View v){
        Intent i1 = new Intent(this, PotholeActivity.class);
        startActivity(i1);
    }

    public void onNoiseClicked(View v){
        Intent i1 = new Intent(this, AudioActivity.class);
        startActivity(i1);

    }
    public void onLuminanceClicked(View v){
        Intent i1 = new Intent(this, LuminanceActivity.class);
        startActivity(i1);

    }



}