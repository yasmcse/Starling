package com.challenge.repository.account

import com.challenge.api.StarlingApiService
import com.challenge.common.NetworkResult
import com.challenge.common.model.accountDomain.AccountsDomain
import com.challenge.di.ApiResponse
import com.challenge.common.utils.DispatcherProvider
import com.challenge.repositorycontract.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val apiService: StarlingApiService,
    private val dispatcherProvider: DispatcherProvider
) :
    AccountRepository, ApiResponse() {
    override suspend fun getAccounts(): Flow<NetworkResult<AccountsDomain>> =
        flow {
            emit(safeApiCall { apiService.getUserAccounts() })
        }.map {
            when (it) {
                is NetworkResult.Loading -> return@map NetworkResult.Loading()
                is NetworkResult.Success -> return@map NetworkResult.Success(it.data?.toAccountsDomain()
                    ?.let { it1 -> AccountsDomain(it1) })
                is NetworkResult.Error -> return@map NetworkResult.Error(it.errorResponse)
            }
        }.flowOn(dispatcherProvider.io)
}