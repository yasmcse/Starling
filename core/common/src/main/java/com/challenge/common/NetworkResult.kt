package com.challenge.common

sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null,
    val errorResponse: ErrorResponse? = null
) {
    class Success<T>(data: T?) : NetworkResult<T>(data)
    class Error<T>(errorResponse: ErrorResponse?) : NetworkResult<T>(null, null,errorResponse)
    class Loading<T>() : NetworkResult<T>()
}