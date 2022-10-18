package com.tradesk.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdditionalImagesWithClientModel(
    val `data`: DataImageClient,
    val message: String,
    val status: Int
) : Parcelable

@Parcelize
data class DataImageClient(
    val leadsData: List<LeadsDataImageClient>,
    val limit: Int,
    val page: Int,
    val totalPages: Int
) : Parcelable

@Parcelize
data class LeadsDataImageClient(
    val _id: String,
    val additional_images: List<AdditionalImageImageClient>,
    val client_details: ClientDetails,
    val project_name: String
) : Parcelable

@Parcelize
data class AdditionalImageImageClient(
    val _id: String,
    val image: String,
    val status: String
) : Parcelable

@Parcelize
data class ClientDetails(
    val _id: String,
    val name: String,
    val type: String
) : Parcelable