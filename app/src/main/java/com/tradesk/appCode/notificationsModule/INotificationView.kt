package com.tradesk.appCode.notificationsModule

import com.tradesk.base.MvpPresenter
import com.tradesk.base.MvpView
import com.tradesk.data.entity.AppVersionEntity
import com.tradesk.data.entity.NotifiactionsModel
import com.tradesk.data.entity.ReminderDetailNewModel


interface INotificationView: MvpView {
    fun onDrugSearched(it: NotifiactionsModel)
    fun onAppVersionResp(it: AppVersionEntity)
    fun onReminderDetail(it: ReminderDetailNewModel)
}

interface INotificationPresenter<V: INotificationView> : MvpPresenter<V> {
//    fun getHome(jsonObject: JsonObject)
}