package com.challenge.common.model.transactiondto

import com.google.gson.annotations.SerializedName

data class TransactionsDto(
    @SerializedName("feedItems") val  feedItems:List<TransactionDto>
)