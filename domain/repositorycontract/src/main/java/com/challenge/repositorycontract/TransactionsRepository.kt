package com.challenge.repositorycontract

import com.challenge.common.model.NetworkResult
import com.challenge.common.model.accountDto.UserAccount
import com.challenge.model.transaction.TransactionsDomain

interface TransactionsRepository {
    suspend fun getTransactionsBetween(
        userAccount: UserAccount
    ): NetworkResult<TransactionsDomain>
}