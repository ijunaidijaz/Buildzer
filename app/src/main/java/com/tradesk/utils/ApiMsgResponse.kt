package com.codebrew.clikat.data.network

import com.google.gson.annotations.SerializedName

class ApiMsgResponse<T> {
    val success: Boolean? = true
    val status: Boolean? = true

    @SerializedName("message", alternate = ["msg"])
    val msg: Any? = null
    val data: Any? = null
}