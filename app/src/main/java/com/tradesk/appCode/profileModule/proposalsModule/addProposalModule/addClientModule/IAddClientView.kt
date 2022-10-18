package com.tradesk.appCode.profileModule.proposalsModule.addProposalModule.addClientModule

import com.tradesk.base.MvpView
import com.tradesk.data.entity.ClientSalesModelNew
import com.tradesk.data.entity.ClientsListModel
import com.tradesk.data.entity.SuccessModel


interface IAddClientView: MvpView {
    fun onAdd(it: SuccessModel)
    fun onUpdateSales(it: SuccessModel)
    fun onDetails(it: ClientSalesModelNew)
    fun onGetClients(it: ClientsListModel)
    fun onerror(data: String)

}