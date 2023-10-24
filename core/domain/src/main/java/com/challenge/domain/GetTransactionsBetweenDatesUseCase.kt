package com.challenge.domain

import com.challenge.repository.UserAccount
import com.challenge.repository.transaction.TransactionsRepository
import javax.inject.Inject

class GetTransactionsBetweenDatesUseCase @Inject constructor(
    private val transactionsRepository: TransactionsRepository
) {
    suspend fun getTransactions(userAccount: UserAccount) =
        transactionsRepository.getTransactionsBetween(userAccount)
}