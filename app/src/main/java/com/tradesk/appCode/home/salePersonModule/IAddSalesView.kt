package com.tradesk.appCode.home.salePersonModule

import com.tradesk.base.MvpView
import com.tradesk.data.entity.ClientSalesModelNew
import com.tradesk.data.entity.ClientsListModel
import com.tradesk.data.entity.SuccessModel
import com.tradesk.data.entity.TradesModel


interface IAddSalesView : MvpView {
    fun onAddSales(it: SuccessModel)
    fun onUpdateSales(it: SuccessModel)
    fun onDetails(it: ClientSalesModelNew)
    fun onTradesDetails(it: TradesModel)
    fun onAddJobSales(it: SuccessModel)
    fun onAddSubUsers(it: SuccessModel)
    fun onSalesList(it: ClientsListModel)
    fun onerror(data: String)

}