package com.example.smartboxv2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference

class LogAdapter(val logList: List<LogItem>, val logActivity: LogActivity) : RecyclerView.Adapter<LogAdapter.MyViewHolder>(){
    private lateinit var dbref : DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogAdapter.MyViewHolder {
        val logView = LayoutInflater.from(parent.context).inflate(R.layout.list_log, parent, false)
        return LogAdapter.MyViewHolder(logView)
    }

    override fun getItemCount(): Int {
        return logList.size
    }

    override fun onBindViewHolder(holder: LogAdapter.MyViewHolder, position: Int) {
        val currentLog = logList[position]

        holder.status.text = currentLog.status
        holder.action.text = currentLog.action
        holder.barcode.text = currentLog.resi
        holder.date.text = currentLog.date
        holder.time.text = currentLog.time

    }

    class MyViewHolder(logView: View) : RecyclerView.ViewHolder(logView){
        val status : TextView = logView.findViewById(R.id.Logstatus)
        val action : TextView = logView.findViewById(R.id.action)
        val barcode : TextView = logView.findViewById(R.id.barcode)
        val time : TextView = logView.findViewById(R.id.time)
        val date : TextView = logView.findViewById(R.id.date)
    }

}
