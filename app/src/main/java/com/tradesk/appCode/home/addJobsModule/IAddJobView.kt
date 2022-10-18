package com.tradesk.appCode.home.addJobsModule

import com.tradesk.base.MvpView
import com.tradesk.data.entity.SuccessModel


interface IAddJobView : MvpView {
    fun onAddJob(it: SuccessModel)
    fun onUpdateJob(it: SuccessModel)
    fun onerror(data: String)

}