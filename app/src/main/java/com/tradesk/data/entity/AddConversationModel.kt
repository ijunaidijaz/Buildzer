package com.tradesk.data.entity

data class AddConversationModel(
    val `data`: AddConversationData,
    val message: String,
    val status: Int
)

data class AddConversationData(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val deleted: Boolean,
    val is_group_message: Boolean,
    val job_id: String,
    val message: Message,
    val receivers: List<Receiver>,
    val room_id: String,
    val sender: String,
    val updatedAt: String
)

data class UserId(
    val _id: String,
    val email: String,
    val name: String,
    val userType: String
)

data class Message(
    val image: String,
    val text: String
)

data class Receiver(
    val _id: String,
    val delivered: Boolean,
    val isRead: Boolean,
    val last_seen: String,
//    val user_id: UserId
)

/*  GetConversationModel*/
data class GetConversationModel(
    val `data`: GetConversationData,
    val message: String,
    val status: Int
)

data class GetConversationData(
    val conversation: List<AddConversationData>,
    val limit: Int,
    val page: Int,
    val totalPages: Int
)
