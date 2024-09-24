package com.challenge.common.model

sealed class NetworkResult<T>(val data: T? = null, val code: Int?,val message: String?) {
    class Success<T>(data: T?) : NetworkResult<T>(data,null,null)
    class Error<T>(code: Int?, message: String?) : NetworkResult<T>(null,code, message)
    class Loading<T> : NetworkResult<T>(null,null,null)
}