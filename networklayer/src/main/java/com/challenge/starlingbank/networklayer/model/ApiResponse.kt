package com.challenge.starlingbank.networklayer.model


import com.challenge.common.model.NetworkResult
import retrofit2.Response

abstract class ApiResponse {
    suspend fun <T, R> handleApiCall(
        apiCall: suspend () -> Response<T>,
        mapToDomain: (T) -> R
    ): NetworkResult<R> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    // Successful response, map the data
                    NetworkResult.Success(mapToDomain(body))
                } else {
                    // Null body, treat as error
                    NetworkResult.Error(response.code(), "Response body is null")
                }
            } else {
                // API call unsuccessful
                NetworkResult.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            // Handle any exception that occurs during the API call
            NetworkResult.Error(null, e.message ?: "An unknown error occurred")
        }
    }
}