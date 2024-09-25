package com.challenge.repository.transaction

import com.challenge.common.model.NetworkResult
import com.challenge.common.model.accountDto.UserAccount
import com.challenge.model.transaction.TransactionsDomain
import com.challenge.mapper.transaction.toTransactionsDomain
import com.challenge.repositorycontract.TransactionsRepository
import javax.inject.Inject


class TransactionsRepositoryImpl @Inject constructor(
    private val apiService: com.challenge.starlingbank.networklayer.api.StarlingApiService
) : TransactionsRepository, com.challenge.starlingbank.networklayer.model.ApiResponse() {
    override suspend fun getTransactionsBetween(
        userAccount: UserAccount
    ): NetworkResult<TransactionsDomain> =
        handleApiCall(
            apiCall = {
                apiService.getTransactionsBetween(
                    userAccount.accountUid,
                    userAccount.categoryUid,
                    userAccount.minTransactionTimeStamp,
                    userAccount.maxTransactionTimeStamp
                )
            },
            mapToDomain = { it.toTransactionsDomain() })
}
