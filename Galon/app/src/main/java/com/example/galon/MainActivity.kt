package com.example.galon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.galon.adapter.MyDrinkAdapter
import com.example.galon.adapter.ViewPagerAdaper
import com.example.galon.databinding.ActivityMainBinding
import com.example.galon.eventbus.UpdateCartEvent
import com.example.galon.fragment.HomeFragment
import com.example.galon.fragment.UserFragment
import com.example.galon.listener.ICartLoadListener
import com.example.galon.listener.IDrinkLoadListener
import com.example.galon.listener.IOrderLoadListener
import com.example.galon.model.CartModel
import com.example.galon.model.DrinkModel
import com.example.galon.model.OrderModel
import com.example.galon.utils.SpaceItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity(), IDrinkLoadListener, ICartLoadListener {

    lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth

    lateinit var loadListener: IDrinkLoadListener
    lateinit var cartLoadListener: ICartLoadListener

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
     fun onUpdateCartEvent(event:UpdateCartEvent){
        countCartFromFirebase()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //bottom nav
        binding.btnUser.setOnClickListener{
            val myFragment = UserFragment()
            val fragment : Fragment?=

            supportFragmentManager.findFragmentByTag(UserFragment::class.java.simpleName)

            if (fragment !is UserFragment){
                supportFragmentManager.beginTransaction()
                    .add(R.id.containerFragment, myFragment, UserFragment::class.java.simpleName)
                    .commit()
            }

            binding.mainLayout.visibility = View.GONE
        }

        binding.btnOrderList.setOnClickListener {
            startActivity(Intent(this,OrderActivity::class.java))
        }
        //cart
        init()
        loadItemFromFirebase()
        countCartFromFirebase()

    }

    private fun countCartFromFirebase() {
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
                    cartLoadListener.onLoadCartSuccess(cartModels)
                }

                override fun onCancelled(error: DatabaseError) {
                    cartLoadListener.onLoadCartFailed(error.message)
                }
            })
    }

    private fun loadItemFromFirebase() {
        val drinkModels : MutableList<DrinkModel> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Drink")
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        for (drinkSnapshot in snapshot.children){
                            val drinkModel = drinkSnapshot.getValue(DrinkModel::class.java)
                            drinkModel!!.key = drinkSnapshot.key
                            drinkModels.add(drinkModel)
                        }
                        loadListener.onLoadSucces(drinkModels)
                    }else
                        loadListener.onLoadFail("Item Not Exist")
                }

                override fun onCancelled(error: DatabaseError) {
                    loadListener.onLoadFail(error.message)
                }
            })
    }



    private fun init() {
        loadListener = this
        cartLoadListener = this

        val gridLayoutManager = GridLayoutManager(this, 2)
        recycler_item.layoutManager = gridLayoutManager
        recycler_item.addItemDecoration(SpaceItemDecoration())

        btnCart.setOnClickListener{ startActivity(Intent(this,CartActivity::class.java))}
    }


    override fun onLoadSucces(drinkModelList: List<DrinkModel>?) {
        val adapter = MyDrinkAdapter(this, drinkModelList!!, cartLoadListener)
        recycler_item.adapter = adapter
    }



    override fun onLoadFail(message: String?) {
        Snackbar.make(binding.mainLayout,message!!, Snackbar.LENGTH_LONG).show()
    }

    override fun onLoadCartSuccess(cartModelList: List<CartModel>) {
        var cartSum = 0
        for (cartModel in cartModelList!!) cartSum+= cartModel!!.quantity
        badge!!.setNumber(cartSum)
    }

    override fun onLoadCartFailed(message: String?) {
        Snackbar.make(binding.mainLayout,message!!, Snackbar.LENGTH_LONG).show()
    }




}