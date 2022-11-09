package com.example.galon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.galon.databinding.ActivityForgetPasswordBinding
import com.example.galon.databinding.ActivityFormDataDiriBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern

class ForgetPasswordActivity : AppCompatActivity() {
    lateinit var binding : ActivityForgetPasswordBinding
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnRessetPass.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val edtEmail = binding.edtEmail

            if (email.isEmpty()){
                edtEmail.error = "Isi email anda"
                edtEmail.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                edtEmail.error = "Email anda tidak valid"
                edtEmail.requestFocus()
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(this, "Email reset password sudah dikirim", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}