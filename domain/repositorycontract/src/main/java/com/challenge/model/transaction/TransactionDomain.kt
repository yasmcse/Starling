package com.challenge.model.transaction

import com.challenge.common.model.Amount

data class TransactionDomain(
    val feedItemUid: String,
    val categoryUid: String,
    val amount: Amount,
    val sourceAmount: Amount,
    val direction: String,
)
