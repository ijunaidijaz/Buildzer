package com.tradesk.data.entity

data class SubUsersDetailModel(
    val `data`: DataSubUsersDetail,
    val message: String,
    val status: Int
)

data class DataSubUsersDetail(
    val details: Details,
    val jobsDetails: List<JobsDetail>
)

data class Details(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val address: Address,
    val createdAt: String,
    val created_by: String,
    val deleted: Boolean,
    val email: String,
    val image: String,
    val job_id: String,
    val mobile_no: String,
    val name: String,
    val trade: String,
    val updatedAt: String
)

data class JobsDetail(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val additional_images: List<AdditionalImage>,
    val address: AddressX,
    val createdAt: String,
    val created_by: String,
    val deleted: Boolean,
    val description: String,
    val end_date: String,
    val image: String,
    val project_name: String,
    val sales: List<Sale>,
    val source: String,
    val start_date: String,
    val status: String,
    val type: String,
    val updatedAt: String
)

data class AddressSubUsersDetail(
    val city: String,
    val country: String,
    val location: Location,
    val postal_code: String,
    val state: String,
    val street: String
)

data class LocationSubUsersDetail(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
)

data class AdditionalImageSubUsersDetail(
    val _id: String,
    val image: String
)

data class AddressXSubUsersDetail(
    val city: String,
    val location: LocationX,
    val state: String,
    val street: String,
    val zipcode: String
)

data class SaleSubUsersDetail(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val address: AddressXX,
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
)

data class LocationXSubUsersDetail(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
)

data class AddressXXSubUsersDetail(
    val city: String,
    val location: LocationXX,
    val state: String,
    val street: String,
    val zipcode: String
)

data class LocationXXSubUsersDetail(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
)