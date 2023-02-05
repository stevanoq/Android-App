package com.example.smartbox

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MyAdapter(val itemList: List<Item>, val mainActivity: MainActivity) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    private lateinit var dbref: DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = itemList[position]

            holder.resi.text = currentitem.resi
            holder.status.text = currentitem.status

        if (holder.status.text.toString().equals("Diterima")){
            holder.status.setTextColor(Color.parseColor("#3DDC84"))
        }

        holder.delete!!.setOnClickListener {

            val dialog = Dialog(mainActivity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.alert_dialog)
            val yes = dialog.findViewById<TextView>(R.id.yesbtn)
            val no = dialog.findViewById<TextView>(R.id.nobtn)

            yes.setOnClickListener {
                val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
                dbref = FirebaseDatabase.getInstance().getReference(userId).child("boxData")
                dbref.child(currentitem.resi.toString()).removeValue()
                dialog.dismiss()
                mainActivity.recreate()
                Toast.makeText(mainActivity, "Resi BERHASIL di Hapus", Toast.LENGTH_SHORT).show()
            }

            no.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()

        }
    }


    override fun getItemCount(): Int {
        return itemList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val resi : TextView = itemView.findViewById(R.id.resi)
        val status : TextView = itemView.findViewById(R.id.status)
        val delete : TextView = itemView.findViewById(R.id.delete)


    }
}