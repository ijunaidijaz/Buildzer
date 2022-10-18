package com.tradesk.data.network

interface ApiHelper {
    fun restService(): RestService
    fun authdetailService(): RestService
    fun distanceService(): RestService

}