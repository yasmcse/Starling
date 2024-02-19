package com.challenge.repositorycontract

import com.challenge.common.NetworkResult
import com.challenge.common.model.accountDomain.AccountsDomain
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    suspend fun getAccounts(): Flow<NetworkResult<AccountsDomain>>
}