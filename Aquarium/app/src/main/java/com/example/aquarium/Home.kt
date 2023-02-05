package com.example.aquarium

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.view.*
import org.w3c.dom.Text

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home : Fragment() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<Item>
    private lateinit var dbref: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userRecyclerView = view.findViewById(R.id.recycle)
        userRecyclerView.layoutManager = LinearLayoutManager(activity)
        userRecyclerView.setHasFixedSize(true)
        userArrayList = arrayListOf<Item>()
        auth = Firebase.auth
        getSceduleFromFirebase()
        getValueFromFirebase()

    }

    private fun getValueFromFirebase() {
        val userId = auth.currentUser?.uid.toString()
        dbref = FirebaseDatabase.getInstance().getReference(userId)
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var tds = snapshot.child("value").child("tds").getValue().toString()
                var temp = snapshot.child("value").child("temp").getValue().toString()

                var tdsMin = snapshot.child("value").child("tdsMin").getValue().toString()
                var tdsMax = snapshot.child("value").child("tdsMax").getValue().toString()
                var tempMin = snapshot.child("value").child("tempMin").getValue().toString()
                var tempMax = snapshot.child("value").child("tempMax").getValue().toString()

                var in_tds : Int = Integer.parseInt(tds)
                var in_tdsMin : Int = Integer.parseInt(tdsMin)
                var in_tdsMax : Int = Integer.parseInt(tdsMax)

                var in_temp : Int = Integer.parseInt(temp)
                var in_tempMin : Int = Integer.parseInt(tempMin)
                var in_tempMax : Int = Integer.parseInt(tempMax)

                val lv_tds = view?.findViewById<TextView>(R.id.tdsValue)
                val lv_temp = view?.findViewById<TextView>(R.id.tempValue)
                val lv_tds_status = view?.findViewById<TextView>(R.id.tds_status)
                val lv_temp_status = view?.findViewById<TextView>(R.id.temp_status)
                val lv_temp_name = view?.findViewById<TextView>(R.id.temp_name)
                val lv_tds_name = view?.findViewById<TextView>(R.id.tds_name)
                val ppm = view?.findViewById<TextView>(R.id.ppm)
                val celcius = view?.findViewById<TextView>(R.id.celcius)

                if (lv_tds != null) {
                    lv_tds.text = tds
                    if (in_tds <= in_tdsMin){
                        lv_tds.setTextColor(Color.parseColor("#3292BC"))
                        lv_tds_status?.setTextColor(Color.parseColor("#3292BC"))
                        lv_tds_name?.setTextColor(Color.parseColor("#3292BC"))
                        ppm?.setTextColor(Color.parseColor("#3292BC"))
                        lv_tds_status?.text = "Rendah"
                    }

                    else if (in_tds > in_tdsMin && in_tds < in_tdsMax){
                        lv_tds.setTextColor(Color.parseColor("#3DDB84"))
                        lv_tds_status?.setTextColor(Color.parseColor("#3DDB84"))
                        lv_tds_name?.setTextColor(Color.parseColor("#3DDB84"))
                        ppm?.setTextColor(Color.parseColor("#3DDB84"))
                        lv_tds_status?.text = "Bagus"
                    }

                    else {
                        lv_tds.setTextColor(Color.parseColor("#FF0000"))
                        lv_tds_status?.setTextColor(Color.parseColor("#FF0000"))
                        lv_tds_name?.setTextColor(Color.parseColor("#FF0000"))
                        ppm?.setTextColor(Color.parseColor("#FF0000"))
                        lv_tds_status?.text = "Tinggi"
                    }
                }
                if (lv_temp != null) {
                    lv_temp.text = temp
                    if (in_temp <= in_tempMin){
                        lv_temp.setTextColor(Color.parseColor("#3292BC"))
                        lv_temp_status?.setTextColor(Color.parseColor("#3292BC"))
                        lv_temp_name?.setTextColor(Color.parseColor("#3292BC"))
                        celcius?.setTextColor(Color.parseColor("#3292BC"))
                        lv_temp_status?.text = "Rendah"
                    }

                    else if (in_temp  > in_tempMin && in_temp < in_tempMax){
                        lv_temp.setTextColor(Color.parseColor("#3DDB84"))
                        lv_temp_status?.setTextColor(Color.parseColor("#3DDB84"))
                        lv_temp_name?.setTextColor(Color.parseColor("#3DDB84"))
                        celcius?.setTextColor(Color.parseColor("#3DDB84"))
                        lv_temp_status?.text = "Bagus"
                    }

                    else {
                        lv_temp.setTextColor(Color.parseColor("#FF0000"))
                        lv_temp_status?.setTextColor(Color.parseColor("#FF0000"))
                        lv_temp_name?.setTextColor(Color.parseColor("#FF0000"))
                        celcius?.setTextColor(Color.parseColor("#FF0000"))
                        lv_temp_status?.text = "Tinggi"
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        dbref.child("username").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val username = snapshot.getValue().toString()
                val tv_username = view?.findViewById<TextView>(R.id.home_username)

                tv_username?.text = username
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun getSceduleFromFirebase() {
        val userId = Firebase.auth.currentUser?.uid.toString()
        dbref = FirebaseDatabase.getInstance().getReference(userId).child("schedule")
        dbref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (userSnapshot in snapshot.children){
                        val item = userSnapshot.getValue(Item::class.java)
                        userArrayList.add(item!!)
                    }
                    userRecyclerView.adapter = MyAdapter(userArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

}