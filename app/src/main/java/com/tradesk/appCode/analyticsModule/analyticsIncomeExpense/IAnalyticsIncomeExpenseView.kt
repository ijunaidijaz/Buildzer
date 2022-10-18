package com.tradesk.appCode.analyticsModule.analyticsIncomeExpense

import com.tradesk.base.MvpPresenter
import com.tradesk.base.MvpView
import com.tradesk.data.entity.ExpensesJobsListModel
import com.tradesk.data.entity.PorposalsListModel


interface IAnalyticsIncomeExpenseView: MvpView {
    fun onInvoicesResponse(it: PorposalsListModel)
    fun onExpenseListResponse(it: ExpensesJobsListModel)
}

interface IAnalyticsIncomeExpensePresenter<V: IAnalyticsIncomeExpenseView> : MvpPresenter<V> {
}