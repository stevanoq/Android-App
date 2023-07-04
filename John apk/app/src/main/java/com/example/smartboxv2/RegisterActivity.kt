package com.example.smartboxv2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.smartboxv2.LoginActivity
import com.example.smartboxv2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        var btnlogin = findViewById<TextView>(R.id.regLogin)
        btnlogin.setOnClickListener {
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        var btnDaftar = findViewById<Button>(R.id.daftar_btn)
        btnDaftar.setOnClickListener {
            signupProccess()
        }

    }

    private fun signupProccess() {
        var etUsername = findViewById<EditText>(R.id.username)
        var etEmail = findViewById<EditText>(R.id.regEmail)
        var etPass = findViewById<EditText>(R.id.regPass)
        var etConfPass = findViewById<EditText>(R.id.regConfimPass)

        var username = etUsername.text.toString()
        var email = etEmail.text.toString()
        var password = etPass.text.toString()
        var confPassword = etConfPass.text.toString()

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confPassword.isEmpty()){
            Toast.makeText(this, "Data Tidak Boleh Kosong", Toast.LENGTH_SHORT).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Email Tidak Valid", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.isEmpty() || password.length < 6){
            Toast.makeText(this, "Password Minimal 6 Karakter", Toast.LENGTH_SHORT).show()
            return
        }

        if (username.isEmpty() || username.length < 4)
        {
            Toast.makeText(this, "Username Minimal 4 Karakter", Toast.LENGTH_SHORT).show()
            return
        }

        if (!password.equals(confPassword)){
            Toast.makeText(this,"Password tidak sama", Toast.LENGTH_SHORT).show()
            return
        }
        registerUser(email, password, username)
    }

    private fun registerUser(email: String, password: String, username: String) {
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    FirebaseDatabase.getInstance().getReference(Firebase.auth.currentUser?.uid.toString())
                        .child("username")
                        .setValue(username)
                    var intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else{
                    Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }
}