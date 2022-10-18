package com.tradesk.appCode.home.leadsDetail.leadsNotesModule

import com.tradesk.base.MvpView
import com.tradesk.data.entity.NotesListModel
import com.tradesk.data.entity.SuccessModel


interface ILeadNotesView : MvpView {
    fun onNotesListSuccess(it: NotesListModel)
    fun onSuccess(it: SuccessModel)
    fun onerror(it: String)

}