package com.example.motitoringaquarium

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getDataFromFirebase()

    }

    private fun getDataFromFirebase() {

        val jam: TextView = findViewById(R.id.jam) as TextView
        val menit: TextView = findViewById(R.id.menit) as TextView
        val detik: TextView = findViewById(R.id.detik) as TextView
        val nilai: TextView = findViewById(R.id.nilai) as TextView

        FirebaseDatabase.getInstance()
            .getReference("data")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val Jam = snapshot.child("jam").getValue().toString()
                    val Menit = snapshot.child("menit").getValue().toString()
                    val Detik = snapshot.child("detik").getValue().toString()
                    val Nilai = snapshot.child("tds").getValue()

                    jam.setText(Jam)
                    menit.setText(Menit)
                    detik.setText(Detik)
                    nilai.setText(Nilai.toString())
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })


    }
}


