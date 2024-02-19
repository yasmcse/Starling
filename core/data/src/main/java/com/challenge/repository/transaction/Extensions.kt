package com.challenge.repository.transaction

import com.challenge.common.model.transactiondomain.TransactionDomain
import com.challenge.common.model.transactiondomain.TransactionsDomain
import com.challenge.common.model.transactiondto.TransactionsDto
import okhttp3.internal.toImmutableList

fun TransactionsDto.toTransactionsDomain(): TransactionsDomain {
    val list = mutableListOf<TransactionDomain>()
    for (transactionDto in feedItems) {
        list.add(
            with(transactionDto) {
                TransactionDomain(
                    feedItemUid,
                    categoryUid,
                    amount,
                    sourceAmount,
                    direction
                )
            })
    }
    return TransactionsDomain(list.toImmutableList())
}