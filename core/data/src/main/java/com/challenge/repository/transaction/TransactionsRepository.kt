package com.challenge.repository.transaction

import com.challenge.api.StarlingApiService
import com.challenge.di.ApiResponse
import com.challenge.di.NetworkResult
import com.challenge.model.Transactions
import com.challenge.repository.UserAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

interface TransactionsRepository {
    suspend fun getTransactionsBetween(
        userAccount: UserAccount
    ): Flow<NetworkResult<Transactions>>
}

class TransactionsRepositoryImpl @Inject constructor(
    private val apiService: StarlingApiService
) : TransactionsRepository, ApiResponse() {
    override suspend fun getTransactionsBetween(
        userAccount: UserAccount
    ): Flow<NetworkResult<Transactions>> {

        return flow {
            emit(safeApiCall {
                apiService.getTransactionsBetween(
                    userAccount.accountUid,
                    userAccount.categoryUid,
                    userAccount.minTransactionTimeStamp,
                    userAccount.maxTransactionTimeStamp
                )
            })
        }.flowOn(Dispatchers.IO)

    }
}