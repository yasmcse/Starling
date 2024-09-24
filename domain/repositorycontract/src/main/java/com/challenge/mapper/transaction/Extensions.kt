package com.challenge.mapper.transaction

import com.challenge.mapper.transaction.model.TransactionDomain
import com.challenge.mapper.transaction.model.TransactionsDomain
import com.challenge.common.model.transactiondto.TransactionsDto

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
    return TransactionsDomain(list.toList())
}