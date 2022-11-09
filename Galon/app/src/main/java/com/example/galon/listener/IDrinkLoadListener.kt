package com.example.galon.listener

import com.example.galon.model.DrinkModel

interface IDrinkLoadListener {
    fun onLoadSucces(drinkModelList: List<DrinkModel>?)
    fun onLoadFail(message: String?)
}