package com.challenge.repository.transaction

import com.challenge.api.StarlingApiService
import com.challenge.di.ApiResponse
import com.challenge.common.NetworkResult
import com.challenge.common.model.accountDto.UserAccount
import com.challenge.common.model.transactiondomain.TransactionsDomain
import com.challenge.common.utils.DispatcherProvider
import com.challenge.repositorycontract.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class TransactionsRepositoryImpl @Inject constructor(
    private val apiService: StarlingApiService,
    private val dispatcherProvider: DispatcherProvider,
) : TransactionsRepository, ApiResponse() {
    override suspend fun getTransactionsBetween(
        userAccount: UserAccount
    ): Flow<NetworkResult<TransactionsDomain>> =
        flow {
            emit(safeApiCall {
                apiService.getTransactionsBetween(
                    userAccount.accountUid,
                    userAccount.categoryUid,
                    userAccount.minTransactionTimeStamp,
                    userAccount.maxTransactionTimeStamp
                )
            })
        }.map {
            when (it) {
                is NetworkResult.Loading -> return@map NetworkResult.Loading()
                is NetworkResult.Success -> return@map NetworkResult.Success(it.data?.toTransactionsDomain())
                is NetworkResult.Error -> return@map NetworkResult.Error(it.errorResponse)
            }
        }.flowOn(dispatcherProvider.io)
}