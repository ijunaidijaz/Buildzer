package com.tradesk.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GallaryModel(
    val `data`: DataGallary,
    val message: String,
    val status: Int
) : Parcelable

@Parcelize
data class DataGallary(
    val leadsData: List<LeadsDataGallary>,
    val limit: Int,
    val page: Int,
    val totalPages: Int
) : Parcelable

@Parcelize
data class LeadsDataGallary(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val additional_images: List<AdditionalImage>,
    val address: AddressGallaryModel,
    val client: List<ClientGallaryModel>,
    val createdAt: String,
    val created_by: String,
    val deleted: Boolean,
    val description: String,
    val end_date: String,
    val image: String,
    val notes: String,
    val project_name: String,
    val sales: List<SaleGallaryModel>,
    val source: String,
    val start_date: String,
    val status: String,
    val type: String,
    val updatedAt: String
) : Parcelable

@Parcelize
data class AdditionalImage(
    val _id: String,
    val image: String
) : Parcelable

@Parcelize
data class AddressGallaryModel(
    val city: String,
    val location: LocationGallaryModel,
    val state: String,
    val street: String,
    val zipcode: String
) : Parcelable

@Parcelize
data class ClientGallaryModel(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val address: AddressXGallaryModel,
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
data class SaleGallaryModel(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val address: AddressXXGallaryModel,
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
data class LocationGallaryModel(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
) : Parcelable

@Parcelize
data class AddressXGallaryModel(
    val city: String,
    val location: LocationXGallaryModel,
    val state: String,
    val street: String,
    val zipcode: String
) : Parcelable

@Parcelize
data class LocationXGallaryModel(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
) : Parcelable

@Parcelize
data class AddressXXGallaryModel(
    val city: String,
    val location: LocationXXGallaryModel,
    val state: String,
    val street: String,
    val zipcode: String
) : Parcelable

@Parcelize
data class LocationXXGallaryModel(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
) : Parcelable