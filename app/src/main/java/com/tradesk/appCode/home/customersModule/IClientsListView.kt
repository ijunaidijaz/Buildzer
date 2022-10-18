package com.tradesk.appCode.home.customersModule

import com.tradesk.base.MvpView
import com.tradesk.data.entity.ClientsListModel


interface IClientsListView : MvpView {
    fun onClientsList(data: ClientsListModel)
    fun onerror(data: String)

}