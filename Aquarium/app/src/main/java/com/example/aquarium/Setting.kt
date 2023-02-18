package com.example.aquarium

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class Setting : Fragment() {

    private lateinit var auth : FirebaseAuth
    private lateinit var dbref : DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val valueSetting_layout = view.findViewById<LinearLayout>(R.id.value_setting_layout)
        val sceduleSetting_layout = view.findViewById<LinearLayout>(R.id.scedule_setting_layout)
        val main_layout = view.findViewById<LinearLayout>(R.id.setting_button_layout)

        val valueSetting_btn = view.findViewById<LinearLayout>(R.id.value_setting)
        val sceduleSetting_btn = view.findViewById<LinearLayout>(R.id.scedule_setting)
        val valueBack_btn = view.findViewById<Button>(R.id.value_cancle_btn)
        val sceduleBack_btn = view.findViewById<Button>(R.id.scedule_no)

        val tdsMax_btn = view.findViewById<Button>(R.id.tds_max_btn)
        val tdsMin_btn = view.findViewById<Button>(R.id.tds_min_btn)
        val tempMax_btn = view.findViewById<Button>(R.id.temp_max_btn)
        val tempMin_btn = view.findViewById<Button>(R.id.suhu_min_btn)

        val et_tdsMax = view.findViewById<EditText>(R.id.tds_max)
        val et_tdsMin = view.findViewById<EditText>(R.id.tds_min)
        val et_tempMax = view.findViewById<EditText>(R.id.temp_max)
        val et_tempMin = view.findViewById<EditText>(R.id.suhu_min)

        val pagi_btn = view.findViewById<Button>(R.id.pagi_btn)
        val siang_btn = view.findViewById<Button>(R.id.siang_btn)
        val sore_btn = view.findViewById<Button>(R.id.sore_btn)
        val malam_btn = view.findViewById<Button>(R.id.malam_btn)

        val pagi_unset_btn = view.findViewById<Button>(R.id.pagi_unset_btn)
        val siang_unset_btn = view.findViewById<Button>(R.id.siang_unset_btn)
        val sore_unset_btn = view.findViewById<Button>(R.id.sore_unset_btn)
        val malam_unset_btn = view.findViewById<Button>(R.id.malam_unset_btn)

        val et_pagi_hour = view.findViewById<EditText>(R.id.hour_pagi)
        val et_siang_hour = view.findViewById<EditText>(R.id.hour_siang)
        val et_sore_hour = view.findViewById<EditText>(R.id.hour_sore)
        val et_malam_hour = view.findViewById<EditText>(R.id.hour_malam)

        val et_pagi_minute = view.findViewById<EditText>(R.id.minute_pagi)
        val et_siang_minute = view.findViewById<EditText>(R.id.minute_siang)
        val et_sore_minute = view.findViewById<EditText>(R.id.minute_sore)
        val et_malam_minute = view.findViewById<EditText>(R.id.minute_malam)

        auth = Firebase.auth
        val userId = auth.currentUser?.uid.toString()
        dbref = FirebaseDatabase.getInstance().getReference(userId)

        valueSetting_btn.setOnClickListener {
            valueSetting_layout.visibility = View.VISIBLE
            sceduleSetting_layout.visibility = View.GONE
            main_layout.visibility = View.GONE
        }

        sceduleSetting_btn.setOnClickListener {
            valueSetting_layout.visibility = View.GONE
            sceduleSetting_layout.visibility = View.VISIBLE
            main_layout.visibility = View.GONE
        }

        valueBack_btn.setOnClickListener {
            valueSetting_layout.visibility = View.GONE
            sceduleSetting_layout.visibility = View.GONE
            main_layout.visibility = View.VISIBLE
        }

        sceduleBack_btn.setOnClickListener {
            valueSetting_layout.visibility = View.GONE
            sceduleSetting_layout.visibility = View.GONE
            main_layout.visibility = View.VISIBLE
        }

        tdsMax_btn.setOnClickListener {
            var tdsMax = et_tdsMax.text.toString()

            if (tdsMax.isEmpty()){
                Toast.makeText(activity, "Nilai TDS Maksimal Harus Diisi", Toast.LENGTH_SHORT).show()
            }
            else {
                dbref.child("value").child("tdsMin").addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var realValue = snapshot.getValue().toString()

                        var in_currentValue : Int = Integer.parseInt(tdsMax)
                        var in_realValue : Int = Integer.parseInt(realValue)

                        if (in_currentValue <= in_realValue){
                            Toast.makeText(activity, "Nilai TDS Maksimal Harus Lebih Besar Dari Minimal", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            dbref.child("value").child("tdsMax").setValue(tdsMax.toInt())
                                .addOnCompleteListener {
                                    if (it.isSuccessful){
                                        Toast.makeText(activity, "Nilai TDS Maksimal Berhasil Diubah", Toast.LENGTH_SHORT).show()
                                        et_tdsMax.text.clear()
                                    }
                                }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

            }
        }

        tdsMin_btn.setOnClickListener {
            var tdsMin = et_tdsMin.text.toString()
            if (tdsMin.isEmpty()){
                Toast.makeText(activity, "Nilai TDS Minimum Harus Diisi", Toast.LENGTH_SHORT).show()
            }

            else{
                dbref.child("value").child("tdsMax").addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var currentValue = et_tdsMin.text.toString()
                        var realValue = snapshot.getValue().toString()

                        var in_currentValue : Int = Integer.parseInt(currentValue)
                        var in_realValue : Int = Integer.parseInt(realValue)

                        if (in_currentValue >= in_realValue){
                            Toast.makeText(activity, "Nilai TDS Minimal Harus Lebih Kecil Dari Maksimal", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            dbref.child("value").child("tdsMin").setValue(tdsMin.toInt())
                                .addOnCompleteListener {
                                    if (it.isSuccessful){
                                        Toast.makeText(activity, "Nilai TDS Minimal Berhasil Diubah", Toast.LENGTH_SHORT).show()
                                        et_tdsMin.text.clear()
                                    }
                                }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }
        }

        tempMax_btn.setOnClickListener {
            val tempMax = et_tempMax.text.toString()
            if (tempMax.isEmpty()){
                Toast.makeText(activity, "Nilai Suhu Maksimal Harus Diisi", Toast.LENGTH_SHORT).show()
            }

            else{
                dbref.child("value").child("tempMin").addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var currentValue = et_tempMax.text.toString()
                        var realValue = snapshot.getValue().toString()

                        var in_currentValue : Float = currentValue.toFloat()
                        var in_realValue : Float = realValue.toFloat()

                        if (in_currentValue <= in_realValue){
                            Toast.makeText(activity, "Nilai Suhu Maksimal Harus Lebih Besar Dari Minimal", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            dbref.child("value").child("tempMax").setValue(tempMax.toFloat())
                                .addOnCompleteListener {
                                    if (it.isSuccessful){
                                        Toast.makeText(activity, "Nilai Suhu Maksimal Berhasil Diubah", Toast.LENGTH_SHORT).show()
                                        et_tempMax.text.clear()
                                    }
                                }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }
        }

        tempMin_btn.setOnClickListener {
            val tempMin = et_tempMin.text.toString()
            if (tempMin.isEmpty()){
                Toast.makeText(activity, "Nilai Suhu Minimum Harus Diisi", Toast.LENGTH_SHORT).show()
            }

            else{
                dbref.child("value").child("tempMax").addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var realValue = snapshot.getValue().toString()
                        var currentValue = et_tempMin.text.toString()

                        var in_currentValue : Float = currentValue.toFloat()
                        var in_realValue : Float = realValue.toFloat()

                        if (in_currentValue >= in_realValue){
                            Toast.makeText(activity, "Nilai Suhu Minimal Harus Lebih Kecil Dari Maksimal", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            dbref.child("value").child("tempMin").setValue(tempMin.toFloat())
                                .addOnCompleteListener {
                                    if (it.isSuccessful){
                                        Toast.makeText(activity, "Nilai Suhu Minimal Berhasil Diubah", Toast.LENGTH_SHORT).show()
                                        et_tempMin.text.clear()
                                    }
                                }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }
        }

        pagi_btn.setOnClickListener {
            val hour = et_pagi_hour.text.toString()
            val minute = et_pagi_minute.text.toString()
            val ref = dbref.child("schedule").child("1")

            if (hour.isEmpty() || minute.isEmpty()){
                Toast.makeText(activity, "Jam dan Menit Tidak Boleh Kosong", Toast.LENGTH_SHORT).show()
            }

            else if (hour.toInt() > 23){
                Toast.makeText(activity, "Jam Tidak Boleh Lebih Dari 23", Toast.LENGTH_SHORT).show()
            }

            else if (minute.toInt() > 59){
                Toast.makeText(activity, "Menit Tidak Boleh Lebih Dari 59", Toast.LENGTH_SHORT).show()
            }

            else{
                ref.child("hour").setValue(hour.toInt())
                ref.child("minute").setValue(minute.toInt())
                ref.child("time").setValue("Pagi")
                ref.child("status").setValue("Belum")
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(activity, "Jadwal Pakan Pagi Berhasi Diatur", Toast.LENGTH_SHORT).show()
                            et_pagi_hour.text.clear()
                            et_pagi_minute.text.clear()
                        }
                    }
            }
        }

        pagi_unset_btn.setOnClickListener {
            dbref.child("schedule").child("1").removeValue().addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(activity, "Jadwal Pakan Pagi Berhasi Dihapus",Toast.LENGTH_SHORT).show()
                }
            }
        }

        siang_btn.setOnClickListener {
            val hour = et_siang_hour.text.toString()
            val minute = et_siang_minute.text.toString()
            val ref = dbref.child("schedule").child("2")

            if (hour.isEmpty() || minute.isEmpty()){
                Toast.makeText(activity, "Jam dan Menit Tidak Boleh Kosong", Toast.LENGTH_SHORT).show()
            }

            else if (hour.toInt() > 23){
                Toast.makeText(activity, "Jam Tidak Boleh Lebih Dari 23", Toast.LENGTH_SHORT).show()
            }

            else if (minute.toInt() > 59){
                Toast.makeText(activity, "Menit Tidak Boleh Lebih Dari 59", Toast.LENGTH_SHORT).show()
            }

            else{
                ref.child("hour").setValue(hour.toInt())
                ref.child("minute").setValue(minute.toInt())
                ref.child("time").setValue("Siang")
                ref.child("status").setValue("Belum")
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(activity, "Jadwal Pakan Siang Berhasil Diatur", Toast.LENGTH_SHORT).show()
                            et_siang_hour.text.clear()
                            et_siang_minute.text.clear()
                        }
                    }
            }
        }

        siang_unset_btn.setOnClickListener {
            dbref.child("schedule").child("2").removeValue().addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(activity, "Jadwal Pakan Siang Berhasi Dihapus",Toast.LENGTH_SHORT).show()
                }
            }
        }

        sore_btn.setOnClickListener {
            val hour = et_sore_hour.text.toString()
            val minute = et_sore_minute.text.toString()
            val ref = dbref.child("schedule").child("3")

            if (hour.isEmpty() || minute.isEmpty()){
                Toast.makeText(activity, "Jam dan Menit Tidak Boleh Kosong", Toast.LENGTH_SHORT).show()
            }

            else if (hour.toInt() > 23){
                Toast.makeText(activity, "Jam Tidak Boleh Lebih Dari 23", Toast.LENGTH_SHORT).show()
            }

            else if (minute.toInt() > 59){
                Toast.makeText(activity, "Menit Tidak Boleh Lebih Dari 59", Toast.LENGTH_SHORT).show()
            }

            else{
                ref.child("hour").setValue(hour.toInt())
                ref.child("minute").setValue(minute.toInt())
                ref.child("time").setValue("Sore")
                ref.child("status").setValue("Belum")
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(activity, "Jadwal Pakan Sore Berhasil Diatur", Toast.LENGTH_SHORT).show()
                            et_sore_hour.text.clear()
                            et_sore_minute.text.clear()
                        }
                    }
            }
        }

        sore_unset_btn.setOnClickListener {
            dbref.child("schedule").child("3").removeValue().addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(activity, "Jadwal Pakan Sore Berhasi Dihapus",Toast.LENGTH_SHORT).show()
                }
            }
        }

        malam_btn.setOnClickListener {
            val hour = et_malam_hour.text.toString()
            val minute = et_malam_minute.text.toString()
            val ref = dbref.child("schedule").child("4")

            if (hour.isEmpty() || minute.isEmpty()){
                Toast.makeText(activity, "Jam dan Menit Tidak Boleh Kosong", Toast.LENGTH_SHORT).show()
            }

            else if (hour.toInt() > 23){
                Toast.makeText(activity, "Jam Tidak Boleh Lebih Dari 23", Toast.LENGTH_SHORT).show()
            }

            else if (minute.toInt() > 59){
                Toast.makeText(activity, "Menit Tidak Boleh Lebih Dari 59", Toast.LENGTH_SHORT).show()
            }

            else{
                ref.child("hour").setValue(hour.toInt())
                ref.child("minute").setValue(minute.toInt())
                ref.child("time").setValue("Malam")
                ref.child("status").setValue("Belum")
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(activity, "Jadwal Pakan Malam Berhasil Diatur", Toast.LENGTH_SHORT).show()
                            et_malam_hour.text.clear()
                            et_malam_minute.text.clear()
                        }
                    }
            }
        }

        malam_unset_btn.setOnClickListener {
            dbref.child("schedule").child("4").removeValue().addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(activity, "Jadwal Pakan Malam Berhasi Dihapus",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}