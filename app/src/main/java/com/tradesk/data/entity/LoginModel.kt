package com.tradesk.data.entity


data class LoginModel(
    val `data`: Data,
    val message: String,
    val status: Int
)

data class Data(
    val token: String,
    val userType: String
)