package com.example.majaes_java;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;
import android.widget.TextView;
import android.os.Bundle;

public class InfoActivity extends AppCompatActivity {
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        tv = (TextView)findViewById(R.id.infotxt);
        tv.setText("Developer:\nChetan patil\n\nConcept :\nSanket L Humane\n\nProgramming assistance :\nRohit Korlekar\n\nSpecial Thanks to:\nYash Manian & Saiprasad Balasubramanian");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.info, menu);
        return true;
    }
}