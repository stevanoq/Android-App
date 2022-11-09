package com.example.galon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        //button register atau signup
            val btnSignup = findViewById<Button>(R.id.btnSignup)
            btnSignup.setOnClickListener{
            val email = findViewById<EditText>(R.id.etEmail).text.toString().trim()
            val etEmail = findViewById<EditText>(R.id.etEmail)
            val password = findViewById<EditText>(R.id.etPassword).text.toString().trim()
            val etPassword = findViewById<EditText>(R.id.etPassword)
            val username = findViewById<EditText>(R.id.username).text.toString().trim()
            val etusername = findViewById<EditText>(R.id.username)

            if (email.isEmpty()){
                etEmail.error = "Email Harus Diisi"
                etEmail.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                etEmail.error = "Email Tidak Valid"
                etEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty() || password.length < 6){
                etPassword.error = "Password minimal 6 caracter"
                etPassword.requestFocus()
                return@setOnClickListener
            }

            if (username.isEmpty() || username.length < 4)
            {
                etusername.error = "Username minimal 4 caracter"
                etusername.requestFocus()
                return@setOnClickListener
            }

            registerUser(email, password, username)

        }

        //button Login pindah activity
        val btnLogin = findViewById<Button>(R.id.btnSignin)
        btnLogin.setOnClickListener{
            Intent(this@RegisterActivity, LoginActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun registerUser(email: String, password: String, username: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Intent(this@RegisterActivity, MainActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                    }
                }else{
                    Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }

    }
    //ketika sudah login maka langsung ke home
    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            Intent(this@RegisterActivity, MainActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }
    }
    }


