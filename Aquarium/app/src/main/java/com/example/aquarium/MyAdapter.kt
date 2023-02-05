package com.example.aquarium

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(val itemView: List<Item>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.scedule_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = itemView[position]
        val status = currentItem.status

        holder.status.text = currentItem.status
        holder.hour.text = currentItem.hour.toString()
        holder.minute.text = currentItem.minute.toString()
        holder.time.text = currentItem.time

        if (status.equals("Sudah")){
            holder.status.setTextColor(Color.parseColor("#3DDB84"))
        }

        else{
            holder.status.setTextColor(Color.parseColor("#FF0000"))
        }

    }

    override fun getItemCount(): Int {
        return itemView.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val status : TextView = itemView.findViewById(R.id.status)
        val hour : TextView = itemView.findViewById(R.id.hour)
        val minute : TextView = itemView.findViewById(R.id.minute)
        val time : TextView = itemView.findViewById(R.id.tv_time)

    }

}