package com.challenge.di

import com.challenge.data.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class TokenInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().apply {
            header("Accept","application/json")
            header("Authorization", "Bearer " + BuildConfig.accessToken )
        }.build()
        return chain.proceed(request)
    }
}