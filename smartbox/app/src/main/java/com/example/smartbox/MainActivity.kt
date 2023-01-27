package com.example.smartbox

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.childEvents
import com.google.firebase.database.ktx.getValue
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<Item>
    private lateinit var dbref : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userRecyclerView = findViewById(R.id.recycle)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.setHasFixedSize(true)

        userArrayList = arrayListOf<Item>()
        getDataFromFirebase()

        val inputButton = findViewById<Button>(R.id.inputBtn)
        inputButton.setOnClickListener {
            sendToFirebase()
        }

        val logutButton = findViewById<Button>(R.id.logoutBtn)
        logutButton.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }



    private fun sendToFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        dbref = FirebaseDatabase.getInstance().getReference(userId).child("boxData")
        val input_resi = findViewById<EditText>(R.id.input_resi).text.toString()
        if (input_resi.isEmpty()){
            Toast.makeText(this, "Resi tidak boleh kosong", Toast.LENGTH_SHORT).show()
        }
        else{
            dbref.child(input_resi).child("resi").setValue(input_resi)
            dbref.child(input_resi).child("status").setValue("Proses")
            findViewById<EditText>(R.id.input_resi).text.clear()
            this.recreate()
        }

    }

    private fun getDataFromFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
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

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }

}