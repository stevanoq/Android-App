package com.example.aquarium

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.zxing.client.result.EmailAddressParsedResult
import com.google.zxing.client.result.EmailAddressResultParser
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var email : EditText
    private lateinit var password : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        email = findViewById(R.id.login_email)
        password = findViewById(R.id.login_pass)
        val masuk_btn = findViewById<Button>(R.id.login_btn)
        val daftar = findViewById<TextView>(R.id.reg_txt)
        val resetPasswordTxt = findViewById<TextView>(R.id.lupa_password_txt)

        masuk_btn.setOnClickListener {
            login_process()
        }

        daftar.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        resetPasswordTxt.setOnClickListener {
            resetPassword()
        }

    }

    private fun resetPassword() {
        val lp : WindowManager.LayoutParams = WindowManager.LayoutParams()
        val dialog = Dialog(this)

        lp.copyFrom(dialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.reset_password_layout)
        val Email = dialog.findViewById<EditText>(R.id.reset_email)
        val yes = dialog.findViewById<Button>(R.id.confirm_reset_button)
        val no = dialog.findViewById<Button>(R.id.batal_reset_button)

        yes.setOnClickListener {
            if (Email.text.toString().isEmpty()){
                Toast.makeText(this, "Email Tidak Boleh Kosong", Toast.LENGTH_LONG).show()
            }

            else if (!Patterns.EMAIL_ADDRESS.matcher(Email.text.toString()).matches()){
                Toast.makeText(this, "Email Anda Tidak Valid", Toast.LENGTH_LONG).show()
            }

            else{
                auth.sendPasswordResetEmail(Email.text.toString())
                Toast.makeText(this, "Email Reset Password Telah Dikirim", Toast.LENGTH_LONG).show()
                dialog.dismiss()
            }
        }

        no.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.attributes = lp
    }

    private fun login_process() {
        var Email = email.text.toString()
        var Password = password.text.toString()

        if (Email.isEmpty() || Password.isEmpty()){
            Toast.makeText(this, "Email dan Password Tidak Boleh Kosong", Toast.LENGTH_SHORT).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
            Toast.makeText(this, "Email Anda Tidak Valid", Toast.LENGTH_SHORT).show()
            return
        }

        loginUser(Email, Password)
    }

    private fun loginUser(logEmail : String, logPass : String) {
        auth.signInWithEmailAndPassword(logEmail, logPass)
            .addOnCompleteListener (this) {
                if (it.isSuccessful){
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                else{
                    Toast.makeText(this, "Email atau Password Anda Salah", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}