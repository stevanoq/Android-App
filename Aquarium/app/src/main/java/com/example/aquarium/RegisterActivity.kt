package com.example.aquarium

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_profile.*
import org.w3c.dom.Text

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var email : EditText
    private lateinit var password : EditText
    private lateinit var name : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = Firebase.auth
        val daftarBtn = findViewById<Button>(R.id.daftarBtn)
        val loginTxt = findViewById<TextView>(R.id.regmasuk)
        email = findViewById(R.id.regEmail)
        password = findViewById(R.id.regPassword)
        name = findViewById(R.id.regName)

        loginTxt.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        daftarBtn.setOnClickListener {
            signUpProcess()
        }

    }

    private fun signUpProcess() {
        val Email = email.text.toString()
        val Password = password.text.toString()
        val Name = name.text.toString()

        if (Email.isEmpty() || Password.isEmpty() || Name.isEmpty()){
            Toast.makeText(this, "Data Tidak Boleh Kosong", Toast.LENGTH_SHORT).show()
            return
        }

        if (Name.length < 4){
            Toast.makeText(this, "Nama Setidaknya 4 Karakter", Toast.LENGTH_SHORT).show()
            return
        }

        if (Password.length < 6){
            Toast.makeText(this, "Password Setidaknya 6 Karakter", Toast.LENGTH_SHORT).show()
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
            Toast.makeText(this, "Email Anda Tidak Valid", Toast.LENGTH_SHORT).show()
            return
        }

        RegisterUser(Email, Password, Name)

    }

    private fun RegisterUser(regEmail : String, regPass : String, regName : String){
        auth.createUserWithEmailAndPassword(regEmail,regPass)
            .addOnCompleteListener(this) {
                if (it.isSuccessful){
                    val userId = auth.currentUser?.uid.toString()
                    FirebaseDatabase.getInstance().getReference(userId)
                        .child("username").setValue(regName)

                    val intent = Intent(this, MainActivity::class.java)

                    setDefaultValue(userId)
                    startActivity(intent)
                    finish()
                }

                else{
                    Toast.makeText(this, "Gagal Buat Akun", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setDefaultValue(userId : String) {
        val ref = FirebaseDatabase.getInstance().getReference(userId)

        ref.child("value").child("tdsMin").setValue(150)
        ref.child("value").child("tdsMax").setValue(250)
        ref.child("value").child("tempMin").setValue(25)
        ref.child("value").child("tempMax").setValue(39)
        ref.child("value").child("tds").setValue(160)
        ref.child("value").child("temp").setValue(28)

        ref.child("schedule").child("1").child("hour").setValue(7)
        ref.child("schedule").child("1").child("minute").setValue(0)
        ref.child("schedule").child("1").child("status").setValue("Belum")
        ref.child("schedule").child("1").child("time").setValue("Pagi")

        ref.child("schedule").child("2").child("hour").setValue(12)
        ref.child("schedule").child("2").child("minute").setValue(0)
        ref.child("schedule").child("2").child("status").setValue("Belum")
        ref.child("schedule").child("2").child("time").setValue("Siang")

        ref.child("schedule").child("3").child("hour").setValue(17)
        ref.child("schedule").child("3").child("minute").setValue(0)
        ref.child("schedule").child("3").child("status").setValue("Belum")
        ref.child("schedule").child("3").child("time").setValue("Sore")

        ref.child("schedule").child("4").child("hour").setValue(22)
        ref.child("schedule").child("4").child("minute").setValue(0)
        ref.child("schedule").child("4").child("status").setValue("Belum")
        ref.child("schedule").child("4").child("time").setValue("Malam")

    }
}