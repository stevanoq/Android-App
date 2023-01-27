package com.example.smartbox

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

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

        if (username.isEmpty()){
            etUsername.error = "Username tidak boleh kosong"
            etUsername.requestFocus()
            return
        }

        if (email.isEmpty()){
            etEmail.error = "Email tidak boleh kosong"
            etEmail.requestFocus()
            return
        }

        if (password.isEmpty()){
            etPass.error = "Password tidak boleh kosong"
            etPass.requestFocus()
            return
        }

        if (confPassword.isEmpty()){
            etConfPass.error = "Confirm password tidak boleh kosong"
            etConfPass.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.error = "Email Tidak Valid"
            etEmail.requestFocus()
            return
        }

        if (password.isEmpty() || password.length < 6){
            etPass.error = "Password minimal 6 caracter"
            etPass.requestFocus()
            return
        }

        if (username.isEmpty() || username.length < 4)
        {
            etUsername.error = "Username minimal 4 caracter"
            etUsername.requestFocus()
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