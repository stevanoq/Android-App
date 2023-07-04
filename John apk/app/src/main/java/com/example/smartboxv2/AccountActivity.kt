package com.example.smartboxv2

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AccountActivity : AppCompatActivity() {
    private lateinit var bottomNav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavBar)
        bottomNav.selectedItemId = R.id.nav_account
        bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_home ->{
                    var intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }

                R.id.nav_account ->{
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

        val logutButton = findViewById<Button>(R.id.logoutBtn)
        logutButton.setOnClickListener {
            val dialog = Dialog(this@AccountActivity)

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.logout_dialog)
            val btnYes = dialog.findViewById<Button>(R.id.logutYes)
            val btnNo = dialog.findViewById<Button>(R.id.logoutNo)

            btnYes.setOnClickListener {
                Firebase.auth.signOut()
                dialog.dismiss()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

            btnNo.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()

        }

        val deleteAccountButton = findViewById<Button>(R.id.deleteAccountBtn)
        deleteAccountButton.setOnClickListener {
            val dialog = Dialog(this@AccountActivity)

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.delete_account_dialog)
            val btnYes = dialog.findViewById<Button>(R.id.DAYes)
            val btnNo = dialog.findViewById<Button>(R.id.DANo)

            btnYes.setOnClickListener {
                val user = Firebase.auth.currentUser
                user?.delete()?.addOnCompleteListener{
                    if (it.isSuccessful){
                        Toast.makeText(this, "Akun Anda BERHASIL Dihapus", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        Toast.makeText(this, "Akun Anda GAGAL Dihapus", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
            }

            btnNo.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        val username  = findViewById<TextView>(R.id.username)
        FirebaseDatabase.getInstance().getReference(Firebase.auth.currentUser?.uid.toString())
            .child("username")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        username.text = snapshot.value.toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
}