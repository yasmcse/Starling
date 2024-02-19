package com.challenge.common.model.accountDto


data class UserAccount(
    val accountUid: String,
    val categoryUid: String,
    val minTransactionTimeStamp: String,
    val maxTransactionTimeStamp: String
)