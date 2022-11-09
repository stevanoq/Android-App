package com.example.majaes_java;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;
import android.widget.TextView;
import android.os.Bundle;

public class TzMemberActivity extends AppCompatActivity {
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tz_member);
        tv = (TextView)findViewById(R.id.memtxt);
        tv.setTextColor(Color.parseColor("#33B5E5"));
        tv.setText("Tech Zephyr Members:\n\nUtkarsh Manjrekar\nTechnical Secretary\n\nRohit Korlekar\nJoint Technical Secretary\n\nRushank Shetty\nCo-TechnicalSecretary");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tzmember, menu);
        return true;
    }
}