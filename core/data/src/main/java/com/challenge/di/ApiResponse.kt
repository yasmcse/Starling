package com.challenge.di


import retrofit2.Response

abstract class ApiResponse {

    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        val response = apiCall()
        try {

            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    return NetworkResult.Success(body)
                }
            }
            return error(getErrorResponse(response.errorBody()!!.string()))
        } catch (e: Exception) {
            return error(getErrorResponse(response.errorBody()!!.string()))
        }
    }

    private fun <T> error(errorResponse: ErrorResponse): NetworkResult<T> =
        NetworkResult.Error(errorResponse)
}