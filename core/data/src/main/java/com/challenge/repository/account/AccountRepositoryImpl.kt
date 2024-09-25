package com.challenge.repository.account

import com.challenge.common.model.NetworkResult
import com.challenge.model.account.AccountsDomain
import com.challenge.mapper.account.toAccountsDomain
import com.challenge.repositorycontract.AccountRepository
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val apiService: com.challenge.starlingbank.networklayer.api.StarlingApiService
) : AccountRepository, com.challenge.starlingbank.networklayer.model.ApiResponse() {
    override suspend fun getAccounts(): NetworkResult<AccountsDomain> =

        handleApiCall(
            apiCall = { apiService.getUserAccounts() },
            mapToDomain = { AccountsDomain(it.toAccountsDomain()) }
        )
}
