package com.example.smartbox

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import java.text.ParseException
import java.util.regex.PatternSyntaxException

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        val login_btn = findViewById<Button>(R.id.loginBtn)
        login_btn.setOnClickListener {
            loginProcces()
        }

        val daftar_btn = findViewById<TextView>(R.id.daftarAkun)
        daftar_btn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        val lupaPasswordButton = findViewById<TextView>(R.id.lupaPassword)
        lupaPasswordButton.setOnClickListener {
            val intent = Intent(this, LupaPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun loginProcces() {
        var email = findViewById<EditText>(R.id.loginEmail)
        var etEmail = email.text.toString()

        var password = findViewById<EditText>(R.id.loginPass)
        var etPassword = password.text.toString()

        if (etEmail.isEmpty()){
            email.error = "Email tidak boleh kosong"
            email.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(etEmail).matches()){
            email.error = "Email tidak valid"
            email.requestFocus()
            return
        }

        if (etPassword.isEmpty()){
            password.error = "Password tidak boleh kosong"
            password.requestFocus()
            return
        }

        if (etPassword.length < 6){
            password.error = "Password tidak boleh kurang dari 6"
            password.requestFocus()
            return
        }

        loginUser(etEmail, etPassword)
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful){
                    var intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                else{
                    Toast.makeText(this, "Email atau password anda salah", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

