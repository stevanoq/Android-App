package com.example.galon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.galon.adapter.MyOrderAdapter
import com.example.galon.databinding.ActivityOrderBinding
import com.example.galon.listener.IOrderLoadListener
import com.example.galon.model.OrderModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_order.*
import kotlinx.android.synthetic.main.activity_order.btnBack
import kotlinx.android.synthetic.main.activity_order.recycler_order

class OrderActivity : AppCompatActivity(), IOrderLoadListener{
    val orderModel = OrderModel()
    var orderLoadListener : IOrderLoadListener?=null

    lateinit var binding : ActivityOrderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        order()
        loadOrderFromFirebase()
        btnHapus.setOnClickListener {
            confirmHapus.visibility = View.VISIBLE
        }
        btnYa.setOnClickListener {
            confirmHapus.visibility = View.GONE
            val userID = FirebaseAuth.getInstance().currentUser?.uid.toString()
            FirebaseDatabase.getInstance()
                .getReference("Order")
                .child(userID)
                .removeValue()
                .addOnSuccessListener {
                    startActivity(Intent(this,MainActivity::class.java))
                }
        }
        binding.btnGaJadi.setOnClickListener {
            confirmHapus.visibility = View.GONE
        }

    }

    private fun loadOrderFromFirebase() {
        val userID = FirebaseAuth.getInstance().currentUser?.uid.toString()
        var orderModels : MutableList<OrderModel> = ArrayList()
        val length = 20
        val orderModel = OrderModel()
        FirebaseDatabase.getInstance()
                            .getReference("Order")
                            .child(userID)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach{
                        val keyR : String = it.key.toString()

                        FirebaseDatabase.getInstance()
                            .getReference("Order")
                            .child(userID)
                            .child(keyR)
                            .addListenerForSingleValueEvent(object :ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    recycler_order!!.clearOnChildAttachStateChangeListeners()
                                    if (snapshot.exists()){
                                        for (orderSnapshot in snapshot.children){
                                            val orderModel = orderSnapshot.getValue(OrderModel::class.java)
                                            orderModel!!.key = orderSnapshot.key
                                            orderModels.add(orderModel)
                                        }
                                        orderLoadListener!!.onLoadOrderSuccess(orderModels)
                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {
                                    orderLoadListener!!.onLoadOrderFailed(error.message)
                                }
                            })
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }

    private fun order() {
        orderLoadListener = this
        val layoutManager = LinearLayoutManager(this)
        recycler_order!!.layoutManager = layoutManager
        recycler_order!!.addItemDecoration(DividerItemDecoration(this,layoutManager.orientation))
        btnBack!!.setOnClickListener { finish() }
    }

    override fun onLoadOrderSuccess(orderModelList: List<OrderModel>) {
        var sum = 0.0
        for (orderModel in orderModelList!!){
            sum += orderModel!!.totalPrice
        }
        totalBelanja.text = StringBuilder("Rp. ").append(sum)
        val adapter = MyOrderAdapter(this, orderModelList)
        recycler_order!!.adapter = adapter
    }

    override fun onLoadOrderFailed(message: String?) {
        Snackbar.make(binding.orderLayout,message!!, Snackbar.LENGTH_LONG).show()
    }

}