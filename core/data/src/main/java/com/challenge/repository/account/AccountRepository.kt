package com.challenge.repository.account

import com.challenge.api.StarlingApiService
import com.challenge.di.ApiResponse
import com.challenge.di.NetworkResult
import com.challenge.model.Account
import com.challenge.model.Accounts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

interface AccountRepository {
    suspend fun getAccounts(): Flow<NetworkResult<Accounts>>
}

class AccountRepositoryImpl @Inject constructor(private val apiService: StarlingApiService) :
    AccountRepository, ApiResponse() {

    override suspend fun getAccounts(): Flow<NetworkResult<Accounts>> {
        return flow {
            emit(safeApiCall { apiService.getUserAccounts() })
        }.flowOn(Dispatchers.IO)
    }
}