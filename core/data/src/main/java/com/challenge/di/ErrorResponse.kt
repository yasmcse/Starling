package com.challenge.di

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("errors") val errors: List<Error>,
    @SerializedName("success") val success: Boolean
)

data class Error(
    val message: String
)

fun getErrorResponse(response:String):ErrorResponse{
    return Gson().fromJson(response, ErrorResponse::class.java)

}