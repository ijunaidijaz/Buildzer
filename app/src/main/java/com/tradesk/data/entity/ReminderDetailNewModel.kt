package com.tradesk.data.entity

data class ReminderDetailNewModel(
    val `data`: DataNew,
    val message: String,
    val status: Int
)

data class DataNew(
    val reminder_details: ReminderDetails
)

data class ReminderDetails(
    val __v: Int,
    val _id: String,
    val client_id: Any,
    val createdAt: String,
    val created_by: String,
    val dateTime: String,
    val deleted: Boolean,
    val description: String,
    val id: Any,
    val remainder_type: String,
    val status: String,
    val timezone: String,
    val type: String,
    val updatedAt: String
)