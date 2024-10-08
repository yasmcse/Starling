package com.challenge.repositories

import com.challenge.common.model.NetworkResult
import com.challenge.model.account.AccountsDomain

interface AccountRepository {
    suspend fun getAccounts(): NetworkResult<AccountsDomain>
}