package com.example.galon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.example.galon.adapter.OrderListAdapter
import com.example.galon.databinding.ActivityOrderBinding
import com.example.galon.model.OrderModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class OrderListViewActivity : AppCompatActivity() {
    lateinit var binding : ActivityOrderBinding
    private lateinit var ref : DatabaseReference
    private lateinit var orderList : MutableList<OrderModel>
    private lateinit var orderPunyaList : ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityOrderBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val userID = FirebaseAuth.getInstance().currentUser?.uid.toString()
        ref =  FirebaseDatabase.getInstance().getReference("Order").child(userID)
        orderList.toMutableList()
        orderPunyaList = findViewById(R.id.lvOrderList)

        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               snapshot.children.forEach {
                   val keyR = it.key.toString()
                   ref.child(keyR)
                       .addValueEventListener(object :ValueEventListener{
                           override fun onDataChange(snapshot: DataSnapshot) {

                               if (snapshot.exists()){
                                   for(h in snapshot.children)
                                   {
                                       val listOrder = h.getValue(OrderModel::class.java)
                                       if (listOrder != null) {
                                           orderList.add(listOrder)
                                       }
                                   }
                                   val adapter = OrderListAdapter(applicationContext, R.layout.order_list_view, orderList)
                                   orderPunyaList.adapter = adapter
                               }
                           }

                           override fun onCancelled(error: DatabaseError) {
                               TODO("Not yet implemented")
                           }

                       })
               }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}