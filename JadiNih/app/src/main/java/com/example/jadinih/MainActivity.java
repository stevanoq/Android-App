package com.example.jadinih;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    TextView nilaiSensor;
    TextView jam;
    TextView menit;
    TextView detik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nilaiSensor = (TextView) findViewById(R.id.nilaiSensor);
        jam = (TextView) findViewById(R.id.jam);
        menit = (TextView) findViewById(R.id.menit);
        detik = (TextView) findViewById(R.id.detik);
        DatabaseReference koneksi = FirebaseDatabase.getInstance().getReference();

        koneksi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Nilai = snapshot.child("nilai").getValue().toString();
                nilaiSensor.setText(Nilai);
                String Jam = snapshot.child("jam").getValue().toString();
                jam.setText(Jam);
                String Menit = snapshot.child("menit").getValue().toString();
                menit.setText(Menit);
                String Detik = snapshot.child("detik").getValue().toString();
                detik.setText(Detik);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}