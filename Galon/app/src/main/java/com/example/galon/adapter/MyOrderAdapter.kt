package com.example.galon.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.galon.R
import com.example.galon.eventbus.UpdateCartEvent
import com.example.galon.eventbus.UpdateOrderEvent
import com.example.galon.listener.IRecyclerClickListener
import com.example.galon.model.CartModel
import com.example.galon.model.OrderModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus
import java.lang.StringBuilder

class MyOrderAdapter (
    private val context: Context,
    private val orderModelList: List<OrderModel>
        ): RecyclerView.Adapter<MyOrderAdapter.MyOrderHolder>() {
    class MyOrderHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        var txtName:TextView?=null
        var txtPrice:TextView?=null
        var txtQuantity:TextView?=null

        private var clickListener: IRecyclerClickListener?=null

        init {
            txtName = itemView.findViewById(R.id.txtName) as TextView
            txtPrice = itemView.findViewById(R.id.txtPrice) as TextView
            txtQuantity = itemView.findViewById(R.id.txtQuantity) as TextView
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrderHolder {
        return MyOrderHolder(LayoutInflater.from(context)
            .inflate(R.layout.layout_order_list,parent,false))
    }

    override fun onBindViewHolder(holder: MyOrderHolder, position: Int) {
        holder.txtName!!.text = StringBuilder().append(orderModelList[position].name)
        holder.txtPrice!!.text = StringBuilder().append(orderModelList[position].price)
        holder.txtQuantity!!.text = StringBuilder().append(orderModelList[position].quantity)
    }



    override fun getItemCount(): Int {
        return orderModelList.size
    }

}