package com.tradesk.data.entity

data class TermsModel(
    val `data`: DataTerms,
    val message: String,
    val status: Int
)

data class DataTerms(
    val privacy_policy: PrivacyPolicy,
    val terms_condition: TermsCondition
)

data class PrivacyPolicy(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val createdAt: String,
    val created_by: String,
    val delete: Boolean,
    val description: String,
    val title: String,
    val updatedAt: String
)

data class TermsCondition(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val createdAt: String,
    val created_by: String,
    val delete: Boolean,
    val description: String,
    val title: String,
    val updatedAt: String
)