package com.challenge.di


import com.challenge.common.ErrorResponse
import com.challenge.common.getErrorResponse
import retrofit2.Response

abstract class ApiResponse {

    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): com.challenge.common.NetworkResult<T> {
        val response = apiCall()
        try {

            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    return com.challenge.common.NetworkResult.Success(body)
                }
            }
            return error(getErrorResponse(response.errorBody()!!.string()))
        } catch (e: Exception) {
            return error(getErrorResponse(response.errorBody()!!.string()))
        }
    }

    private fun <T> error(errorResponse: ErrorResponse): com.challenge.common.NetworkResult<T> =
        com.challenge.common.NetworkResult.Error(errorResponse)
}