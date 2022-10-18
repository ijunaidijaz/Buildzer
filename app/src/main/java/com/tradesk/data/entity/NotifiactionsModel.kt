package com.tradesk.data.entity

data class NotifiactionsModel(
    val `data`: DataNotifiactions,
    val message: String,
    val status: Int
)

data class DataNotifiactions(
    val limit: Int,
    val notification_details: List<NotificationDetail>,
    val page: Int,
    val totalPages: Int
)

data class NotificationDetail(
    val Assigned_by: String,
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val createdAt: String,
    val created_by: String,
    val deleted: Boolean,
    val isRead: Boolean,
    val message: String,
    val refId: String,
    val type: String,
    val updatedAt: String
)