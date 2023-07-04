package com.example.smartboxv2

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.util.ArrayList

class LogActivity : AppCompatActivity() {
    private lateinit var bottomNav : BottomNavigationView
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<LogItem>
    private lateinit var dbref : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavBar)
        bottomNav.selectedItemId = R.id.nav_log
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
                    var intent = Intent(this, BarcodeActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }

                R.id.nav_log ->{
                    true
                }

                else -> {
                    false
                }
            }
        }

        userRecyclerView = findViewById(R.id.LogRecycle)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.setHasFixedSize(true)

        userArrayList = arrayListOf<LogItem>()
        getDataFromFirebase()

        val deleteLogButton = findViewById<Button>(R.id.deleteLogBtn)
        deleteLogButton.setOnClickListener{
            val dialog = Dialog(this@LogActivity)

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.delete_log_dialog)
            val btnYes = dialog.findViewById<Button>(R.id.DLYes)
            val btnNo = dialog.findViewById<Button>(R.id.DLNo)

            btnYes.setOnClickListener {
                val userID = Firebase.auth.currentUser?.uid.toString()
                FirebaseDatabase.getInstance().getReference(userID)
                    .child("dataLog")
                    .removeValue()
                    .addOnCompleteListener{
                    if (it.isSuccessful){
                        Toast.makeText(this, "Log Akun Anda BERHASIL Dihapus", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        Toast.makeText(this, "Log Akun Anda GAGAL Dihapus", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
            }

            btnNo.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        val refresh = findViewById<SwipeRefreshLayout>(R.id.refreshLog)
        refresh.setOnRefreshListener {
            this.recreate()
            refresh.isRefreshing = false
        }
    }

    private fun getDataFromFirebase() {
        val userId = Firebase.auth.currentUser?.uid.toString()
        dbref = FirebaseDatabase.getInstance().getReference(userId).child("dataLog")
        dbref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (userSnapshot in snapshot.children){
                        val log = userSnapshot.getValue(LogItem::class.java)
                        userArrayList.add(log!!)
                    }
                }
                userRecyclerView.adapter = LogAdapter(userArrayList, this@LogActivity)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LogActivity, error.message, Toast.LENGTH_SHORT)
            }

        })
    }
}