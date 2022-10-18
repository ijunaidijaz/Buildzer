package com.tradesk.data.entity

data class SearchDrugEntity(
    val `data`: List<SearchDrugData>,
    val message: String,
    val success: Boolean
)

data class SearchDrugData(
    val display_drug_name: String,
    val drug_generic_name: String,
    val drug_name: String
)