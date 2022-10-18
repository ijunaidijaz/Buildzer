package com.tradesk.listeners

import com.tradesk.data.entity.Client

interface CustomSpinnerListener {
    fun onItemClick(item:String)
}

interface CustomCheckBoxListener {
    fun onCheckBoxClick(item:Int)
}

interface SingleListCLickListener {
    fun onSingleListClick(item:Any,position:Int)
}

interface PairListCLickListener {
    fun onFirstListClick(item:Any,position:Int)
    fun onSecondListClick(item:Any,position:Int)
}
interface AttachedDocListener {
    fun onDocClick(item:Any,position:Int)
}
interface DocListener {
    fun onCrossClick(item:Any,position:Int)
}
interface SingleItemCLickListener {
    fun onSingleItemClick(item:Any,position:Int)
}

interface AddClientListener {
    fun onAddClientClick(item:Client, position: Int)
}
interface AddSalesListener {
    fun onAddSalesClick(item:Client, position: Int)
}
interface OnItemRemove {
    fun onRemove(item:Any,position:Int)
}