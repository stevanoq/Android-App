package com.example.smartboxv2

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder

class BarcodeActivity : AppCompatActivity() {

    private lateinit var bottomNav : BottomNavigationView
    private lateinit var dbref : DatabaseReference
    private lateinit var code : String
    private lateinit var count : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode)

        bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavBar)
        bottomNav.selectedItemId = R.id.nav_barcode
        bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_home ->{
                    var intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }

                R.id.nav_account ->{
                    var intent = Intent(this, AccountActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }

                R.id.nav_barcode ->{
                    true
                }

                R.id.nav_log ->{
                    var intent = Intent(this, LogActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }

                else -> {
                    false
                }
            }
        }

        getCode()
        val barcodeContainer = findViewById<CardView>(R.id.barcodeContainer)
        val generateButton = findViewById<Button>(R.id.generateBtn)
        generateButton.setOnClickListener {
            val userId = Firebase.auth.currentUser?.uid.toString()
            dbref = FirebaseDatabase.getInstance().getReference(userId).child("barcode")
            dbref.child("code").setValue(randomStringGenerator())
            dbref.child("state").setValue("0")
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(this, "Barcode BERHASIL Dibuat", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(this, "Barcode GAGAL Dibuat", Toast.LENGTH_SHORT).show()
                    }
                }
        }


    }

    private fun randomStringGenerator(): String? {
        var randomString : String?=""
        var str  = "1234567890"
        for (i in 1..12){
            randomString += str.random()
        }

        return randomString
    }

    private fun barcodeGenerator() {
        val writer : MultiFormatWriter = MultiFormatWriter()
        val matrix : BitMatrix = writer.encode(code, BarcodeFormat.CODE_128, 400,150)
        val encoder : BarcodeEncoder = BarcodeEncoder()
        val bitmap : Bitmap = encoder.createBitmap(matrix)

        val barcodeImage = findViewById<ImageView>(R.id.barcodeImage)
        val barcodeText = findViewById<TextView>(R.id.barcodeText)
        val tutupButton = findViewById<Button>(R.id.tutupBtn)

        barcodeImage.setImageBitmap(bitmap)
        barcodeText.setText(code)
    }

    private fun getCode(){
        val userId = Firebase.auth.currentUser?.uid.toString()
        val barcodeContainer = findViewById<CardView>(R.id.barcodeContainer)
        val generateButton = findViewById<Button>(R.id.generateBtn)
        dbref = FirebaseDatabase.getInstance().getReference(userId)
        dbref.child("barcode").child("code")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        code = snapshot.getValue().toString()
                        barcodeGenerator()
                        barcodeContainer.visibility = View.VISIBLE
                        generateButton.visibility = View.GONE
                    }
                    else{
                        barcodeContainer.visibility = View.GONE
                        generateButton.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })


        dbref.child("esp")
            .child("count")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    count = snapshot.getValue().toString()
                    if (count.equals("1")){
                        FirebaseDatabase.getInstance().getReference(userId)
                            .child("esp")
                            .child("count")
                            .setValue("0")

                        FirebaseDatabase.getInstance().getReference(userId)
                            .child("barcode")
                            .removeValue()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
}