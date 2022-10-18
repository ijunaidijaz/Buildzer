package com.tradesk.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdditonalImagesClassModel(
    val `data`: DataAdditonalImagesClass,
    val message: String,
    val status: Int
) : Parcelable

@Parcelize
data class DataAdditonalImagesClass(
    val leadsData: List<LeadsDataAdditonalImagesClass>,
    val limit: Int,
    val page: Int,
    val totalPages: Int
) : Parcelable

@Parcelize
data class LeadsDataAdditonalImagesClass(
    val _id: String,
    val additional_images: List<AdditionalImageAdditonalImagesClass>,
    val project_name: String
) : Parcelable

@Parcelize
data class AdditionalImageAdditonalImagesClass(
    val _id: String,
    val image: String,
    val status: String
) : Parcelable