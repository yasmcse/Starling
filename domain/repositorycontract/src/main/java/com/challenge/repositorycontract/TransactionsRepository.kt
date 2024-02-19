package com.challenge.repositorycontract

import com.challenge.common.NetworkResult
import com.challenge.common.model.accountDto.UserAccount
import com.challenge.common.model.transactiondomain.TransactionsDomain
import kotlinx.coroutines.flow.Flow

interface TransactionsRepository {
    suspend fun getTransactionsBetween(
        userAccount: UserAccount
    ): Flow<NetworkResult<TransactionsDomain>>
}