package com.example.galon.listener

import com.example.galon.model.OrderModel

interface IOrderLoadListener {
    fun onLoadOrderSuccess(orderModelList: List<OrderModel>)
    fun onLoadOrderFailed(message:String?)
}