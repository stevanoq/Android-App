package com.example.smartbox

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.childEvents
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<Item>
    private lateinit var dbref : DatabaseReference
    private lateinit var code : String
    private lateinit var count : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userRecyclerView = findViewById(R.id.recycle)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.setHasFixedSize(true)

        userArrayList = arrayListOf<Item>()
        getDataFromFirebase()
        barcodeButtonState()
        getCode()
        Log.d("username", Firebase.auth.currentUser?.displayName.toString())

        val inputButton = findViewById<Button>(R.id.inputBtn)
        inputButton.setOnClickListener {
            sendToFirebase()
        }

        val logutButton = findViewById<Button>(R.id.logoutBtn)
        logutButton.setOnClickListener {
            val dialog = Dialog(this@MainActivity)

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

        val generateButton = findViewById<Button>(R.id.generateBtn)
        generateButton.setOnClickListener {
            val userId = Firebase.auth.currentUser?.uid.toString()
            dbref = FirebaseDatabase.getInstance().getReference(userId).child("barcode")
            dbref.child("code").setValue(randomStringGenerator())
            dbref.child("state").setValue("0")
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(this, "Barcode BERHASIL Dibuat", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(this, "Barcode GAGAL Dibuat", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        val lihatButton = findViewById<Button>(R.id.lihatBtn)
        lihatButton.setOnClickListener {
            Log.d("code", code)
            barcodeGenerator()
        }
    }

    private fun getCode(){
        val userId = Firebase.auth.currentUser?.uid.toString()
        dbref = FirebaseDatabase.getInstance().getReference(userId)
        dbref.child("barcode").child("code")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    code = snapshot.getValue().toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun barcodeGenerator() {
        val writer : MultiFormatWriter = MultiFormatWriter()
        val matrix : BitMatrix = writer.encode(code, BarcodeFormat.CODE_128, 400,150)
        val encoder : BarcodeEncoder = BarcodeEncoder()
        val bitmap : Bitmap = encoder.createBitmap(matrix)
        val dialog = Dialog(this@MainActivity)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.barcode_layout)

        val barcodeImage = dialog.findViewById<ImageView>(R.id.barcodeImage)
        val barcodeText = dialog.findViewById<TextView>(R.id.barcodeText)
        val tutupButton = dialog.findViewById<Button>(R.id.tutupBtn)

        barcodeImage.setImageBitmap(bitmap)
        barcodeText.setText(code)

        tutupButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun barcodeButtonState() {
        val userId = Firebase.auth.currentUser?.uid.toString()

        dbref = FirebaseDatabase.getInstance().getReference(userId)
        dbref.child("barcode")
            .addValueEventListener(object : ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {
                    val lihatButton = findViewById<Button>(R.id.lihatBtn)
                    val generateButton = findViewById<Button>(R.id.generateBtn)

                    if (snapshot.exists()){
                        generateButton.visibility = View.GONE
                        lihatButton.visibility = View.VISIBLE
                    }
                    else{
                        generateButton.visibility = View.VISIBLE
                        lihatButton.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

    }

    private fun randomStringGenerator(): String? {
        var randomString : String?=""
        var str  = "1234567890"
        for (i in 1..12){
            randomString += str.random()
        }

        return randomString
    }

    private fun sendToFirebase() {
        val userId = Firebase.auth.currentUser?.uid.toString()
        dbref = FirebaseDatabase.getInstance().getReference(userId).child("boxData")
        val input_resi = findViewById<EditText>(R.id.input_resi).text.toString()
        if (input_resi.isEmpty()){
            Toast.makeText(this, "Resi Tidak Boleh Kosong", Toast.LENGTH_SHORT).show()
        }
        else{
            dbref.child(input_resi).child("resi").setValue(input_resi)
            dbref.child(input_resi).child("status").setValue("Proses")
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(this, "Resi BERHASIL di Input", Toast.LENGTH_SHORT).show()
                        findViewById<EditText>(R.id.input_resi).text.clear()
                        this.recreate()
                    }
                    else{
                        Toast.makeText(this, "Resi GAGAL di Input", Toast.LENGTH_SHORT).show()
                    }
                }

//            if (dbref.child(input_resi).child("status").setValue("Proses").isSuccessful){
//
//            }

        }
    }

    private fun getDataFromFirebase() {
        val userId = Firebase.auth.currentUser?.uid.toString()
        dbref = FirebaseDatabase.getInstance().getReference(userId).child("boxData")

            dbref.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        count = snapshot.childrenCount.toString()
                        FirebaseDatabase.getInstance().getReference(userId)
                            .child("esp")
                            .child("count")
                            .setValue(count)
                        for (userSnapshot in snapshot.children){
                            val item = userSnapshot.getValue(Item::class.java)
                            userArrayList.add(item!!)
                        }
                        userRecyclerView.adapter = MyAdapter(userArrayList,this@MainActivity)
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_SHORT)
                }
            })
    }

}