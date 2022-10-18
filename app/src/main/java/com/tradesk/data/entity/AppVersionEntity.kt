package com.tradesk.data.entity

data class AppVersionEntity(
    val `data`: AppVersionData,
    val message: String,
    val success: Boolean
)

data class AppVersionData(
    var version: String
)