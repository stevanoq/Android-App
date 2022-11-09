package com.example.galon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.galon.databinding.ActivityFormDataDiriBinding
import com.example.galon.eventbus.UpdateCartEvent
import com.example.galon.model.OrderModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_form_data_diri.*
import org.greenrobot.eventbus.EventBus

class FormDataDiriActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityFormDataDiriBinding

    private lateinit var etNama : EditText
    private lateinit var etAlamat : EditText
    private lateinit var etNoHp : EditText
    private lateinit var btnCOD : Button
    private lateinit var btnTransfer : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityFormDataDiriBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        etNama = findViewById(R.id.etNama)
        etAlamat = findViewById(R.id.etAlamat)
        etNoHp = findViewById(R.id.etNoHp)
        btnCOD = findViewById(R.id.btnCOD)
        btnTransfer = findViewById(R.id.btnTransfer)

        btnCOD.setOnClickListener (this)
        btnTransfer.setOnClickListener btnTransfer@{
            binding.formRekening.visibility = View.VISIBLE
        }

        btnOke.setOnClickListener btnOke@{
            saveDataTransfer()

        }
    }

    private fun saveDataTransfer() {
        val nama  = etNama.text.toString().trim()
        val alamat = etAlamat.text.toString().trim()
        val noHp = etNoHp.text.toString().trim()

        if (nama.isEmpty()){
            etNama.error = "Isi Nama Penerima"
            return
        }

        if (alamat.isEmpty()){
            etAlamat.error = "Isi Alamat Penerima"
            return
        }

        if (noHp.isEmpty()){
            etNoHp.error ="Isi No.Hp Penerima"
            return
        }

        val ref = FirebaseDatabase.getInstance().getReference("DataOrder")
        val doid = ref.push().key.toString()
        val data = DataOrder(doid, nama, alamat, noHp, metodePembayaran = "Transfer")

        if (doid != null) {
            ref.child(doid).setValue(data).addOnCompleteListener {
                Toast.makeText(applicationContext, "Pesanan Sudah Dibuat", Toast.LENGTH_SHORT).show()
            }
        }

        checkOut()
        finish()
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun saveData() {
        val nama  = etNama.text.toString().trim()
        val alamat = etAlamat.text.toString().trim()
        val noHp = etNoHp.text.toString().trim()

        if (nama.isEmpty()){
            etNama.error = "Isi Nama Penerima"
            return
        }

        if (alamat.isEmpty()){
            etAlamat.error = "Isi Alamat Penerima"
            return
        }

        if (noHp.isEmpty()){
            etNoHp.error ="Isi No.Hp Penerima"
            return
        }

        val ref = FirebaseDatabase.getInstance().getReference("DataOrder")
        val doid = ref.push().key.toString()
        val data = DataOrder(doid, nama, alamat, noHp, metodePembayaran = "COD")

        if (doid != null) {
            ref.child(doid).setValue(data).addOnCompleteListener {
                Toast.makeText(applicationContext, "Pesanan Sudah Dibuat", Toast.LENGTH_SHORT).show()
            }
        }

        checkOut()
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun checkOut() {
        val orderModels : MutableList<OrderModel> = ArrayList()
        val userID = FirebaseAuth.getInstance().currentUser?.uid.toString()

        FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child(userID)
            .removeValue()
            .addOnSuccessListener {
                EventBus.getDefault().postSticky(UpdateCartEvent()) }
    }

    override fun onClick(v: View?) {
        saveData()

    }


}