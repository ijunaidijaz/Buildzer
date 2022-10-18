package com.tradesk.data.entity

data class RevenueModel(
    val `data`: DataRevenue? = null,
    val message: String,
    val status: Int
)

data class DataRevenue(
    val details: DetailsRevenue
)

data class DetailsRevenue(
    val _id: Any,
    val convertedJobInvoiceAmount: Int,
    val convertedJobProposalAmount: Int,
    val totalConvertedJobsCount: Int,
    val totalHours: String,
    val totalProposalAmount: Int,
    val totalProposalsCount: Int,
    val totalRevenue: Int,
    val total_invoice_expenses: Int,
    val total_proposal_expenses: Int
)


