package com.example.smartboxv2

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<Item>
    private lateinit var dbref : DatabaseReference
    private lateinit var code : String
    private lateinit var count : String
    private lateinit var bottomNav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavBar)
        bottomNav.selectedItemId = R.id.nav_home
        bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_home ->{
                    true
                }

                R.id.nav_account ->{
                    var intent = Intent(this, AccountActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }

                R.id.nav_barcode ->{
                    var intent = Intent(this, BarcodeActivity::class.java)
                    startActivity(intent)
                    finish()
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

        userRecyclerView = findViewById(R.id.recycle)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.setHasFixedSize(true)

        userArrayList = arrayListOf<Item>()
        getDataFromFirebase()
        Log.d("username", Firebase.auth.currentUser?.displayName.toString())

        val inputButton = findViewById<Button>(R.id.inputBtn)
        inputButton.setOnClickListener {
            sendToFirebase()
        }

        val refresh = findViewById<SwipeRefreshLayout>(R.id.refreshHome)
        refresh.setOnRefreshListener {
            this.recreate()
            refresh.isRefreshing = false
        }

    }


    private fun sendToFirebase() {
        val userId = Firebase.auth.currentUser?.uid.toString()
        dbref = FirebaseDatabase.getInstance().getReference(userId).child("boxData")
        val input_resi = findViewById<EditText>(R.id.input_resi).text.toString()
        if (input_resi.isEmpty()){
            Toast.makeText(this, "Resi Tidak Boleh Kosong", Toast.LENGTH_SHORT).show()
        }
        else{
            dbref.child(input_resi).child("resi").setValue(input_resi)
            dbref.child(input_resi).child("status").setValue("Proses")
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(this, "Resi BERHASIL di Input", Toast.LENGTH_SHORT).show()
                        findViewById<EditText>(R.id.input_resi).text.clear()
                        this.recreate()
                    }
                    else{
                        Toast.makeText(this, "Resi GAGAL di Input", Toast.LENGTH_SHORT).show()
                    }
                }

//            if (dbref.child(input_resi).child("status").setValue("Proses").isSuccessful){
//
//            }

        }
    }

    private fun getDataFromFirebase() {
        val userId = Firebase.auth.currentUser?.uid.toString()
        dbref = FirebaseDatabase.getInstance().getReference(userId).child("boxData")

        dbref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (userSnapshot in snapshot.children){
                        val item = userSnapshot.getValue(Item::class.java)
                        userArrayList.add(item!!)
                    }
                    userRecyclerView.adapter = MyAdapter(userArrayList,this@MainActivity)
                }
//                this@MainActivity.recreate()

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_SHORT)
            }
        })
    }

}