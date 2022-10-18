package com.tradesk.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdditionalImagesModel(
    val `data`: DataAdditionalImage,
    val message: String,
    val status: Int
) : Parcelable

@Parcelize
data class DataAdditionalImage(
    val leadsData: List<LeadsDataAdditionalImage>,
    val limit: Int,
    val page: Int,
    val totalPages: Int
) : Parcelable

@Parcelize
data class LeadsDataAdditionalImage(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val additional_images: List<AdditionalImageAdditionalImage>,
    val address: AddressAdditionalImage,
    val client: List<ClientAdditionalImage>,
    val converted_to_job: Boolean,
    val createdAt: String,
    val created_by: String,
    val deleted: Boolean,
    val description: String,
    val image: String,
    val project_name: String,
    val sales: List<SaleAdditionalImage>,
    val source: String,
    val status: String,
    val type: String,
    val updatedAt: String
) : Parcelable

@Parcelize
data class AdditionalImageAdditionalImage(
    val _id: String,
    val image: String,
    val status: String
) : Parcelable

@Parcelize
data class AddressAdditionalImage(
    val city: String,
    val location: LocationAdditionalImage,
    val state: String,
    val street: String,
    val zipcode: String
) : Parcelable

@Parcelize
data class ClientAdditionalImage(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val address: AddressXAdditionalImage,
    val createdAt: String,
    val created_by: String,
    val deleted: Boolean,
    val email: String,
    val home_phone_number: String,
    val image: String,
    val name: String,
    val phone_no: String,
    val privatenotes: String,
    val trade: String,
    val type: String,
    val updatedAt: String
) : Parcelable

@Parcelize
data class SaleAdditionalImage(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val address: AddressXXAdditionalImage,
    val createdAt: String,
    val created_by: String,
    val deleted: Boolean,
    val email: String,
    val home_phone_number: String,
    val image: String,
    val name: String,
    val phone_no: String,
    val privatenotes: String,
    val trade: String,
    val type: String,
    val updatedAt: String
) : Parcelable

@Parcelize
data class LocationAdditionalImage(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
) : Parcelable

@Parcelize
data class AddressXAdditionalImage(
    val city: String,
    val location: LocationXAdditionalImage,
    val state: String,
    val street: String,
    val zipcode: String
) : Parcelable

@Parcelize
data class LocationXAdditionalImage(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
) : Parcelable

@Parcelize
data class AddressXXAdditionalImage(
    val city: String,
    val location: LocationXXAdditionalImage,
    val state: String,
    val street: String,
    val zipcode: String
) : Parcelable

@Parcelize
data class LocationXXAdditionalImage(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
) : Parcelable