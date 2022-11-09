package com.example.galon.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.galon.DataOrder
import com.example.galon.R
import com.example.galon.model.OrderModel

class OrderListAdapter(val oCtx : Context, val layoutId : Int, val orderList: List<OrderModel>) : ArrayAdapter<OrderModel>(oCtx, layoutId, orderList){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater : LayoutInflater = LayoutInflater.from(oCtx)
        val view : View = layoutInflater.inflate(layoutId, null)
        val tvNama : TextView = view.findViewById(R.id.lvItem)
        val tvHarga : TextView = view.findViewById(R.id.lvharga)
        val tvJumlah : TextView = view.findViewById(R.id.lvJumlah)

        val order = orderList[position]

        tvNama.text = order.name
        tvJumlah.text = order.quantity.toString()
        tvHarga.text = order.price

        return view
    }
}