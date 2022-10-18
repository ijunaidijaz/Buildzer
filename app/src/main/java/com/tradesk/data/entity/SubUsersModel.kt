package com.tradesk.data.entity

data class SubUsersModel(
    val `data`: DataSubUsers,
    val message: String,
    val status: Int
)

data class DataSubUsers(
    val limit: Int,
    val page: Int,
    val subUsers: List<SubUser>,
    val totalPages: Int
)

data class SubUser(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val address: AddressSubUsers,
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

data class AddressSubUsers(
    val city: String,
    val country: String,
    val location: LocationSubUsers,
    val postal_code: String,
    val state: String,
    val street: String
)

data class LocationSubUsers(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
)