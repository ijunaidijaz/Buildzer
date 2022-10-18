package com.tradesk.appCode.profileModule.proposalsModule

import com.tradesk.base.MvpView
import com.tradesk.data.entity.*


interface IProposalsView: MvpView {
    fun onAdd(it: AddProposalsModel)
    fun onList(it: PorposalsListModel)
    fun onDefaultList(it: DefaultItemsModel)
    fun onDetails(it: ProposalDetailModel)
    fun onSend(it: SuccessModel)
    fun onChangeStatus(it: ChangeProposalStatus)
    fun onDelete(it: SuccessModel)
    fun onerror(data: String)
    fun onProfile(it: ProfileModel)

}