package com.challenge.repository


data class UserAccount(
    val accountUid: String,
    val categoryUid: String,
    val minTransactionTimeStamp: String,
    val maxTransactionTimeStamp: String
)