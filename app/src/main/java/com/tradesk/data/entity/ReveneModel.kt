package com.tradesk.data.entity

data class ReveneModel(
    val `data`: DataRevene,
    val message: String,
    val status: Int
)

data class DataRevene(
    val jobsData: JobsDataRevene
)

data class JobsDataRevene(
    val NetIncome: Int,
    val Revenue: Int,
    val Sales: Int,
    val _id: Any,
    val totalHours: String,
    val total_expenses: Int
)