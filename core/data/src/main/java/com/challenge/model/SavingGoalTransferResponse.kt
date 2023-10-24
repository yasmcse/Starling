package com.challenge.model

data class SavingGoalTransferResponse(
    val savingsGoalTransferResponseV2: TransferResponse
)

data class TransferResponse(
    val transferUid: String,
    val success: Boolean
)