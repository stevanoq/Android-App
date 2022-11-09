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
import com.example.galon.listener.ICartLoadListener
import com.example.galon.listener.IOrderLoadListener
import com.example.galon.listener.IRecyclerClickListener
import com.example.galon.model.CartModel
import com.example.galon.model.DrinkModel
import com.example.galon.model.OrderModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus
import java.lang.StringBuilder

class MyDrinkAdapter (

    private val context : Context,
    private val list : List<DrinkModel>,
    private val cartListener: ICartLoadListener

    ): RecyclerView.Adapter<MyDrinkAdapter.MyDrinkHolder>(){
    class MyDrinkHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var imageView: ImageView?=null
        var txtName: TextView?=null
        var txtPrice: TextView?=null

        private var clickListener: IRecyclerClickListener?=null

        fun setOnClickListener(clickListener: IRecyclerClickListener){
            this.clickListener = clickListener;
        }

        init {
            imageView = itemView.findViewById(R.id.imageView) as ImageView
            txtName = itemView.findViewById(R.id.txtName) as TextView
            txtPrice = itemView.findViewById(R.id.txtPrice) as TextView

            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
           clickListener!!.onItemClickListener(v,adapterPosition)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyDrinkHolder {
        return MyDrinkHolder(LayoutInflater.from(context)
            .inflate(R.layout.layout_drink_item,parent,false))
    }

    override fun onBindViewHolder(holder: MyDrinkHolder, position: Int) {
        Glide.with(context)
            .load(list[position].image)
            .into(holder.imageView!!)
        holder.txtName!!.text = StringBuilder().append(list[position].name)
        holder.txtPrice!!.text = StringBuilder("Rp.").append(list[position].price)

        holder.setOnClickListener(object :IRecyclerClickListener{
            override fun onItemClickListener(view: View?, position: Int) {
                addToCart(list[position])
            }
        })
    }

    private fun addToCart(drinkModel: DrinkModel) {
        val userID = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val userCart = FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child(userID)// Here is similar user id, can use firebase auth


        userCart.child(drinkModel.key!!)
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists())// kalo barang udah di cart
                    {
                        val cartModel = snapshot.getValue(CartModel::class.java)
                        val updateData: MutableMap<String,Any> = HashMap()
                        cartModel!!.quantity = cartModel!!.quantity+1;
                        updateData["quantit"] = cartModel!!.quantity
                        updateData["totalPrice"] = cartModel!!.quantity * cartModel.price!!.toFloat()

                        userCart.child(drinkModel.key!!)
                            .updateChildren(updateData)
                            .addOnSuccessListener {
                                cartListener.onLoadCartFailed("Disimpan Di Keranjang")
                            }
                            .addOnFailureListener{ e-> cartListener.onLoadCartFailed(e.message)}
                    }else{
                        val cartModel = CartModel()
                        cartModel.key = drinkModel.key
                        cartModel.name = drinkModel.name
                        cartModel.image = drinkModel.image
                        cartModel.price = drinkModel.price
                        cartModel.quantity = 1
                        cartModel.totalPrice = drinkModel.price!!.toFloat()

                        userCart.child(drinkModel.key!!)
                            .setValue(cartModel)
                            .addOnSuccessListener {
                                EventBus.getDefault().postSticky(UpdateCartEvent())
                                cartListener.onLoadCartFailed("Disimpan Di Keranjang")
                            }
                            .addOnFailureListener{ e-> cartListener.onLoadCartFailed(e.message)}
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    cartListener.onLoadCartFailed(error.message)
                }

            })

    }

    override fun getItemCount(): Int {
        return list.size
    }
}