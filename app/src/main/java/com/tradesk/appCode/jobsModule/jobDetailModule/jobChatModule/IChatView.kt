package com.tradesk.appCode.jobsModule.jobDetailModule.jobChatModule

import com.tradesk.base.MvpPresenter
import com.tradesk.base.MvpView
import com.tradesk.data.entity.AddConversationModel
import com.tradesk.data.entity.AppVersionEntity
import com.tradesk.data.entity.GetConversationData
import com.tradesk.data.entity.LeadsModel


interface IChatView : MvpView {
    fun onAppVersionResp(it: AppVersionEntity)
    fun onAddMsgResp(it: AddConversationModel)
    fun onGetChatResp(data: GetConversationData)
    fun onGetRecentChatResp(data: GetConversationData)
    fun onChatUsersList(it: LeadsModel)
}

interface IChatPresenter<V : IChatView> : MvpPresenter<V> {
//    fun getChat(jsonObject: JsonObject)
}