package com.challenge.domain

import com.challenge.di.NetworkResult
import com.challenge.model.Accounts
import com.challenge.repository.account.AccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAccountsUseCase @Inject constructor(private val accountRepository: AccountRepository) {
    suspend fun getAccounts():Flow<NetworkResult<Accounts>> = accountRepository.getAccounts()
}