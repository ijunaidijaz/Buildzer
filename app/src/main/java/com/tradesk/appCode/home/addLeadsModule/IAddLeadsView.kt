package com.tradesk.appCode.home.addLeadsModule

import com.tradesk.base.MvpView
import com.tradesk.data.entity.SuccessModel


interface IAddLeadsView : MvpView {
    fun onAddLeads(it: SuccessModel)
    fun onUpdateLeads(it: SuccessModel)
    fun onerror(data: String)

}