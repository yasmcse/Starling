package com.challenge.repositorycontract

import com.challenge.common.model.NetworkResult
import com.challenge.mapper.account.model.AccountsDomain

interface AccountRepository {
    suspend fun getAccounts(): NetworkResult<AccountsDomain>
}