package com.example.galon

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SnapHelper
import com.example.galon.adapter.MyCartAdapter
import com.example.galon.adapter.MyOrderAdapter
import com.example.galon.databinding.ActivityCartBinding
import com.example.galon.databinding.ActivityMainBinding
import com.example.galon.eventbus.UpdateCartEvent
import com.example.galon.eventbus.UpdateOrderEvent
import com.example.galon.listener.ICartLoadListener
import com.example.galon.listener.IOrderLoadListener
import com.example.galon.model.CartModel
import com.example.galon.model.DrinkModel
import com.example.galon.model.OrderModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_cart.btnBack
import kotlinx.android.synthetic.main.activity_order.*
import okhttp3.internal.cache.DiskLruCache
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.StringBuilder

class CartActivity : AppCompatActivity(), ICartLoadListener, IOrderLoadListener {
    lateinit var binding: ActivityCartBinding
    var cartLoadListener : ICartLoadListener?=null
    var orderLoadListener : IOrderLoadListener?=null

    private lateinit var list : List<DrinkModel>
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if (EventBus.getDefault().hasSubscriberForEvent(UpdateCartEvent::class.java))
            EventBus.getDefault().removeStickyEvent(UpdateCartEvent::class.java)
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onUpdateCartEvent(event: UpdateCartEvent){
        loadCartFromFirebase()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        loadCartFromFirebase()
        btnCheckOut.setOnClickListener {
            binding.panel.visibility = View.GONE
            binding.recyclerCart.visibility = View.GONE
            binding.confirmOrder.visibility = View.VISIBLE
        }

        btnBatal.setOnClickListener {
            binding.confirmOrder.visibility = View.GONE
            binding.panel.visibility = View.VISIBLE
            binding.recyclerCart.visibility = View.VISIBLE
        }

     btnPesan.setOnClickListener {
            createOrder()
            binding.confirmOrder.visibility = View.GONE
            binding.panel.visibility = View.GONE
            binding.recyclerCart.visibility = View.GONE
         startActivity(Intent(this, FormDataDiriActivity::class.java))
        }



    }

    private fun createOrder() {
        val cartModels : MutableList<CartModel> = ArrayList()
        val orderModels : MutableList<OrderModel> = ArrayList()
        val cartModel = CartModel()
        val orderModel = OrderModel()
        val userID = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val orderID = FirebaseDatabase.getInstance()
            .getReference("Order")
            .child(userID)
            .push().key.toString()

        if (orderID != null){
            FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child(userID)
                .addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (cartSnapshot in snapshot.children){
                            val cartModel = cartSnapshot.getValue(CartModel::class.java)

                            FirebaseDatabase.getInstance()
                                .getReference("Order")
                                .child(userID)
                                .child(orderID)
                                .child(cartModel?.key!!)
                                .setValue(cartModel)
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {

                    }
                })
        }



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

    private fun loadCartFromFirebase() {
        val cartModels : MutableList<CartModel> = ArrayList()
        val userID = FirebaseAuth.getInstance().currentUser?.uid.toString()
        FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child(userID)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (cartSnapshot in snapshot.children){
                        val cartModel = cartSnapshot.getValue(CartModel::class.java)
                        cartModel!!.key = cartSnapshot.key
                        cartModels.add(cartModel)
                    }
                    cartLoadListener!!.onLoadCartSuccess(cartModels)
                }

                override fun onCancelled(error: DatabaseError) {
                    cartLoadListener!!.onLoadCartFailed(error.message)
                }
            })
    }


    private fun init() {
        cartLoadListener = this
        val layoutManager = LinearLayoutManager(this)
        recycler_cart!!.layoutManager = layoutManager
        recycler_cart!!.addItemDecoration(DividerItemDecoration(this,layoutManager.orientation))
        btnBack!!.setOnClickListener { finish() }
    }

    override fun onLoadCartSuccess(cartModelList: List<CartModel>) {
        var sum = 0.0
        for (cartModel in cartModelList!!){
            sum+= cartModel!!.totalPrice
        }
        txtTotal.text = StringBuilder("Rp. ").append(sum)
        val adapter = MyCartAdapter(this, cartModelList)
        recycler_cart!!.adapter = adapter
    }

    override fun onLoadCartFailed(message: String?) {
        Snackbar.make(binding.mainLayout,message!!, Snackbar.LENGTH_LONG).show()
    }

    override fun onLoadOrderSuccess(orderModelList: List<OrderModel>) {
        val adapter = MyOrderAdapter(this, orderModelList)
        recycler_order!!.adapter = adapter
    }

    override fun onLoadOrderFailed(message: String?) {
        Snackbar.make(binding.mainLayout,message!!, Snackbar.LENGTH_LONG).show()
    }

    fun getRandomString(length: Int) : String {
        val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }
}