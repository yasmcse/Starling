package com.challenge.repositorycontract

import com.challenge.common.model.NetworkResult
import com.challenge.common.model.accountDto.UserAccount
import com.challenge.mapper.transaction.model.TransactionsDomain

interface TransactionsRepository {
    suspend fun getTransactionsBetween(
        userAccount: UserAccount
    ): NetworkResult<TransactionsDomain>
}