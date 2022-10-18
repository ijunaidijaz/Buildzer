package com.tradesk.data.entity

data class SavePharmacyFavData(
    val drug_generic_name: String,
    val drug_name: String,
    val is_location: String,
    val from_drug_search: String,
    val form: String,
    val dosage: String,
    val quantity: String,
    val sort_type: String,
    val latitude: String,
    val longitude: String
)
