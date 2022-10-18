package com.tradesk.appCode.addExpensesModule

import com.tradesk.base.MvpView
import com.tradesk.data.entity.*


interface IExpensesView: MvpView {
    fun onAdd(it: AddExpenseModel)
    fun onList(it: ExpensesListModel)
    fun onDelete(it: SuccessModel)
    fun onerror(data: String)

}