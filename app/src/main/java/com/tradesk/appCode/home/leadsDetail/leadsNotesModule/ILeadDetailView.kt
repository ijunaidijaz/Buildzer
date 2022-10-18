package com.tradesk.appCode.home.leadsDetail.leadsNotesModule

import com.tradesk.base.MvpView
import com.tradesk.data.entity.AdditionalImagesWithClientModel
import com.tradesk.data.entity.LeadDetailModel
import com.tradesk.data.entity.ProfileModel
import com.tradesk.data.entity.SuccessModel


interface ILeadDetailView : MvpView {
    fun onLeadDetail(it: LeadDetailModel)
    fun onDeleteImage(it: SuccessModel)
    fun onStatus(it: SuccessModel)
    fun onLeadDeleteStatus(it: SuccessModel)
    fun onDocDeleteStatus(it: SuccessModel)
    fun onAddImage(it: SuccessModel)
    fun onAddReminder(it: SuccessModel)
    fun onProfileData(it: ProfileModel)
    fun onAdditionalImagesData(it: AdditionalImagesWithClientModel)
    fun onerror(data: String)

}