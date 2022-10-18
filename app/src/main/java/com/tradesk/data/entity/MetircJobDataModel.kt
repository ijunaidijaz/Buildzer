package com.tradesk.data.entity

data class MetircJobDataModel(
    val `data`: DataMetricJob,
    val message: String,
    val status: Int
)

data class DataMetricJob(
    val jobsData: JobsDataMetricJob? = null
)

data class JobsDataMetricJob(
    val NetIncome: Int,
    val Revenue: Int,
    val Sales: Int,
    val _id: Any,
    val totalHours: String,
    val total_expenses: Int
)