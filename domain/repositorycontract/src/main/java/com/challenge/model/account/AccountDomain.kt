package com.challenge.model.account

data class AccountDomain(
    val accountUid: String,
    val accountType: String,
    val defaultCategory: String
)